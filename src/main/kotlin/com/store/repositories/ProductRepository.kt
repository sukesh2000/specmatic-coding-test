package com.store.repositories

import com.store.dtos.ProductDetailsDto
import org.springframework.stereotype.Repository

@Repository
class ProductRepository {
    private val productDetailsDtoMap = mutableMapOf<Int, ProductDetailsDto>()
    private var currentId = 1

    fun findAll() = productDetailsDtoMap.values.toList()

    fun findById(id: Int) = productDetailsDtoMap[id]

    fun save(productDetailsDto: ProductDetailsDto): Int {
        val id = productDetailsDto.id ?: currentId++
        val product = productDetailsDto.copy(id = id)
        productDetailsDtoMap[id] = product
        return id
    }
}
