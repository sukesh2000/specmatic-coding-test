package com.store.dtos

data class ProductDetails(
    val id: Int? = null,
    val name: String,
    val type: ProductType,
    val inventory: Int
)