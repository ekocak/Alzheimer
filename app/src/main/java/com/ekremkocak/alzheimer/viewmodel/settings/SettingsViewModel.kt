package com.ekremkocak.alzheimer.viewmodel.settings

import android.view.View
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekremkocak.alzheimer.util.PrefHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val prefHelper: PrefHelper
) : ViewModel() {

    private val _trackingEnabled = MutableLiveData<Boolean>()
    val trackingEnabled: LiveData<Boolean> = _trackingEnabled

    init {
        _trackingEnabled.value = prefHelper.isTrackingEnabled()
    }

    fun onSwitchChecked(view: View, isChecked: Boolean) {
        _trackingEnabled.value = isChecked
        viewModelScope.launch(Dispatchers.IO) {
            prefHelper.saveTrackingEnabled(isChecked)
        }
    }
}