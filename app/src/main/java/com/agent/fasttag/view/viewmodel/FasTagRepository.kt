package com.agent.fasttag.view.viewmodel

import com.agent.fasttag.encript.Encryption
import com.agent.fasttag.view.api.RetrofitService
import com.agent.fasttag.view.util.AppConstants
import com.google.gson.JsonObject
import okhttp3.MultipartBody
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.http.Part
import retrofit2.http.PartMap

class FasTagRepository constructor(private val retrofitService: RetrofitService) {
    suspend fun generateOtp(tenant:String,partnerId:String,partnerToken:String,jsonObj:RequestBody)=
        retrofitService.generateOtp(AppConstants.LoginBaseUrl+AppConstants.GetEncryptedData,tenant,partnerId,AppConstants.baseURL+"kyc/customer/generate/otp",partnerToken,jsonObj)

    suspend fun customerRegistration(tenant:String,partnerId:String,partnerToken:String,jsonObj:RequestBody)=
        retrofitService.generateOtp(AppConstants.LoginBaseUrl+AppConstants.GetEncryptedData,tenant,partnerId,AppConstants.baseURL+"kyc/v2/register",partnerToken,jsonObj)

    suspend fun vehicleRegistration(tenant:String,partnerToken:String,jsonObj:RequestBody)=
        retrofitService.vehicleRegistrationEncript(AppConstants.LoginBaseUrl+AppConstants.GetEncryptedData,tenant,partnerToken,AppConstants.baseURL+"Yappay/registration-manager/v3/register",jsonObj)

    suspend fun getTagList(tenant:String,authorization:String,jsonObj:RequestBody)=
        retrofitService.getTagInfoEncript(AppConstants.LoginBaseUrl+AppConstants.GetEncryptedData,tenant,AppConstants.baseURL+AppConstants.getTagList,authorization,jsonObj)

    suspend fun tagClosure(tenant:String,authorization:String,jsonObj:RequestBody)=
        retrofitService.getTagInfoEncript(AppConstants.LoginBaseUrl+AppConstants.GetEncryptedData,tenant,AppConstants.baseURL+AppConstants.tagClosure,authorization,jsonObj)


    suspend fun unLockKit(tenant:String,jsonObj:RequestBody)=
        retrofitService.unLockKit(AppConstants.LoginBaseUrl+AppConstants.GetEncryptedData,tenant,AppConstants.baseURL+AppConstants.UnregisteredNegativeList,jsonObj)

    suspend fun fileUpload(tenant:String,partnerId:String, partnerToken:String,  partMap: MutableMap<String,RequestBody>,
                            addressProof: MultipartBody.Part, dpProof: MultipartBody.Part, ackDocument: MultipartBody.Part)=
        retrofitService.uploadKycResponse(AppConstants.baseURL_NON_ENCRIPT+AppConstants.uploadKyc,tenant,partnerId,partnerToken,partMap,addressProof,dpProof,ackDocument)


    suspend fun replaceTag(tenant:String,authorization:String,jsonObj:RequestBody)=
        retrofitService.getTagInfoEncript(AppConstants.LoginBaseUrl+AppConstants.GetEncryptedData,tenant,AppConstants.baseURL+AppConstants.replaceTag,authorization,jsonObj)

    suspend fun loadWalletRequest(tenant:String,jsonObj:RequestBody)=
        retrofitService.paymentWallet(AppConstants.LoginBaseUrl+AppConstants.GetEncryptedData,tenant,AppConstants.baseURL+AppConstants.paymentLoadWalletBaseUrl,jsonObj)

//    suspend fun unLockKit(tenant:String,jsonObj:RequestBody)=
//        retrofitService.unLockKit(AppConstants.SSLTestBaseUrl+AppConstants.UnregisteredNegativeList,tenant,jsonObj)

    /*suspend fun getTagList(tenant:String,authorization:String,jsonObj:RequestBody)=
        retrofitService.getTagList(AppConstants.SSLTestBaseUrl+AppConstants.getTagList,tenant,authorization,jsonObj)
*/
//    suspend fun getTagList(tenant:String,authorization:String,jsonObj:RequestBody)=
//        retrofitService.getTagListEncript(AppConstants.LoginBaseUrl+AppConstants.GetEncryptedData,tenant,authorization,AppConstants.SSLTestBaseUrl+AppConstants.getTagList,jsonObj)

//    suspend fun tagClosure(tenant:String,authorization:String,jsonObj:RequestBody)=
//        retrofitService.tagClosure(AppConstants.SSLTestBaseUrl+AppConstants.tagClosure,tenant,authorization,jsonObj)
//
//    suspend fun replaceTag(tenant:String,authorization:String,jsonObj:RequestBody)=
//        retrofitService.replaceTag(AppConstants.SSLTestBaseUrl+AppConstants.replaceTag,tenant,authorization,jsonObj)

