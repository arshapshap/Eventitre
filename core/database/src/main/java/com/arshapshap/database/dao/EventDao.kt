package com.arshapshap.database.dao

import androidx.room.*
import com.arshapshap.database.models.EventEntity

@Dao
abstract class EventDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun add(eventEntity: EventEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    abstract suspend fun addList(list: List<EventEntity>): List<Long>

    @Update
    abstract suspend fun update(eventEntity: EventEntity)

    @Query("DELETE FROM Event WHERE event_id = :id")
    abstract suspend fun deleteById(id: Long)

    @Query("DELETE FROM Event")
    abstract suspend fun deleteAll()

    @Query("SELECT * FROM Event")
    abstract suspend fun getAll(): List<EventEntity>

    @Query("SELECT * FROM Event WHERE event_id=:id")
    abstract suspend fun getById(id: Long): EventEntity?
}