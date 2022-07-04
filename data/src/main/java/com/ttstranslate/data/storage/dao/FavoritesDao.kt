package com.ttstranslate.data.storage.dao

import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.ttstranslate.data.storage.entity.FavoritesEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoritesDao {
    @Query("SELECT * FROM favorites ORDER BY dateAdded DESC")
    fun observeAll(): Flow<List<FavoritesEntity>>

    @Query("SELECT * FROM favorites ORDER BY dateAdded")
    fun pagingSource(): DataSource.Factory<Int, FavoritesEntity>

    @Query("SELECT * FROM favorites where LOWER(textFrom) LIKE LOWER(:textFrom) LIMIT 1")
    suspend fun getByFromField(textFrom: String): FavoritesEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun add(favoriteEntity: FavoritesEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addAll(favorites: List<FavoritesEntity>)

    @Query("SELECT * FROM favorites WHERE id=:id")
    suspend fun getById(id: Long): FavoritesEntity

    @Query("DELETE FROM favorites")
    suspend fun drop()

    @Query("DELETE FROM favorites WHERE id = :id")
    suspend fun deleteById(id: Long)
}