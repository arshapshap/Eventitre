package com.arshapshap.files.domain

internal interface FilesReader {

    suspend fun getJsonFile(callback: suspend (String) -> Unit)
}