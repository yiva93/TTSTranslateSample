package com.ttstranslate.global.speech

import android.speech.RecognitionListener
import android.os.Bundle
import java.util.ArrayList

class RecognitionListenerDelegator : RecognitionListener {
    private val recognitionListenerList: MutableList<RecognitionListener> = ArrayList()
    fun addRecognitionListener(listener: RecognitionListener) {
        recognitionListenerList.add(listener)
    }

    fun removeRecognitionListener(listener: RecognitionListener) {
        recognitionListenerList.remove(listener)
    }

    fun clear() {
        recognitionListenerList.clear()
    }

    override fun onReadyForSpeech(params: Bundle) {
        for (l in recognitionListenerList) {
            l.onReadyForSpeech(params)
        }
    }

    override fun onBeginningOfSpeech() {
        for (l in recognitionListenerList) {
            l.onBeginningOfSpeech()
        }
    }

    override fun onRmsChanged(rmsdB: Float) {
        for (l in recognitionListenerList) {
            l.onRmsChanged(rmsdB)
        }
    }

    override fun onBufferReceived(buffer: ByteArray) {
        for (l in recognitionListenerList) {
            l.onBufferReceived(buffer)
        }
    }

    override fun onEndOfSpeech() {
        for (l in recognitionListenerList) {
            l.onEndOfSpeech()
        }
    }

    override fun onError(error: Int) {
        for (l in recognitionListenerList) {
            l.onError(error)
        }
    }

    override fun onResults(results: Bundle) {
        for (l in recognitionListenerList) {
            l.onResults(results)
        }
    }

    override fun onPartialResults(partialResults: Bundle) {
        for (l in recognitionListenerList) {
            l.onPartialResults(partialResults)
        }
    }

    override fun onEvent(eventType: Int, params: Bundle) {
        for (l in recognitionListenerList) {
            l.onEvent(eventType, params)
        }
    }
}