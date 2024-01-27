package com.agent.fasttag.view.phonepay.model


data class PaymentInitiatedModel(
    val code: String,
    val data: Data,
    val message: String,
    val success: Boolean
)