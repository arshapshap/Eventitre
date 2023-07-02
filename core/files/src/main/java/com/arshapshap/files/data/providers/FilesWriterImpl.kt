package com.arshapshap.files.data.providers

import android.content.ContentResolver
import android.net.Uri
import com.arshapshap.files.data.observer.LifecycleObserver
import com.arshapshap.files.data.observer.listeners.ActivityResultCreateDocumentListener
import com.arshapshap.files.domain.FilesWriter
import javax.inject.Inject

class FilesWriterImpl @Inject constructor(
    private val observer: LifecycleObserver,
    private val contentResolver: ContentResolver
) : FilesWriter, ActivityResultCreateDocumentListener {

    private var jsonToExport: String? = null
    private var callback: (() -> Unit)? = null

    override suspend fun createJson(json: String, callback: () -> Unit) {
        this.jsonToExport = json
        this.callback = callback
        observer.addListener(this)
        observer.exportToJson()
    }

    override fun onDocumentCreated(uri: Uri?) {
        observer.removeListener(this)
        if (uri != null && uri.path != null) {
            val outputStream = contentResolver.openOutputStream(uri)
            outputStream?.write(jsonToExport!!.toByteArray())
            outputStream?.close()
            this.callback!!.invoke()
        }
        this.jsonToExport = null
        this.callback = null
    }
}