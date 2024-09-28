package com.store.controllers

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.readValue
import com.store.dtos.ProductDetailsDto
import com.store.dtos.ProductType
import com.store.services.ProductService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.net.URI


@RestController
class ProductController(private val productService: ProductService) {
    @GetMapping("/products")
    fun getProducts(@RequestParam(required = false) type: ProductType?): ResponseEntity<List<ProductDetailsDto>> {
        if (type != null)
            return ResponseEntity(productService.getProductsByType(type), HttpStatus.OK)

        return ResponseEntity(productService.getAllProducts(), HttpStatus.OK)
    }

    @PostMapping("/products")
    fun addProduct(request: HttpServletRequest): ResponseEntity<Map<String, Int>> {
        val wrappedRequest = request.getAttribute("wrappedRequest") as HttpServletRequest
        val inputStream = wrappedRequest.inputStream

        val productDetailsDto = ObjectMapper().readValue<ProductDetailsDto>(inputStream)
        val addedProductId = productService.addProduct(productDetailsDto)

        return ResponseEntity.created(URI("/product/$addedProductId")).body(mapOf("id" to addedProductId))
    }
}
