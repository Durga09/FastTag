package com.agent. fasttag.view

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.widget.ArrayAdapter
import android.widget.FrameLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.agent.fasttag.R
import com.agent.fasttag.databinding.ActivityDocumentsDetailsBinding
import com.agent.fasttag.databinding.ChhoseImageFromBinding
import com.agent.fasttag.databinding.LayoutDailogListViewBinding
import com.agent.fasttag.databinding.LayoutTlBottomSheetBinding
import com.agent.fasttag.view.adapter.CommanAdapter
import com.agent.fasttag.view.adapter.VehicleNumbersAdapter
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.*
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_agentID
import com.agent.fasttag.view.util.Status
import com.agent.fasttag.view.viewmodel.FasTagRepository
import com.agent.fasttag.view.viewmodel.FasTagViewModelFactory
import com.agent.fasttag.view.viewmodel.FastTagViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.zxing.integration.android.IntentIntegrator
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONException
import org.json.JSONObject
import java.io.File
import java.io.FileOutputStream
import java.util.*


class DocumentsDetailsActivity : AppCompatActivity() {
    private lateinit var binding: ActivityDocumentsDetailsBinding
    private val REQUEST_PERMISSION = 100
    private val REQUEST_IMAGE_CAPTURE = 1
    private val REQUEST_PICK_IMAGE = 2
    private var imageFrom=0
    var addOrReplaceArr = ArrayList<String>()
    var documentsTypeArr = ArrayList<String>()
    var vehicleNumberVal = ""
    var scanned_kit_number=""
    var kit_number=""
    var mProfileId=""
    var mTagId=""
    var oldKitNumber=""
    var newKitNumber=""
    var newKitNumberForReplace=""
    var saveCustomerDetails="not done"
    var addReplaceRegistrationNumber=""
    var kitNumberUpdate=false
    var customerFile:File?=null
    var rcBackSideFile:File?=null
    lateinit var personalDetailsData: PersonalDetailsData
    private var qrScanIntegrator: IntentIntegrator? = null
    var retrofitService: RetrofitService? =null
    //    lateinit var kycviewModel: FastTagViewModel
    lateinit var vehicleRegviewModel:FastTagViewModel
    //    lateinit var unLockKitviewModel:FastTagViewModel
    var SelectAddReplace=""
    val profilerIdsArry:Map<String, String> = mapOf(
        "VC4" to "MC4 Car/Jeep/Van",
        "VC4" to "MC20 Tata Ace/Similar Light Commercial Vehicle", "VC5" to "MC5 Light Commercial Vehicle",
        "VC5" to "MC9 Mini Bus", "VC6" to "MC8 Bus (3 Axie)", "VC6" to "MC11 Truck(3 Axie)/Light Commercial Vehicle(3 Axie)",
        "VC7" to "MC7 BUs(2 Axie)","VC7" to "MC10 Truck(2 Axie)", "VC12" to "MC12 Truck (4 Axie)","VC12" to "MC13 Truck(5 Axie)",
        "VC12" to "MC14 Truck(6 Axie","VC15" to "MC15 Truck(7 Axie & above)","VC16" to "MC16 Earth Moving Machinery",
        "VC16" to "MC17 Heavy Construction Machinery")
    val mapWithDuplicateKeys=mutableListOf<Pair<String,String>>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDocumentsDetailsBinding.inflate(layoutInflater)
        setContentView(binding.root)
        checkCameraPermission()
        initView()
        setupScanner()
        setupVehicleRegviewModelViewModel()
        mapWithDuplicateKeys.add( "VC4" to "MC4 Car/Jeep/Van")
        mapWithDuplicateKeys.add( "VC4" to "MC20 Tata Ace/Similar Light Commercial Vehicle")
        mapWithDuplicateKeys.add( "VC5" to "MC5 Light Commercial Vehicle")
        mapWithDuplicateKeys.add( "VC6" to "MC11 Truck(3 Axie)/Light Commercial Vehicle(3 Axie)")
        mapWithDuplicateKeys.add( "VC7" to "MC7 BUs(2 Axie)")
        mapWithDuplicateKeys.add( "VC7" to "MC10 Truck(2 Axie)")
        mapWithDuplicateKeys.add( "VC12" to "MC12 Truck (4 Axie)")
        mapWithDuplicateKeys.add( "VC12" to "MC12 Truck (5 Axie)")
        mapWithDuplicateKeys.add( "VC12" to "MC14 Truck(6 Axie")
        mapWithDuplicateKeys.add( "VC15" to "MC15 Truck(7 Axie & above)")
        mapWithDuplicateKeys.add("VC16" to "MC16 Earth Moving Machinery")
        mapWithDuplicateKeys.add("VC16" to "MC16 Earth Moving Machinery")
        mapWithDuplicateKeys.add("VC16" to "MC17 Heavy Construction Machinery")



