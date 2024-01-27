package com.agent.fasttag.view.model

data class TagListResponseData (
    val result:CardList,
    val exception: ErrorResponse
        )

data class CardList(
    val cardList:ArrayList<String>,
    val kitList:ArrayList<String>,
)