package com.ekremkocak.alzheimer.viewmodel.home

import android.location.Address
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.ekremkocak.alzheimer.data.model.LocationEntity
import com.ekremkocak.alzheimer.data.repository.LocationRepository
import com.ekremkocak.alzheimer.data.sealed.FlowState
import com.ekremkocak.alzheimer.util.GeocoderHelper
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val locationRepository: LocationRepository,
    private val geocoderHelper: GeocoderHelper,
): ViewModel() {

    private val _locationsState = MutableStateFlow<FlowState<List<LocationEntity>>>(FlowState.Loading)
    val locationsState: StateFlow<FlowState<List<LocationEntity>>> = _locationsState

    private val _address = MutableStateFlow<List<Address>?>(null)
    val address: StateFlow<List<Address>?> = _address


    fun fetchLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            _locationsState.value = FlowState.Loading
            try {
                locationRepository.getLocations().collect(){ locations ->
                    _locationsState.value = FlowState.Success(locations)
                }
            } catch (e: Exception) {
                _locationsState.value = FlowState.Error(e)
            }
        }
    }

    fun clearMarks(){
        val exceptionHandler = CoroutineExceptionHandler {_, exception ->
            println("CoroutineExceptionHandler caught $exception")
        }
        viewModelScope.launch(exceptionHandler + Dispatchers.IO) {
            locationRepository.softDeleteMarks()
        }
    }


    fun getAddressFromGeocoder( lat: Double, lng: Double) {

        viewModelScope.launch {
            try {
                val address = geocoderHelper.getAddress(lat, lng)
                _address.value = address
            } catch (e: Exception) {
                _address.value = null
            }
        }


    }


}