package com.papayacoders.phonepesdk

import com.agent.fasttag.view.phonepay.model.Data

data class ApiResponse(
    val code: String,
    val `data`: Data,
    val message: String,
    val success: Boolean
)