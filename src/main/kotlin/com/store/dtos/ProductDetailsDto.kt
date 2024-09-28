package com.store.dtos

data class ProductDetailsDto(
    val id: Int? = null,
    val name: String,
    val type: ProductType,
    val inventory: Int
)