package com.arshapshap.settings.presentation.screens.settings

import com.arshapshap.common_ui.base.BaseFragment
import com.arshapshap.common_ui.extensions.showAlert
import com.arshapshap.common_ui.extensions.showAlertWithThreeButtons
import com.arshapshap.common_ui.extensions.showAlertWithTwoButtons
import com.arshapshap.common_ui.extensions.showToast
import com.arshapshap.common_ui.viewmodel.lazyViewModel
import com.arshapshap.settings.R
import com.arshapshap.settings.databinding.FragmentSettingsBinding
import com.arshapshap.settings.di.SettingsFeatureComponent
import com.arshapshap.settings.di.SettingsFeatureViewModel

class SettingsFragment : BaseFragment<FragmentSettingsBinding, SettingsViewModel>(
    FragmentSettingsBinding::inflate
) {

    private val component by lazy {
        getFeatureComponent<SettingsFeatureViewModel, SettingsFeatureComponent>()
    }

    override val viewModel: SettingsViewModel by lazyViewModel {
        component.settingsViewModel().create()
    }

    override fun inject() {
        component.inject(this)
    }

    override fun initViews() {
        with (binding) {
            exportButton.setOnClickListener {
                viewModel.exportEvents()
            }

            importButton.setOnClickListener {
                viewModel.requestImportEvents()
            }
        }
    }

    override fun subscribe() {
        with (viewModel) {
            eventsToImportLiveData.observe(viewLifecycleOwner) {
                if (it.list.isEmpty())
                    showAlert(
                        title = getString(R.string.import_events),
                        message = getString(R.string.no_events_to_import)
                    )
                else if (it.listWithoutConflicts.isEmpty())
                    showAlertWithTwoButtons(
                        title = getString(R.string.import_events),
                        message = getString(R.string.events_prepared_for_import_with_conflicts, it.list.size, it.list.size - it.listWithoutConflicts.size),
                        onPositiveButtonClick = viewModel::importEventsWithOverwriting
                    )
                else if (it.list.size == it.listWithoutConflicts.size)
                    showAlertWithTwoButtons(
                        title = getString(R.string.import_events),
                        message = getString(R.string.events_prepared_for_import, it.list.size),
                        onPositiveButtonClick = viewModel::importEventsWithOverwriting
                    )
                else
                    showAlertWithThreeButtons(
                        title = getString(R.string.import_events),
                        message = getString(R.string.events_prepared_for_import_with_conflicts, it.list.size, it.listWithoutConflicts.size),
                        neutralButtonText = getString(R.string.add_only_new),
                        onPositiveButtonClick = viewModel::importEventsWithOverwriting,
                        onNeutralButtonClick = viewModel::importOnlyNewEvents
                    )
            }

            importedEventsLiveData.observe(viewLifecycleOwner) {
                showToast(getString(R.string.imported_events_number, it))
            }

            exportedEventsLiveData.observe(viewLifecycleOwner) {
                showToast(getString(R.string.exported_events_number, it))
            }
        }
    }
}