package io.project.townguide.android.ui.storycreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.townguide.android.data.network.api.ApiClient
import io.project.townguide.android.data.network.dto.CityResponse
import io.project.townguide.android.data.network.dto.StoryCreateRequest
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AddStoryViewModel : ViewModel() {

    private val _cities = MutableStateFlow<List<CityResponse>>(emptyList())
    val cities = _cities.asStateFlow()

    private val _loadingCities = MutableStateFlow(false)
    val loadingCities = _loadingCities.asStateFlow()

    private val _selectedCity = MutableStateFlow<CityResponse?>(null)
    val selectedCity = _selectedCity.asStateFlow()

    private val _body = MutableStateFlow("")
    val body = _body.asStateFlow()

    private val _submitting = MutableStateFlow(false)
    val submitting = _submitting.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _successMessage = MutableStateFlow<String?>(null)
    val successMessage = _successMessage.asStateFlow()

    init {
        loadCities()
    }

    fun loadCities() {
        viewModelScope.launch {
            _loadingCities.value = true
            _error.value = null

            try {
                _cities.value = ApiClient.cityApi.getCities()
                    .sortedBy { it.name.lowercase() }
            } catch (e: IOException) {
                _error.value = "Не удалось загрузить список городов"
            } catch (e: Exception) {
                _error.value = e.message ?: "Не удалось загрузить список городов"
            } finally {
                _loadingCities.value = false
            }
        }
    }

    fun selectCity(city: CityResponse) {
        _selectedCity.value = city
        _body.value = ""
        _error.value = null
        _successMessage.value = null
    }

    fun backToCitySelection() {
        _selectedCity.value = null
        _body.value = ""
        _error.value = null
        _successMessage.value = null
    }

    fun onBodyChanged(value: String) {
        _body.value = value
        _error.value = null
        _successMessage.value = null
    }

    fun onCreateStoryClick() {
        val city = selectedCity.value
        if (city == null) {
            _error.value = "Сначала выберите город"
            return
        }

        val storyBody = body.value.trim()
        if (storyBody.isBlank()) {
            _error.value = "Введите текст истории"
            return
        }

        viewModelScope.launch {
            _submitting.value = true
            _error.value = null
            _successMessage.value = null

            try {
                ApiClient.storiesApi.createStory(
                    StoryCreateRequest(
                        cityId = city.id,
                        body = storyBody
                    )
                )
                _body.value = ""
                _successMessage.value = "История добавлена для города ${city.name}"
            } catch (e: HttpException) {
                _error.value = when (e.code()) {
                    400 -> "Проверьте текст истории"
                    401, 403 -> "Недостаточно прав для добавления истории"
                    else -> "Ошибка сервера (${e.code()})"
                }
            } catch (e: IOException) {
                _error.value = "Не удалось подключиться к backend API"
            } catch (e: Exception) {
                _error.value = e.message ?: "Не удалось создать историю"
            } finally {
                _submitting.value = false
            }
        }
    }
}
