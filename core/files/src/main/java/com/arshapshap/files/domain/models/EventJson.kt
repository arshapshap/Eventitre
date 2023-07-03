package com.arshapshap.files.domain.models

import com.google.gson.annotations.SerializedName

data class EventJson(
    @SerializedName("id") val id: Long?,
    @SerializedName("date_start") val dateStart: String?,
    @SerializedName("date_finish") val dateFinish: String?,
    @SerializedName("name") val name: String?,
    @SerializedName("description") val description: String?
)