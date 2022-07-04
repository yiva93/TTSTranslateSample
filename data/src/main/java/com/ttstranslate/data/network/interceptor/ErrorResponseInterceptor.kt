package com.ttstranslate.data.network.interceptor

import com.ttstranslate.data.global.NetErrorCode.BAD_REQUEST
import com.ttstranslate.data.preference.PreferencesWrapper
import mu.KotlinLogging
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

private val logger = KotlinLogging.logger {}

class ErrorResponseInterceptor(private val preferences: PreferencesWrapper) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response = chain.proceed(chain.request())
        if (response.code >= BAD_REQUEST) {
            logger.warn(response.toString())
        }
        return response
    }
}
