package com.agent.fasttag.view.phonepay.model


data class Data(
    val instrumentResponse: InstrumentResponse,
    val merchantId: String,
    val merchantTransactionId: String
)