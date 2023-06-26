package com.arshapshap.events.domain.models

import java.util.Date

internal data class Event(
    val id: Int,
    val dateStart: Date,
    val dateFinish: Date,
    val name: String,
    val description: String
)
