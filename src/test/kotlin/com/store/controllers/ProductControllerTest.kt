package com.store.controllers

import com.store.dtos.ProductDetailsDto
import com.store.dtos.ProductType
import com.store.requestWrappers.InputStreamReplacingRequestWrapper
import com.store.services.ProductService
import jakarta.servlet.ReadListener
import jakarta.servlet.ServletInputStream
import jakarta.servlet.http.HttpServletRequest
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.mockito.kotlin.any
import org.mockito.Mockito.*
import org.springframework.http.HttpStatus
import java.io.ByteArrayInputStream
import java.net.URI

class ProductControllerTest {

    private val productService = mock(ProductService::class.java)
    private val productController = ProductController(productService)

    @Test
    fun shouldGetAllProductsWhenTypeIsNull() {
        val allProducts = listOf(
            ProductDetailsDto(id = 1, name = "Product 1", type = ProductType.book, inventory = 10),
            ProductDetailsDto(id = 2, name = "Product 2", type = ProductType.gadget, inventory = 5)
        )
        `when`(productService.getAllProducts()).thenReturn(allProducts)

        val result = productController.getProducts(null)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(allProducts, result.body)
        verify(productService).getAllProducts()
        verify(productService, never()).getProductsByType(any())
    }

    @Test
    fun shouldGetProductsByTypeWhenTypeIsNotNull() {
        val bookProducts = listOf(
            ProductDetailsDto(id = 3, name = "Book 1", type = ProductType.book, inventory = 8)
        )
        `when`(productService.getProductsByType(ProductType.book)).thenReturn(bookProducts)

        val result = productController.getProducts(ProductType.book)

        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(bookProducts, result.body)
        verify(productService, never()).getAllProducts()
        verify(productService).getProductsByType(ProductType.book)
    }

    @Test
    fun shouldAddProductAndReturnCreatedResponse() {
        val requestBody = """
            {
                "name": "New Product",
                "type": "other",
                "inventory": 30
            }
        """.trimIndent()

        val request = mock(HttpServletRequest::class.java)
        val wrappedRequest = mock(InputStreamReplacingRequestWrapper::class.java)
        `when`(request.getAttribute("wrappedRequest")).thenReturn(wrappedRequest)
        `when`(wrappedRequest.inputStream).thenReturn(getServeletInputStream(requestBody.toByteArray()))
        `when`(productService.addProduct(any())).thenReturn(4) // Simulate saved ID

        val result = productController.addProduct(request)

        assertEquals(HttpStatus.CREATED, result.statusCode)
        assertEquals(URI("/product/4"), result.headers.location)
        assertEquals(mapOf("id" to 4), result.body)
        verify(productService).addProduct(any())
    }

    private fun getServeletInputStream(byteArray: ByteArray): ServletInputStream = object : ServletInputStream() {
        private val inputStream = ByteArrayInputStream(byteArray)
        override fun read(): Int = inputStream.read()
        override fun read(b: ByteArray, off: Int, len: Int): Int = inputStream.read(b, off, len)
        override fun isFinished(): Boolean = inputStream.available() == 0
        override fun isReady(): Boolean = true
        override fun setReadListener(readListener: ReadListener?) {}
    }
}