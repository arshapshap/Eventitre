package com.arshapshap.settings.data.repositories

import com.arshapshap.common.domain.models.Event
import com.arshapshap.database.dao.EventDao
import com.arshapshap.database.models.EventEntity
import com.arshapshap.files.domain.models.EventJson
import com.arshapshap.files.domain.repositories.EventsJsonRepository
import com.arshapshap.settings.data.mappers.EventMapper
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
import java.util.Date

internal class EventsRepositoryImplTest {
    private lateinit var localSource: EventDao
    private lateinit var jsonRepository: EventsJsonRepository
    private lateinit var mapper: EventMapper
    private lateinit var repository: EventsRepositoryImpl

    @BeforeEach
    fun setUp() {
        localSource = mockk(relaxed = true)
        jsonRepository = mockk(relaxed = true)
        mapper = mockk(relaxed = true)
        repository = EventsRepositoryImpl(localSource, jsonRepository, mapper)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getEventsFromJson should return filtered and mapped events from jsonRepository`() = runBlocking {
        // Arrange
        val eventJson1 = EventJson(
            id = 1L,
            dateStart = "1688737200",
            dateFinish = "1688742000",
            name = "Event 1",
            description = "Description 1"
        )
        val event1 = Event(
            id = 1L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
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
        val event2 = Event(
            id = 2L,
            dateStart = Date(1688937200000),
            dateFinish = Date(1688942000000),
            name = "Event 2",
            description = "Description 2"
        )

        val eventJsonWithWrongDates = EventJson(
            id = 3L,
            dateStart = "1688937200",
            dateFinish = "1688937200",
            name = "Event with wrong dates",
            description = "Description 3"
        )
        val eventWithWrongDates = Event(
            id = 3L,
            dateStart = Date(1688937200000),
            dateFinish = Date(1688937200000),
            name = "Event with wrong dates",
            description = "Description 3"
        )

        val eventJsonWithNull = EventJson(
            id = 4L,
            dateStart = null,
            dateFinish = "1688742000",
            name = "Event with null date start",
            description = "Description 1"
        )
        val jsonEvents = listOf(eventJson1, eventJson2, eventJsonWithWrongDates, eventJsonWithNull)
        val correctJsonEvents = listOf(eventJson1, eventJson2, eventJsonWithWrongDates)
        val mappedEvents = listOf(event1, event2, eventWithWrongDates)
        val filteredEvents = listOf(event1, event2)
        coEvery { jsonRepository.getEventsFromJson() } returns jsonEvents
        every { mapper.mapFromJson(any()) } returnsMany mappedEvents

        // Act
        val result = repository.getEventsFromJson()

        // Assert
        coVerify { jsonRepository.getEventsFromJson() }
        correctJsonEvents.forEach { jsonEvent ->
            verify { mapper.mapFromJson(jsonEvent) }
        }
        assert(result == filteredEvents)
    }

    @Test
    fun `exportEventsToJson should call jsonRepository's saveEventsInJson method with the correct arguments`() = runBlocking {
        // Arrange
        val event1 = Event(
            id = 1L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            name = "Event 1",
            description = "Description 1"
        )
        val eventJson1 = EventJson(
            id = 1L,
            dateStart = "1688737200",
            dateFinish = "1688742000",
            name = "Event 1",
            description = "Description 1"
        )

        val event2 = Event(
            id = 2L,
            dateStart = Date(1688937200000),
            dateFinish = Date(1688942000000),
            name = "Event 2",
            description = "Description 2"
        )
        val eventJson2 = EventJson(
            id = 2L,
            dateStart = "1688937200",
            dateFinish = "1688942000",
            name = "Event 2",
            description = "Description 2"
        )
        val events = listOf(event1, event2)
        val eventJsons = listOf(eventJson1, eventJson2)
        every { mapper.mapToJson(any()) } returnsMany eventJsons
        coEvery { jsonRepository.saveEventsInJson(eventJsons, EventsRepositoryImpl.EXPORTED_JSON_FILE_NAME) } just runs

        // Act
        repository.exportEventsToJson(events)

        // Assert
        coVerify { jsonRepository.saveEventsInJson(eventJsons, EventsRepositoryImpl.EXPORTED_JSON_FILE_NAME) }
    }

