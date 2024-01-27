package com.agent.fasttag.view.phonepay

import com.agent.fasttag.view.phonepay.model.status.CheckStatusModel
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.HeaderMap
import retrofit2.http.Path

interface ApiInterface {


    @GET("apis/pg-sandbox/pg/v1/status/{merchantId}/{transactionId}")
     fun checkStatus(
        @Path("merchantId") merchantId: String,
        @Path("transactionId") transactionId: String,
        @HeaderMap headers: Map<String, String>,

        ): Response<CheckStatusModel>
}