package com.ttstranslate.data.network.interceptor

import com.ttstranslate.data.preference.PreferencesWrapper
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException

private const val AUTHORIZATION = "Authorization"
private const val TOKEN_TEMPLATE = "JWT %s"

class HeaderInterceptor(private val prefs: PreferencesWrapper) : Interceptor {
    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()
        val requestBuilder = request.newBuilder()
        requestBuilder.removeHeader(AUTHORIZATION)
        if (prefs.authToken.get().isNotBlank()) {
            requestBuilder.addHeader(
                AUTHORIZATION,
                TOKEN_TEMPLATE.format(prefs.authToken.get())
            )
        }
        return chain.proceed(requestBuilder.build())
    }
}
