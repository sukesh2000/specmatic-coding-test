package com.store.services

import com.store.dtos.ProductDetailsDto
import com.store.dtos.ProductType
import com.store.repositories.ProductRepository
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.mockito.Mockito.*

class ProductServiceTest {

    private lateinit var productService: ProductService
    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setUp() {
        productRepository = mock(ProductRepository::class.java)
        productService = ProductService(productRepository)
    }

    @Test
    fun shouldGetAllProductsFromRepository() {
        val products = listOf(
            ProductDetailsDto(id = 1, name = "Product 1", type = ProductType.book, inventory = 10),
            ProductDetailsDto(id = 2, name = "Product 2", type = ProductType.gadget, inventory = 5)
        )
        `when`(productRepository.findAll()).thenReturn(products)

        val result = productService.getAllProducts()

        assertEquals(products, result)
        verify(productRepository, times(1)).findAll()
    }

    @Test
    fun shouldGetProductByIdFromRepository() {
        val product = ProductDetailsDto(id = 3, name = "Product 3", type = ProductType.food, inventory = 20)
        `when`(productRepository.findById(3)).thenReturn(product)

        val result = productService.getProductById(3)

        assertEquals(product, result)
        verify(productRepository, times(1)).findById(3)
    }

    @Test
    fun shouldGetProductsByTypeFromRepository() {
        val products = listOf(
            ProductDetailsDto(id = 1, name = "Product 1", type = ProductType.book, inventory = 10),
            ProductDetailsDto(id = 2, name = "Product 2", type = ProductType.gadget, inventory = 5),
            ProductDetailsDto(id = 3, name = "Product 3", type = ProductType.book, inventory = 8)
        )
        `when`(productRepository.findAll()).thenReturn(products)

        val result = productService.getProductsByType(ProductType.book)

        assertEquals(2, result.size)
        assertTrue(result.contains(products[0]))
        assertTrue(result.contains(products[2]))
        verify(productRepository, times(1)).findAll()
    }

    @Test
    fun shouldAddProductUsingRepository() {
        val product = ProductDetailsDto(name = "New Product", type = ProductType.other, inventory = 30)
        `when`(productRepository.save(product)).thenReturn(4)

        val savedId = productService.addProduct(product)

        assertEquals(4, savedId)
        verify(productRepository, times(1)).save(product)
    }

    @Test
    fun shouldReturnNullWhenGettingProductByIdAndProductDoesNotExist() {
        `when`(productRepository.findById(99)).thenReturn(null)

        val result = productService.getProductById(99)

        assertNull(result)
        verify(productRepository, times(1)).findById(99)
    }

    @Test
    fun shouldReturnEmptyListWhenGettingProductsByTypeAndNoMatchingProductsExist() {
        `when`(productRepository.findAll()).thenReturn(emptyList())

        val result = productService.getProductsByType(ProductType.food)

        assertTrue(result.isEmpty())
        verify(productRepository, times(1)).findAll()
    }
}