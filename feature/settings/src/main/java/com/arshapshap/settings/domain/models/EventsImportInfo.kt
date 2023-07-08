package com.arshapshap.settings.domain.models

import com.arshapshap.common.domain.models.Event

data class EventsImportInfo(
    val allEvents: List<Event>,
    val newEvents: List<Event>
)