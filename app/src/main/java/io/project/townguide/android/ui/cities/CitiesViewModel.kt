package io.project.townguide.android.ui.cities

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.townguide.android.data.network.api.ApiClient
import io.project.townguide.android.data.network.dto.CityResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class CitiesViewModel : ViewModel() {

    private val _cities = MutableStateFlow<List<CityResponse>>(emptyList())
    val cities = _cities.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    init {
        loadCities()
    }

    private fun loadCities() {
        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                _cities.value = ApiClient.cityApi.getCities()
            } catch (e: Exception) {
                _error.value = "Не удалось загрузить города"
            } finally {
                _loading.value = false
            }
        }
    }
}
