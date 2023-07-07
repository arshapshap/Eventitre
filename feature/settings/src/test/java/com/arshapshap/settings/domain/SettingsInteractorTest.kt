package com.arshapshap.settings.domain

import com.arshapshap.common.domain.models.Event
import com.arshapshap.settings.domain.repositories.EventsRepository
import io.mockk.*
import kotlinx.coroutines.runBlocking
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import java.util.Date

class SettingsInteractorTest {
    private lateinit var repository: EventsRepository
    private lateinit var interactor: SettingsInteractor

    @BeforeEach
    fun setUp() {
        repository = mockk(relaxed = true)
        interactor = SettingsInteractor(repository)
    }

    @AfterEach
    fun tearDown() {
        clearAllMocks()
    }

    @Test
    fun `getEventsFromJson should return EventsImportInfo with all events and new events without conflicts`() = runBlocking {
        // Arrange
        val event1 = Event(
            id = 1L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
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
        val eventsFromJson = listOf(event1, event2)
        val eventsWithoutConflicts = listOf(event1)
        coEvery { repository.getEventsFromJson() } returns eventsFromJson
        coEvery { repository.getEventById(1L) } returns null
        coEvery { repository.getEventById(2L) } returns event2

        // Act
        val result = interactor.getEventsFromJson()

        // Assert
        coVerify { repository.getEventsFromJson() }
        eventsFromJson.forEach { event ->
            coVerify { repository.getEventById(event.id) }
        }
        assert(result.allEvents == eventsFromJson)
        assert(result.newEvents == eventsWithoutConflicts)
    }

    @Test
    fun `importEvents should return EventsImportResult with the number of imported events`() = runBlocking {
        // Arrange
        val event1 = Event(
            id = 1L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
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
        val eventsToImport = listOf(event1, event2)
        val importedEventsIds = listOf(1L, 2L)
        coEvery { repository.addEvents(eventsToImport) } returns importedEventsIds

        // Act
        val result = interactor.importEvents(eventsToImport)

        // Assert
        coVerify { repository.addEvents(eventsToImport) }
        assert(result.importedNumber == importedEventsIds.size)
    }

    @Test
    fun `exportEvents should return EventsExportResult with the number of exported events`() = runBlocking {
        // Arrange
        val event1 = Event(
            id = 1L,
            dateStart = Date(1688737200000),
            dateFinish = Date(1688742000000),
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
        val events = listOf(event1, event2)
        val exportedNumber = events.size
        coEvery { repository.getEvents() } returns events
        coEvery { repository.exportEventsToJson(events) } just runs

        // Act
        val result = interactor.exportEvents()

        // Assert
        coVerify { repository.getEvents() }
        coVerify { repository.exportEventsToJson(events) }
        assert(result.exportedNumber == exportedNumber)
    }
}
