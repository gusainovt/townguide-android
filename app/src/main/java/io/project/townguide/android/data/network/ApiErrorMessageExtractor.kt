package io.project.townguide.android.data.network

import com.google.gson.JsonParser
import java.io.IOException
import okhttp3.ResponseBody
import retrofit2.HttpException
import retrofit2.Response

object ApiErrorMessageExtractor {

    fun extract(
        exception: HttpException,
        defaultMessage: String,
        unauthorizedMessage: String? = null,
        forbiddenMessage: String? = null,
        badRequestMessage: String? = null
    ): String {
        val responseMessage = parseMessage(exception.response()?.errorBody())

        if (!responseMessage.isNullOrBlank()) {
            return responseMessage
        }

        return when (exception.code()) {
            400 -> badRequestMessage ?: "$defaultMessage (400)"
            401 -> unauthorizedMessage ?: "Сессия истекла. Войдите заново."
            403 -> forbiddenMessage ?: "Недостаточно прав для выполнения операции."
            else -> "$defaultMessage (${exception.code()})"
        }
    }

    fun extract(
        exception: IOException,
        defaultMessage: String = "Не удалось подключиться к backend API."
    ): String = exception.message?.takeIf { it.isNotBlank() } ?: defaultMessage

    fun extract(
        response: Response<*>,
        defaultMessage: String
    ): String {
        val responseMessage = parseMessage(response.errorBody())
        return responseMessage ?: "$defaultMessage (${response.code()})"
    }

    private fun parseMessage(errorBody: ResponseBody?): String? {
        return errorBody?.string()?.let(::parseMessage)
    }

    private fun parseMessage(rawBody: String): String? {
        return runCatching {
            val json = JsonParser.parseString(rawBody)
            when {
                json.isJsonObject -> {
                    val obj = json.asJsonObject
                    listOf("message", "error", "details", "detail", "description")
                        .firstNotNullOfOrNull { key ->
                            obj.get(key)
                                ?.takeIf { it.isJsonPrimitive }
                                ?.asString
                                ?.trim()
                                ?.takeIf { it.isNotBlank() }
                        }
                }

                json.isJsonPrimitive -> json.asString.trim().takeIf { it.isNotBlank() }
                else -> null
            }
        }.getOrNull()
    }
}
