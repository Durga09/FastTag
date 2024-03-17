package com.agent.fasttag.view

//import androidx.lifecycle.lifecycleScope
//import com.papayacoders.phonepe.ApiUtilities
//import com.phonepe.intent.sdk.api.models.PhonePeEnvironment

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityPhonePayPaymentGatewayBinding
import com.agent.fasttag.databinding.ActivityTransactionsListBinding
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.*
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.PAYMENTBACKURL
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_USERNAME
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_agentID
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_firstName
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_lastName
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_parentId
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_phoneNumber
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_roleId
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_roleName
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.gson.Gson
import com.phonepe.intent.sdk.api.B2BPGRequestBuilder
import com.phonepe.intent.sdk.api.PhonePe
import com.phonepe.intent.sdk.api.PhonePeInitException
import com.phonepe.intent.sdk.api.models.PhonePeEnvironment
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.nio.charset.Charset
import java.security.MessageDigest


private lateinit var vehicleRegviewModel: FastTagViewModel

class PhonePayPaymentGatewayActivity: AppCompatActivity() {
    var apiEndPoint = "/pg/v1/pay"
    val salt = "5bb51303-f908-43be-b6ed-515c12fb63b6" // salt key
    val MERCHANT_ID = "PGTESTPAYUAT97"  // Merhcant id
//    val MERCHANT_ID = "PGTESTPAYUAT"  // Merhcant id
    var MERCHANT_TID = "MT7850590068188104"
    var TRASACTION_STATUS_RETRY_NUMBER= 0
    var paymentType="PAY_PAGE"
    private val B2B_PG_REQUEST_CODE = 777
    private var rollID=AppConstants.agentRollId
    private var loginFrom=""
    private lateinit var fasTagPref: SharedPreferences
    var parentId=""
    var loginUserPhoneNumber=""
    var to_payment_gateway=""
    var TRANSACTION_ID_BY_TRSACTIONS_HIST=""
    private lateinit var binding: ActivityPhonePayPaymentGatewayBinding


    private fun getInstalledUPIApps(): ArrayList<String>? {
        val upiList: ArrayList<String> = ArrayList()
        val uri: Uri = Uri.parse(String.format("%s://%s", "upi", "pay"))
        val upiUriIntent = Intent()
        upiUriIntent.data = uri
        val packageManager = application.packageManager
        val resolveInfoList =
            packageManager.queryIntentActivities(upiUriIntent, PackageManager.MATCH_DEFAULT_ONLY)
        for (resolveInfo in resolveInfoList) {
            upiList.add(resolveInfo.activityInfo.packageName)
        }
        return upiList
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPhonePayPaymentGatewayBinding.inflate(layoutInflater)
        setContentView(binding.root)
        PhonePe.init(this,PhonePeEnvironment.SANDBOX,MERCHANT_ID,"")
        loginFrom = AppConstants.loginFrom
        binding.tvPhonePayAmount.text =getString(R.string.Rs)+" "+(AppConstants.referralCodeVal*100)
        var string_signature = PhonePe.getPackageSignature()
         fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)
         parentId=fasTagPref.USER_parentId!!
        loginUserPhoneNumber=fasTagPref.USER_phoneNumber!!
        var roleId=fasTagPref.USER_roleId
        binding.roleName.text = fasTagPref.USER_roleName
        binding.userName.text = fasTagPref.USER_firstName+" "+fasTagPref.USER_lastName

        if(roleId=="1"){
            parentId = fasTagPref.USER_agentID!!
        }

        var bundle :Bundle ?=intent.extras
        to_payment_gateway= bundle?.getString(getString(R.string.to_payment_gateway)).toString()
        TRANSACTION_ID_BY_TRSACTIONS_HIST=bundle?.getString(getString(R.string.transaction_id)).toString()
        println("TRANSACTION_ID_BY_TRSACTIONS_HIST::"+TRANSACTION_ID_BY_TRSACTIONS_HIST)
        val button = findViewById<Button>(R.id.pay_button)
        button.setOnClickListener {
            paymentConformationAlertMessage("Are you sure, do you want to proceed to payment")
            /*try {
                PhonePe.getImplicitIntent( this, b2BPGRequest, "")
                    ?.let { it1 -> startActivityForResult(it1,B2B_PG_REQUEST_CODE) };
            } catch( e:PhonePeInitException){

            }*/
        }

