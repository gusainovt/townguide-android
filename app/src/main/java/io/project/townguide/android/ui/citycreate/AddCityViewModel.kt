package io.project.townguide.android.ui.citycreate

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import io.project.townguide.android.data.network.api.ApiClient
import io.project.townguide.android.data.network.dto.CityCreateRequest
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException

class AddCityViewModel : ViewModel() {

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _nameEng = MutableStateFlow("")
    val nameEng = _nameEng.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _loading = MutableStateFlow(false)
    val loading = _loading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error = _error.asStateFlow()

    private val _creationCompleted = MutableStateFlow(false)
    val creationCompleted = _creationCompleted.asStateFlow()

    fun onNameChanged(value: String) {
        _name.value = value
        _error.value = null
    }

    fun onNameEngChanged(value: String) {
        _nameEng.value = value
        _error.value = null
    }

    fun onDescriptionChanged(value: String) {
        _description.value = value
        _error.value = null
    }

    fun onCreateClick() {
        val request = CityCreateRequest(
            name = name.value.trim(),
            nameEng = nameEng.value.trim(),
            description = description.value.trim()
        )

        when {
            request.name.isBlank() -> {
                _error.value = "Введите название города"
                return
            }

            request.nameEng.isBlank() -> {
                _error.value = "Введите название латиницей"
                return
            }
        }

        viewModelScope.launch {
            _loading.value = true
            _error.value = null

            try {
                ApiClient.cityApi.createCity(request)
                _creationCompleted.value = true
            } catch (e: HttpException) {
                _error.value = when (e.code()) {
                    400 -> "Проверьте заполнение полей"
                    401, 403 -> "Недостаточно прав для создания города"
                    else -> "Ошибка сервера (${e.code()})"
                }
            } catch (e: IOException) {
                _error.value = "Не удалось подключиться к backend API"
            } catch (e: Exception) {
                _error.value = e.message ?: "Не удалось создать город"
            } finally {
                _loading.value = false
            }
        }
    }
}
