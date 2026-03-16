package io.project.townguide.android.ui.placecreate

import android.content.ContentResolver
import android.net.Uri
import android.provider.OpenableColumns
import android.webkit.MimeTypeMap
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.gson.Gson
import io.project.townguide.android.TownguideApp
import io.project.townguide.android.data.network.ApiErrorMessageExtractor
import io.project.townguide.android.data.network.api.ApiClient
import io.project.townguide.android.data.network.dto.CityResponse
import io.project.townguide.android.data.network.dto.PlaceCreateRequest
import java.io.File
import java.io.IOException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException

class AddPlaceViewModel : ViewModel() {

    private val gson = Gson()

    private val _cities = MutableStateFlow<List<CityResponse>>(emptyList())
    val cities = _cities.asStateFlow()

    private val _loadingCities = MutableStateFlow(false)
    val loadingCities = _loadingCities.asStateFlow()

    private val _selectedCity = MutableStateFlow<CityResponse?>(null)
    val selectedCity = _selectedCity.asStateFlow()

    private val _name = MutableStateFlow("")
    val name = _name.asStateFlow()

    private val _description = MutableStateFlow("")
    val description = _description.asStateFlow()

    private val _selectedImageUri = MutableStateFlow<Uri?>(null)
    val selectedImageUri = _selectedImageUri.asStateFlow()

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
                _cities.value = ApiClient.cityApi.getCities().sortedBy { it.name.lowercase() }
            } catch (e: HttpException) {
                _error.value = ApiErrorMessageExtractor.extract(
                    exception = e,
                    defaultMessage = "Не удалось загрузить список городов",
                    unauthorizedMessage = "Сессия истекла. Войдите заново.",
                    forbiddenMessage = "Недостаточно прав для просмотра городов"
                )
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
        _error.value = null
        _successMessage.value = null
    }

    fun backToCitySelection() {
        _selectedCity.value = null
        _error.value = null
        _successMessage.value = null
    }

    fun onNameChanged(value: String) {
        _name.value = value
        _error.value = null
        _successMessage.value = null
    }

    fun onDescriptionChanged(value: String) {
        _description.value = value
        _error.value = null
        _successMessage.value = null
    }

    fun onImageSelected(uri: Uri?) {
        _selectedImageUri.value = uri
        _error.value = null
        _successMessage.value = null
    }

    fun onCreatePlaceClick() {
        val city = selectedCity.value
        if (city == null) {
            _error.value = "Сначала выберите город"
            return
        }

        val placeName = name.value.trim()
        if (placeName.isBlank()) {
            _error.value = "Введите название места"
            return
        }

        val placeDescription = description.value.trim()
        if (placeDescription.isBlank()) {
            _error.value = "Введите описание места"
            return
        }

        val imageUri = selectedImageUri.value
        if (imageUri == null) {
            _error.value = "Выберите фотографию"
            return
        }

        viewModelScope.launch {
            _submitting.value = true
            _error.value = null
            _successMessage.value = null

            try {
                val dataBody = gson.toJson(
                    PlaceCreateRequest(
                        cityId = city.id,
                        name = placeName,
                        description = placeDescription
                    )
                ).toRequestBody("application/json".toMediaType())

                ApiClient.placeApi.createPlace(dataBody, buildFilePart(imageUri))

                _name.value = ""
                _description.value = ""
                _selectedImageUri.value = null
                _successMessage.value = "Место добавлено для города ${city.name}"
            } catch (e: HttpException) {
                _error.value = ApiErrorMessageExtractor.extract(
                    exception = e,
                    defaultMessage = "Ошибка сервера",
                    badRequestMessage = "Проверьте заполнение полей и формат файла",
                    unauthorizedMessage = "Сессия истекла. Войдите заново.",
                    forbiddenMessage = "Недостаточно прав для добавления места"
                )
            } catch (e: IOException) {
                _error.value = "Не удалось подключиться к backend API"
            } catch (e: Exception) {
                _error.value = e.message ?: "Не удалось создать место"
            } finally {
                _submitting.value = false
            }
        }
    }

    private fun buildFilePart(uri: Uri): MultipartBody.Part {
        val resolver = TownguideApp.appContext.contentResolver
        val mimeType = resolver.getType(uri) ?: "image/*"
        val extension = MimeTypeMap.getSingleton().getExtensionFromMimeType(mimeType)?.let { ".$it" } ?: ".jpg"
        val fileName = resolveFileName(resolver, uri) ?: "place_photo$extension"
        val tempFile = File.createTempFile("place_upload_", extension, TownguideApp.appContext.cacheDir)

        resolver.openInputStream(uri)?.use { input ->
            tempFile.outputStream().use { output -> input.copyTo(output) }
        } ?: throw IOException("Не удалось прочитать выбранный файл")

        return MultipartBody.Part.createFormData(
            "file",
            fileName,
            tempFile.asRequestBody(mimeType.toMediaType())
        )
    }

    private fun resolveFileName(resolver: ContentResolver, uri: Uri): String? {
        return resolver.query(uri, arrayOf(OpenableColumns.DISPLAY_NAME), null, null, null)?.use { cursor ->
            val nameIndex = cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
            if (nameIndex >= 0 && cursor.moveToFirst()) cursor.getString(nameIndex) else null
        }
    }
}
