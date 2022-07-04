package com.ttstranslate.global.dispatcher.error

import android.content.res.Resources
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.ObjectMapper
import com.ttstranslate.R
import com.ttstranslate.data.global.NetErrorCode.BAD_REQUEST
import com.ttstranslate.data.global.NetErrorCode.FORBIDDEN
import com.ttstranslate.data.global.NetErrorCode.TRANSLATION_ERROR
import com.ttstranslate.data.network.response.ErrorModel
import com.ttstranslate.global.navigation.AppRouter
import kotlinx.coroutines.suspendCancellableCoroutine
import mu.KotlinLogging
import retrofit2.HttpException
import java.io.IOException
import java.net.NoRouteToHostException
import java.net.SocketTimeoutException
import kotlin.coroutines.resume

private val logger = KotlinLogging.logger {}

class ErrorHandler(var resources: Resources, private val router: AppRouter) {
    fun proceed(
        error: Throwable,
        allowFullscreenStubs: Boolean = true,
        callback: (String) -> Unit
    ) {
        logger.error(error.message, error)
        when {
            else -> {
                callback(getUserMessage(error))
            }
        }
    }

    suspend fun proceedSuspend(error: Throwable): String = suspendCancellableCoroutine {
        logger.error(error.message, error)
        when {
            else -> it.resume(getUserMessage(error))
        }
    }

    private fun getUserMessage(error: Throwable): String {
        return when (error) {
            is IOException -> resources.getString(getIOErrorMessage(error))
            is HttpException -> getServerErrorMessage(error)
            is SecurityException -> resources.getString(R.string.security_error)
            else -> {
                sendCrashlyticsReport(error)
                resources.getString(R.string.unknown_error)
            }
        }
    }

    private fun getIOErrorMessage(error: IOException): Int {
        return if (error is NoRouteToHostException || error is SocketTimeoutException) {
            R.string.server_not_available_error
        } else {
            R.string.network_error
        }
    }

    private fun getServerErrorMessage(exception: HttpException): String {
        val errorBody = exception.response()?.errorBody()?.string()
        val objectMapper = ObjectMapper()
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
        val errorModel = objectMapper.readValue(errorBody, ErrorModel::class.java)
        sendCrashlyticsReport(exception)
        return when {
            exception.code() == BAD_REQUEST -> resources.getString(R.string.bad_request_error)
            exception.code() == FORBIDDEN -> resources.getString(R.string.forbidden_error)
            exception.code() == TRANSLATION_ERROR -> resources.getString(R.string.translation_error)
            !errorModel.error.isNullOrBlank() -> errorModel.error!!
            else -> resources.getString(R.string.unknown_error)
        }
    }

    private fun sendCrashlyticsReport(error: Throwable) {
        // FirebaseCrashlytics.getInstance().recordException(error)
    }
}
