package com.arshapshap.files.data.providers

import android.content.ContentResolver
import android.net.Uri
import com.arshapshap.files.data.observer.ActivityResultListener
import com.arshapshap.files.data.observer.LifecycleObserver
import com.arshapshap.files.domain.FilesProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

internal class FilesProviderImpl @Inject constructor(
    private val observer: LifecycleObserver,
    private val contentResolver: ContentResolver,
    private val applicationScope: CoroutineScope
) : FilesProvider, ActivityResultListener {

    private var callback: (suspend (String) -> Unit)? = null

    override suspend fun getJsonFile(callback: suspend (String) -> Unit) {
        this.callback = callback
        observer.addListener(this)
        observer.selectJson()
    }

    override fun onGetContent(uri: Uri?) {
        observer.removeListener(this)
        val callback = callback!!
        this.callback = null
        if (uri != null && uri.path != null) {
            applicationScope.launch {
                val inputStream = contentResolver.openInputStream(uri)
                val content = inputStream?.bufferedReader().use { it?.readText() }
                withContext(Dispatchers.IO) {
                    inputStream?.close()
                }
                callback.invoke(content!!)
            }
        }
    }
}