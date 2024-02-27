package com.agent.fasttag.view.viewmodel

import android.annotation.SuppressLint
import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.agent.fasttag.view.model.*
import com.agent.fasttag.view.util.Resource
import com.google.gson.GsonBuilder
import kotlinx.coroutines.*
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject


class FastTagViewModel(private val fasTagRepository: FasTagRepository): ViewModel() {

    val generateOtpData = MutableLiveData<Resource<OTPResponseData>>()
    val customerRegistrationData = MutableLiveData<Resource<PersonalDetailsResponseData>>()
    val vehicleRegistrationData = MutableLiveData<Resource<VehicleRegResponseData>>()
    val fileUploadKycnData = MutableLiveData<Resource<UploadKycResData>>()
    val kitResultData = MutableLiveData<Resource<KitResultData>>()
    val tagListData = MutableLiveData<Resource<TagListResponseData>>()
    val tagClosure = MutableLiveData<Resource<TagClosureResponseData>>()
    val replaceTag = MutableLiveData<Resource<TagReplaceResponseData>>()
    val loginRequestData = MutableLiveData<Resource<LoginResponse>>()
    val forgotPasswordRequestData = MutableLiveData<Resource<LoginResponse>>()
    val createAgentRequestData = MutableLiveData<Resource<SaveOrEditAgentResponse>>()
    val rollsReuestData = MutableLiveData<Resource<RollsData>>()
    val teamLeadsListRequestData = MutableLiveData<Resource<TeamLeadsResponseData>>()
    val createCustomerRequestData = MutableLiveData<Resource<CustomerCreateResponseData>>()
    val getCustomerRequestByMobileData = MutableLiveData<Resource<CustomerCreateResponseData>>()
    val resetPasswordRequestData = MutableLiveData<Resource<LoginResponse>>()
    val deleteCustomerRequestData = MutableLiveData<Resource<CustomerDeleteResponseData>>()
    val getOtpRequestData = MutableLiveData<Resource<SendOTPResponse>>()
    val checkStatusModelData = MutableLiveData<Resource<PaymentBase64Response>>()
    val checkStatusOfPaymentPhonePe = MutableLiveData<Resource<PaymentBase64Response>>()
    val loadWalletRequestData = MutableLiveData<Resource<PaymentLoadWalletResponse>>()
    val getTransactionStatusRequestData = MutableLiveData<Resource<GetTransactionStatusResData>>()
    val getGetTransactionsResDataByAgent = MutableLiveData<Resource<GetTransactionsResDataByAgent>>()

    val getTransactionIdRequestData = MutableLiveData<Resource<GetTransactionResponseData>>()

    var job: Job?=null
    val errorMessage = MutableLiveData<String>()
    val loading = MutableLiveData<Boolean>()

