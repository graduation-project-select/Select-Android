package com.konkuk.select.network

import com.konkuk.select.model.DefaultResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.*

interface ServiceApi {

    // img upload test
    @Multipart
    @POST("removebg")
    fun saveProfilePicture(
        @Part("X-Api-Key") key: String,
        @Part file: MultipartBody.Part
    ): Call<DefaultResponse>

}