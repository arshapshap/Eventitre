package com.arshapshap.database.models

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "Event")
data class EventEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "event_id") val id: Long,
    @ColumnInfo(name = "date_start") val dateStartInMilliseconds: Long,
    @ColumnInfo(name = "date_finish") val dateFinishInMilliseconds: Long,
    @ColumnInfo(name = "name") val name: String,
    @ColumnInfo(name = "description") val description: String
)