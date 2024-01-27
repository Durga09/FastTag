package com.agent.fasttag.view.phonepay.model

import com.google.gson.annotations.SerializedName

data class PaymentRequest(
    @SerializedName("request")
    val requestJson: String
)
