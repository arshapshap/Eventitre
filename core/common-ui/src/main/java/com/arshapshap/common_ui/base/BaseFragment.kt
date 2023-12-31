package com.arshapshap.common_ui.base

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.viewbinding.ViewBinding
import com.arshapshap.common.di.base.BaseFeatureComponent
import com.arshapshap.common.di.base.BaseFeatureViewModel
import com.arshapshap.common_ui.extensions.showAlert
import com.arshapshap.common_ui.extensions.showToast

typealias Inflate<T> = (LayoutInflater, ViewGroup?, Boolean) -> T

abstract class BaseFragment<VB : ViewBinding, VM : BaseViewModel>(
    private val inflate: Inflate<VB>,
) : Fragment() {

    private var _binding: VB? = null
    protected val binding: VB get() = _binding!!

    abstract val viewModel: VM

    protected inline fun <reified VM: BaseFeatureViewModel<C>, C: BaseFeatureComponent> getFeatureComponent(): C {
        return ViewModelProvider(requireActivity())[VM::class.java].component
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = inflate.invoke(inflater, container, false)
        return binding.root
    }

    override fun onAttach(context: Context) {
        inject()
        super.onAttach(context)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
        subscribe()
    }

    abstract fun inject()

    abstract fun initViews()

    open fun subscribe() {
        viewModel.errorLiveData.observe(viewLifecycleOwner) {
            when (it.level) {
                ViewModelErrorLevel.Error ->
                    showAlert(
                        title = getString(com.arshapshap.common_ui.R.string.error),
                        message = getString(it.messageRes)
                    )
                ViewModelErrorLevel.Message ->
                    showToast(
                        message = getString(it.messageRes)
                    )
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}