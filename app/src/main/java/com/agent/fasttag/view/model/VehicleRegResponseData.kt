package com.agent.fasttag.view.model

data class VehicleRegResponseData (
    val result : VehicleResultData,
    val exception : VehicleResultExceptionData

)
data class VehicleResultData(
    val kitNo:String = "",
    val sorCustomerId:String = "",
    val kycStatus:String = "",
    val kycExpiryDate:String = "",
    val entityId:String = "",
    val kycRefNo:String = "",
    val status:String = "",

)
data class VehicleResultExceptionData(
    val detailMessage:String = ""
)