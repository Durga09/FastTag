package com.agent.fasttag.view

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import android.widget.ListView
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityPersonalDetailsBinding
import com.agent.fasttag.databinding.LayoutDailogListViewBinding
import com.agent.fasttag.view.adapter.CommanAdapter
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.*
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_agentID
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_parentId
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_roleId
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.gson.Gson
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.Calendar

class PersonalDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalDetailsBinding
    var genderArr = ArrayList<String>()
    var stateArr = ArrayList<String>()
    var documentsTypeArr = ArrayList<String>()
    var retrofitService: RetrofitService? =null
    lateinit var viewModel: FastTagViewModel
    lateinit var adapter: ArrayAdapter<String>
    lateinit var listView: ListView
    lateinit var alertDialog: AlertDialog.Builder
    private var firstNameVal = ""
    private var lastNameVal = ""
    private var genderVal = ""
    private var dobVal = ""
    private var vehicleNumberVal = ""
    private var emailVal = ""
    private var addressLine1Val = ""
    private var addressLine2Val = ""
    private var districtVal = ""
    private var documentTypeVal = ""
    private var documentIdVal = ""
    private var stateVal = ""
    private var pincodeVal = ""
    private var cityVal = ""
    private var customerId=""
    lateinit var dialog: AlertDialog
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initView()
        setupViewModel()
        setUpObserver()
        generateOTP()
    }

    private fun initView() {
        binding.headerLayout.tvToolbarHederTitle.visibility = View.GONE
        binding.headerLayout.tvToolbarTitle.visibility = View.VISIBLE
        binding.headerLayout.ivToolBarBack.visibility = View.VISIBLE
        binding.headerLayout.tvToolbarHederTitle.text = getString(R.string.personal_details)
        genderArr.add("Male")
        genderArr.add("Female")
        stateArr.add("Andhra Pradesh")
        stateArr.add("Telangana")
        stateArr.add("Tamil Nadu")
        stateArr.add("Karnataka")
        documentsTypeArr.add("PAN")
        documentsTypeArr.add("VoterId")
        documentsTypeArr.add("Aadhar card")
        documentsTypeArr.add("Diving License")
       /* binding.etFirstNameInput.setText("Goutham")
        binding.etLastNameInput.setText("Arunagiri")
        binding.etDateOfBirthInput.setText("1998-05-31")
        binding.etVehicleNumberInput.setText("TS10FB9977")
        binding.etAddressLineInput1.setText("Paravathi Flats puzhitivakkam")
        binding.etAddressLineInput2.setText("21/A Kalaimagal street")
        binding.etGenderInput.setText("Male")
        binding.etDistrictInput.setText("Rangareddy")
        binding.etCityInput.setText("Chennai")
        binding.etEmailInput.setText("goutham@m2p.in")
        binding.etStateInput.setText("Tamilnadu")
        binding.etPinCodeInput.setText("500034")*/
//        binding.etDocumentNumberInput.setText("YTRDF5455P")
//        binding.etSelectDocumentTypeInput.setText("PAN")
        binding.etDateOfBirthInput.setOnClickListener {
            getDob()
        }
        binding.etGenderInput.setOnClickListener {
            openDialog(getString(R.string.select_gender), genderArr)
        }
        binding.etStateInput.setOnClickListener {
            openDialog(getString(R.string.select_state), stateArr)
        }
        binding.etSelectDocumentTypeInput.setOnClickListener {
            openDialog(getString(R.string.select_document_type), documentsTypeArr)
        }
        binding.getotp.setOnClickListener {
        viewModel!!.getGenerateOtp(AppConstants.tenant, AppConstants.partnerId, AppConstants.partnerToken, getOtpRequestJson())

        }
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
        viewModel.customerRegistrationData().observe(this) {
            println("customerRegistration Success:: " + it)
         AppConstants.cancelSunsetDialog()
            when (it.status) {
                Status.SUCCESS -> {

                    if(it.data?.result!=null ) {

                        if (it.data?.result.success) {
                            Toast.makeText(
                                this,
                                "Success "+it.data?.result.entityId,
                                Toast.LENGTH_LONG
                            ).show()
                            startActivity(Intent(this, DocumentsDetailsActivity::class.java))
                            overridePendingTransition(
                                R.anim.slide_in_right,
                                R.anim.slide_out_left
                            )
                        } else {
                            Toast.makeText(
                                this,
                                it.data?.exception!!.shortMessage,
                                Toast.LENGTH_LONG
                            ).show()
                            if(customerId!=""){
                                var requestData= DeleteCustomerRequestJson(customerId)
                                val loginRequestJsonData = Gson().toJson(requestData)
                                val jsonObject = JSONObject(loginRequestJsonData)
                                val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
                                println("DeleteCustomerRequestJson:: $request")
                                AppConstants.launchSunsetDialog(this)
                                if(AppConstants.isNetworkAvailable(this)) {
                                    viewModel.deleteCustomerDetailsRequest(request)
                                }else{
                                    AppConstants.showNoInternetConnectionMessageAlert(this)
                                }
                            }
                            /*  startActivity(Intent(this, DocumentsDetailsActivity::class.java))
                              overridePendingTransition(
                                  R.anim.slide_in_right,
                                  R.anim.slide_out_left
                              )*/
                        }
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
        viewModel.createCustomerRequestData().observe(this){
            AppConstants.cancelSunsetDialog()

            when(it.status){
                Status.SUCCESS ->{
//                    showResponseMessageAlert(this,it.data!!.message)
                    var vehicleData = ArrayList<VehicleData>()
                    if(it.data?.code==0){
//                        saveCustomerDetails = "success"
                        Toast.makeText(this,it.data!!.message,Toast.LENGTH_SHORT).show()
                        customerId=it.data.reponseData.customerId!!
                        AppConstants.personalDetail=PersonalDetailsData(
                            false,
                            pincodeVal,
                            AppConstants.country,
                            stateVal,
                            cityVal,
                            addressLine1Val,
                            addressLine2Val,
                            addressLine1Val,
                            emailVal,
                            AppConstants.phoneNumber,
                            lastNameVal,
                            firstNameVal,
                            ""+genderVal.first().uppercase(),
                            AppConstants.entityType,
                            dobVal,
                            documentTypeVal,
                            documentIdVal,
                            customerId,
                        vehicleData)
                        if(AppConstants.isNetworkAvailable(this)) {
                            AppConstants.launchSunsetDialog(this)
                            viewModel.customerRegistration(
                                AppConstants.tenant,
                                AppConstants.partnerId,
                                AppConstants.partnerToken,
                                getRequestJson()
                            )
                        }else{
                            AppConstants.showNoInternetConnectionMessageAlert(this)
                        }


                    }else{
                        AppConstants.showMessageAlert(this,it.data!!.message)
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
        viewModel.deleteCustomerRequestData().observe(this){
            AppConstants.cancelSunsetDialog()

            when(it.status){
                Status.SUCCESS ->{
//                    showResponseMessageAlert(this,it.data!!.message)
                    if(it.data?.code==0){
//                        saveCustomerDetails = "success"
                        Toast.makeText(this,it.data!!.message,Toast.LENGTH_SHORT).show()
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
    }
         fun next(view: View) {
            firstNameVal = binding.etFirstNameInput.text.toString().trim()
            lastNameVal = binding.etLastNameInput.text.toString().trim()
            genderVal = binding.etGenderInput.text.toString().trim()
            dobVal = binding.etDateOfBirthInput.text.toString().trim()
            vehicleNumberVal = binding.etVehicleNumberInput.text.toString().trim()
            emailVal = binding.etEmailInput.text.toString().trim()
            addressLine1Val = binding.etAddressLineInput1.text.toString().trim()
            addressLine2Val = binding.etAddressLineInput2.text.toString().trim()
            districtVal = binding.etDistrictInput.text.toString().trim()
            stateVal = binding.etStateInput.text.toString().trim()
            pincodeVal = binding.etPinCodeInput.text.toString().trim()
            cityVal = binding.etCityInput.text.toString().trim()
            documentTypeVal=binding.etSelectDocumentTypeInput.text.toString().trim()
            documentIdVal=binding.etDocumentNumberInput.text.toString().trim()

//            var requestJsonData = getRequestJson()
//            println(requestJsonData)

//            AppConstants.launchSunsetDialog(this)
//            startActivity(Intent(this, DocumentsDetailsActivity::class.java))
//            overridePendingTransition(
//                R.anim.slide_in_right,
//                R.anim.slide_out_left
//            )
             if(AppConstants.isNetworkAvailable(this)) {
                 callSaveCustomerdetails()
             }else{
                 AppConstants.showNoInternetConnectionMessageAlert(this)
             }




            /* startActivity(Intent(this, DocumentsDetailsActivity::class.java))
         overridePendingTransition(
             R.anim.slide_in_right,
             R.anim.slide_out_left
         );*/
        }

         fun back(view: View) {
            onBackPressed()
        }

        override fun onBackPressed() {
            super.onBackPressed()
            overridePendingTransition(
                R.anim.slide_in_left,
                R.anim.slide_out_right
            );
        }
    private fun callSaveCustomerdetails(){
        val fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)
        var parentId=fasTagPref.USER_parentId!!
        var rollId=fasTagPref.USER_roleId!!
        var agentId=fasTagPref.USER_agentID!!

        var requestData= CreateCustomerRequestJson(agentId,firstNameVal+" "+lastNameVal,
            emailVal,AppConstants.phoneNumber.substring(3),genderVal,pincodeVal,stateVal,AppConstants.country,AppConstants.entityType,AppConstants.entityId,
            "KYC123",dobVal,addressLine1Val+" "+addressLine2Val,documentTypeVal,documentIdVal,
            "0000777",AppConstants.kycStatus,"","","","0","0","Approved",false)
        val loginRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(loginRequestJsonData)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
        AppConstants.launchSunsetDialog(this)
        viewModel.saveCustomerDetailsRequest(request)
    }
        private fun getDob() {
            var calendar = Calendar.getInstance()
            val year = calendar.get(Calendar.YEAR)
            val month = calendar.get(Calendar.MONTH)
            val day = calendar.get(Calendar.DAY_OF_MONTH)
            val datePickerDialog =
                DatePickerDialog(
                    this@PersonalDetailsActivity, DatePickerDialog.OnDateSetListener
                    { view, year, monthOfYear, dayOfMonth ->

//                        binding.etDateOfBirthInput.setText("" + dayOfMonth + "-" + (monthOfYear + 1) + "-" + year)
                        binding.etDateOfBirthInput.setText("" + year + "-" + (monthOfYear + 1) + "-" + dayOfMonth)


                    }, year, month, day
                )
            datePickerDialog.show()
        }

        private fun openDialog(dropFrom: String, listArr: ArrayList<String>) {

            var layoutDailogListViewBinding = LayoutDailogListViewBinding.inflate(layoutInflater)
            val shareAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(this)
            shareAlertBuilder.setView(layoutDailogListViewBinding.root)
            layoutDailogListViewBinding.tvHeader.text = dropFrom

            val alertDialog: AlertDialog = shareAlertBuilder.create()
            layoutDailogListViewBinding.listView.adapter = CommanAdapter(this, listArr)
            layoutDailogListViewBinding.listView.setOnItemClickListener { parent, view, position, id ->
                if (dropFrom == getString(R.string.select_gender)) {
                    binding.etGenderInput.setText(listArr[position])
                }
                else if(dropFrom == getString(R.string.select_document_type)){
                binding.etSelectDocumentTypeInput.setText(listArr[position])
                }else {
                    binding.etStateInput.setText(listArr[position])

                }
                alertDialog.dismiss()
            }
            alertDialog.show()
        }

        fun closeDialog(view: View) {
            dialog.dismiss()
            Toast.makeText(baseContext, "Dialog Closed", Toast.LENGTH_SHORT).show()
        }
    fun getOtpRequestJson():String{
        val mobileNumber=AppConstants.phoneNumber
        val data =GenerateOTPJson(mobileNumber,AppConstants.entityId,"CUSOTMER","")
        val vehicleRegData = Gson().toJson(data)
//        val jsonObject = JSONObject(vehicleRegData)
//        println("jsonObject:: $jsonObject")
        return  vehicleRegData
    }
        private fun getRequestJson() : String {
            val dateInfo = DateInfo(dobVal, AppConstants.dob)
            val communicationInfo = CommunicationInfo(emailVal, true, AppConstants.phoneNumber)
            val addressInfo = AddressInfo(
                pincodeVal,
                AppConstants.country,
                stateVal,
                cityVal,
                "",
                addressLine2Val,
                addressLine1Val,
                AppConstants.addressCategory
            )
            val KycInfo = KycInfo(documentIdVal, documentTypeVal, "3456")
            val kitInfo = KitInfo(
                AppConstants.cardType,
                AppConstants.cardCategory,
                AppConstants.cardRegStatus,
                "$firstNameVal $lastNameVal"
            )
            val kycDocuments = KycDocuments("", "")
            val fleetAddField = FleetAddField("", "")
            val address = Address(AppConstants.country, cityVal, addressLine2Val, addressLine1Val)
            val preferenceData = PreferenceData(arrayListOf(address))

            var personalDetailsReqJson = PersonalDetailsReqJson(
                dateInfo = arrayListOf(dateInfo),
                communicationInfo = arrayListOf(communicationInfo),
                addressInfo = arrayListOf(addressInfo),
                kycInfo = arrayListOf(KycInfo),
                kitInfo = arrayListOf(kitInfo),
                kycDocuments = arrayListOf(kycDocuments),
                customerStatus = AppConstants.customerStatus,
                countryCode = AppConstants.countryCode,
                channelName = AppConstants.channelName,
                kycStatus = AppConstants.channelName,
                fatcaDecl = AppConstants.fatcaDecl,
                consent = AppConstants.consent,
                politicallyExposed = AppConstants.politicallyExposed,
                entityType = "CUSTOMER",
                businessType = AppConstants.businessType,
                business = AppConstants.businessType,
                businessId = AppConstants.entityId,
                specialDate = AppConstants.specialDate,
                branch = AppConstants.businessType,
                corporate = AppConstants.businessType,
                entityId = AppConstants.entityId,
                countryofIssue = AppConstants.countryofIssue,
                dependent = AppConstants.dependent,
                fleetAddField = fleetAddField,
                otp = "",
                contactNo = AppConstants.phoneNumber,
                city = cityVal,
                state = stateVal,
                pincode = pincodeVal,
                address2 = addressLine2Val,
                address = addressLine1Val,
                dap = "",
                idNumber = "",
                proofType = "",
                dob = dobVal,
                gender = "" + genderVal.first().uppercase(),
                lastName = lastNameVal,
                firstName = firstNameVal,
                emailAddress = emailVal,
                country = AppConstants.country,
                reqBranch = "",
                preference = preferenceData,
                title = AppConstants.title

            )
            val jsonData = Gson().toJson(personalDetailsReqJson)
            return jsonData
        }

    fun generateOTP(){
        viewModel!!.generateOtpData().observe(this, Observer {
            when (it.status) {
                Status.SUCCESS -> {
                    AppConstants.cancelSunsetDialog()
                    if(it.data?.result!!.success=="true") {
                        println("entityId" + it.data?.result!!.entityId)
                        AppConstants.showMessageAlert(this,"OTP has sent to "+AppConstants.entityId)
                        AppConstants.entityId = it.data?.result!!.entityId
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
    }

