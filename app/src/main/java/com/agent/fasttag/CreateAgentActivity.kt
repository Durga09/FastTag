package com.agent.fasttag

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.agent.fasttag.databinding.ActivityAgentHomeBinding
import com.agent.fasttag.databinding.ActivityCreateAgentBinding
import com.agent.fasttag.databinding.LayoutTlBottomAdapeterBinding
import com.agent.fasttag.databinding.LayoutTlBottomSheetBinding
import com.agent.fasttag.view.adapter.TeamLeadsListAdapter
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.*
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_agentID
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_parentId
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject

class CreateAgentActivity : AppCompatActivity() {
    var roleFrom =""
    private lateinit var binding: ActivityCreateAgentBinding
    var retrofitService: RetrofitService? =null
    lateinit var viewModel: FastTagViewModel
    lateinit var rollsDataList:ArrayList<RollSResponseData>
    lateinit var teamLeadsListDataList:ArrayList<ReponseData>
    lateinit var selectedTeamLeadData:ReponseData


    private var firstNameVal=""
    private var lastNameVal=""
    private var phoneNumberVal=""
    private var emailVal=""
    private var addressVal=""
    private var rollID=AppConstants.agentRollId
    private var loginFrom=""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateAgentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()

    }
    private fun initView(){

        binding.etFirstNameInput.setText("durga Prasad")
        binding.etLastNameInput.setText("Prasad")
        binding.etFirstNameInput.setText("Durga")
        binding.etEmailInput.setText("durga@gamil.com")
        binding.etPhoneNumberInput.setText("9177883161")

        var bundle :Bundle ?=intent.extras
        roleFrom = bundle!!.getString(getString(R.string.role_from))!!
         loginFrom = AppConstants.loginFrom

        if(roleFrom==getString(R.string.agent)){
            println("LOGIN FROM $loginFrom")

            if(loginFrom==getString(R.string.team_lead)) {
                binding.etTeamLead.visibility = View.GONE
            }else{
                binding.etTeamLead.visibility = View.VISIBLE

            }
            binding.title.text=getString(R.string.agent_details)
            rollID = AppConstants.agentRollId

        }else{
            binding.etTeamLead.visibility=View.GONE
            rollID = AppConstants.teamLeadRollId

        }
        binding.etSelectTeamLead.setOnClickListener {
            showTeamLeadsBottomSheet(teamLeadsListDataList)
        }
        binding.submit.setOnClickListener {

            firstNameVal = binding.etFirstNameInput.text.toString().trim()
            lastNameVal = binding.etLastNameInput.text.toString().trim()
            phoneNumberVal = binding.etPhoneNumberInput.text.toString().trim()
            emailVal = binding.etEmailInput.text.toString().trim()

            if(firstNameVal == ""){

                AppConstants.showMessageAlert(this,getString(R.string.please_enter_first_name))
            }else if( lastNameVal == ""){

                AppConstants.showMessageAlert(this,getString(R.string.please_enter_last_name))
            }else if(emailVal == ""){
                AppConstants.showMessageAlert(this,getString(R.string.please_enter_email))

            }else{
                val fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)
                 var parentId=fasTagPref.USER_parentId!!
                if(rollID == AppConstants.agentRollId && loginFrom!=getString(R.string.team_lead)){
                    parentId =selectedTeamLeadData.parentId
                }
                 if(loginFrom == getString(R.string.team_lead)){
                     parentId =fasTagPref.USER_agentID!!

                 }
                println("parentId:: $parentId")
//            println("USER NAME:: "+defaultPrefs.USER_USERNAME+" USER_parentId::"+defaultPrefs.USER_parentId+" USER_agentID:: "+defaultPrefs.USER_agentID)
                var requestData= CreateAgentRequestJson("$firstNameVal $lastNameVal",parentId,rollID,"",phoneNumberVal,firstNameVal,lastNameVal,emailVal,"")
                val agentRequestJsonData = Gson().toJson(requestData)
                val jsonObject = JSONObject(agentRequestJsonData)
                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
                println("CreateAgentRequestJson:: "+jsonObject)

                AppConstants.launchSunsetDialog(this)
                viewModel.createTlOrAgent(request)
            }
        }

        setupViewModel()
        setUpObserver()
        println("rollID FROM $rollID")

        if(rollID == AppConstants.agentRollId && loginFrom!=getString(R.string.team_lead)){
            callGetAllTeamLeads()
        }
    }
    private fun callGetAllTeamLeads(){
        AppConstants.launchSunsetDialog(this)
        viewModel.getAllTeamLeadsReuest()
    }
    private fun setupViewModel() {
        retrofitService = RetrofitService.getInstance(AppConstants.baseURL)
        var repository = FasTagRepository(retrofitService!!)
        viewModel = ViewModelProvider(
            this,
            FasTagViewModelFactory(repository)
        )[FastTagViewModel::class.java]
    }
    private fun setUpObserver(){
        viewModel.getAllTeamLeadsRequest().observe(this){
            AppConstants.cancelSunsetDialog()
            when(it.status){
                Status.SUCCESS ->{
                    teamLeadsListDataList=it.data!!.reponseData
                    println("teamLeadsDataList:: $teamLeadsListDataList")

//                    showResponseMessageAlert(this,it.data!!.message)
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
       viewModel.getAllRosllsRequest().observe(this){
            AppConstants.cancelSunsetDialog()
            when(it.status){
                Status.SUCCESS ->{
                    rollsDataList=it.data?.reponseData!!
                    println("rollsDataList:: $rollsDataList")

//                    showResponseMessageAlert(this,it.data!!.message)
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
        viewModel.createAgentRequest().observe(this){
            AppConstants.cancelSunsetDialog()
            when(it.status){
                Status.SUCCESS ->{
                    println("createAgentRequest:: $it")
                    showMessageAlert(this,it.data!!.message)
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
    }
    fun showMessageAlert(mContext: Activity, message: String?) {
        mContext.runOnUiThread {
            val builder = AlertDialog.Builder(mContext)
            builder.setMessage(message)
            builder.setTitle(mContext.getString(R.string.app_name))
            builder.setCancelable(false)
            builder.setPositiveButton(
                Html.fromHtml("<font color=" + mContext.resources.getColor(R.color.colorAccent) + ">OK</font>"),
                DialogInterface.OnClickListener { dialog: DialogInterface, which: Int -> dialog.dismiss() })
            /* builder.setNegativeButton("No", (DialogInterface.OnClickListener) (dialog, which) -> {
            dialog.cancel();
        });*/
            val alertDialog = builder.create()
            // Show the Alert Dialog box
            alertDialog.show()
        }
    }
    private fun showTeamLeadsBottomSheet(teamLeadsResponseData: ArrayList<ReponseData>){
        val dialog = BottomSheetDialog(this)
        println("showTeamLeadsBottomSheet ::"+teamLeadsResponseData)
        var BottomSheetBinding= LayoutTlBottomSheetBinding.inflate(layoutInflater)
//        val view = layoutInflater.inflate(R.layout.layout_tl_bottom_sheet, null)
        val view = BottomSheetBinding.root

//        val btnClose = view.findViewById<ImageView>(R.id.idIVCourse)
        val teamLeadsListAdapter = TeamLeadsListAdapter(teamLeadsResponseData) {
            println("TeamLeadsListAdapter:: Click$it")
            binding.etSelectTeamLead.setText(it.userName)
            selectedTeamLeadData=it
            dialog.dismiss()
        }

//        val recyclerView: RecyclerView = view.findViewById(R.id.rc_team_list)
        BottomSheetBinding.rcTeamList.layoutManager = LinearLayoutManager(this)

        BottomSheetBinding.rcTeamList.adapter = teamLeadsListAdapter
        BottomSheetBinding.idIVCourse.setOnClickListener {

            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

}