        CallObserveuploadVehicleRegistration()
        callObserveuploadFiles()
        getTagLisDataObserver()
        closeTagObserver()
        replaceTagObserver()

    }
    private fun initView(){
        AppConstants.vehicleNumberVal=""
        binding.headerLayout.tvToolbarHederTitle.visibility= View.GONE
        binding.headerLayout.tvToolbarTitle.visibility= View.VISIBLE
        binding.headerLayout.ivToolBarBack.visibility= View.VISIBLE
        binding.headerLayout.tvToolbarHederTitle.text=getString(R.string.personal_details)
        documentsTypeArr.add("Pan")
        documentsTypeArr.add("VoterId")
        documentsTypeArr.add("Aadhar card")
        documentsTypeArr.add("Diving License")
        AppConstants.entityType=getString(R.string.TRUCK_CORPORATE)
        addOrReplaceArr.add(getString(R.string.Add_kit))
        addOrReplaceArr.add(getString(R.string.Replace_Kit))
        personalDetailsData =AppConstants.personalDetail!!
        binding.etSelectAddReplaceKitInput.setText(getString(R.string.Add_kit))
        binding.etCustomerNameInput.setText(personalDetailsData.firstName)
        binding.etSelectAddReplaceKitInput.setOnClickListener {
            if(personalDetailsData.isCustomerAlreadyRegister){
                openDialog(getString(R.string.select_add_replace_kit),addOrReplaceArr)
            }
        }

//        binding.etKitNumberInput.setText("652210-108-1034068")
//        binding.etVehicleRegistrationNumberInput.setText("TN19JY2023")
//        binding.scannedKtNumber.setText("652210-108-1034068")

        binding.getTagList.setOnClickListener {
            var registerVehicleNumber = binding.addReplaceRegistrationNumberInput.text.toString()
            addReplaceRegistrationNumber=registerVehicleNumber
            if (registerVehicleNumber != "") {
                if(AppConstants.isNetworkAvailable(this)) {
                    AppConstants.launchSunsetDialog(this)
                    var unlockData = GetTagListReqJson(registerVehicleNumber)
                    val jsonData = Gson().toJson(unlockData)
                    vehicleRegviewModel.getTagList(
                        AppConstants.tenant,
                        AppConstants.authorization,
                        jsonData
                    )
                }else{
                    AppConstants.showNoInternetConnectionMessageAlert(this)
                }
            }else{
                AppConstants.showMessageAlert(this,getString(R.string.please_select_vehicle_number))
            }
        }
        binding.addReplaceRegistrationNumberInput.setOnClickListener {
            showVehicleNumbersBottomSheet(personalDetailsData.vehicleData)
        }


    }
    fun profileIdReferenceImage(view:View){
        val factory = LayoutInflater.from(this)
        val view: View = factory.inflate(R.layout.proile_image_dialog, null)
        val dialog = Dialog(this)
        dialog.setContentView(view)
        dialog.show()
    }
    @SuppressLint("SuspiciousIndentation")
    fun next(view:View){

//         var filePart :MultipartBody.Part= MultipartBody.Part.createFormData("file",
//             file.getName(), RequestBody.create(
//             "image/*".toMediaTypeOrNull(), file))
        /*  startActivity(Intent(this, FastLinkActivity::class.java))
          overridePendingTransition(
              R.anim.slide_in_right,
              R.anim.slide_out_left
          )*/
        vehicleNumberVal = binding.etVehicleRegistrationNumberInput.text.toString().trim()
        AppConstants.vehicleNumberVal =vehicleNumberVal
        scanned_kit_number = binding.scannedKtNumber.text.toString().trim()
        kit_number = binding.etKitNumberInput.text.toString().trim()
        mProfileId = binding.etProfileIdInput.text.toString().trim().split(" ")[0]
        mTagId = binding.etTagIdInput.text.toString().trim()
        println("mProfileId::$mProfileId")

        SelectAddReplace=binding.etSelectAddReplaceKitInput.text.toString()
        if(SelectAddReplace==getString(R.string.Add_kit)) {
            if (kit_number == "") {
                Toast.makeText(this, "Please enter kit number", Toast.LENGTH_SHORT).show()
            }
            /*  else if (scanned_kit_number == "") {
                  Toast.makeText(this, "Please bar code number number", Toast.LENGTH_SHORT).show()

              } */
            else if(mTagId == ""){
                Toast.makeText(this, "Please enter tag id", Toast.LENGTH_SHORT).show()

            }
            else if(mProfileId == ""){
                Toast.makeText(this, "Please select profile Id", Toast.LENGTH_SHORT).show()

            }
           else if (vehicleNumberVal == "") {
                Toast.makeText(this, "Please enter vehicle number", Toast.LENGTH_SHORT).show()

            }/* else if (rcBackSideFile == null) {
                Toast.makeText(this, "Please select RC back side image", Toast.LENGTH_SHORT).show()

            } else if (customerFile == null) {
                Toast.makeText(this, "Please select customer image", Toast.LENGTH_SHORT).show()

            }*/

            else {
                if(mTagId=="VC4"){
                    AppConstants.amountByTagId=150
                }else{
                    AppConstants.amountByTagId=100

                }

                updateCustomerdetails(true,kit_number,vehicleNumberVal)


            }
        }else{
            oldKitNumber=binding.etOldKitNumberInput.text.toString().trim()
            newKitNumber=binding.etNewKitNumberInput.text.toString().trim()
            var replaceProfile_id=binding.etReplaceProfileIdInput.text.toString().trim()

            if(oldKitNumber==""){
                Toast.makeText(this, "Please enter old kit number", Toast.LENGTH_SHORT).show()

            }else if(newKitNumber==""){
                Toast.makeText(this, "Please enter new kit number", Toast.LENGTH_SHORT).show()

            }else if(oldKitNumber==newKitNumber){
                Toast.makeText(this, "Old and new kit numbers should not same", Toast.LENGTH_SHORT).show()

            }
            else if(replaceProfile_id == ""){
                Toast.makeText(this, "Please select profile Id", Toast.LENGTH_SHORT).show()

            }
            else{
                AppConstants.launchSunsetDialog(this)
                var unlockData = TagClosureReqJson(oldKitNumber,"06","add")
                val jsonData = Gson().toJson(unlockData)
                if(AppConstants.isNetworkAvailable(this)) {
                    vehicleRegviewModel.tagClosure(
                        AppConstants.tenant,
                        AppConstants.authorization,
                        jsonData
                    )
                    kitNumberUpdate = true
                }else{
                    AppConstants.showNoInternetConnectionMessageAlert(this)
                }
            }
        }
    }

    private fun callVehicleRegistration(){

        AppConstants.launchSunsetDialog(this)
        vehicleRegviewModel.vehicleRegistration(
            AppConstants.tenant,
            AppConstants.vehicleToken,
            getRequestJson()
        )
    }
    private fun callUploadFile(){
        AppConstants.launchSunsetDialog(this)

        val map: MutableMap<String, RequestBody> = mutableMapOf()

        val entityId = createPartFromString(vehicleNumberVal)
        val businessType = createPartFromString(AppConstants.businessType_vehicle)
        val entityType = createPartFromString(personalDetailsData.vehicleClass)
        map[getString(R.string.entityId)] = entityId
        map[getString(R.string.businessType)] = businessType
        map[getString(R.string.entityType)] = entityType
        vehicleRegviewModel.uploadKycDocuments(
            AppConstants.tenant,
            AppConstants.partnerId,
            AppConstants.vehicleToken,
            map,
            getUploadFileMultipartBody(rcBackSideFile!!,"addressProof"),
            getUploadFileMultipartBody(customerFile!!,"idProof"),
            getUploadFileMultipartBody(rcBackSideFile!!,"ackDocument")
        )
    }
    private fun updateCustomerdetails(isAdditionalVehicle:Boolean,kitNumber:String,vehicleNumber:String){
        val fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)
        /* var parentId=fasTagPref.USER_parentId!!
         var rollId=fasTagPref.USER_roleId!!*/
        var agentId=fasTagPref.USER_agentID!!

        var requestData= UpdateCustomerRequestJson(agentId,personalDetailsData.firstName,
            personalDetailsData.emailAddress,AppConstants.phoneNumber.substring(3),personalDetailsData.gender,personalDetailsData.pincode,personalDetailsData.state,AppConstants.country,""+AppConstants.entityType,AppConstants.entityId,
            "KYC123",personalDetailsData.dob,personalDetailsData.address1,personalDetailsData.documentType,personalDetailsData.documentNumber,
            "0000777",AppConstants.kycStatus,kitNumber,kitNumber,vehicleNumber,"0","0","Approved",personalDetailsData.customerId,isAdditionalVehicle)
        val loginRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(loginRequestJsonData)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
