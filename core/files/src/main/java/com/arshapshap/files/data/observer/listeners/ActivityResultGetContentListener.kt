package com.arshapshap.files.data.observer.listeners

import android.net.Uri

interface ActivityResultGetContentListener {

    fun onContentRecieved(uri: Uri?)
}