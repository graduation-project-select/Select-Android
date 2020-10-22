package com.konkuk.select.network

import com.konkuk.select.model.ClothesProp
import com.konkuk.select.model.DefaultResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ServiceApi {

    @GET("/")
    fun connectionTest(
    ):Call<DefaultResponse>

    @Multipart
    @POST("/predict")
    fun predictClothesProp(
        @Part("filename") filename: String,
        @Part image:MultipartBody.Part
    ):Call<ClothesProp>

}