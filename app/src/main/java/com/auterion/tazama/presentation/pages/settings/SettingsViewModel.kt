package com.auterion.tazama.presentation.pages.settings

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor() : ViewModel() {
    var _fakeVehiclePosition = MutableStateFlow<Boolean>(false)
    var fakeVehiclePosition = _fakeVehiclePosition.asStateFlow()

    fun setFakeVehiclePosition(fake : Boolean) {
        _fakeVehiclePosition.value = fake
    }
}