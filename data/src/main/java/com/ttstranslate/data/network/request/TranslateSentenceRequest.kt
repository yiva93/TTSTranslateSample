package com.ttstranslate.data.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TranslateSentenceRequest(
    @SerialName("q") val query: String,
    @SerialName("source") val source: String,
    @SerialName("target") val target: String,
    @SerialName("format") val format: String,
    @SerialName("api_key") val apiKey: String = ""
)
