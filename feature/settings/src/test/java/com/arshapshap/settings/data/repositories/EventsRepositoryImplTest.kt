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
        val id1 = 1L
        val name1 = "Event 1"
        val description1 = "Description 1"
        val dateStartInMilliseconds1 = 1688737200000
        val dateFinishInMilliseconds1 = 1688742000000
        val eventJson1 = EventJson(
            id = id1,
            dateStart = (dateStartInMilliseconds1 / 1000).toString(),
            dateFinish = (dateFinishInMilliseconds1 / 1000).toString(),
            name = name1,
            description = description1
        )
        val event1 = Event(
            id = id1,
            dateStart = Date(dateStartInMilliseconds1),
            dateFinish = Date(dateFinishInMilliseconds1),
            name = name1,
            description = description1
        )

        val id2 = 2L
        val name2 = "Event 2"
        val description2 = "Description 2"
        val dateStartInMilliseconds2 = 1688937200000
        val dateFinishInMilliseconds2 = 1688942000000
        val eventJson2 = EventJson(
            id = id2,
            dateStart = (dateStartInMilliseconds2 / 1000).toString(),
            dateFinish = (dateFinishInMilliseconds2 / 1000).toString(),
            name = name2,
            description = description2
        )
        val event2 = Event(
            id = id2,
            dateStart = Date(dateStartInMilliseconds2),
            dateFinish = Date(dateFinishInMilliseconds2),
            name = name2,
            description = description2
        )

        val id3 = 3L
        val name3 = "Event with wrong dates"
        val description3 = "Description 3"
        val dateStartInMilliseconds3 = 1688937200000
        val dateFinishInMilliseconds3 = 1688937200000
        val eventJsonWithWrongDates = EventJson(
            id = id3,
            dateStart = (dateStartInMilliseconds3 / 1000).toString(),
            dateFinish = (dateFinishInMilliseconds3 / 1000).toString(),
            name = name3,
            description = description3
        )
        val eventWithWrongDates = Event(
            id = id3,
            dateStart = Date(dateStartInMilliseconds3),
            dateFinish = Date(dateFinishInMilliseconds3),
            name = name3,
            description = description3
        )

        val eventJsonWithNull = EventJson(
            id = 4L,
            dateStart = null,
            dateFinish = (dateFinishInMilliseconds1 / 1000).toString(),
            name = "Event with null date start",
            description = description1
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
        val id1 = 1L
        val name1 = "Event 1"
        val description1 = "Description 1"
        val dateStartInMilliseconds1 = 1688737200000
        val dateFinishInMilliseconds1 = 1688742000000
        val event1 = Event(
            id = id1,
            dateStart = Date(dateStartInMilliseconds1),
            dateFinish = Date(dateFinishInMilliseconds1),
            name = name1,
            description = description1
        )
        val eventJson1 = EventJson(
            id = id1,
            dateStart = (dateStartInMilliseconds1 / 1000).toString(),
            dateFinish = (dateFinishInMilliseconds1 / 1000).toString(),
            name = name1,
            description = description1
        )

        val id2 = 2L
        val name2 = "Event 2"
        val description2 = "Description 2"
        val dateStartInMilliseconds2 = 1688937200000
        val dateFinishInMilliseconds2 = 1688942000000
        val event2 = Event(
            id = id2,
            dateStart = Date(dateStartInMilliseconds2),
            dateFinish = Date(dateFinishInMilliseconds2),
            name = name2,
            description = description2
        )
        val eventJson2 = EventJson(
            id = id2,
            dateStart = (dateStartInMilliseconds2 / 1000).toString(),
            dateFinish = (dateFinishInMilliseconds2 / 1000).toString(),
            name = name2,
            description = description2
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
        val id1 = 1L
        val name1 = "Event 1"
        val description1 = "Description 1"
        val dateStartInMilliseconds1 = 1688737200000
        val dateFinishInMilliseconds1 = 1688742000000
        val event1 = Event(
            id = id1,
            dateStart = Date(dateStartInMilliseconds1),
            dateFinish = Date(dateFinishInMilliseconds1),
            name = name1,
            description = description1
        )
        val eventLocal1 = EventEntity(
            id = id1,
            dateStartInMilliseconds = dateStartInMilliseconds1,
            dateFinishInMilliseconds = dateFinishInMilliseconds1,
            name = name1,
            description = description1
        )

        val id2 = 2L
        val name2 = "Event 2"
        val description2 = "Description 2"
        val dateStartInMilliseconds2 = 1688937200000
        val dateFinishInMilliseconds2 = 1688942000000
        val event2 = Event(
            id = id2,
            dateStart = Date(dateStartInMilliseconds2),
            dateFinish = Date(dateFinishInMilliseconds2),
            name = name2,
            description = description2
        )
        val eventLocal2 = EventEntity(
            id = id2,
            dateStartInMilliseconds = dateStartInMilliseconds2,
            dateFinishInMilliseconds = dateFinishInMilliseconds2,
            name = name2,
            description = description2
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
        val mappedEvent = Event(
            id = id,
            dateStart = Date(dateStartInMilliseconds),
            dateFinish = Date(dateFinishInMilliseconds),
            name = name,
            description = description
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
        val id1 = 1L
        val name1 = "Event 1"
        val description1 = "Description 1"
        val dateStartInMilliseconds1 = 1688737200000
        val dateFinishInMilliseconds1 = 1688742000000
        val event1 = Event(
            id = id1,
            dateStart = Date(dateStartInMilliseconds1),
            dateFinish = Date(dateFinishInMilliseconds1),
            name = name1,
            description = description1
        )
        val eventLocal1 = EventEntity(
            id = id1,
            dateStartInMilliseconds = dateStartInMilliseconds1,
            dateFinishInMilliseconds = dateFinishInMilliseconds1,
            name = name1,
            description = description1
        )

        val id2 = 2L
        val name2 = "Event 2"
        val description2 = "Description 2"
        val dateStartInMilliseconds2 = 1688937200000
        val dateFinishInMilliseconds2 = 1688942000000
        val event2 = Event(
            id = id2,
            dateStart = Date(dateStartInMilliseconds2),
            dateFinish = Date(dateFinishInMilliseconds2),
            name = name2,
            description = description2
        )
        val eventLocal2 = EventEntity(
            id = id2,
            dateStartInMilliseconds = dateStartInMilliseconds2,
            dateFinishInMilliseconds = dateFinishInMilliseconds2,
            name = name2,
            description = description2
        )
        val events = listOf(event1, event2)
        val eventEntities = listOf(eventLocal1, eventLocal2)
        val expectedIds = listOf(id1, id2)
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
