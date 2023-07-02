package com.arshapshap.files.data.observer

import android.net.Uri

interface ActivityResultListener {

    fun onGetContent(uri: Uri?)
}