    suspend fun loginAgent(jsonObj:RequestBody)=
        retrofitService.loginAgent(AppConstants.LoginBaseUrl+AppConstants.AgentLogin,AppConstants.LoginAuthorization,jsonObj)

    suspend fun agentForgotPassword(jsonObj:RequestBody)=
        retrofitService.loginAgent(AppConstants.LoginBaseUrl+AppConstants.AgentforgotPassword,AppConstants.LoginAuthorization,jsonObj)

    suspend fun agentResetPassword(jsonObj:RequestBody)=
        retrofitService.agentResetPassword(AppConstants.LoginBaseUrl+AppConstants.AgentResetPassword,AppConstants.LoginAuthorization,jsonObj)

    suspend fun saveOrEditAgent(jsonObj:RequestBody)=
        retrofitService.saveOrEditAgent(AppConstants.LoginBaseUrl+AppConstants.SaveOrEditAgent,AppConstants.LoginAuthorization,jsonObj)

    suspend fun getAllRolls()=
        retrofitService.getAllRolls(AppConstants.LoginBaseUrl+AppConstants.GetAllRolls,AppConstants.LoginAuthorization)

    suspend fun getAllTeamLeads()=
        retrofitService.getAllTeamLeads(AppConstants.LoginBaseUrl+AppConstants.GetAllTeamLeads,AppConstants.LoginAuthorization)

    suspend fun saveCustomerDetails(jsonObj:RequestBody)=
        retrofitService.saveCustomerDetails(AppConstants.LoginBaseUrl+AppConstants.saveCustomeDetails,AppConstants.LoginAuthorization,jsonObj)

    suspend fun deleteCustomerDetails(jsonObj:RequestBody)=
        retrofitService.deleteCustomerDetails(AppConstants.LoginBaseUrl+AppConstants.deleteCustomeDetails,AppConstants.LoginAuthorization,jsonObj)

    suspend fun getCustomerDetailsBuMobile(jsonObj:RequestBody)=
        retrofitService.getCustomerDetailsByMobile(AppConstants.LoginBaseUrl+AppConstants.getCustomerByMb,AppConstants.LoginAuthorization,jsonObj)

    suspend fun getAgentsById(jsonObj:RequestBody)=
        retrofitService.getAgentsById(AppConstants.LoginBaseUrl+AppConstants.getAgentsByID,AppConstants.LoginAuthorization,jsonObj)

    suspend fun getTransaction(jsonObj:RequestBody)=
        retrofitService.getTransactionId(AppConstants.LoginBaseUrl+AppConstants.GetTransactionId,AppConstants.LoginAuthorization,jsonObj)

    suspend fun getTransactionStatus(jsonObj:RequestBody)=
        retrofitService.getTransactionStatus(AppConstants.LoginBaseUrl+AppConstants.GetTransactionStatus,AppConstants.LoginAuthorization,jsonObj)

    suspend fun getTransactionDetailsByAgent(jsonObj:RequestBody)=
        retrofitService.getTransactionDetailsByAgent(AppConstants.LoginBaseUrl+AppConstants.GetTransactionByAgent,AppConstants.LoginAuthorization,jsonObj)

    suspend fun getOTP(jsonObj:RequestBody)=
        retrofitService.getOtp(AppConstants.LoginBaseUrl+AppConstants.getOtp,jsonObj)

    suspend fun paymentGatewayrequest(headers:Map<String, String>,jsonObj:RequestBody)=
        retrofitService.paymentGatewayrequest(AppConstants.paymentGatewayBaseUrl,headers,jsonObj)

    suspend fun paymentLoadWalletPeRequest(headers:Map<String, String>,merchantId:String,merchantTransactionId:String)=
        retrofitService.checkStatusOfPayment(merchantId,merchantTransactionId,headers)

    suspend fun paymentCheckStatusFromPhonePeRequest(headers:Map<String, String>,merchantId:String,merchantTransactionId:String)=
        retrofitService.checkStatusOfPayment(merchantId,merchantTransactionId,headers)

//    suspend fun loadWalletRequest(tenant:String,jsonObj:RequestBody)=
//        retrofitService.paymentWallet(AppConstants.SSLTestBaseUrl+AppConstants.paymentLoadWalletBaseUrl,tenant,jsonObj)

    suspend fun uploadTagsRequest(jsonObj:RequestBody)=
        retrofitService.uploadTags(AppConstants.LoginBaseUrl+AppConstants.uploadTags,jsonObj,AppConstants.LoginAuthorization)

    suspend fun getTagBySerialNumber(jsonObj:RequestBody)=
        retrofitService.getTagBySerialNumber(AppConstants.LoginBaseUrl+AppConstants.getTagBySerailNo,jsonObj,AppConstants.LoginAuthorization)
}