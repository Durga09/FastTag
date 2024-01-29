package com.agent.fasttag.view

//import androidx.lifecycle.lifecycleScope
//import com.papayacoders.phonepe.ApiUtilities
//import com.phonepe.intent.sdk.api.models.PhonePeEnvironment

import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.agent.fasttag.R
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.model.*
import com.agent.fasttag.view.util.AppConstants
import com.agent.fasttag.view.util.FasTagSharedPreference
import com.agent.fasttag.view.util.FasTagSharedPreference.USER_agentID
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


lateinit var vehicleRegviewModel: FastTagViewModel

class PhonePayPaymentGatewayActivity: AppCompatActivity() {
    var apiEndPoint = "/pg/v1/pay"
    val salt = "5bb51303-f908-43be-b6ed-515c12fb63b6" // salt key
    val MERCHANT_ID = "PGTESTPAYUAT97"  // Merhcant id
//    val MERCHANT_ID = "PGTESTPAYUAT"  // Merhcant id
    val MERCHANT_TID = "MT7850590068188104"
    var paymentType="PAY_PAGE"
    private val B2B_PG_REQUEST_CODE = 777
    private var rollID=AppConstants.agentRollId
    private var loginFrom=""
    private lateinit var fasTagPref: SharedPreferences
    var parentId=""
    var loginUserPhoneNumber=""
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_phone_pay_payment_gateway)

        PhonePe.init(this,PhonePeEnvironment.SANDBOX,MERCHANT_ID,"")
        loginFrom = AppConstants.loginFrom

        var string_signature = PhonePe.getPackageSignature()
         fasTagPref = FasTagSharedPreference.customPreference(this, FasTagSharedPreference.CUSTOM_PREF_NAME)
         parentId=fasTagPref.USER_parentId!!
        loginUserPhoneNumber=fasTagPref.USER_phoneNumber!!
        var roleId=fasTagPref.USER_roleId
        if(roleId=="1"){
            parentId = fasTagPref.USER_agentID!!
        }

        var requestData= PhonePayRequest(MERCHANT_ID,MERCHANT_TID,
            "MUID123",1000,
            "https://webhook.site/ac624d79-08ee-4882-867f-e03f46dbbb04",loginUserPhoneNumber,
                    PaymentInstrument(paymentType))

        val agentRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(agentRequestJsonData)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
        println("GetAgentsByIdRequestJson:: "+jsonObject)

        val payloadBase64 = android.util.Base64.encodeToString(
            jsonObject.toString().toByteArray(Charset.defaultCharset()), android.util.Base64.DEFAULT
        )
        val checksum = sha256(payloadBase64 + apiEndPoint + salt) + "###1";

        val b2BPGRequest = B2BPGRequestBuilder()
            .setData(payloadBase64)
            .setChecksum(checksum)
            .setUrl(apiEndPoint)
            .build()

        val button = findViewById<Button>(R.id.pay_button)
        button.setOnClickListener {
            getTransactionId()
            /*try {
                PhonePe.getImplicitIntent( this, b2BPGRequest, "")
                    ?.let { it1 -> startActivityForResult(it1,B2B_PG_REQUEST_CODE) };
            } catch( e:PhonePeInitException){

            }*/
        }
        setupVehicleRegviewModelViewModel()
        CallObserveuploadVehicleRegistration()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        Log.d("PAPAYACODERS", "onActivityResult: ${Gson().toJson(data)}")
        if (requestCode == B2B_PG_REQUEST_CODE ) {
            checkStatus()
        }
    }
    fun getTransactionId(){
        AppConstants.launchSunsetDialog(this)
        var requestData= GetTransactionIdRequestJson(fasTagPref.USER_parentId!!,fasTagPref.USER_agentID!!,
            fasTagPref.USER_phoneNumber!!,fasTagPref.USER_phoneNumber!!,"20143","Initiated","200")

        val agentRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(agentRequestJsonData)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());
        println("GetAgentsByIdRequestJson:: "+jsonObject)
        vehicleRegviewModel.getTransactionID(request)
    }
    fun getTransactionStatus(trasactionId:String){
        AppConstants.launchSunsetDialog(this)
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
                    AppConstants.launchSunsetDialog(this)
                     var requestBody=generateLoadWalletRequestJson()
                    vehicleRegviewModel.paymentLoadWaletRequest(AppConstants.tenant_M2P,requestBody)
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
        vehicleRegviewModel.loadWalletRequestData().observe(this) {
            AppConstants.cancelSunsetDialog()
            println("loadWalletRequestData:: $it")
            when (it.status) {
                Status.SUCCESS -> {
                    if(it.data?.result!=null) {
                        AppConstants.showMessageAlert(this, it.data?.result?.txId)
                    }else{
                        AppConstants.showMessageAlert(this, it.data?.exception?.detailMessage)

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
        vehicleRegviewModel.getTransactionIdData().observe(this) {
            AppConstants.cancelSunsetDialog()
            println("loadWalletRequestData:: $it")
            when (it.status) {
                Status.SUCCESS -> {
                    if(it.data?.status==true) {
//                        AppConstants.showMessageAlert(this, it.data?.reponseData?.transactionId)
                        getTransactionStatus(it.data?.reponseData?.transactionId!!)

                    }else{
                        AppConstants.showMessageAlert(this, it.data?.message)

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
        vehicleRegviewModel.getTransactionStatusRequestData().observe(this) {
            AppConstants.cancelSunsetDialog()
            println("loadWalletRequestData:: $it")
            when (it.status) {
                Status.SUCCESS -> {
                    if(it.data?.status==true) {
                        AppConstants.showMessageAlert(this, it.data?.message)
                    }else{
                        AppConstants.showMessageAlert(this, it.data?.message)

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
            "transferfunds","200","M2C","LQFLEET","LQFLEET",
            "MOBILE",""+externalTransactionId,"1234")
        val agentRequestJsonData = Gson().toJson(requestData)
        val jsonObject = JSONObject(agentRequestJsonData)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

       return request
    }
}