    val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        onError("Exception handled: ${throwable.localizedMessage}")
    }
    fun generateOtpData(): MutableLiveData<Resource<OTPResponseData>> {
        return generateOtpData
    }
    fun customerRegistrationData(): MutableLiveData<Resource<PersonalDetailsResponseData>> {
        return customerRegistrationData
    }
    fun fileUploadKycnData(): MutableLiveData<Resource<UploadKycResData>> {
        return fileUploadKycnData
    }
    fun vehicleRegistrationData(): MutableLiveData<Resource<VehicleRegResponseData>> {
        return vehicleRegistrationData
    }
    fun tagListData(): MutableLiveData<Resource<TagListResponseData>> {
        return tagListData
    }
    fun tagClosure(): MutableLiveData<Resource<TagClosureResponseData>> {
        return tagClosure
    }
    fun replaceTag(): MutableLiveData<Resource<TagReplaceResponseData>> {
        return replaceTag
    }
    fun loginRequest(): MutableLiveData<Resource<LoginResponse>> {
        return loginRequestData
    }
    fun forgotPasswordRequest(): MutableLiveData<Resource<LoginResponse>> {
        return forgotPasswordRequestData
    }
    fun createAgentRequest(): MutableLiveData<Resource<SaveOrEditAgentResponse>> {
        return createAgentRequestData
    }
    fun getAllRosllsRequest(): MutableLiveData<Resource<RollsData>> {
        return rollsReuestData
    }
    fun getAllTeamLeadsRequest(): MutableLiveData<Resource<TeamLeadsResponseData>> {
        return teamLeadsListRequestData
    }
    fun createCustomerRequestData(): MutableLiveData<Resource<CustomerCreateResponseData>> {
        return createCustomerRequestData
    }
    fun getCustomerRequestDataByMobile(): MutableLiveData<Resource<CustomerCreateResponseData>> {
        return getCustomerRequestByMobileData
    }
    fun resetPassword(): MutableLiveData<Resource<LoginResponse>> {
        return resetPasswordRequestData
    }
    fun deleteCustomerRequestData(): MutableLiveData<Resource<CustomerDeleteResponseData>> {
        return deleteCustomerRequestData
    }
    fun getOtpRequestData(): MutableLiveData<Resource<SendOTPResponse>> {
        return getOtpRequestData
    }
    fun checkStatusModelData(): MutableLiveData<Resource<PaymentBase64Response>> {
        return checkStatusModelData
    }
    fun checkStatusOfPaymentPhonePe(): MutableLiveData<Resource<PaymentBase64Response>> {
        return checkStatusOfPaymentPhonePe
    }
    fun loadWalletRequestData(): MutableLiveData<Resource<PaymentLoadWalletResponse>> {
        return loadWalletRequestData
    }
    fun getTransactionIdData(): MutableLiveData<Resource<GetTransactionResponseData>> {
        return getTransactionIdRequestData
    }
    fun getTransactionStatusRequestData(): MutableLiveData<Resource<GetTransactionStatusResData>> {
        return getTransactionStatusRequestData
    }
    fun getTransactionsResDataByAgentRequestData(): MutableLiveData<Resource<GetTransactionsResDataByAgent>> {
        return getGetTransactionsResDataByAgent
    }




    fun getGenerateOtp(tenant:String,partnerId:String,partnerToken:String,jsonObj: String){

        println("OBJ:: "+jsonObj)
        val jsonObject = JSONObject(jsonObj)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.generateOtp(tenant,partnerId,partnerToken,request)
            withContext(Dispatchers.Main){
                response.message()

                println("response code:: ${response.code()}")

                if(response.code()==200){

                    println("response:: $response")

                    generateOtpData.postValue(Resource.success(response.body()))
                    loading.value=false
               }else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = ResultData("false",locerr.exception.detailMessage)
                    val oTPResponseData = OTPResponseData(resultData)
                    generateOtpData.postValue(Resource.success(oTPResponseData))
                }else{
                    onError("Error : ${response.message()} ")

                }
            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun customerRegistration(tenant:String, partnerId:String, partnerToken:String, jsonObj: String){

        println("OBJ:: "+jsonObj)
        val jsonObject = JSONObject(jsonObj)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.customerRegistration(tenant,partnerId,partnerToken,request)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: $response")

                    customerRegistrationData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = PersonalDetailsException(detailMessage = locerr.exception.detailMessage, locerr.exception.detailMessage)
                  var personalDetailsResponseData=  PersonalDetailsResponseData(exception = resultData, result = PersonalDetailsResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    customerRegistrationData.postValue(Resource.success(personalDetailsResponseData))
                }else{
                    onError("Error : ${response.message()} ")

                }

            }
        }
    }

    @SuppressLint("SuspiciousIndentation")
    fun paymentLoadWaletRequest(tenant:String, jsonObj: RequestBody){

        println("paymentLoadWaletRequest:: "+jsonObj)
//        val jsonObject = JSONObject(jsonObj)
//        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.loadWalletRequest(tenant,jsonObj)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: $response")

                    loadWalletRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = PaymentLoadWalletResponse(null, LoadWalletExceptionData(locerr.exception.detailMessage))
//                    var personalDetailsResponseData=  PersonalDetailsResponseData(exception = resultData, result = PersonalDetailsResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    loadWalletRequestData.postValue(Resource.success(resultData))
                }else{
                    onError("Error : ${response.message()} ")

                }

            }
        }
    }
    fun uploadKycDocuments(tenant:String,   partnerId:String, partnerToken:String, partMap: MutableMap<String, RequestBody>,
                           addressProof: MultipartBody.Part, dpProof: MultipartBody.Part, ackDocument: MultipartBody.Part){

        println("uploadKycDocuments:: $partMap addressProof:: $addressProof  dpProof:: $dpProof  partnerId $partnerId ackDocument:: $ackDocument")
//        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.fileUpload(tenant,partnerId,partnerToken,partMap,addressProof,dpProof,ackDocument)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("uploadKycDocuments success:: $response")

                    fileUploadKycnData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun agentLogin(loginData: RequestBody){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.loginAgent(loginData)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    loginRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun agentForgotPassword(loginData: RequestBody){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.agentForgotPassword(loginData)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    forgotPasswordRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun getOTP(otpData: RequestBody){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.getOTP(otpData)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    getOtpRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun agentResetPassword(loginData: RequestBody){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.agentResetPassword(loginData)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    resetPasswordRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    resetPasswordRequestData.postValue(Resource.success(LoginResponse(1,"Please try again",null,)))
                }
            }
        }
    }
    fun createTlOrAgent(loginData: RequestBody){
        print("response createTlOrAgent:: ${loginData}")

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.saveOrEditAgent(loginData)
            println("createTlOrAgent responseCode:: ${response.code()}")

            withContext(Dispatchers.Main){
                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    createAgentRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun getAllRolls(){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.getAllRolls()
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    rollsReuestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun getAllTeamLeadsReuest(){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.getAllTeamLeads()
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    teamLeadsListRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun getAgentsByRequest(customerData: RequestBody){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.getAgentsById(customerData)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    teamLeadsListRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun getTransactionID(customerData: RequestBody){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.getTransaction(customerData)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    getTransactionIdRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==401 || response.code()==400){

                    getTransactionIdRequestData.postValue(Resource.success(GetTransactionResponseData(false,"Something went wrong. Please try again..",null)))

                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
                      getTransactionIdRequestData.postValue(Resource.success(GetTransactionResponseData(false,locerr.exception.detailMessage,null)))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun getTransactionStatus(customerData: RequestBody){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.getTransactionStatus(customerData)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    getTransactionStatusRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==401 || response.code()==400){

                    getTransactionStatusRequestData.postValue(Resource.success(
                        GetTransactionStatusResData(false,"Something went wrong. Please try again..",null)
                    ))

                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
                    getTransactionStatusRequestData.postValue(Resource.success(GetTransactionStatusResData(false,locerr.exception.detailMessage,null)))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun getTransactionDetailsByAgent(customerData: RequestBody){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.getTransactionDetailsByAgent(customerData)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    getGetTransactionsResDataByAgent.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==401 || response.code()==400){

//                    getGetTransactionsResDataByAgent.postValue(Resource.success(
//                        GetTransactionsResDataByAgent(false,"Something went wrong. Please try again..",null)
//                    ))

                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    getGetTransactionsResDataByAgent.postValue(Resource.success(GetTransactionStatusResData(false,locerr.exception.detailMessage,null)))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun saveCustomerDetailsRequest(customerData: RequestBody){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.saveCustomerDetails(customerData)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    createCustomerRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun paymentGatewayrequest(headers:Map<String, String>,body: RequestBody){
        Log.d("phonepe", "checkStatusFromPhonePay : $headers")

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.paymentGatewayrequest(headers,body)
            withContext(Dispatchers.Main){
//                print("checkStatusFromPhonePay header:: ${response.raw().request.headers}")
                Log.d("phonepe", "checkStatusFromPhonePay123 : ${response.raw().request}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    checkStatusModelData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun paymentCheckStatusFromPhonePe(headers:Map<String, String>,merchantId:String,merchantTransactionId:String){
        Log.d("phonepe", "checkStatusFromPhonePay : $headers")

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.paymentCheckStatusFromPhonePeRequest(headers,merchantId,merchantTransactionId)
            withContext(Dispatchers.Main){
//                print("checkStatusFromPhonePay header:: ${response.raw().request.headers}")
                Log.d("phonepe", "checkStatusFromPhonePay123 : ${response.raw().request}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    checkStatusOfPaymentPhonePe.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
                    checkStatusOfPaymentPhonePe.postValue(Resource.success(response.body()))

//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
//                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
//                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
////                    val oTPResponseData = PersonalDetailsException(response.body())
//                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    checkStatusOfPaymentPhonePe.postValue(Resource.success(response.body()))
                }
            }
        }
    }
    fun deleteCustomerDetailsRequest(customerData: RequestBody){
        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.deleteCustomerDetails(customerData)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    deleteCustomerRequestData.postValue(Resource.success(response.body()))
                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun getCustomerDetailsByMobileRequest(customerData: RequestBody){
        println("getCustomerDetailsByMobileRequest1234:: ${customerData}")

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.getCustomerDetailsBuMobile(customerData)
            withContext(Dispatchers.Main){
                if(response.code()==200){
                    println("response 200 success:: ${response.body()}")

                    getCustomerRequestByMobileData.postValue(Resource.success(response.body()))

                    loading.value=false
                }
                else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = UploadKycException(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  UploadKycResData(exception = resultData, result = UploadKycResultData(false))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    fileUploadKycnData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.message()} ")
                }
            }
        }
    }
    fun unLockKit(tenant:String,jsonObj: String){

        println("OBJ:: "+jsonObj)
        val jsonObject = JSONObject(jsonObj)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.unLockKit(tenant,request)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: $response")

                    kitResultData.postValue(Resource.success((response.body())))
                    loading.value=false
                }  else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = KitResultDataExceptionData(detailMessage = locerr.exception.detailMessage)
                    var list=ArrayList<KitResult>()
                    list.add(KitResult(false))
                    var personalDetailsResponseData=  KitResultData(exception = resultData, result = list)
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    kitResultData.postValue(Resource.success(personalDetailsResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.errorBody()} ")

                }
            }
        }
    }
    fun vehicleRegistration(tenant:String,partnerToken:String,jsonObj: String){

        val jsonObject = JSONObject(jsonObj)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.vehicleRegistration(tenant,partnerToken,request)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: $response")

                    vehicleRegistrationData.postValue(Resource.success(response.body()))
                    loading.value=false
                } else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val resultData = VehicleResultExceptionData(detailMessage = locerr.exception.detailMessage)
                    var personalDetailsResponseData=  VehicleRegResponseData(exception = resultData, result = VehicleResultData("",""))
                    vehicleRegistrationData.postValue(Resource.success(personalDetailsResponseData))
                }else{
                    onError("Error : ${response.message()} ")

                }
            }
        }
    }
    fun getTagList(tenant:String,authorization:String,jsonObj: String){

        println("OBJ:: "+jsonObj)
        val jsonObject = JSONObject(jsonObj)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.getTagList(tenant,authorization,request)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: $response")

                    tagListData.postValue(Resource.success((response.body())))
                    loading.value=false
                }  else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val exceptionData = ErrorResponse(exception = ExceptionData(locerr.exception.detailMessage))
                    var list=ArrayList<KitResult>()
                    list.add(KitResult(false))
                    var tagListResponseData=  TagListResponseData(exception = exceptionData, result = CardList(
                        ArrayList(), ArrayList()
                    ))
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    tagListData.postValue(Resource.success(tagListResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.errorBody()} ")

                }
            }
        }
    }
    fun tagClosure(tenant:String,authorization:String,jsonObj: String){

        println(" tagClosure OBJ:: "+jsonObj)
        val jsonObject = JSONObject(jsonObj)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.tagClosure(tenant,authorization,request)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: $response")

                    tagClosure.postValue(Resource.success((response.body())))
                    loading.value=false
                }  else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    var errorarr = ArrayList<TagColureResultData>()

                    val exceptionData = ErrorResponse(exception = ExceptionData(locerr.exception.detailMessage))
                    var tagListResponseData=  TagClosureResponseData(exception = exceptionData, result =errorarr)
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    tagClosure.postValue(Resource.success(tagListResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.errorBody()} ")

                }
            }
        }
    }
    fun tagReplace(tenant:String,authorization:String,jsonObj: String){

        println(" tagReplace OBJ:: "+jsonObj)
        val jsonObject = JSONObject(jsonObj)
        val request = jsonObject.toString().toRequestBody("application/json".toMediaTypeOrNull());

        CoroutineScope(Dispatchers.IO+exceptionHandler).launch {
            val response = fasTagRepository.replaceTag(tenant,authorization,request)
            withContext(Dispatchers.Main){
                print("response header:: ${response.raw().request.headers}")

                if(response.code()==200){
                    println("response 200 success:: $response")

                    replaceTag.postValue(Resource.success((response.body())))
                    loading.value=false
                }  else if(response.code()==500){
                    val gson = GsonBuilder()
                        .setLenient()
                        .create()

                    var locerr = gson.fromJson(response?.errorBody()?.string(),
                        ErrorResponse::class.java)
                    println("errorResponse:: ${locerr.exception}")
//                    onError("Error errorBody: ${locerr.exception.detailMessage} ")
                    val exceptionData = ErrorResponse(exception = ExceptionData(locerr.exception.detailMessage))
                    var tagListResponseData=  TagReplaceResponseData(exception = exceptionData, result = "")
//                    val oTPResponseData = PersonalDetailsException(response.body())
                    replaceTag.postValue(Resource.success(tagListResponseData))
                }
                else{
                    println("response:: $response")
                    onError("Error : ${response.errorBody()} ")

                }
            }
        }
    }
    private fun onError(message: String) {
        errorMessage.postValue( message)
        loading.postValue( false)
    }

    override fun onCleared() {
        super.onCleared()
        job?.cancel()
    }
}