package com.arshapshap.common.di

import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.SavedStateHandle
import com.arshapshap.common.base.BaseViewModel
import com.arshapshap.common.viewmodel.ViewModelFactory

inline fun <reified T : BaseViewModel> Fragment.lazyViewModel(
        noinline create: (stateHandle: SavedStateHandle) -> T
) = viewModels<T> {
    ViewModelFactory(this, create)
}