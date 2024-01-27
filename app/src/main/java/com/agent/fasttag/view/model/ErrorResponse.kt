package com.agent.fasttag.view.model

data class ErrorResponse (
    val result:String="",
    val  exception:ExceptionData
        )

data class ExceptionData(
    val detailMessage:String=""
)