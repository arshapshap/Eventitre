package com.arshapshap.files.data.repositories

import com.arshapshap.files.data.mappers.EventJsonMapper
import com.arshapshap.files.domain.FilesReader
import com.arshapshap.files.domain.FilesWriter
import com.arshapshap.files.domain.models.EventJson
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import io.mockk.verify
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

internal class EventsJsonRepositoryImplTest {
    private lateinit var filesReader: FilesReader
    private lateinit var filesWriter: FilesWriter
    private lateinit var mapper: EventJsonMapper
    private lateinit var repository: EventsJsonRepositoryImpl

    @BeforeEach
    fun setUp() {
        filesReader = mockk(relaxed = true)
        filesWriter = mockk(relaxed = true)
        mapper = mockk(relaxed = true)
        repository = EventsJsonRepositoryImpl(filesReader, filesWriter, mapper)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getEventsFromJson should return mapped events from filesReader`() = runBlocking {
        // Arrange
        val jsonString = """[
  {
    "id":1,
    "date_start": "1688737200",
    "date_finish": "1688742000",
    "name": "Event 1",
    "description": "Description 1"
  },
  {
    "id":2,
    "date_start": "1688937200",
    "date_finish": "1688942000",
    "name": "Event 2",
    "description": "Description 2"
  }
]"""
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
        val eventJsons = listOf(eventJson1, eventJson2)
        coEvery { filesReader.getJsonFile() } returns jsonString
        every { mapper.mapFromJsonString(jsonString) } returns eventJsons

        // Act
        val result = repository.getEventsFromJson()

        // Assert
        coVerify { filesReader.getJsonFile() }
        verify { mapper.mapFromJsonString(jsonString) }
        assert(result == eventJsons)
    }

    @Test
    fun `saveEventsInJson should call filesWriter's createJson method with the correct arguments`() = runBlocking {
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
        val jsonString = """[
  {
    "id":1,
    "date_start": "1688737200",
    "date_finish": "1688742000",
    "name": "Event 1",
    "description": "Description 1"
  },
  {
    "id":2,
    "date_start": "1688937200",
    "date_finish": "1688942000",
    "name": "Event 2",
    "description": "Description 2"
  }
]"""
        val fileName = "exported_events"
        every { mapper.mapToJsonString(events) } returns jsonString
        coEvery { filesWriter.createJson(jsonString, "$fileName.json") } just runs

        // Act
        repository.saveEventsInJson(events, fileName)

        // Assert
        coVerify { filesWriter.createJson(jsonString, "$fileName.json") }
    }
}
