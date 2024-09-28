package com.store.dtos

import com.fasterxml.jackson.databind.annotation.JsonDeserialize
import com.store.serialization.StringOnlyDeserializer
import jakarta.validation.constraints.Max
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.NotNull

data class ProductDetailsDto(
    val id: Int? = null,

    @field:NotBlank(message = "Name is required")
    @JsonDeserialize(using = StringOnlyDeserializer::class)
    val name: String,

    @field:NotNull(message = "Type is required")
    val type: ProductType,

    @field:NotNull(message = "Inventory is required")
    @field:Min(value = 1, message = "Inventory value must be at least 0")
    @field:Max(value = 9999, message = "Inventory value must be at most 120")
    val inventory: Int?
)