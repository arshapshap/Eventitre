package com.arshapshap.files.data.observer.listeners

import android.net.Uri

interface ActivityResultCreateDocumentListener {

    fun onDocumentCreated(uri: Uri?)
}