        setupVehicleRegviewModelViewModel()
        CallObserveuploadVehicleRegistration()
        if(AppConstants.referralCodeKey==getString(R.string.zero)){

            getTransactionId()

        }
    }
    private fun initiatePhonePeRequest(MERCHANT_T_ID:String,callBackUrl:String){
        MERCHANT_TID = MERCHANT_T_ID
        var requestData= PhonePayRequest(MERCHANT_ID,MERCHANT_T_ID,
            "MUID123",AppConstants.referralCodeVal,
            callBackUrl.replace("\\/","/"),loginUserPhoneNumber,
            PaymentInstrument(paymentType))


        fasTagPref.PAYMENTBACKURL = callBackUrl
        val agentRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(agentRequestJsonData).toString().replace("\\/","/")
        val request = jsonObject.toRequestBody("application/json".toMediaTypeOrNull());
        println("initiatePhonePeRequest:: "+jsonObject)

        val payloadBase64 = android.util.Base64.encodeToString(
            jsonObject.toString().toByteArray(Charset.defaultCharset()), android.util.Base64.DEFAULT
        )
        val checksum = sha256(payloadBase64 + apiEndPoint + salt) + "###1";


        val b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checksum)
            .setUrl(apiEndPoint)
            .build()

        try {
            PhonePe.getImplicitIntent( this, b2BPGRequest, "")
                ?.let { it1 -> startActivityForResult(it1,B2B_PG_REQUEST_CODE) };
        } catch( e:PhonePeInitException){

        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("PAPAYACODERS", "onActivityResult: ${Gson().toJson(data)}")
        if (requestCode == B2B_PG_REQUEST_CODE && data!= null) {
//            checkStatus()
            getTransactionStatus(MERCHANT_TID)

        }
    }
    fun getTransactionId(){
        AppConstants.launchSunsetDialog(this)
        var amount=0
        var status="COMPLETED"
        if(AppConstants.referralCodeKey!=getString(R.string.zero)) {
             amount=AppConstants.referralCodeVal
             status="Initiated"
        }
        var requestData= GetTransactionIdRequestJson(fasTagPref.USER_parentId!!,fasTagPref.USER_agentID!!,
            fasTagPref.USER_phoneNumber!!,fasTagPref.USER_phoneNumber!!,AppConstants.vehicleNumberVal,status,""+amount)

//        var requestData= GetTransactionIdRequestJson("c59a93f1-0655-4c92-b54c-6ca6637998ff","d63060b6-c5cf-4424-837f-b027a413277a",
//            "9874563210","9874563210","TS10FB9015","Initiated","200")

        val agentRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(agentRequestJsonData)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
        println("GetAgentsByIdRequestJson:: "+jsonObject)
        vehicleRegviewModel.getTransactionID(request)
    }
    fun getTransactionStatus(trasactionId:String){
        var requestData= GetTransactionStatusRequestJson(trasactionId)

        val agentRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(agentRequestJsonData)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
        println("GetAgentsByIdRequestJson:: "+jsonObject)
        vehicleRegviewModel.getTransactionStatus(request)
    }
    fun CallObserveuploadVehicleRegistration() {

        vehicleRegviewModel.checkStatusOfPaymentPhonePe().observe(this) {
            AppConstants.cancelSunsetDialog()
            println("checkStatusOfPaymentPhonePe:: $it")
            when (it.status) {
                Status.SUCCESS -> {
//                    AppConstants.showMessageAlert(this,it.data!!.message)
//                    AppConstants.launchSunsetDialog(this)
//                     var requestBody=generateLoadWalletRequestJson()
//                    vehicleRegviewModel.paymentLoadWaletRequest(AppConstants.tenant_M2P,requestBody)
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
        vehicleRegviewModel.loadWalletRequestData().observe(this) {
            AppConstants.cancelSunsetDialog()
            println("loadWalletRequestData:: $it")
            when (it.status) {
                Status.SUCCESS -> {
                    if(it.data?.result!=null) {
                        statusPendingMessage( "LoadWalletRequestData  Success"+it.data?.result?.txId)
                    }else{
                        AppConstants.showMessageAlert(this, it.data?.exception?.detailMessage)

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
        vehicleRegviewModel.getTransactionIdData().observe(this) {
            AppConstants.cancelSunsetDialog()
            println("loadWalletRequestData:: $it")
            when (it.status) {
                Status.SUCCESS -> {
                    if(it.data?.status==true) {
//                        AppConstants.showMessageAlert(this, it.data?.reponseData?.transactionId)
                        if(AppConstants.referralCodeKey==getString(R.string.zero)){
                            AppConstants.launchSunsetDialog(this)
                            var requestBody = generateLoadWalletRequestJson()
                            vehicleRegviewModel.paymentLoadWaletRequest( AppConstants.tenant_M2P,requestBody )
                        }else {

                            initiatePhonePeRequest(
                                it.data?.reponseData?.transactionId!!,
                                it.data?.reponseData?.callBackUrl!!
                            )
                        }

                    }else{
                        AppConstants.showMessageAlert(this, it.data?.message)

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
        vehicleRegviewModel.getTransactionStatusRequestData().observe(this) {
            println("loadWalletRequestData:: $it")
            when (it.status) {
                Status.SUCCESS -> {
                    if(it.data?.status==true) {
//                        AppConstants.showMessageAlert(this, it.data?.message)
//                        AppConstants.showMessageAlert(this,it.data!!.message)
                        println("TRASACTION_STATUS_RETRY_NUMBER:: $TRASACTION_STATUS_RETRY_NUMBER")
                        if(it.data.reponseData?.status=="COMPLETED") {
                            AppConstants.cancelSunsetDialog()
                            AppConstants.launchSunsetDialog(this)
                            var requestBody = generateLoadWalletRequestJson()
                            vehicleRegviewModel.paymentLoadWaletRequest( AppConstants.tenant_M2P,requestBody )
                        }else{
                            if(TRASACTION_STATUS_RETRY_NUMBER>4){
                                AppConstants.cancelSunsetDialog()
                                TRASACTION_STATUS_RETRY_NUMBER=0
                                statusPendingMessage("Status not updated.")
                            }else {
                                if(TRASACTION_STATUS_RETRY_NUMBER!=0){
                                    Thread.sleep(2000)
                                }
                                getTransactionStatus(MERCHANT_TID)

                                if(TRASACTION_STATUS_RETRY_NUMBER==0) {
                                    AppConstants.launchSunsetDialog(this)
                                }

                                TRASACTION_STATUS_RETRY_NUMBER += 1
                            }
                        }
                    }else{
                        AppConstants.cancelSunsetDialog()
                        AppConstants.showMessageAlert(this, it.data?.message)

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
    private fun statusPendingMessage(message: String?)
    {

            runOnUiThread {
                val builder = AlertDialog.Builder(this)
                builder.setMessage(message)
                builder.setTitle(getString(R.string.app_name))
                builder.setCancelable(false)
                builder.setPositiveButton(
                    Html.fromHtml("<font color=" + this.resources.getColor(R.color.colorAccent) + ">OK</font>"),
                    DialogInterface.OnClickListener {
                            dialog: DialogInterface, _: Int -> dialog.dismiss()
                        var intent=Intent(this, TransactionsListActivity::class.java)
                        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK

                        intent.putExtra(getString(R.string.role_from),getString(R.string.team_lead))
                        startActivity(intent)
                        AppConstants.slideToRightAnim(this)

                    })
                /* builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
                dialog.cancel();
            });*/
                val alertDialog = builder.create()
                // Show the Alert Dialog box
                alertDialog.show()
            }
        }
    private fun paymentConformationAlertMessage(message: String?)
    {

        runOnUiThread {
            val builder = AlertDialog.Builder(this)
            builder.setMessage(message)
            builder.setTitle(getString(R.string.app_name))
            builder.setCancelable(false)
            builder.setPositiveButton(
                Html.fromHtml("<font color=" + this.resources.getColor(R.color.green) + ">Yes</font>"),
                DialogInterface.OnClickListener {
                        dialog: DialogInterface, _: Int -> dialog.dismiss()
                        dialog.dismiss()
                    println("to_payment_gateway::"+to_payment_gateway)
                    if(to_payment_gateway!=getString(R.string.transactions)) {
                        getTransactionId()
                    }else{
                        println("PAYMENTBACKURL::"+fasTagPref.PAYMENTBACKURL!!)
                        initiatePhonePeRequest(TRANSACTION_ID_BY_TRSACTIONS_HIST,fasTagPref.PAYMENTBACKURL!!)

                    }
                })
            builder.setNegativeButton(
                Html.fromHtml("<font color=" + this.resources.getColor(R.color.black) + ">No</font>"),
                DialogInterface.OnClickListener {
                        dialog: DialogInterface, _: Int -> dialog.dismiss()
                   dialog.dismiss()
                })
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
        }
    }

    private fun setupVehicleRegviewModelViewModel() {
        RetrofitService.retrofitService=null
        var retrofitService = RetrofitService.getInstance(AppConstants.baseURL)
        var repository = FasTagRepository(retrofitService!!)
        vehicleRegviewModel = ViewModelProvider(
            this,
            FasTagViewModelFactory(repository)
        )[FastTagViewModel::class.java]
    }
    private fun checkStatus() {

        AppConstants.launchSunsetDialog(this)
        val xVerify =
            sha256("/pg/v1/status/$MERCHANT_ID/$MERCHANT_TID"+salt) + "###1"
        val headers = mapOf(
            "Content-Type" to "application/json",
            "X-VERIFY" to xVerify,
        )
      vehicleRegviewModel.paymentCheckStatusFromPhonePe(headers,MERCHANT_ID,MERCHANT_TID)

    }
    private fun sha256(input: String): String {
        val bytes = input.toByteArray(Charsets.UTF_8)
        val md = MessageDigest.getInstance("SHA-256")
        val digest = md.digest(bytes)
        return digest.fold("") { str, it -> str + "%02x".format(it) }
    }
   private fun generateLoadWalletRequestJson():RequestBody{
       var externalTransactionId=System.currentTimeMillis()
        var requestData= LoadWalletRequest("LQFLEET10001",loginUserPhoneNumber,"GENERAL",
            "transferfunds",""+AppConstants.amountByTagId,"M2C","LQFLEET","LQFLEET",
            "MOBILE",""+externalTransactionId,"1234")
        val agentRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(agentRequestJsonData)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

       return request
    }
}