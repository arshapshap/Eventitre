package com.arshapshap.common.domain.models

import java.util.Date

data class Event(
    val id: Long,
    val dateStart: Date,
    val dateFinish: Date,
    val name: String,
    val description: String
)
