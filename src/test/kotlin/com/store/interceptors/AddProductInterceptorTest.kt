package com.store.interceptors

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.store.requestWrappers.InputStreamReplacingRequestWrapper
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*
import org.springframework.http.HttpMethod
import org.springframework.http.HttpStatus
import org.springframework.http.MediaType
import org.springframework.mock.web.MockHttpServletRequest
import org.springframework.mock.web.MockHttpServletResponse
import java.io.ByteArrayInputStream

class AddProductInterceptorTest {

    private val mapper = ObjectMapper()
    private val interceptor = AddProductInterceptor()

    @Test
    fun shouldDoNothingForNonPostRequests() {
        val request = mock(MockHttpServletRequest::class.java)
        val response = mock(MockHttpServletResponse::class.java)
        `when`(request.method).thenReturn(HttpMethod.GET.name())

        val result = interceptor.preHandle(request, response, Any())

        assertTrue(result)
        verify(request, never()).inputStream
        verify(response, never()).status = anyInt()
    }

    @Test
    fun shouldProcessValidPostRequest() {
        val requestBody = """
            {
                "name": "Test Product",
                "type": "book",
                "inventory": 50,
                "cost": 100
            }
        """.trimIndent()

        val request = spy(MockHttpServletRequest::class.java)
        val response = mock(MockHttpServletResponse::class.java)
        `when`(request.method).thenReturn(HttpMethod.POST.name())
        `when`(request.inputStream).thenReturn(getServeletInputStream(requestBody.toByteArray()))

        val result = interceptor.preHandle(request, response, Any())

        assertTrue(result)
        verify(response, never()).status = anyInt()

        val wrappedRequest = request.getAttribute("wrappedRequest")
        assertNotNull(wrappedRequest)
        assertTrue(wrappedRequest is InputStreamReplacingRequestWrapper)
    }

    @Test
    fun shouldReturnHandleInvalidRequestForInvalidName() {
        testInvalidInput("""{"type": "gadget", "inventory": 10}""") { response -> // Missing "name"
            verifyHandleInvalidRequestResponse(response, "name")
        }

        testInvalidInput("""{"name": "", "type": "gadget", "inventory": 10}""") { response -> // Blank "name"
            verifyHandleInvalidRequestResponse(response, "name")
        }

        testInvalidInput("""{"name": 123, "type": "gadget", "inventory": 10}""") { response -> // "name" not a string
            verifyHandleInvalidRequestResponse(response, "name")
        }
    }

    @Test
    fun shouldReturnHandleInvalidRequestForInvalidType() {
        testInvalidInput("""{"name": "Product", "inventory": 10}""") { response -> // Missing "type"
            verifyHandleInvalidRequestResponse(response, "type")
        }

        testInvalidInput("""{"name": "Product", "type": "invalidType", "inventory": 10}""") { response -> // Invalid "type"
            verifyHandleInvalidRequestResponse(response, "type")
        }

        testInvalidInput("""{"name": "Product", "type": 123, "inventory": 10}""") { response -> // "type" not a string
            verifyHandleInvalidRequestResponse(response, "type")
        }
    }

    @Test
    fun shouldReturnHandleInvalidRequestForInvalidInventory() {
        testInvalidInput("""{"name": "Product", "type": "food"}""") { response -> // Missing "inventory"
            verifyHandleInvalidRequestResponse(response, "inventory")
        }

        testInvalidInput("""{"name": "Product", "type": "food", "inventory": 0}""") { response -> // "inventory" out of range (low)
            verifyHandleInvalidRequestResponse(response, "inventory")
        }

        testInvalidInput("""{"name": "Product", "type": "food", "inventory": 10000}""") { response -> // "inventory" out of range (high)
            verifyHandleInvalidRequestResponse(response, "inventory")
        }

        testInvalidInput("""{"name": "Product", "type": "food", "inventory": "not a number"}""") { response -> // "inventory" not an integer
            verifyHandleInvalidRequestResponse(response, "inventory")
        }
    }

    @Test
    fun shouldReturnHandleInvalidRequestForInvalidCost() {
        testInvalidInput("""{"name": "Product", "type": "other", "inventory": 5, "cost": "not a number"}""") { response -> // "cost" not an integer
            verifyHandleInvalidRequestResponse(response, "cost")
        }

        testInvalidInput("""{"name": "Product", "type": "other", "inventory": 5, "cost": null}""") { response -> // "cost" is null
            verifyHandleInvalidRequestResponse(response, "cost")
        }
    }

    private fun getServeletInputStream(byteArray: ByteArray): ServletInputStream = object : ServletInputStream() {
        private val inputStream = ByteArrayInputStream(byteArray)
        override fun read(): Int = inputStream.read()
        override fun read(b: ByteArray, off: Int, len: Int): Int = inputStream.read(b, off, len)
        override fun isFinished(): Boolean = inputStream.available() == 0
        override fun isReady(): Boolean = true
        override fun setReadListener(readListener: ReadListener?) {}
    }

    private fun testInvalidInput(requestBody: String, verification: (MockHttpServletResponse) -> Unit) {
        val request = spy(MockHttpServletRequest::class.java)
        val response = spy(MockHttpServletResponse::class.java)
        `when`(request.method).thenReturn(HttpMethod.POST.name())
        `when`(request.inputStream).thenReturn(getServeletInputStream(requestBody.toByteArray()))

        val result = interceptor.preHandle(request, response, Any())

        assertFalse(result)
        verification(response)
    }

    private fun verifyHandleInvalidRequestResponse(response: MockHttpServletResponse, invalidField: String) {
        verify(response).status = HttpStatus.BAD_REQUEST.value()
        verify(response).contentType = MediaType.APPLICATION_JSON_VALUE
        val errorResponse = mapper.readValue<Map<String, Any>>(response.contentAsString)
        assertEquals(HttpStatus.BAD_REQUEST.value(), errorResponse["status"])
        assertEquals("$invalidField field is invalid", errorResponse["error"])
    }
}