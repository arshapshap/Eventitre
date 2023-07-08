package com.arshapshap.files.data.mappers

import com.arshapshap.files.domain.models.EventJson
import com.google.gson.Gson
import java.util.*
import javax.inject.Inject

internal class EventJsonMapper @Inject constructor() {

    internal fun mapFromJsonString(json: String): List<EventJson> {
        return Gson().fromJson(json, Array<EventJson>::class.java).toList()
    }

    internal fun mapToJsonString(events: List<EventJson>): String {
        return Gson().toJson(events)
    }
}