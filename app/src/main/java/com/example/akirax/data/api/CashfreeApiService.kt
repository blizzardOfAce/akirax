package com.example.akirax.data.api

import com.example.akirax.data.ApiKeys.cashfreeClientId
import com.example.akirax.data.ApiKeys.cashfreeClientSecret
import com.example.akirax.domain.model.Data
import com.example.akirax.domain.model.PaymentModel
import com.example.akirax.domain.model.PaymentStatusModel
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Path

class CashfreeHeaderInterceptor : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request().newBuilder()
            .addHeader("x-client-id", cashfreeClientId)
            .addHeader("x-client-secret", cashfreeClientSecret)
            .addHeader("x-api-version", "2022-09-01")
            .addHeader("Accept", "application/json")
            .build()
        return chain.proceed(request)
    }
}

interface CashfreeApiService {
    @POST("/pg/orders")
    fun getOrderID(@Body data: Data): Call<PaymentModel>

    @GET("/pg/orders/{order_id}")
    fun create(@Path("order_id") orderId: String): Call<PaymentStatusModel>
}

