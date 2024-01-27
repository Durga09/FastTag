package com.agent.fasttag.view.model

data class TagClosureResponseData(

    val result:ArrayList<TagColureResultData>,
    val exception:ErrorResponse
)
data class TagColureResultData(
    val status:String="",
    val tagId:String="",
    val code:String="",
    val description:String=""

)
