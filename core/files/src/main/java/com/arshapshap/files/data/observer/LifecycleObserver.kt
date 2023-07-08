package com.arshapshap.files.data.observer

import android.content.Intent
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import com.arshapshap.files.data.observer.listeners.ActivityResultCreateDocumentListener
import com.arshapshap.files.data.observer.listeners.ActivityResultGetContentListener

class LifecycleObserver(
    private val registry: ActivityResultRegistry
) : DefaultLifecycleObserver {

    class Provider {

        private var observer: LifecycleObserver? = null

        fun create(registry: ActivityResultRegistry): LifecycleObserver {
            return LifecycleObserver(registry)
        }

        fun attachObserver(observer: LifecycleObserver) {
            this.observer = observer
        }

        fun get(): LifecycleObserver {
            return observer ?: throw java.lang.IllegalStateException("LifecycleObserver must be attached")
        }

        fun detachObserver() {
            this.observer = null
        }
    }

    private val getContentListeners = mutableListOf<ActivityResultGetContentListener>()
    private val createDocumentListeners = mutableListOf<ActivityResultCreateDocumentListener>()

    private lateinit var getContent: ActivityResultLauncher<String>
    private lateinit var createDocument: ActivityResultLauncher<Intent>

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register(
            "key_get_content", owner, ActivityResultContracts.GetContent()
        ) { uri ->
            getContentListeners.forEach {
                it.onContentRecieved(uri)
            }
        }
        createDocument = registry.register(
            "key_create_document", owner, ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val uri = result.data?.data
            if (uri != null) {
                createDocumentListeners.forEach {
                    it.onDocumentCreated(uri)
                }
            }
        }
    }

    fun selectJson() {
        getContent.launch("application/json")
    }

    fun exportJson(fileName: String) {
        val intent = Intent(Intent.ACTION_CREATE_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "application/json"
            putExtra(Intent.EXTRA_TITLE, fileName)
        }

        createDocument.launch(intent)
    }

    fun addListener(listener: ActivityResultGetContentListener) {
        getContentListeners.add(listener)
    }

    fun addListener(listener: ActivityResultCreateDocumentListener) {
        createDocumentListeners.add(listener)
    }

    fun removeListener(listener: ActivityResultGetContentListener) {
        getContentListeners.remove(listener)
    }

    fun removeListener(listener: ActivityResultCreateDocumentListener) {
        createDocumentListeners.remove(listener)
    }
}