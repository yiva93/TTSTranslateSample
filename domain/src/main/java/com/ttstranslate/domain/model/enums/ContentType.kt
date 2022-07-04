package com.ttstranslate.domain.model.enums

enum class ContentType(val id: Int, val tag: String) {
    NONE(0, ""),
    TEXT(1, "TEXT"),
    IMAGE(2, "IMAGE"),
    FILE(3, "FILE");

    companion object {
        fun getValueByTag(tag: String?): ContentType =
            values().find { it.tag == tag } ?: NONE

        fun getValueByContentType(contentType: String?): ContentType =
            if (contentType?.matches(Regex("image/.+")) == true) IMAGE else FILE

        fun getValueById(id: Int?): ContentType = values().find { it.id == id } ?: NONE
    }
}
