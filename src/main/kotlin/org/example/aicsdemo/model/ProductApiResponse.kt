package org.example.aicsdemo.model

import com.fasterxml.jackson.annotation.JsonProperty

data class ProductApiResponse(
    val products: List<ProductApi>,
)

data class ProductApi(
    val id: Long,
    val title: String,
    val vendor: String?,
    @JsonProperty("product_type")
    val productType: String?,
    val variants: List<VariantApi>?,
)

data class VariantApi(
    val id: Long,
    val title: String,
    val price: String?,
    @JsonProperty("compare_at_price")
    val compareAtPrice: String?,
)