    @Test
    fun `getEvents should return mapped events from localSource`() = runBlocking {
        // Arrange
        val event1 = Event(
            id = 1L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            name = "Event 1",
            description = "Description 1"
        )
        val eventLocal1 = EventEntity(
            id = 1L,
            dateStartInMilliseconds = 1688737200000,
            dateFinishInMilliseconds = 1688742000000,
            name = "Event 1",
            description = "Description 1"
        )

        val event2 = Event(
            id = 2L,
            dateStart = Date(1688937200000),
            dateFinish = Date(1688942000000),
            name = "Event 2",
            description = "Description 2"
        )
        val eventLocal2 = EventEntity(
            id = 2L,
            dateStartInMilliseconds = 1688937200000,
            dateFinishInMilliseconds = 1688942000000,
            name = "Event 2",
            description = "Description 2"
        )
        val eventEntities = listOf(eventLocal1, eventLocal2)
        val mappedEvents = listOf(event1, event2)
        coEvery { localSource.getAll() } returns eventEntities
        every { mapper.mapFromLocal(any()) } returnsMany mappedEvents

        // Act
        val result = repository.getEvents()

        // Assert
        coVerify { localSource.getAll() }
        eventEntities.forEach { eventEntity ->
            verify { mapper.mapFromLocal(eventEntity) }
        }
        assert(result == mappedEvents)
    }

    @Test
    fun `getEventById should return mapped event from localSource`() = runBlocking {
        // Arrange
        val id = 123L
        val eventEntity = EventEntity(
            id = id,
            dateStartInMilliseconds = 1688737200000,
            dateFinishInMilliseconds = 1688742000000,
            name = "Event",
            description = "Description"
        )
        val mappedEvent = Event(
            id = id,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            name = "Event",
            description = "Description"
        )
        coEvery { localSource.getById(id) } returns eventEntity
        every { mapper.mapFromLocal(eventEntity) } returns mappedEvent

        // Act
        val result = repository.getEventById(id)

        // Assert
        coVerify { localSource.getById(id) }
        verify { mapper.mapFromLocal(eventEntity) }
        assert(result == mappedEvent)
    }

    @Test
    fun `addEvents should return a list of IDs returned by localSource's addList method`() = runBlocking {
        // Arrange
        val event1 = Event(
            id = 1L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            name = "Event 1",
            description = "Description 1"
        )
        val eventLocal1 = EventEntity(
            id = 1L,
            dateStartInMilliseconds = 1688737200000,
            dateFinishInMilliseconds = 1688742000000,
            name = "Event 1",
            description = "Description 1"
        )

        val event2 = Event(
            id = 2L,
            dateStart = Date(1688937200000),
            dateFinish = Date(1688942000000),
            name = "Event 2",
            description = "Description 2"
        )
        val eventLocal2 = EventEntity(
            id = 2L,
            dateStartInMilliseconds = 1688937200000,
            dateFinishInMilliseconds = 1688942000000,
            name = "Event 2",
            description = "Description 2"
        )
        val events = listOf(event1, event2)
        val eventEntities = listOf(eventLocal1, eventLocal2)
        val expectedIds = listOf(1L, 2L)
        every { mapper.mapToLocal(any()) } returnsMany eventEntities
        coEvery { localSource.addList(eventEntities) } returns expectedIds

        // Act
        val result = repository.addEvents(events)

        // Assert
        events.forEach { event ->
            verify { mapper.mapToLocal(event) }
        }
        coVerify { localSource.addList(eventEntities) }
        assert(result == expectedIds)
    }
}
