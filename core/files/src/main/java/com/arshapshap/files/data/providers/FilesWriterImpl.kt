package com.arshapshap.files.data.providers

import android.content.ContentResolver
import android.net.Uri
import com.arshapshap.files.data.observer.LifecycleObserver
import com.arshapshap.files.data.observer.listeners.ActivityResultCreateDocumentListener
import com.arshapshap.files.domain.FilesWriter
import javax.inject.Inject
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class FilesWriterImpl @Inject constructor(
    private val observer: LifecycleObserver,
    private val contentResolver: ContentResolver
) : FilesWriter {

    override suspend fun createJson(json: String) = suspendCoroutine { continuation ->
        observer.addListener(object : ActivityResultCreateDocumentListener {
            override fun onDocumentCreated(uri: Uri?) {
                observer.removeListener(this)
                if (uri != null && uri.path != null) {
                    val outputStream = contentResolver.openOutputStream(uri)
                    outputStream?.write(json.toByteArray())
                    outputStream?.close()
                    continuation.resume(Unit)
                }
                else {
                    continuation.resumeWithException(NullPointerException("Uri is null"))
                }
            }
        })
        observer.exportJson()
    }
}