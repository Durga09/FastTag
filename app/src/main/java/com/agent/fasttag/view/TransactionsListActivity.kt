package com.agent.fasttag.view

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityTransactionsListBinding
import com.agent.fasttag.view.adapter.TransactionsListAdapter
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.GetTransactionByAgentsByIdRequestJson
import com.agent.fasttag.view.model.GetTransactionByIdRes
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_agentID
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.security.MessageDigest

class TransactionsListActivity : AppCompatActivity() {
    private lateinit var binding:ActivityTransactionsListBinding
    lateinit var viewModel: FastTagViewModel
    var apiEndPoint = "/pg/v1/pay"
    val MERCHANT_ID = "PGTESTPAYUAT97"  // Merhcant id

    val salt = "5bb51303-f908-43be-b6ed-515c12fb63b6" // salt key
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTransactionsListBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setupModelViewModel()
        CallObserveupload()
        getTransactionByAgent()
    }

    override fun onBackPressed() {
        super.onBackPressed()
         backToHome()
    }
    private fun initView(){
        binding.headerLayout.tvToolbarHederTitle.visibility= View.VISIBLE
        binding.headerLayout.tvToolbarTitle.visibility= View.GONE
        binding.headerLayout.tvToolbarSubHeaderTitle.visibility= View.GONE

        binding.headerLayout.ivToolBarBack.visibility= View.VISIBLE
        binding.headerLayout.tvToolbarHederTitle.text=getString(R.string.transactions)
        binding.headerLayout.ivToolBarBack.setOnClickListener {
          backToHome()
        }
    }
    private fun backToHome(){
        var intent= Intent(this, AgentHomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

        intent.putExtra(getString(R.string.role_from),getString(R.string.team_lead))
        startActivity(intent)
        AppConstants.slideToLeftAnim(this)
    }

    private fun checkStatus(MERCHANT_ID:String,MERCHANT_TID:String) {

        val xVerify =
            sha256("/pg/v1/status/$MERCHANT_ID/$MERCHANT_TID"+salt) + "###1"
        println("xVerify"+"  "+xVerify)
        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-VERIFY" to xVerify,
        )
        viewModel.paymentCheckStatusFromPhonePe(headers,MERCHANT_ID,MERCHANT_TID)

    }
    private fun setupModelViewModel() {
        RetrofitService.retrofitService=null
        var retrofitService = RetrofitService.getInstance(AppConstants.baseURL)
        var repository = FasTagRepository(retrofitService!!)
        viewModel = ViewModelProvider(
            this,
            FasTagViewModelFactory(repository)
        )[FastTagViewModel::class.java]
    }
    private fun getTransactionByAgent(){
        var fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)
        var agentID=fasTagPref.USER_agentID!!
//        var agentID="d63060b6-c5cf-4424-837f-b027a413277a"
        AppConstants.launchSunsetDialog(this)
        var requestData= GetTransactionByAgentsByIdRequestJson(agentID)
        val agentRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(agentRequestJsonData)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
        viewModel.getTransactionDetailsByAgent(request)
    }
    fun CallObserveupload() {
        viewModel.checkStatusOfPaymentPhonePe().observe(this) {
            AppConstants.cancelSunsetDialog()
            println("checkStatusOfPaymentPhonePe:: $it")
            when (it.status) {
                Status.SUCCESS -> {
                    if(it.data!=null) {
                        showMessageAlert( it.data!!.message+" \n"+it.data.data?.paymentInstrument?.type+" \n"+it.data.data?.merchantTransactionId)
                    }else{
                        AppConstants.showMessageAlert(this, "Status not updated. Please try again..")

                    }

                }
                Status.LOADING -> {
                    AppConstants.launchSunsetDialog(this)
                }
                Status.ERROR -> {
                    //Handle Error
                    AppConstants.cancelSunsetDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        viewModel.getTransactionsResDataByAgentRequestData().observe(this) { it ->
            AppConstants.cancelSunsetDialog()
            println("getTransactionsResDataByAgentRequestData:: $it")
            when (it.status) {
                Status.SUCCESS -> {
//                    AppConstants.showMessageAlert(this,it.data!!.message)
                    if (it.data?.reponseData!!.isNotEmpty()){
                       var transactionData= sortBydateList(it.data?.reponseData)
                    var trasactionsListAdapter = TransactionsListAdapter(transactionData) {
                        println("TeamLeadsListAdapter:: Click$it")
                        if(it.status.equals("failed", ignoreCase = true)){
                            var intent=Intent(this, PhonePayPaymentGatewayActivity::class.java)
                            intent.putExtra(getString(R.string.transaction_id),it.transactionId)
                            intent.putExtra(getString(R.string.to_payment_gateway),getString(R.string.transactions))
                            startActivity(intent)
                            AppConstants.slideToRightAnim(this)
                        }
                        else if(it.status.equals("initiated", ignoreCase = true)){
                            AppConstants.launchSunsetDialog(this)
                            checkStatus(MERCHANT_ID, it.transactionId)
                        }
                    }
                    binding.transactionList.layoutManager = LinearLayoutManager(this)

                    binding.transactionList.adapter = trasactionsListAdapter
//                    showResponseMessageAlert(this,it.data!!.message)
                    binding.noDataAvailable.visibility = View.GONE
                    binding.transactionList.visibility = View.VISIBLE
                }
                else {
                    binding.noDataAvailable.visibility = View.VISIBLE
                    binding.transactionList.visibility = View.GONE
                }

        }


                Status.LOADING -> {
                }
                Status.ERROR -> {
                    //Handle Error
                    AppConstants.cancelSunsetDialog()
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
    }
    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
    private fun sortBydateList(documentsList : List<GetTransactionByIdRes>): List<GetTransactionByIdRes> {
        val sortedList = documentsList.sortedByDescending {  it.createdOn }
        return sortedList
    }
    fun showMessageAlert( message: String?) {
        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(message)
            builder.setTitle(getString(R.string.app_name))
            builder.setCancelable(false)
            builder.setPositiveButton(
                Html.fromHtml("<font color=" + resources.getColor(R.color.colorAccent) + ">OK</font>"),
                DialogInterface.OnClickListener {
                        dialog: DialogInterface, which: Int -> dialog.dismiss()
                    getTransactionByAgent()
                })
            /* builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });*/
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
        }
    }
}