//        println("updateCustomerdetails:: "+jsonObject)
        if(AppConstants.isNetworkAvailable(this)) {
            AppConstants.launchSunsetDialog(this)
            vehicleRegviewModel.saveCustomerDetailsRequest(request)
        }else{
            AppConstants.showNoInternetConnectionMessageAlert(this)
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
    fun CallObserveuploadVehicleRegistration(){
        vehicleRegviewModel.createCustomerRequestData().observe(this){
            AppConstants.cancelSunsetDialog()
            when(it.status){
                Status.SUCCESS ->{
//                    showResponseMessageAlert(this,it.data!!.message)
                    if(it.data?.code==0){
//                        saveCustomerDetails = "success"
                        Toast.makeText(this,it.data!!.message,Toast.LENGTH_SHORT).show()
                        if (!kitNumberUpdate){
                            if(AppConstants.isNetworkAvailable(this)) {
                                callVehicleRegistration()
                            }else{
                                AppConstants.showNoInternetConnectionMessageAlert(this)
                            }
                        }else {
                            val i = Intent(this, AgentHomeActivity::class.java)
                            i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            startActivity(i)
                            AppConstants.slideToLeftAnim(this)
                        }
                        kitNumberUpdate=false
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
                    Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                }
            }
        }
        vehicleRegviewModel.vehicleRegistrationData().observe(this) {
            AppConstants.cancelSunsetDialog()
            if(it.data!!.exception==null) {
                Toast.makeText(this, "vehicle registration Success.", Toast.LENGTH_SHORT).show()
                if(AppConstants.isNetworkAvailable(this)) {
                    callUploadFile()
                }else{
                    AppConstants.showNoInternetConnectionMessageAlert(this)
                }
            }else{
                Toast.makeText(this, it.data!!.exception.detailMessage, Toast.LENGTH_SHORT).show()

            }

        }

        vehicleRegviewModel.getTagsBySerailNumberRequestData().observe(this){
            AppConstants.cancelSunsetDialog()
            when(it.status){
                Status.SUCCESS ->{

                    if(it.data?.message=="Success"){
//                        AppConstants.showMessageAlert(this,it.data.reponseData?.kitNumber)
                        var profileID=it.data.reponseData?.profileId!!
                        val valuesMatchingKEY1 = mapWithDuplicateKeys.filter { it.first== profileID }.map{it.second}


                        unlockKit(it.data.reponseData?.kitNumber!!)
                        newKitNumberForReplace=it.data.reponseData?.kitNumber!!
//                        binding.etProfileIdInput.setText(profileID)

                        binding.etTagIdInput.setText(it.data.reponseData?.tagId!!)
                        binding.etProfileIdInput.setOnClickListener {
                            println("profilerIdsArry:: $valuesMatchingKEY1")

                            if(valuesMatchingKEY1.isNotEmpty()) {
                                openDialog(
                                    getString(R.string.Select_Profile_Id),
                                    valuesMatchingKEY1 as ArrayList<String>
                                )
                            }
                        }
                        binding.etReplaceProfileIdInput.setOnClickListener {
                            println("profilerIdsArry:: $valuesMatchingKEY1")

                            if(valuesMatchingKEY1.isNotEmpty()) {
                                openDialog(
                                    getString(R.string.Select_Profile_Id),
                                    valuesMatchingKEY1 as ArrayList<String>
                                )
                            }
                        }

                        binding.etReplaceTagIdInput.setText(it.data.reponseData?.tagId!!)

                    }else {
                    AppConstants.showMessageAlert(this,it.data?.message)
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
    fun getTagLisDataObserver() {
//                binding.scannedKtNumber.setText("34161FA82073E764D9E85321")
        vehicleRegviewModel.tagListData().observe(this) {
            AppConstants.cancelSunsetDialog()
            if(it.data!!.exception==null) {
                if(it.data!!.result.cardList.size>0) {
//                        Toast.makeText(this, "Success.", Toast.LENGTH_SHORT).show()

                    /* AppConstants.personalDetail= PersonalDetailsData(pincodeVal,
                 AppConstants.country,stateVal,cityVal,addressLine1Val,addressLine2Val,addressLine1Val,
                 AppConstants.phoneNumber,lastNameVal,firstNameVal)*/
                    binding.etOldKitNumber.visibility = View.VISIBLE
                    binding.llNewKitDetails.visibility = View.VISIBLE
                    binding.btnNext.visibility = View.VISIBLE

                    val adapter = ArrayAdapter<String>(
                        this,
                        R.layout.spinner_item,
                        it.data!!.result.cardList
                    )

                    // Give the suggestion after 1 words.

                    // Give the suggestion after 1 words.
                    binding.etOldKitNumberInput.threshold = 1

                    // Set the adapter for data as a list

                    // Set the adapter for data as a list
                    binding.etOldKitNumberInput.setAdapter(adapter)
                    binding.etOldKitNumberInput.setTextColor(Color.BLACK)

                }else{
                    Toast.makeText(this, "No tag details available.", Toast.LENGTH_SHORT).show()

                }
            }else{
                Toast.makeText(this, it.data!!.exception.exception.detailMessage, Toast.LENGTH_SHORT).show()
                binding.etOldKitNumber.visibility=View.GONE
                binding.llNewKitDetails.visibility=View.GONE
                binding.btnNext.visibility = View.GONE

            }

        }
        vehicleRegviewModel.errorMessage.observe(this) {
            AppConstants.cancelSunsetDialog()
            Toast.makeText(this, "Please try again..", Toast.LENGTH_SHORT).show()
            vehicleRegviewModel.loading.observe(this, Observer {
                if (!it) {
                    AppConstants.cancelSunsetDialog()

                    /* pd?.dismiss()
         pd=null*/
                }
            })
        }

    }
    fun closeTagObserver() {
//                binding.scannedKtNumber.setText("34161FA82073E764D9E85321")


        vehicleRegviewModel.tagClosure().observe(this) {
            AppConstants.cancelSunsetDialog()
            if(it.data!!.exception==null) {
                Toast.makeText(this, "Tag Closure Success.", Toast.LENGTH_SHORT).show()
                if(AppConstants.isNetworkAvailable(this)) {
                    AppConstants.launchSunsetDialog(this)
                    var replaceProfile_id=binding.etReplaceProfileIdInput.text.toString().split(" ")[0]
                    var unlockData = ReplaceTagReqJson(
                        addReplaceRegistrationNumber,
                        oldKitNumber,
                        newKitNumberForReplace,
                        replaceProfile_id
                    )
                    val jsonData = Gson().toJson(unlockData)
                    vehicleRegviewModel.tagReplace(
                        AppConstants.tenant,
                        AppConstants.authorization,
                        jsonData
                    )
                }else{
                    AppConstants.showNoInternetConnectionMessageAlert(this)
                }
            }else{
                /* AppConstants.launchSunsetDialog(this)
                 var unlockData = ReplaceTagReqJson(addReplaceRegistrationNumber,oldKitNumber,newKitNumber,"VC4")
                 val jsonData = Gson().toJson(unlockData)
                 vehicleRegviewModel.tagReplace(AppConstants.tenant,AppConstants.authorization,jsonData)*/
                Toast.makeText(this, "Tag close failed", Toast.LENGTH_SHORT).show()



            }
        }
        vehicleRegviewModel.errorMessage.observe(this) {
            AppConstants.cancelSunsetDialog()
            Toast.makeText(this, "Tag close failed.Please try again..", Toast.LENGTH_SHORT).show()
            vehicleRegviewModel.loading.observe(this, Observer {
                if (!it) {
                    AppConstants.cancelSunsetDialog()
                }
            })



        }
    }
    fun replaceTagObserver() {
//                binding.scannedKtNumber.setText("34161FA82073E764D9E85321")

        vehicleRegviewModel.kitResultData().observe(this) {

            println("kitResultData:: "+it)

            if(it.data!!.exception==null) {
                Toast.makeText(this, "Kit number unlocked success.", Toast.LENGTH_SHORT).show()
                /* AppConstants.personalDetail= PersonalDetailsData(pincodeVal,
                 AppConstants.country,stateVal,cityVal,addressLine1Val,addressLine2Val,addressLine1Val,
                 AppConstants.phoneNumber,lastNameVal,firstNameVal)*/
            }else{
                Toast.makeText(this, it.data!!.exception.detailMessage, Toast.LENGTH_SHORT).show()
            }
        }
        vehicleRegviewModel.errorMessage.observe(this) {

            AppConstants.cancelSunsetDialog()
            Toast.makeText(this, "Kit number unlocked not success. Please try again..", Toast.LENGTH_SHORT).show()
            vehicleRegviewModel.loading.observe(this, Observer {
                if (!it) {
                    AppConstants.cancelSunsetDialog()

                    /* pd?.dismiss()
         pd=null*/
                }
            })

        }
        vehicleRegviewModel.replaceTag().observe(this) {
            AppConstants.cancelSunsetDialog()
            if(it.data!!.exception==null) {
                Toast.makeText(this, " Replace Tag Success.", Toast.LENGTH_SHORT).show()

                updateCustomerdetails(false,newKitNumber,addReplaceRegistrationNumber)
//
                /* AppConstants.personalDetail= PersonalDetailsData(pincodeVal,
             AppConstants.country,stateVal,cityVal,addressLine1Val,addressLine2Val,addressLine1Val,
             AppConstants.phoneNumber,lastNameVal,firstNameVal)*/
            }else{
                Toast.makeText(this, "Replace Tag  "+it.data!!.exception.exception.detailMessage, Toast.LENGTH_SHORT).show()

            }

        }
        vehicleRegviewModel.errorMessage.observe(this) { it ->

            AppConstants.cancelSunsetDialog()
            Toast.makeText(this, "Replace Tag Failed. Please try again..", Toast.LENGTH_SHORT).show()
            vehicleRegviewModel.loading.observe(this, Observer {
                if (!it) {
                    AppConstants.cancelSunsetDialog()
                }
            })



        }
    }
    fun unlockKit(kitNumber:String) {
//                binding.scannedKtNumber.setText("34161FA82073E764D9E85321")

        var kitNo =kitNumber
        println("kitNumber:: "+kitNumber)
        if (kitNo != "") {
            var unlockData = UnlockKitReqJson(kitNo, "01", "REMOVE")
            val jsonData = Gson().toJson(unlockData)

            if(AppConstants.isNetworkAvailable(this)) {
                vehicleRegviewModel.unLockKit(AppConstants.tenant, jsonData)
            }else{
                AppConstants.showNoInternetConnectionMessageAlert(this)
            }

        }
    }
    fun callObserveuploadFiles(){


        vehicleRegviewModel.fileUploadKycnData().observe(this) {
            AppConstants.cancelSunsetDialog()
            if(it.data?.result!!.status  == true) {
                Toast.makeText(this, "File Uploaded Success.", Toast.LENGTH_SHORT).show()
                /* AppConstants.personalDetail= PersonalDetailsData(pincodeVal,
                 AppConstants.country,stateVal,cityVal,addressLine1Val,addressLine2Val,addressLine1Val,
                 AppConstants.phoneNumber,lastNameVal,firstNameVal)*/
                /* val i = Intent(this, AgentHomeActivity::class.java)
                 i.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                 startActivity(i)
                 AppConstants.slideToLeftAnim(this)*/
                var intent=  Intent(this, PhonePayPaymentGatewayActivity::class.java)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
//            intent.putExtra(getString(R.string.login_from),getString(R.string.agent))
                startActivity(intent)
                AppConstants.slideToRightAnim(this)

            }else if(it.data?.exception!=null){
                Toast.makeText(this, it.data.exception.detailMessage, Toast.LENGTH_SHORT).show()

            }

        }

    }


    public fun rcfrontside(view:View){
        imageFrom=1
        chooseImagesFrom()
    }
    public fun rcbackside(view:View){
        imageFrom=2
        chooseImagesFrom()
    }
    public fun docfrontSide(view:View){
        imageFrom=3
        chooseImagesFrom()
    }
    public fun docbackSide(view:View){
        imageFrom=4
        chooseImagesFrom()
    }
    public fun back(view:View){
        onBackPressed()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(
            R.anim.slide_in_left,
            R.anim.slide_out_right
        )
    }

    private fun chooseImagesFrom(){
        var chhoseImageFromBinding=ChhoseImageFromBinding.inflate(layoutInflater)
        var  mBottomDialogNotificationAction = BottomSheetDialog(this)
        mBottomDialogNotificationAction.setContentView(chhoseImageFromBinding.root)
        mBottomDialogNotificationAction.setCancelable(false)
        mBottomDialogNotificationAction.show()
        // Remove default white color background
        val bottomSheet = mBottomDialogNotificationAction.findViewById(com.google.android.material.R.id.design_bottom_sheet) as FrameLayout?
        bottomSheet?.background = null
        /*  val shareAlertBuilder: AlertDialog.Builder = AlertDialog.Builder(activity)
          shareAlertBuilder.setView(chhoseImageFromBinding.root)
          val alertDialog: AlertDialog = shareAlertBuilder.create()*/
        chhoseImageFromBinding.llCamera.setOnClickListener {
            mBottomDialogNotificationAction.dismiss()
            openCamera()
        }
        chhoseImageFromBinding.llgallery.setOnClickListener {
            mBottomDialogNotificationAction.dismiss()
            openGalleryForImages()
        }
        chhoseImageFromBinding.llClose.setOnClickListener {
            mBottomDialogNotificationAction.dismiss()
        }
    }
    private fun checkCameraPermission() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                arrayOf(Manifest.permission.CAMERA),
                REQUEST_PERMISSION)
        }
    }
    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        cameraIntent.putExtra("android.intent.extras.CAMERA_FACING", 1)
        startActivityForResult(cameraIntent, REQUEST_IMAGE_CAPTURE)
    }
    private fun openGalleryForImages() {

        if (Build.VERSION.SDK_INT < 19) {
            var intent = Intent()
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(
                Intent.createChooser(intent, "Choose Pictures")
                , REQUEST_PICK_IMAGE
            )
        }
        else { // For latest versions API LEVEL 19+
            var intent = Intent(Intent.ACTION_OPEN_DOCUMENT)
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_PICK_IMAGE);
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        // In fragment class callback
        if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_IMAGE_CAPTURE) {
            val bitmap = data?.extras?.get("data") as Bitmap
            var file:File=convertBitmapToFile(bitmap)
            setImage(bitmap,file)

        }
        else if (resultCode == Activity.RESULT_OK && requestCode == REQUEST_PICK_IMAGE){
            // if multiple images are selected
            if (data?.data != null) {
                // if single image is selected
                var imageUri: Uri = data.data!!
                var filePath1:File=File(imageUri.path)
//                 var filePath=FileUtils.getPath(this,imageUri)
                var filePath = AppConstants.createTmpFileFromUri(this,imageUri,filePath1.name)
                val bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, imageUri)
                if (filePath != null) {
                    setImage(rotateImage(bitmap,0f),filePath)
                }
            }
        }else{
            val result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data)

            if ( result==null || result.contents == null) {
                Toast.makeText(this, getString(R.string.result_not_found), Toast.LENGTH_LONG).show()
            } else {
                // If QRCode contains data.
                try {
                    binding.scannedKtNumber.setText(result.contents)
                    binding.etKitNumberInput.setText(result.contents)
                    Toast.makeText(this,result.contents,Toast.LENGTH_SHORT).show()
                } catch (e: JSONException) {
                    e.printStackTrace()

                    // Data not in the expected format. So, whole object as toast message.
                    Toast.makeText(this, result.contents, Toast.LENGTH_LONG).show()
                }
            }
        }

    }
    fun convertBitmapToFile( bitmap: Bitmap): File{
        val wrapper = ContextWrapper(this)
        var file = wrapper.getDir("Images", Context.MODE_PRIVATE)
        file = File(file,"${UUID.randomUUID()}.jpg")
        val stream = FileOutputStream(file)
        bitmap.compress(Bitmap.CompressFormat.JPEG,25,stream)
        stream.flush()
        stream.close()
        return file

        return file
    }
    private fun rotateImage(bitmap: Bitmap, degrees: Float): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degrees)
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }

    private fun flipImage(bitmap: Bitmap, horizontal: Boolean, vertical: Boolean): Bitmap {
        val matrix = Matrix()
        matrix.preScale((if (horizontal) -1 else 1).toFloat(), (if (vertical) -1 else 1).toFloat())
        return Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
    }
    private fun setImage(bitmap:Bitmap,file:File){
        when (imageFrom){
            1->{binding.rcFrontSide.setImageBitmap(bitmap)
            }
            2->{binding.rcBackSide.setImageBitmap(bitmap)
                rcBackSideFile=file}
//            3->binding.idDocFrontSide.setImageBitmap(bitmap)
            4-> {
                binding.customerIcon.setImageBitmap(bitmap)
                customerFile = file
            }
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

            SelectAddReplace=""


            if(dropFrom == getString(R.string.select_document_type)){
//                binding.etSelectDocumentTypeInput.setText(listArr[position])
            }
            else if(dropFrom == getString(R.string.Select_Profile_Id)){
                binding.etProfileIdInput.setText(listArr[position])
                binding.etReplaceProfileIdInput.setText(listArr[position])
            }
            else{
                binding.etSelectAddReplaceKitInput.setText(listArr[position])
                if(listArr[position]==getString(R.string.Add_kit)){
                    binding.addKitContentLayout.visibility=View.VISIBLE
                    binding.replaceKitContentLayout.visibility=View.GONE
                    binding.btnNext.visibility = View.VISIBLE
                    binding.llscanbar.visibility=View.VISIBLE

                    binding.etProfileIdInput.setText("")
                    binding.etReplaceProfileIdInput.setText("")
                    binding.etReplaceTagIdInput.setText("")


                    binding.etKitNumberInput.setText("")
                    binding.etReplaceTagIdInput.setText("")
                    binding.etTagIdInput.setText("")
                    binding.etReplaceProfileIdInput.setText("")

                }else{
                    binding.addKitContentLayout.visibility=View.GONE
                    binding.replaceKitContentLayout.visibility=View.VISIBLE
                    binding.btnNext.visibility = View.GONE
                    binding.llscanbar.visibility=View.GONE

                    binding.etProfileIdInput.setText("")
                    binding.etReplaceProfileIdInput.setText("")
                    binding.etReplaceTagIdInput.setText("")

                    binding.etKitNumberInput.setText("")
                    binding.etReplaceTagIdInput.setText("")
                    binding.etTagIdInput.setText("")
                    binding.etReplaceProfileIdInput.setText("")
                }

            }
            alertDialog.dismiss()
        }
        alertDialog.show()
    }
    fun getUploadFileMultipartBody(file:File?,name:String):MultipartBody.Part{
        val requestFile: RequestBody = RequestBody.create(
            "image/jpg".toMediaType(),
            "file"
        )
        var multipartImage =
            MultipartBody.Part.createFormData(name, file?.name, requestFile);
        return multipartImage
    }
    fun createPartFromString(stringData: String): RequestBody {
        return stringData.toRequestBody("text/plain".toMediaTypeOrNull())
    }
    fun getRequestJson(): String {
        val kycDocuments = KycDocumentsVehicle("","")
        val documents = Documents("2042-10-12",AppConstants.RC_NUMBER)
        val fleetAddField = FleetAddFieldVehicle(vehicleNumberVal,"","",
            AppConstants.customerType,"",true,true,"","",
            "","2022-10-02",false)
        val vehicleRegReqJsonData= VehicleRegReqJsonData(
            idExpiry = "2040-05-05",
            pincode = personalDetailsData.pincode,
            country = personalDetailsData.country,
            state = personalDetailsData.state,
            city = personalDetailsData.city,
            address2 = personalDetailsData.address1,
            address = personalDetailsData.address2,
            emailAddress = personalDetailsData.emailAddress,
            contactNo= personalDetailsData.contactNo,
            lastName = personalDetailsData.lastName,
            firstName = personalDetailsData.firstName,
            specialDate = "2022-10-02",
            dependant = true,
            kycDocuments = arrayListOf( kycDocuments),
            gender = personalDetailsData.gender,
            programType = AppConstants.businessType_vehicle,
            kycStatus= AppConstants.kycStatus,
            business = AppConstants.businessType_vehicle,
            parentEntityId = AppConstants.phoneNumber.substring(3),
            fleetAddField=fleetAddField,
            businessType = AppConstants.businessType_vehicle,
            businessId = vehicleNumberVal,
            documents=arrayListOf(documents),
            countryofIssue = "IND",
            idType = AppConstants.RC_NUMBER,
//            entityType = AppConstants.entityType,
            entityType = "TRUCK_CORPORATE",

            color = AppConstants.color,
            kitNo = kit_number,
            profileId = mProfileId,
            tagId = mTagId,
            entityId = vehicleNumberVal)

        val jsonData = Gson().toJson(vehicleRegReqJsonData)
        return jsonData
    }
    private fun setupScanner() {
        qrScanIntegrator = IntentIntegrator(this)
        qrScanIntegrator?.setOrientationLocked(true)

    }
    public fun submit(view:View){
        startActivity(Intent(this, SuccessActivity::class.java))

        overridePendingTransition(
            R.anim.slide_in_right,
            R.anim.slide_out_left
        );
    }

    public fun scanBarCode(view:View){
        performAction()
    }
    public fun getTagBySerialNumber(view:View){
//        binding.etKitNumberInput.setText("652210-108-1034162")
         binding.etProfileIdInput.setText("")
         binding.etTagIdInput.setText("")
        var serialNumber= binding.etKitNumberInput.text.toString()
        getTagsBySerialNumberRequest(serialNumber)
    }
    public fun getTagBySerialNumberForReplace(view:View){
//        binding.etKitNumberInput.setText("652210-108-1034162")
        var serialNumber= binding.etNewKitNumberInput.text.toString()
        getTagsBySerialNumberRequest(serialNumber)
    /*    AppConstants.launchSunsetDialog(this)
        if(serialNumber==null || serialNumber==""){
            Toast.makeText(this,"Please enter serial number",Toast.LENGTH_SHORT).show()
        }else {
            val serialNumberReq = GetTagListReqBySerialNumberJson(serialNumber)
            val jsonData = Gson().toJson(serialNumberReq)

            vehicleRegviewModel.getTagsBySerialNumberRequest(jsonData)
        }*/
    }
    private fun getTagsBySerialNumberRequest(serialNumber:String){
        AppConstants.launchSunsetDialog(this)
//        var serialNumber= binding.etKitNumberInput.text.toString()
        if(serialNumber==null || serialNumber==""){
            Toast.makeText(this,"Please enter serial number",Toast.LENGTH_SHORT).show()
        }else {
            val serialNumberReq = GetTagListReqBySerialNumberJson(serialNumber)
            val jsonData = Gson().toJson(serialNumberReq)

            vehicleRegviewModel.getTagsBySerialNumberRequest(jsonData)
        }
    }
    private fun performAction() {
        // Code to perform action when button is clicked.
        qrScanIntegrator?.initiateScan()
    }

    private fun showVehicleNumbersBottomSheet(vehicleLeadsResponseData: ArrayList<VehicleData>?){
        val dialog = BottomSheetDialog(this)
        println("showTeamLeadsBottomSheet ::"+vehicleLeadsResponseData)
        var BottomSheetBinding= LayoutTlBottomSheetBinding.inflate(layoutInflater)
//        val view = layoutInflater.inflate(R.layout.layout_tl_bottom_sheet, null)
        val view = BottomSheetBinding.root

//        val btnClose = view.findViewById<ImageView>(R.id.idIVCourse)
        val teamLeadsListAdapter = VehicleNumbersAdapter(vehicleLeadsResponseData!!) {
            println("TeamLeadsListAdapter:: Click$it")
            binding.addReplaceRegistrationNumberInput.setText(it.vehicleNumber)
//            selectedTeamLeadData=it
            dialog.dismiss()
        }

//        val recyclerView: RecyclerView = view.findViewById(R.id.rc_team_list)
        BottomSheetBinding.rcTeamList.layoutManager = LinearLayoutManager(this)
        BottomSheetBinding.bottomSheetHeader.text = getString(R.string.select_vehicle_registration_number)

        BottomSheetBinding.rcTeamList.adapter = teamLeadsListAdapter
        BottomSheetBinding.idIVCourse.setOnClickListener {

            dialog.dismiss()
        }
        dialog.setCancelable(false)
        dialog.setContentView(view)
        dialog.show()
    }

}
