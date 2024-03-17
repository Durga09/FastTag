package com.agent.fasttag.view.api

import com.agent.fasttag.view.model.*
import com.agent.fasttag.view.phonepay.model.status.CheckStatusModel
import com.agent.fasttag.view.util.AppConstants
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*


interface RetrofitService {
    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST("customer/generate/otp")
    suspend fun generateOtp(@Header("TENANT")  TENANT:String,@Header("partnerId")  partnerId:String,
                            @Header("partnerToken")  partnerToken:String, @Body jsonObject: RequestBody): Response<OTPResponseData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST("v2/register")
    suspend fun customerRegistration(@Header("TENANT")  TENANT:String,@Header("partnerId")  partnerId:String,
                            @Header("partnerToken")  partnerToken:String, @Body jsonObject: RequestBody): Response<PersonalDetailsResponseData>
    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST
    suspend fun vehicleRegistration(@Url url:String,@Header("TENANT")  TENANT:String,
                                     @Header("Authorization")  partnerToken:String, @Body jsonObject: RequestBody): Response<VehicleRegResponseData>
    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST
    suspend fun paymentWallet(@Url url:String,@Header("TENANT")  TENANT:String,@Body jsonObject: RequestBody): Response<PaymentLoadWalletResponse>


    @Multipart
    @POST
    suspend fun uploadKycResponse(@Url  url:String,
        @Header("TENANT")  TENANT:String,@Header("partnerId")  partnerId:String,
        @Header("partnerToken")  partnerToken:String,
        @PartMap() partMap: MutableMap<String,RequestBody>,
        @Part addressProof: MultipartBody.Part, @Part idProof: MultipartBody.Part, @Part ackDocument: MultipartBody.Part
    ):Response<UploadKycResData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun unLockKit(@Url url:String,@Header("TENANT")  TENANT:String,@Body jsonObject: RequestBody): Response<KitResultData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun getTagList(@Url url:String,@Header("TENANT")  TENANT:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<TagListResponseData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun tagClosure(@Url url:String,@Header("TENANT")  TENANT:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<TagClosureResponseData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun replaceTag(@Url url:String,@Header("TENANT")  TENANT:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<TagReplaceResponseData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun loginAgent(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<LoginResponse>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun agentFrogotPassword(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<LoginResponse>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun agentResetPassword(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<LoginResponse>


    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun saveOrEditAgent(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<SaveOrEditAgentResponse>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @GET()
    suspend fun getAllRolls(@Url url:String,@Header("Authorization")  authorization:String): Response<RollsData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @GET()
    suspend fun getAllTeamLeads(@Url url:String,@Header("Authorization")  authorization:String): Response<TeamLeadsResponseData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun getAgentsById(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<TeamLeadsResponseData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun getTransactionId(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<GetTransactionResponseData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun getTransactionStatus(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<GetTransactionStatusResData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun getTransactionDetailsByAgent(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<GetTransactionsResDataByAgent>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun saveCustomerDetails(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<CustomerCreateResponseData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun deleteCustomerDetails(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<CustomerDeleteResponseData>


    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun getCustomerDetailsByMobile(@Url url:String,@Header("Authorization")  authorization:String,@Body jsonObject: RequestBody): Response<CustomerCreateResponseData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun getOtp(@Url url:String,@Body jsonObject: RequestBody): Response<SendOTPResponse>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun paymentGatewayrequest(
        @Url url:String,
        @HeaderMap headers: Map<String, String>,
        @Body jsonObject: RequestBody
        ): Response<PaymentBase64Response>


    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun uploadTags(@Url url:String,@Body jsonObject: RequestBody,@Header("Authorization")  authorization:String): Response<UploadTagsResponseData>


    @Headers( "Content-Type: application/json; charset=utf-8")
    @POST()
    suspend fun getTagBySerialNumber(@Url url:String,@Body jsonObject: RequestBody,@Header("Authorization")  authorization:String): Response<GetTagsBySerialNoResponseData>

    @Headers( "Content-Type: application/json; charset=utf-8")
    @GET("https://api-preprod.phonepe.com/apis/pg-sandbox/pg/v1/status/{merchantId}/{merchantTransactionId}")
    suspend fun checkStatusOfPayment(
        @Path("merchantId") merchantId:String,
        @Path("merchantTransactionId") merchantTransactionId:String,
        @HeaderMap headers: Map<String, String>,
    ): Response<PaymentBase64Response>


    companion object {
        var retrofitService: RetrofitService? = null
        var retrofit:Retrofit? =null
        fun getInstance(url: String): RetrofitService {
            if (retrofitService == null) {
                 retrofit = Retrofit.Builder()
                    .baseUrl(url)
                    .addConverterFactory(GsonConverterFactory.create())
                    .client(BaseRetrofit.okhttpClient())
                    .build()

                retrofitService = retrofit?.create(RetrofitService::class.java)


//            AppConstants.retrofit=retrofit
            }
            return retrofitService!!
        }
    }

}