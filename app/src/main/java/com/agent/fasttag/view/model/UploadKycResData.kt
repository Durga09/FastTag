package com.agent.fasttag.view.model

data class UploadKycResData(

val result : UploadKycResultData?=null,
val exception : UploadKycException
)
data class UploadKycResultData(
    val status: Boolean?
)
data class UploadKycException(
    val detailMessage:String = ""
)

