package com.arshapshap.database.dao

import androidx.room.*
import com.arshapshap.database.models.EventEntity

@Dao
abstract class EventDao {

    @Insert
    abstract suspend fun add(eventEntity: EventEntity): Long

    @Update
    abstract suspend fun update(eventEntity: EventEntity)

    @Delete
    abstract suspend fun delete(eventEntity: EventEntity)

    @Query("SELECT * FROM Event")
    abstract suspend fun getAll(): List<EventEntity>

    @Query("SELECT * FROM Event WHERE event_id=:id")
    abstract suspend fun getById(id: Long): EventEntity
}