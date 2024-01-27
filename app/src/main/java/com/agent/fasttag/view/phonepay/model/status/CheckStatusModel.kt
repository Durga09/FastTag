package com.agent.fasttag.view.phonepay.model.status

data class CheckStatusModel(
    val code: String,
    val data: Data,
    val message: String,
    val success: Boolean
)