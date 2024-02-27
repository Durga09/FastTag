package com.agent.fasttag.view.model

data class LoginResponse(

    val code:Int?,
    val message:String?,
    val reponseData:ReponseData?,
    )

data class SendOTPResponse(
    val code:Int?,
    val message:String?,
    val reponseData:GetOTPResponse?,
)
data class GetOTPResponse(
    val apiId:String?,
    val success:String?,
    val message:String?,
    val otp:String?,
    val messageUUID:String?,

    )
data class SaveOrEditAgentResponse(
    val code:Int?,
    val message:String?,
    val reponseData:ReponseData?,
)
data class PaymentBase64Response(
    val success:Boolean,
    val message:String?,
    val data:PaymentBase64ResponseData?,
)
data class GetTransactionResponseData(
    val status:Boolean,
    val message:String?,
    val reponseData:TransactionResponseData?,
)
data class TransactionResponseData(
    val transactionId:String,
    val callBackUrl:String?,
)
data class PaymentLoadWalletResponse(
    val result:LoadWalletResultData?,
    val exception:LoadWalletExceptionData?,
)
data class GetTransactionStatusResData(
    val status:Boolean,
    val message:String?,
    val reponseData:GetTransactionIdResData?,
)
data class GetTransactionsResDataByAgent(
    val status:Boolean,
    val message:String?,
    val reponseData:List<GetTransactionByIdRes>,
)
data class LoadWalletResultData(
    val txId:String,

)
data class LoadWalletExceptionData(
    val detailMessage:String,

    )
data class PaymentBase64ResponseData(
    val merchantId:String,
    val merchantTransactionId:String?,
    val instrumentResponse:InstrumentResponse?,
    val paymentInstrument:PaymentInstrumentByPhonepe?,
)
data class PaymentInstrumentByPhonepe(
    val type:String,
    val pgTransactionId:String?,
    val pgServiceTransactionId:String?,
//    val bankTransactionId:String?,
    val bankId:String?,
    val arn:String?

)
data class InstrumentResponse(
    val type:String,
//    val intentUrl:String?,
val redirectInfo:RedirectInfo
)
data class RedirectInfo(
    val url:String,

    )
data class ReponseData(

    val agentId:String,
    val parentId:String,
    val userName:String,
    val password:String="",
    val email:String,
    val phoneNumber:String,
    val firstName:String,
    val lastName:String,
    val roleId:String,
    val roleName:String,
    )
data class RollsData(
    val code:Int?,
    val message:String?,
    val reponseData:ArrayList<RollSResponseData>,

)
data class RollSResponseData(

    val roleId:String,
    val roleName:String,
)
data class TeamLeadsResponseData(

    val code:Int?,
    val message:String?,
    val reponseData:ArrayList<ReponseData>,
)
data class CustomerCreateResponseData(

    val code:Int?,
    val message:String?,
    val reponseData:CustomerCreateInternalResponseData,
)
data class CustomerDeleteResponseData(

    val code:Int?,
    val message:String?,
    val reponseData:String,
)
data class CustomerCreateInternalResponseData(
    val customerId:String?,
    val agentId:String?,
    val name:String?,
    val email:String?,
    val phoneNumber:String?,
    val entityId:String?,
    val kycReferenceNumber:String?,
    val password:String?,
    val dateOfBirth:String?,
    val address:String?,
    val documentType:String?,
    val documentNumber:String?,
    val kycToken:String?,
    val kycStatus:String?,
    val entityType:String?,
    val gender:String?,
    val pincode:String?,
    val state:String?,
    val country:String?,
    val vehicleData:ArrayList<VehicleData>,
    val fastTagData:ArrayList<FastTagData?>,

    )
 data class VehicleData(
     val vehicleId:String?,
     val vehicleNumber:String?,

     )
data class FastTagData(
    val fastTagId:String?,
    val kitNo:String?,
    val kitId:String?,
    val status:String?,

    )