package com.arshapshap.settings.data.mappers

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
        val eventEntity = EventEntity(
            id = 123L,
            dateStartInMilliseconds = 1688737200000,
            dateFinishInMilliseconds = 1688742000000,
            name = "Event",
            description = "Description"
        )
        val expectedEvent = Event(
            id = 123L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            name = "Event",
            description = "Description"
        )

        // Act
        val result = mapper.mapFromLocal(eventEntity)

        // Assert
        assertEquals(expectedEvent, result)
    }

    @Test
    fun `mapToLocal should correctly map Event to EventEntity`() {
        // Arrange
        val event = Event(
            id = 123L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            name = "Event",
            description = "Description"
        )
        val expectedEventEntity = EventEntity(
            id = 123L,
            dateStartInMilliseconds = 1688737200000,
            dateFinishInMilliseconds = 1688742000000,
            name = "Event",
            description = "Description"
        )

        // Act
        val result = mapper.mapToLocal(event)

        // Assert
        assertEquals(expectedEventEntity, result)
    }

    @Test
    fun `mapToJson should correctly map Event to EventJson`() {
        // Arrange
        val event = Event(
            id = 123L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            name = "Event",
            description = "Description"
        )
        val expectedEventJson = EventJson(
            id = 123L,
            dateStart = "1688737200",
            dateFinish = "1688742000",
            name = "Event",
            description = "Description"
        )

        // Act
        val result = mapper.mapToJson(event)

        // Assert
        assertEquals(expectedEventJson, result)
    }

    @Test
    fun `mapFromJson should correctly map EventJson to Event`() {
        // Arrange
        val eventJson = EventJson(
            id = 123L,
            dateStart = "1688737200",
            dateFinish = "1688742000",
            name = "Event",
            description = "Description"
        )
        val expectedEvent = Event(
            id = 123L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            name = "Event",
            description = "Description"
        )

        // Act
        val result = mapper.mapFromJson(eventJson)

        // Assert
        assertEquals(expectedEvent, result)
    }
}
