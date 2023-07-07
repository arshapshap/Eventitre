package com.arshapshap.events.data.mappers

import com.arshapshap.common.domain.models.Event
import com.arshapshap.database.models.EventEntity
import com.arshapshap.files.domain.models.EventJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import java.util.*

class EventMapperTest {
    private val mapper = EventMapper()

    @Test
    fun `mapFromLocal should correctly map EventEntity to Event`() {
        // Arrange
        val id = 123L
        val name = "Event"
        val description = "Description"
        val dateStartInMilliseconds = 1688737200000
        val dateFinishInMilliseconds = 1688742000000
        val eventEntity = EventEntity(
            id = id,
            dateStartInMilliseconds = dateStartInMilliseconds,
            dateFinishInMilliseconds = dateFinishInMilliseconds,
            name = name,
            description = description
        )
        val expectedEvent = Event(
            id = id,
            dateStart = Date(dateStartInMilliseconds),
            dateFinish = Date(dateFinishInMilliseconds),
            name = name,
            description = description
        )

        // Act
        val result = mapper.mapFromLocal(eventEntity)

        // Assert
        assertEquals(expectedEvent, result)
    }

    @Test
    fun `mapToLocal should correctly map Event to EventEntity`() {
        // Arrange
        val id = 123L
        val name = "Event"
        val description = "Description"
        val dateStartInMilliseconds = 1688737200000
        val dateFinishInMilliseconds = 1688742000000
        val event = Event(
            id = id,
            dateStart = Date(dateStartInMilliseconds),
            dateFinish = Date(dateFinishInMilliseconds),
            name = name,
            description = description
        )
        val expectedEventEntity = EventEntity(
            id = id,
            dateStartInMilliseconds = dateStartInMilliseconds,
            dateFinishInMilliseconds = dateFinishInMilliseconds,
            name = name,
            description = description
        )

        // Act
        val result = mapper.mapToLocal(event)

        // Assert
        assertEquals(expectedEventEntity, result)
    }

    @Test
    fun `mapToJson should correctly map Event to EventJson`() {
        // Arrange
        val id = 123L
        val name = "Event"
        val description = "Description"
        val dateStartInMilliseconds = 1688737200000
        val dateFinishInMilliseconds = 1688742000000
        val event = Event(
            id = id,
            dateStart = Date(dateStartInMilliseconds),
            dateFinish = Date(dateFinishInMilliseconds),
            name = name,
            description = description
        )
        val expectedEventJson = EventJson(
            id = id,
            dateStart = (dateStartInMilliseconds / 1000).toString(),
            dateFinish = (dateFinishInMilliseconds / 1000).toString(),
            name = name,
            description = description
        )

        // Act
        val result = mapper.mapToJson(event)

        // Assert
        assertEquals(expectedEventJson, result)
    }
}
