package com.arshapshap.files.data.mappers

import com.arshapshap.files.domain.models.EventJson
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class EventJsonMapperTest {
    private val mapper = EventJsonMapper()

    @Test
    fun `mapFromJsonString should correctly map JSON string to List of EventJson`() {
        // Arrange
        val jsonString = """[{"id":1,"date_start":"1688737200","date_finish":"1688742000","name":"Event 1","description":"Description 1"},{"id":2,"date_start":"1688937200","date_finish":"1688942000","name":"Event 2","description":"Description 2"}]"""
        val eventJson1 = EventJson(
            id = 1L,
            dateStart = "1688737200",
            dateFinish = "1688742000",
            name = "Event 1",
            description = "Description 1"
        )
        val eventJson2 = EventJson(
            id = 2L,
            dateStart = "1688937200",
            dateFinish = "1688942000",
            name = "Event 2",
            description = "Description 2"
        )
        val expectedEvents = listOf(eventJson1, eventJson2)

        // Act
        val result = mapper.mapFromJsonString(jsonString)

        // Assert
        assertEquals(expectedEvents, result)
    }

    @Test
    fun `mapToJsonString should correctly map List of EventJson to JSON string`() {
        // Arrange
        val eventJson1 = EventJson(
            id = 1L,
            dateStart = "1688737200",
            dateFinish = "1688742000",
            name = "Event 1",
            description = "Description 1"
        )
        val eventJson2 = EventJson(
            id = 2L,
            dateStart = "1688937200",
            dateFinish = "1688942000",
            name = "Event 2",
            description = "Description 2"
        )
        val events = listOf(eventJson1, eventJson2)
        val expectedJsonString = """[{"id":1,"date_start":"1688737200","date_finish":"1688742000","name":"Event 1","description":"Description 1"},{"id":2,"date_start":"1688937200","date_finish":"1688942000","name":"Event 2","description":"Description 2"}]"""

        // Act
        val result = mapper.mapToJsonString(events)

        // Assert
        assertEquals(expectedJsonString, result)
    }
}
