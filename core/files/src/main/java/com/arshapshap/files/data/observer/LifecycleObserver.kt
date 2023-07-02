package com.arshapshap.files.data.observer

import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.ActivityResultRegistry
import androidx.activity.result.contract.ActivityResultContracts
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

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

    private val listeners = mutableListOf<ActivityResultListener>()

    lateinit var getContent: ActivityResultLauncher<String>

    override fun onCreate(owner: LifecycleOwner) {
        getContent = registry.register(
            "key", owner, ActivityResultContracts.GetContent()
        ) { uri ->
            listeners.forEach {
                it.onGetContent(uri)
            }
        }
    }

    fun selectJson() {
        getContent.launch("application/json")
    }

    fun addListener(listener: ActivityResultListener) {
        listeners.add(listener)
    }

    fun removeListener(listener: ActivityResultListener) {
        listeners.remove(listener)
    }
}