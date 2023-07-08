package com.arshapshap.files.domain

internal interface FilesReader {

    suspend fun getJsonFile(): String
}