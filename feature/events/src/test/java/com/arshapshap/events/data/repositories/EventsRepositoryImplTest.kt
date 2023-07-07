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
            name = "Event",
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            description = "Description",
            id = expectedId
        )
        val eventLocal = EventEntity(
            name = "Event",
            dateStartInMilliseconds = 1688737200000,
            dateFinishInMilliseconds = 1688742000000,
            description = "Description",
            id = expectedId
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
            name = name,
            dateStart = Date(dateStartInMilliseconds),
            dateFinish = Date(dateFinishInMilliseconds),
            description = description,
            id = id
        )
        val eventLocal = EventEntity(
            name = name,
            dateStartInMilliseconds = dateStartInMilliseconds,
            dateFinishInMilliseconds = dateFinishInMilliseconds,
            description = description,
            id = id
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
            name = name1,
            dateStartInMilliseconds = dateStartInMilliseconds1,
            dateFinishInMilliseconds = dateFinishInMilliseconds1,
            description = description1,
            id = id1
        )
        val event1 = Event(
            name = name1,
            dateStart = Date(dateStartInMilliseconds1),
            dateFinish = Date(dateFinishInMilliseconds1),
            description = description1,
            id = id1
        )

        val id2 = 2L
        val name2 = "Event 2"
        val description2 = "Description 2"
        val dateStartInMilliseconds2 = 1688937200000
        val dateFinishInMilliseconds2 = 1688942000000
        val eventLocal2 = EventEntity(
            name = name2,
            dateStartInMilliseconds = dateStartInMilliseconds2,
            dateFinishInMilliseconds = dateFinishInMilliseconds2,
            description = description2,
            id = id2
        )
        val event2 =Event(
            name = name2,
            dateStart = Date(dateStartInMilliseconds2),
            dateFinish = Date(dateFinishInMilliseconds2),
            description = description2,
            id = id2
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
            name = name,
            dateStart = Date(dateStartInMilliseconds),
            dateFinish = Date(dateFinishInMilliseconds),
            description = description,
            id = id
        )
        val eventLocal = EventEntity(
            name = name,
            dateStartInMilliseconds = dateStartInMilliseconds,
            dateFinishInMilliseconds = dateFinishInMilliseconds,
            description = description,
            id = id
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
            name = name,
            dateStart = Date(dateStartInMilliseconds),
            dateFinish = Date(dateFinishInMilliseconds),
            description = description,
            id = id
        )
        val eventLocal = EventEntity(
            name = name,
            dateStartInMilliseconds = dateStartInMilliseconds,
            dateFinishInMilliseconds = dateFinishInMilliseconds,
            description = description,
            id = id
        )
        val eventJson = EventJson(
            name = name,
            dateStart = (dateStartInMilliseconds / 1000).toString(),
            dateFinish = (dateFinishInMilliseconds / 1000).toString(),
            description = description,
            id = id
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
