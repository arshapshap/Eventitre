package com.arshapshap.common_ui.base

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

abstract class BaseViewModel : ViewModel() {

    protected val _errorLiveData = MutableLiveData<ViewModelError>()
    val errorLiveData: LiveData<ViewModelError>
        get() = _errorLiveData

    protected val _loadingLiveData = MutableLiveData<Boolean>(true)
    val loadingLiveData: LiveData<Boolean>
        get() = _loadingLiveData
}