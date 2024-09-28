package com.store.interceptors

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.store.dtos.ProductType
import com.store.requestWrappers.InputStreamReplacingRequestWrapper
import jakarta.servlet.*
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.apache.commons.io.input.TeeInputStream
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.web.servlet.HandlerInterceptor
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.IOException
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter

class AddProductInterceptor : HandlerInterceptor {
    @Throws(IOException::class, ServletException::class)
    override fun preHandle(request: HttpServletRequest, response: HttpServletResponse, handler: Any): Boolean {

        if (request.method.equals(HttpMethod.POST.name())){
            val mapper = ObjectMapper()

            val originalInputStream = request.inputStream
            val byteArrayOutputStream = ByteArrayOutputStream()

            val teeInputStream = TeeInputStream(originalInputStream, byteArrayOutputStream)

            val data = mapper.readValue<Map<String, Any?>>(teeInputStream)
            if (!valid(data, response, mapper))
                return false

            val newInputStream = ByteArrayInputStream(byteArrayOutputStream.toByteArray())

            val wrappedRequest = InputStreamReplacingRequestWrapper(request, newInputStream)
            request.setAttribute("wrappedRequest", wrappedRequest)
        }
        return true
    }

    private fun valid(data: Map<String, Any?>, response: HttpServletResponse, mapper: ObjectMapper): Boolean {
        if (isMissingOrNull(data, "name") || data["name"] !is String || (data["name"] as String).isBlank()) {
            badRequest("name", response, mapper)
            return false
        }

        if (isMissingOrNull(data, "type") || try {
                enumValueOf<ProductType>(data["type"].toString())
                false
        } catch (e: IllegalArgumentException) {
            true }) {
            badRequest("type", response, mapper)
            return false
        }

        if (isMissingOrNull(data, "inventory") ||
            data["inventory"] !is Int ||
            (data["inventory"] as Int) !in 1..9999) {
            badRequest("inventory", response, mapper)
            return false
        }

        if (data.containsKey("cost") && (data["cost"] == null || data["cost"] !is Int)) {
            badRequest("cost", response, mapper)
            return false
        }

        return true
    }

    private fun isMissingOrNull(data: Map<String, Any?>, requestField: String) =
        !data.containsKey(requestField) || data[requestField] == null

    private fun badRequest(responseField: String, response: HttpServletResponse, mapper: ObjectMapper) {
        response.status = HttpStatus.BAD_REQUEST.value()
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        val formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS")
        val responseBody = mapOf(
            "timestamp" to (LocalDateTime.now().format(formatter) + ZoneOffset.of("+00:00")),
            "status" to HttpStatus.BAD_REQUEST.value(),
            "error" to "$responseField field is invalid",
            "path" to "/products"
        )

        mapper.writeValue(response.writer, responseBody)
    }
}
