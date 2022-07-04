package com.ttstranslate.data.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranslateSentenceResponse(
    @SerialName("translatedText") val translatedText: String
)