package com.demo.newedgedemo.data.remote

import com.demo.newedgedemo.data.remote.dto.GrammarCheckResponseDto
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface GrammarApiService {
    @FormUrlEncoded
    @POST("check")
    suspend fun checkGrammar(
        @Field("text") text: String,
        @Field("language") language: String = "en-US"
    ): GrammarCheckResponseDto

    companion object {
        const val BASE_URL = "https://api.languagetool.org/v2/"
    }
}
