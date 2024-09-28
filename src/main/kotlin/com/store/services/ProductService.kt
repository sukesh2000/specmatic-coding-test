package com.store.services

import com.store.dtos.ProductDetailsDto
import com.store.repositories.ProductRepository

class ProductService(private val productRepository: ProductRepository) {
    fun getAllProducts() = productRepository.findAll()

    fun getProductById(id: Int) = productRepository.findById(id)

    fun addProduct(productDetailsDto: ProductDetailsDto) = productRepository.save(productDetailsDto)
}