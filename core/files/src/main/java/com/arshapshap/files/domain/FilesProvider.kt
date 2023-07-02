package com.arshapshap.files.domain

interface FilesProvider {

    suspend fun getJsonFile(callback: suspend (String) -> Unit)
}