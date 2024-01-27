package com.agent.fasttag.view.model

data class PersonalDetailsResponseData(

    val result:PersonalDetailsResultData,
    val exception:PersonalDetailsException
)
data class PersonalDetailsResultData(
    val success : Boolean=true,
    val entityId : String="",
    val kitNo : String=""
)

data class PersonalDetailsException(
    val detailMessage : String="",
    val shortMessage : String="",
    val errorCode : String=""

)
