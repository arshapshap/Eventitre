package com.arshapshap.files.data.providers

import android.content.ContentResolver
import android.net.Uri
import com.arshapshap.files.data.observer.listeners.ActivityResultGetContentListener
import com.arshapshap.files.data.observer.LifecycleObserver
import com.arshapshap.files.domain.FilesReader
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

internal class FilesReaderImpl @Inject constructor(
    private val observer: LifecycleObserver,
    private val contentResolver: ContentResolver,
    private val applicationScope: CoroutineScope
) : FilesReader {

    override suspend fun getJsonFile(): String = suspendCoroutine { continuation ->
        observer.addListener(object : ActivityResultGetContentListener {
            override fun onContentRecieved(uri: Uri?) {
                observer.removeListener(this)
                if (uri != null && uri.path != null) {
                    applicationScope.launch {
                        val inputStream = contentResolver.openInputStream(uri)
                        val content = inputStream?.bufferedReader().use { it?.readText() }
                        withContext(Dispatchers.IO) {
                            inputStream?.close()
                        }
                        continuation.resume(content!!)
                    }
                } else {
                    continuation.resumeWithException(NullPointerException("Uri is null"))
                }
            }
        })
        observer.selectJson()
    }
}