package com.agent.fasttag.view.model

data class KitResultData (

    val result:List<KitResult>,
    var exception:KitResultDataExceptionData
)
data class KitResult(
    val status : Boolean=true,
    val tagId : String="",
    val code : String="",
    val description : String=""

)
data class KitResultDataExceptionData(
    val detailMessage:String=""
)
