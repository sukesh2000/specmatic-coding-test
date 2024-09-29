package com.store.repositories

import com.store.dtos.ProductDetailsDto
import com.store.dtos.ProductType
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test

class ProductRepositoryTest {

    private lateinit var productRepository: ProductRepository

    @BeforeEach
    fun setUp() {
        productRepository = ProductRepository()
    }

    @Test
    fun shouldReturnEmptyListWhenRepositoryIsEmpty() {
        assertTrue(productRepository.findAll().isEmpty())
    }

    @Test
    fun shouldReturnNullWhenFindingByIdAndProductDoesNotExist() {
        assertNull(productRepository.findById(99))
    }

    @Test
    fun shouldReturnAllProductsWhenRepositoryHasProducts() {
        val product1 = ProductDetailsDto(name = "Book 1", type = ProductType.book, inventory = 10, cost = 20)
        val product2 = ProductDetailsDto(name = "Gadget 1", type = ProductType.gadget, inventory = 5)
        productRepository.save(product1)
        productRepository.save(product2)

        val products = productRepository.findAll()
        assertEquals(2, products.size)
        assertTrue(products.contains(product1.copy(id = 1)))
        assertTrue(products.contains(product2.copy(id = 2)))
    }

    @Test
    fun shouldReturnProductWhenProductExists() {
        val product = ProductDetailsDto(name = "Food 1", type = ProductType.food, inventory = 20)
        val savedId = productRepository.save(product)

        val foundProduct = productRepository.findById(savedId)
        assertNotNull(foundProduct)
        assertEquals(savedId, foundProduct?.id)
        assertEquals("Food 1", foundProduct?.name)
    }

    @Test
    fun shouldReturnNullWhenProductDoesNotExist() {
        assertNull(productRepository.findById(1))
    }

    @Test
    fun shouldAssignIdAndSaveNewProduct() {
        val product = ProductDetailsDto(name = "Other 1", type = ProductType.other, inventory = 30)
        val savedId = productRepository.save(product)

        assertEquals(1, savedId)
        val foundProduct = productRepository.findById(savedId)
        assertNotNull(foundProduct)
        assertEquals(savedId, foundProduct?.id)
    }

    @Test
    fun shouldUpdateExistingProductWhenSaving() {
        val product = ProductDetailsDto(id = 5, name = "Gadget 2", type = ProductType.gadget, inventory = 8)
        val savedId = productRepository.save(product)

        assertEquals(5, savedId)
        val foundProduct = productRepository.findById(savedId)
        assertNotNull(foundProduct)
        assertEquals(5, foundProduct?.id)
        assertEquals("Gadget 2", foundProduct?.name)
    }

    @Test
    fun shouldAutoGenerateIdForNewProducts() {
        val product1 = ProductDetailsDto(name = "Book 2", type = ProductType.book, inventory = 15)
        val product2 = ProductDetailsDto(name = "Food 2", type = ProductType.food, inventory = 25)
        val savedId1 = productRepository.save(product1)
        val savedId2 = productRepository.save(product2)

        assertEquals(1, savedId1)
        assertEquals(2, savedId2)
    }
}