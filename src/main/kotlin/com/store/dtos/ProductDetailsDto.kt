package com.store.dtos

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.annotation.JsonProperty

data class ProductDetailsDto (
    @JsonProperty("id") val id: Int? = null,
    @JsonProperty("name") val name: String,
    @JsonProperty("type") val type: ProductType,
    @JsonProperty("inventory") val inventory: Int,
    @JsonProperty("cost") @JsonInclude(JsonInclude.Include.NON_NULL) val cost: Int? = null
)