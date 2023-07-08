package com.arshapshap.files.domain

internal interface FilesWriter {

    suspend fun createJson(json: String, fileName: String)
}