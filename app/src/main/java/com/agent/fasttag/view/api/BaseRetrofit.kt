package com.agent.fasttag.view.api

import android.util.Base64
import com.agent.fasttag.BuildConfig
import com.google.gson.Gson
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import okhttp3.Response
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.converter.scalars.ScalarsConverterFactory
object BaseRetrofit {
    public const val CONNECTION_TIMEOUT = 60
    public val isDebugging: Boolean = BuildConfig.DEBUG

    public fun okhttpClient(): OkHttpClient {
        val httpClient = OkHttpClient.Builder()
        httpClient.readTimeout(60, TimeUnit.SECONDS);
        httpClient.connectTimeout(
            CONNECTION_TIMEOUT.toLong(),
            TimeUnit.SECONDS
        )
        val interceptor = HttpLoggingInterceptor()
        interceptor.level = if (isDebugging) HttpLoggingInterceptor.Level.BODY else HttpLoggingInterceptor.Level.NONE

//        httpClient.addInterceptor(AuthInterceptor())
        httpClient.addNetworkInterceptor(interceptor)
        return httpClient.build()
    }
/*
    class AuthInterceptor : Interceptor {
        override fun intercept(chain: Interceptor.Chain): okhttp3.Response {
            val requestBuilder = chain.request().newBuilder()
            if (AppController.controller.my_Place_Details != null) {
                val token =
                    "${AppController.controller?.my_Place_Details?.username?.trim()}:${AppController.controller?.my_Place_Details?.password?.trim()}"
                val message: ByteArray = token.toByteArray()
                val encoded = Base64.encodeToString(message, Base64.NO_WRAP)
                val contractNumber = AppController.controller?.my_Place_Details?.jobNumber ?: ""
                requestBuilder.addHeader("Authorization", "Basic $encoded")
                requestBuilder.addHeader("ContractNumber", contractNumber)
            }
            return chain.proceed(requestBuilder.build())
        }
    }*/

}