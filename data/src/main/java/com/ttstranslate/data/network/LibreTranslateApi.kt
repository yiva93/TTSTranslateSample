package com.ttstranslate.data.network

import com.ttstranslate.data.network.request.TranslateSentenceRequest
import com.ttstranslate.data.network.response.TranslateSentenceResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface LibreTranslateApi {
    @POST("/translate")
    suspend fun translate(@Body request: TranslateSentenceRequest): TranslateSentenceResponse
}