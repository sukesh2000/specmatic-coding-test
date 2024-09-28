package com.store.controllers

import com.store.dtos.ProductDetailsDto
import com.store.dtos.ProductType
import com.store.services.ProductService
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI


@RestController
class ProductController(private val productService: ProductService) {
    @GetMapping(name = "/products")
    fun getProducts(@RequestParam(required = false) type: ProductType?): ResponseEntity<List<ProductDetailsDto>> {
        if (type != null)
            return ResponseEntity(productService.getProductsByType(type), HttpStatus.OK)

        return ResponseEntity(productService.getAllProducts(), HttpStatus.OK)
    }

    @PostMapping(name = "/products")
    fun addProduct(@RequestBody productDetailsDto: ProductDetailsDto): ResponseEntity<Map<String, Int>> {
        val addedProductId = productService.addProduct(productDetailsDto)

        return ResponseEntity.created(URI("/product/$addedProductId")).body(mapOf("id" to addedProductId))
    }
}
