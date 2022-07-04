package com.ttstranslate.global.speech

import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.content.ServiceConnection
import android.content.pm.ResolveInfo
import android.os.IBinder
import android.speech.RecognitionListener
import android.speech.RecognitionService
import android.speech.SpeechRecognizer
import android.util.Log
import com.ttstranslate.global.dispatcher.error.ErrorHandler

class SpeechRecognitionService(
    private var context: Context,
    private var errorHandler: ErrorHandler
) {
    private var speechRecognizer: SpeechRecognizer? = null
    private val listenerDelegator = RecognitionListenerDelegator()

    @Throws(ServiceNotEnabledException::class, ServiceNotAvailableException::class)
    fun startListening(intent: Intent) {
        checkIsServiceAvailable(context)
        try {
            releaseSpeechRecognizer()
            startListening(intent, componentName(context))
        } catch (e: SecurityException) {
            releaseSpeechRecognizer()
            startListening(intent, null)
        }
    }

    private fun startListening(intent: Intent, componentName: ComponentName?) {
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context, componentName)
        speechRecognizer?.setRecognitionListener(listenerDelegator)
        speechRecognizer?.startListening(intent)
    }

    fun stopListening() {
        if (speechRecognizer != null) {
            speechRecognizer!!.cancel()
        }
    }

    fun release() {
        listenerDelegator.clear()
        releaseSpeechRecognizer()
    }

    fun addRecognitionListener(listener: RecognitionListener) {
        listenerDelegator.addRecognitionListener(listener)
    }

    fun removeRecognitionListener(listener: RecognitionListener) {
        listenerDelegator.removeRecognitionListener(listener)
    }

    private fun releaseSpeechRecognizer() {
        try {
            if (speechRecognizer != null) {
                speechRecognizer!!.destroy()
                speechRecognizer = null
            }
        } catch (ignored: Exception) {
        }
    }

    @Throws(ServiceNotAvailableException::class, ServiceNotEnabledException::class)
    private fun checkIsServiceAvailable(context: Context) {
        if (!SpeechRecognizer.isRecognitionAvailable(context)) {
            throw ServiceNotAvailableException()
        }
        val componentName = componentName(context)
        if (!isServiceComponentAvailable(context, componentName)) {
            errorHandler.proceed(
                Exception(
                    "RecognitionService not available:" +
                            componentName?.flattenToString()
                )
            ) {
                Log.d(
                    SpeechRecognitionService.toString(),
                    "RecognitionService not available:" + componentName?.flattenToString()
                )
            }
            throw ServiceNotEnabledException()
        }
    }

    private fun isServiceComponentAvailable(
        context: Context?,
        componentName: ComponentName?
    ): Boolean {
        if (componentName == null) {
            return false
        }
        val serviceIntent = Intent(RecognitionService.SERVICE_INTERFACE)
        serviceIntent.component = componentName
        val connection: ServiceConnection = object : ServiceConnection {
            override fun onServiceConnected(name: ComponentName, service: IBinder) {
                //stub service
            }

            override fun onServiceDisconnected(name: ComponentName) {
                //stub service
            }
        }
        return try {
            context!!.bindService(serviceIntent, connection, Context.BIND_AUTO_CREATE)
        } catch (e: SecurityException) {
            true
        } finally {
            try {
                context!!.unbindService(connection)
            } catch (ignored: Exception) {
            }
        }
    }

    private fun componentName(context: Context?): ComponentName? {
        val recognitionIntent = Intent(RecognitionService.SERVICE_INTERFACE)
        val infoList = context!!.packageManager.queryIntentServices(recognitionIntent, 0)
        for (resolveInfo in infoList) {
            if (resolveInfo.serviceInfo.packageName == SEARCH_PACKAGE_NAME && resolveInfo.serviceInfo.name == VOICE_RECOGNITION_SERVICE) {
                return componentName(resolveInfo)
            }
        }
        return if (infoList.size == 0) null else componentName(
            infoList[0]
        )
    }

    private fun componentName(resolveInfo: ResolveInfo): ComponentName {
        return ComponentName(resolveInfo.serviceInfo.packageName, resolveInfo.serviceInfo.name)
    }

    companion object {
        private const val SEARCH_PACKAGE_NAME = "com.google.android.googlequicksearchbox"
        private const val VOICE_RECOGNITION_SERVICE =
            "com.google.android.voicesearch.serviceapi.GoogleRecognitionService"
    }

    class ServiceNotAvailableException : Exception()

    class ServiceNotEnabledException : Exception()
}