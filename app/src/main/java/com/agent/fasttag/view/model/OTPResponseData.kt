package com.agent.fasttag.view.model

data class OTPResponseData(

    val result:ResultData
)
data class ResultData(
    val success : String="",
    val entityId : String
)
