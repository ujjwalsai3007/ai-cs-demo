package org.example.aicsdemo.service

import com.fasterxml.jackson.databind.ObjectMapper
import org.example.aicsdemo.model.Product
import org.example.aicsdemo.model.ProductApiResponse
import org.example.aicsdemo.repository.ProductRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.math.BigDecimal

@Service
class ProductService(
    private val productRepository: ProductRepository,
    private val restTemplate: RestTemplate = RestTemplate(),
    private val objectMapper: ObjectMapper
) {
    
    private val logger = LoggerFactory.getLogger(ProductService::class.java)
    
    fun getAllProducts(): List<Product> {
        return productRepository.findAll()
    }
    
    fun getProductById(id: Long): Product? {
        return productRepository.findById(id)
    }
    
    fun searchProducts(title: String): List<Product> {
        return productRepository.searchByTitle(title)
    }
    
    fun saveProduct(product: Product): Product {
        return productRepository.save(product)
    }
    
    fun deleteProduct(id: Long): Boolean {
        return productRepository.deleteById(id)
    }
    
    @Scheduled(initialDelay = 0, fixedDelay = 3600000) // Run immediately, then every hour
    fun fetchAndSaveProducts() {
        try {
            logger.info("Starting scheduled product fetch from external API...")
            
            val currentCount = productRepository.count()
            if (currentCount >= 50) {
                logger.info("Already have $currentCount products, skipping fetch")
                return
            }
            
            val response = restTemplate.getForObject(
                "https://famme.no/products.json",
                ProductApiResponse::class.java
            )
            
            response?.let {
                val productsToSave = it.products.take(50 - currentCount.toInt())
                logger.info("Fetched ${it.products.size} products, saving ${productsToSave.size}")
                
                productsToSave.forEach { productApi ->
                    try {
                        // Get the first variant's price if available
                        val price = productApi.variants?.firstOrNull()?.price?.let { priceStr ->
                            try {
                                BigDecimal(priceStr)
                            } catch (e: NumberFormatException) {
                                null
                            }
                        }
                        
                        val product = Product(
                            title = productApi.title,
                            vendor = productApi.vendor,
                            productType = productApi.productType,
                            price = price,
                            rawData = objectMapper.writeValueAsString(productApi)
                        )
                        
                        productRepository.save(product)
                        logger.debug("Saved product: ${product.title}")
                    } catch (e: Exception) {
                        logger.error("Error saving product ${productApi.title}: ${e.message}")
                    }
                }
                
                logger.info("Completed product fetch. Total products in database: ${productRepository.count()}")
            }
        } catch (e: Exception) {
            logger.error("Error fetching products from external API: ${e.message}", e)
        }
    }
} 