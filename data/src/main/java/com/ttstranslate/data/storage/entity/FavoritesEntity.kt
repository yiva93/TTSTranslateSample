package com.ttstranslate.data.storage.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.ttstranslate.domain.model.Translation
import com.ttstranslate.domain.model.enums.Language

@Entity(tableName = "favorites")
data class FavoritesEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val textFrom: String?,
    val result: String?,
    val dateAdded: Long?,
    val languageFromId: Long,
    val languageToId: Long
)

fun FavoritesEntity.toTranslation() = Translation(
    favoriteId = this.id,
    textFrom = this.textFrom ?: "",
    result = this.result ?: "",
    dateAdded = this.dateAdded ?: 0,
    languageFromTo = Pair(
        Language[this.languageFromId.toInt()],
        Language[this.languageToId.toInt()]
    )
)

fun Translation.toFavoriteEntity() = FavoritesEntity(
    textFrom = this.textFrom,
    result = this.result,
    dateAdded = this.dateAdded,
    languageFromId = this.languageFromTo.first.id.toLong(),
    languageToId = this.languageFromTo.second.id.toLong()
)