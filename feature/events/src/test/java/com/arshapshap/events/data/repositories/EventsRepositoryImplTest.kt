package com.arshapshap.events.data.repositories

import com.arshapshap.common.domain.models.Event
import com.arshapshap.database.dao.EventDao
import com.arshapshap.database.models.EventEntity
import com.arshapshap.events.data.mappers.EventMapper
import com.arshapshap.files.domain.models.EventJson
import com.arshapshap.files.domain.repositories.EventsJsonRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
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
    fun `addEvent should call localSource's add method and return added event's id`() = runBlocking {
        // Arrange
        val expectedId = 123L
        val event = Event(
            id = expectedId,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            name = "Event",
            description = "Description"
        )
        val eventLocal = EventEntity(
            id = expectedId,
            dateStartInMilliseconds = 1688737200000,
            dateFinishInMilliseconds = 1688742000000,
            name = "Event",
            description = "Description"
        )
        every { mapper.mapToLocal(event) } returns eventLocal
        coEvery { localSource.add(eventLocal) } returns expectedId

        // Act
        val result = repository.addEvent(event)

        // Assert
        coVerify { localSource.add(eventLocal) }
        assert(result == expectedId)
    }

    @Test
    fun `updateEvent should call localSource's update method`() = runBlocking {
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
        val eventLocal = EventEntity(
            id = id,
            dateStartInMilliseconds = dateStartInMilliseconds,
            dateFinishInMilliseconds = dateFinishInMilliseconds,
            name = name,
            description = description
        )
        every { mapper.mapToLocal(event) } returns eventLocal

        // Act
        repository.updateEvent(event)

        // Assert
        coVerify { localSource.update(eventLocal) }
    }

    @Test
    fun `getEvents should return a list of events mapped from localSource's getAll method`() = runBlocking {
        // Arrange
        val id1 = 1L
        val name1 = "Event 1"
        val description1 = "Description 1"
        val dateStartInMilliseconds1 = 1688737200000
        val dateFinishInMilliseconds1 = 1688742000000
        val eventLocal1 = EventEntity(
            id = id1,
            dateStartInMilliseconds = dateStartInMilliseconds1,
            dateFinishInMilliseconds = dateFinishInMilliseconds1,
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
        val eventLocal2 = EventEntity(
            id = id2,
            dateStartInMilliseconds = dateStartInMilliseconds2,
            dateFinishInMilliseconds = dateFinishInMilliseconds2,
            name = name2,
            description = description2
        )
        val event2 =Event(
            id = id2,
            dateStart = Date(dateStartInMilliseconds2),
            dateFinish = Date(dateFinishInMilliseconds2),
            name = name2,
            description = description2
        )
        val eventLocalList = listOf(eventLocal1, eventLocal2)
        coEvery { localSource.getAll() } returns eventLocalList
        every { mapper.mapFromLocal(eventLocal1) } returns event1
        every { mapper.mapFromLocal(eventLocal2) } returns event2

        // Act
        val result = repository.getEvents()

        // Assert
        coVerify { localSource.getAll() }
        assert(result == listOf(event1, event2))
    }

    @Test
    fun `getEventById should return the mapped event from localSource's getById method`() = runBlocking {
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
        val eventLocal = EventEntity(
            id = id,
            dateStartInMilliseconds = dateStartInMilliseconds,
            dateFinishInMilliseconds = dateFinishInMilliseconds,
            name = name,
            description = description
        )
        coEvery { localSource.getById(id) } returns eventLocal
        every { mapper.mapFromLocal(eventLocal) } returns event

        // Act
        val result = repository.getEventById(id)

        // Assert
        coVerify { localSource.getById(id) }
        assert(result == event)
    }

    @Test
    fun `deleteEventById should call localSource's deleteById method`() = runBlocking {
        // Arrange
        val id = 123L

        // Act
        repository.deleteEventById(id)

        // Assert
        coVerify { localSource.deleteById(id) }
    }

    @Test
    fun `exportEventToJson should call jsonRepository's saveEventsInJson method`() = runBlocking {
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
        val eventLocal = EventEntity(
            id = id,
            dateStartInMilliseconds = dateStartInMilliseconds,
            dateFinishInMilliseconds = dateFinishInMilliseconds,
            name = name,
            description = description
        )
        val eventJson = EventJson(
            id = id,
            dateStart = (dateStartInMilliseconds / 1000).toString(),
            dateFinish = (dateFinishInMilliseconds / 1000).toString(),
            name = name,
            description = description
        )
        val expectedFileName = event.name
        every { mapper.mapToLocal(event) } returns eventLocal
        every { mapper.mapToJson(event) } returns eventJson
        coEvery { jsonRepository.saveEventsInJson(listOf(eventJson), expectedFileName) } just runs

        // Act
        repository.exportEventToJson(event)

        // Assert
        coVerify { jsonRepository.saveEventsInJson(listOf(eventJson), expectedFileName) }
    }
}
