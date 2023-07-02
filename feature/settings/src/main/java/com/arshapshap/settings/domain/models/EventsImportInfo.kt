package com.arshapshap.settings.domain.models

import com.arshapshap.common.di.domain.models.Event

data class EventsImportInfo(
    val list: List<Event>,
    val listWithoutConflicts: List<Event>
)