package com.agent.fasttag.view

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityVehicleRegistrationBinding
import com.agent.fasttag.databinding.LayoutDailogListViewBinding
import com.agent.fasttag.view.adapter.CommanAdapter
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.CustomerDetailsByIdReequest
import com.agent.fasttag.view.model.PersonalDetailsData
import com.agent.fasttag.view.model.VehicleData
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class VehicleRegistration : AppCompatActivity() {
    private lateinit var binding: ActivityVehicleRegistrationBinding
    private var referralCode=""
    private var vehicleClass=""
    private var agentId=""
    private var phoneNumber=""
    var vehicleClassArr = ArrayList<String>()
    var retrofitService: RetrofitService? =null
    lateinit var viewModel: FastTagViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityVehicleRegistrationBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initView()
        setUpObserver()

    }

    private fun setupViewModel() {
        retrofitService = RetrofitService.getInstance(AppConstants.baseURL)
        var repository = FasTagRepository(retrofitService!!)
        viewModel = ViewModelProvider(
            this,
            FasTagViewModelFactory(repository)
        )[FastTagViewModel::class.java]
    }
    private fun initView(){
        setupViewModel()
        binding.headerLayout.tvToolbarHederTitle.visibility=View.GONE
        binding.headerLayout.tvToolbarTitle.visibility=View.VISIBLE
        binding.headerLayout.ivToolBarBack.visibility=View.VISIBLE
        binding.headerLayout.tvToolbarHederTitle.text=getString(R.string.vehicle_details)
        binding.etVehicleClassInput.setText(getString(R.string.CUSTOMER))
        binding.etPhoneNumberInput.setText("2233778899")
        vehicleClassArr.add(getString(R.string.CUSTOMER))
        vehicleClassArr.add(getString(R.string.TRUCK_RETAIL))
        vehicleClassArr.add(getString(R.string.TRUCK_CORPORATE))
        binding.etVehicleClassInput.setOnClickListener {
            openDialog(getString(R.string.select_vehicle_type),vehicleClassArr)
        }
        binding.validateReferralCode.setOnClickListener {
            var referralCodeKey=binding.referralCodeInput.text.toString()
            AppConstants.referralCodeKey=referralCodeKey

            when (referralCodeKey){

                "ZERO"  -> AppConstants.referralCodeVal=150
                "HUNDRED"  -> AppConstants.referralCodeVal=100
                "TWO HUNDRED"  -> AppConstants.referralCodeVal=200
                "THREE HUNDRED"  -> AppConstants.referralCodeVal=300
                "FOUR HUNDRED"  -> AppConstants.referralCodeVal=400
                "FIVE HUNDRED"  -> AppConstants.referralCodeVal=500

            }

            if(AppConstants.referralCodeVal==150 || AppConstants.referralCodeVal==100 || AppConstants.referralCodeVal==200
                ||AppConstants.referralCodeVal==300 || AppConstants.referralCodeVal == 400 || AppConstants.referralCodeVal == 500){
                Toast.makeText(this,"Referral code added success",Toast.LENGTH_SHORT).show()
            }else{
                Toast.makeText(this,"Please Enter Referral Code ",Toast.LENGTH_SHORT).show()
            }

           /* if(referralCodeVal=="ZERO" ){
                AppConstants.referralCodeVal=150
            }
             else if( referralCodeVal =="HUNDRED" ){
                AppConstants.referralCodeVal=100
            }
             else if( referralCodeVal =="TWO HUNDRED" ){
                AppConstants.referralCodeVal=200
            }
            else if(referralCodeVal == "THREE HUNDRED"){
                AppConstants.referralCodeVal=300
            }*/


        }

    }



    fun openDialog(dropFrom:String,listArr:ArrayList<String>) {

        var layoutDailogListViewBinding =  LayoutDailogListViewBinding.inflate(layoutInflater)
        val shareAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
        shareAlertBuilder.setView(layoutDailogListViewBinding.root)
        layoutDailogListViewBinding.tvHeader.text=dropFrom

        val alertDialog: AlertDialog = shareAlertBuilder.create()
        layoutDailogListViewBinding.listView.adapter= CommanAdapter(this,listArr)
        layoutDailogListViewBinding.listView.setOnItemClickListener { parent, view, position, id ->
            if(dropFrom == getString(R.string.select_vehicle_type)){
                binding.etVehicleClassInput.setText(listArr[position])
            }else{
//                binding.etStateInput.setText(listArr[position])

            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
    public fun next(view:View){

//        viewModel.generateOtpData.removeObservers(this);


        referralCode = binding.referralCodeInput.text.toString()
        vehicleClass = binding.etVehicleClassInput.text.toString()
        agentId      = binding.etAgentIdInput.text.toString()
        phoneNumber  = binding.etPhoneNumberInput.text.toString()

        if(phoneNumber==""){
            Toast.makeText(this,"Please enter phone number",Toast.LENGTH_SHORT).show()
        }
//        else if(vehicleClass==""){
//            Toast.makeText(this,"Please select vehicle class",Toast.LENGTH_SHORT).show()
//
//        }
        else if(AppConstants.referralCodeKey==""){
            Toast.makeText(this,"Please enter referral code",Toast.LENGTH_SHORT).show()
        }
       else {
            vehicleClass = vehicleClass.replace(" ", "_")
//            println("vehicleClass:: $vehicleClass")
            AppConstants.entityType=vehicleClass
            AppConstants.entityId=phoneNumber

            AppConstants.phoneNumber = "+91$phoneNumber"
            var requestData= CustomerDetailsByIdReequest(phoneNumber)
            val agentRequestJsonData = Gson().toJson(requestData)
            val jsonObject = JSONObject(agentRequestJsonData)
            val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
//            println("customerDetailsByIdReequest:: "+jsonObject)
            if(AppConstants.isNetworkAvailable(this)) {
                AppConstants.launchSunsetDialog(this)
                viewModel.getCustomerDetailsByMobileRequest(request)
            }else{
                AppConstants.showNoInternetConnectionMessageAlert(this)
            }

        }
    }
    private fun setUpObserver(){
        viewModel.getCustomerRequestDataByMobile().observe(this){
            AppConstants.cancelSunsetDialog()
            when(it.status){
                Status.SUCCESS ->{
                    println("Code:: "+it.data?.code)
                    if(it.data?.code==2) {
                         AppConstants.registerFromCode=1
                        startActivity(Intent(this, PersonalDetailsActivity::class.java))
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
//                    showResponseMessageAlert(this,it.data!!.message)
                    }else if(it.data?.code==0){
                        AppConstants.registerFromCode=0

                        var teamLeadsListDataList = it.data!!.reponseData
                        println("getCustomerRequestDataByMobile:: $teamLeadsListDataList")
//                    AppConstants.personalDetail = it.data.reponseData
                        var resData = it.data.reponseData
                        var vehicleData= ArrayList<VehicleData>()
                        if(resData.vehicleData!=null){
                            vehicleData=resData.vehicleData!!
                        }
                        AppConstants.personalDetail = PersonalDetailsData(
                            true,
                            resData.pincode!!,
                            resData.country!!,
                            resData.state!!,
                            resData.address!!,
                            resData.address!!,
                            resData.address!!,
                            resData.address!!,
                            resData.email!!,
                            resData.phoneNumber!!,
                            resData.name!!,
                            resData.name!!,
                            resData.gender?.first()!!.uppercase(),
                            "" + resData.entityType!!,
                            resData.dateOfBirth!!,
                            resData.documentType!!,
                            resData.documentNumber!!,
                            resData.customerId!!,
                            vehicleData

                        )
                        startActivity(Intent(this, DocumentsDetailsActivity::class.java))
                        overridePendingTransition(
                            R.anim.slide_in_right,
                            R.anim.slide_out_left
                        )
                    }else{

                     AppConstants.showMessageAlert(this,it.data!!.message)

                    }
                }
                Status.LOADING -> {
                    AppConstants.launchSunsetDialog(this)

                }
                Status.ERROR -> {
                    //Handle Error
                    AppConstants.cancelSunsetDialog()
                    if(AppConstants.isNetworkAvailable(this)){
                        Toast.makeText(this, "No internet connection. Please try again..", Toast.LENGTH_LONG).show()

                    }else {
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                }
            }
        }
        viewModel!!.generateOtpData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppConstants.cancelSunsetDialog()
                    if(it.data?.result!!.success=="true") {
                        println("entityId" + it.data?.result!!.entityId)
                        AppConstants.entityId = it.data?.result!!.entityId
//                       navigateNextActivity()
                    }else{
                        Toast.makeText(this,  it.data?.result!!.entityId, Toast.LENGTH_LONG).show()

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
        })


    }

    public fun back(view:View){
      onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        );
    }
}