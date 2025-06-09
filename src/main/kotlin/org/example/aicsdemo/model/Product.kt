package org.example.aicsdemo.model

import java.math.BigDecimal
import java.time.LocalDateTime

data class Product(
    val id: Long? = null,
    val title: String,
    val vendor: String? = null,
    val productType: String? = null,
    val price: BigDecimal? = null,
    val rawData: String? = null, // JSONB stored as string
    val createdAt: LocalDateTime? = null,
    val updatedAt: LocalDateTime? = null,
)
