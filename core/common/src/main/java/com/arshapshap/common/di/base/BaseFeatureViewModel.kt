package com.arshapshap.common.di.base

import androidx.lifecycle.ViewModel

abstract class BaseFeatureViewModel<T: BaseFeatureComponent> : ViewModel() {
    abstract val component: T
}