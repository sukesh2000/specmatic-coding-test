package com.store.services

import com.store.dtos.ProductDetailsDto
import com.store.dtos.ProductType
import com.store.repositories.ProductRepository
import org.springframework.stereotype.Service

@Service
class ProductService(private val productRepository: ProductRepository) {
    fun getAllProducts() = productRepository.findAll()

    fun getProductById(id: Int) = productRepository.findById(id)

    fun getProductsByType(productType: ProductType) =
        productRepository.findAll().filter { productDetailsDto -> productDetailsDto.type.equals(productType) }

    fun addProduct(productDetailsDto: ProductDetailsDto) = productRepository.save(productDetailsDto)
}