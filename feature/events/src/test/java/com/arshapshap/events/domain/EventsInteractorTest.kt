package com.arshapshap.events.domain

import com.arshapshap.common.domain.models.Event
import com.arshapshap.events.domain.repositories.EventsRepository
import io.mockk.clearAllMocks
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.just
import io.mockk.mockk
import io.mockk.runs
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class EventsInteractorTest {
    private lateinit var eventsRepository: EventsRepository
    private lateinit var eventsInteractor: EventsInteractor

    @BeforeEach
    fun setUp() {
        eventsRepository = mockk(relaxed = true)
        eventsInteractor = EventsInteractor(eventsRepository)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `addEvent should call repository's addEvent method and return added event's id`() = runBlocking {
        // Arrange
        val event = Event(
            name = "Event",
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            description = "Description",
            id = 123L
        )
        val expectedId = 123L
        coEvery { eventsRepository.addEvent(event) } returns expectedId

        // Act
        val result = eventsInteractor.addEvent(event)

        // Assert
        coVerify { eventsRepository.addEvent(event) }
        assert(result == expectedId)
    }

    @Test
    fun `updateEvent should call repository's updateEvent method`() = runBlocking {
        // Arrange
        val event = Event(
            name = "Event",
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            description = "Description",
            id = 123L
        )

        // Act
        eventsInteractor.updateEvent(event)

        // Assert
        coVerify { eventsRepository.updateEvent(event) }
    }

    @Test
    fun `getEventsByDateRange should return events grouped by date within the specified range`() = runBlocking {
        // Arrange
        val dateStart = Date()
        val dateFinish = Date()
        val events = listOf(Event(
            name = "Event",
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            description = "Description",
            id = 123L
        ))
        val expectedResult = mutableMapOf<Date, List<Event>>()
        coEvery { eventsRepository.getEvents() } returns events

        // Act
        val result = eventsInteractor.getEventsByDateRange(dateStart, dateFinish)

        // Assert
        coVerify { eventsRepository.getEvents() }
        assert(result == expectedResult)
    }

    @Test
    fun `getEventById should call repository's getEventById method and return the result`() = runBlocking {
        // Arrange
        val id = 123L
        val expectedEvent = Event(
            name = "Event",
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            description = "Description",
            id = id
        )
        coEvery { eventsRepository.getEventById(id) } returns expectedEvent

        // Act
        val result = eventsInteractor.getEventById(id)

        // Assert
        coVerify { eventsRepository.getEventById(id) }
        assert(result == expectedEvent)
    }

    @Test
    fun `deleteEventById should call repository's deleteEventById method`() = runBlocking {
        // Arrange
        val id = 123L

        // Act
        eventsInteractor.deleteEventById(id)

        // Assert
        coVerify { eventsRepository.deleteEventById(id) }
    }

    @Test
    fun `exportEvent should call repository's getEventById and exportEventToJson methods and return true if event is found`() = runBlocking {
        // Arrange
        val id = 123L
        val event = Event(
            name = "Event",
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
            description = "Description",
            id = id
        )
        coEvery { eventsRepository.getEventById(id) } returns event
        coEvery { eventsRepository.exportEventToJson(event) } just runs

        // Act
        val result = eventsInteractor.exportEvent(id)

        // Assert
        coVerify { eventsRepository.getEventById(id) }
        coVerify { eventsRepository.exportEventToJson(event) }
        assert(result)
    }

    @Test
    fun `exportEvent should return false if event is not found`() = runBlocking {
        // Arrange
        val id = 123L
        coEvery { eventsRepository.getEventById(id) } returns null

        // Act
        val result = eventsInteractor.exportEvent(id)

        // Assert
        coVerify { eventsRepository.getEventById(id) }
        assert(!result)
    }
}
