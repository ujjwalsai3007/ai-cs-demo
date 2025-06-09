package org.example.aicsdemo.controller

import org.example.aicsdemo.model.Product
import org.example.aicsdemo.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal
import org.springframework.http.HttpHeaders
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Controller
class ProductController(private val productService: ProductService) {

    @GetMapping("/")
    fun index(model: Model): String {
        model.addAttribute("products", emptyList<Product>())
        return "index"
    }

    @GetMapping("/products")
    fun loadProducts(): String {
        return "fragments/product-table :: table"
    }

    @PostMapping("/products")
    fun addProduct(
        @RequestParam title: String?,
        @RequestParam vendor: String?,
        @RequestParam productType: String?,
        @RequestParam price: String?,
        model: Model
    ): String {
        // Server-side validation
        if (title.isNullOrBlank()) {
            // Return current table without changes if validation fails
            model.addAttribute("allProducts", productService.getAllProducts())
            return "fragments/product-table :: table"
        }

        val priceValue = price?.takeIf { it.isNotBlank() }?.let {
            try {
                BigDecimal(it)
            } catch (e: NumberFormatException) {
                null
            }
        }

        val product = Product(
            title = title,
            vendor = vendor?.takeIf { it.isNotBlank() },
            productType = productType?.takeIf { it.isNotBlank() },
            price = priceValue
        )

        productService.saveProduct(product)
        model.addAttribute("allProducts", productService.getAllProducts())
        return "fragments/product-table :: table"
    }

    @DeleteMapping("/products/{id}")
    @ResponseBody
    fun deleteProduct(@PathVariable id: Long): Map<String, Any> {
        return try {
            val deleted = productService.deleteProduct(id)
            if (deleted) {
                mapOf("success" to true, "message" to "Product deleted successfully")
            } else {
                mapOf("success" to false, "message" to "Product not found")
            }
        } catch (e: Exception) {
            mapOf("success" to false, "message" to "Error deleting product: ${e.message}")
        }
    }

    @DeleteMapping("/products/{id}/htmx")
    fun deleteProductHtmx(@PathVariable id: Long, model: Model): String {
        productService.deleteProduct(id)
        model.addAttribute("allProducts", productService.getAllProducts())
        return "fragments/product-table :: table"
    }

    @GetMapping("/products/{id}/edit")
    fun editProduct(@PathVariable id: Long, model: Model): String {
        val product = productService.getProductById(id)
        model.addAttribute("product", product)
        return "edit-product"
    }

    @PostMapping("/products/{id}")
    fun updateProduct(
        @PathVariable id: Long,
        @RequestParam title: String,
        @RequestParam vendor: String?,
        @RequestParam productType: String?,
        @RequestParam price: String?
    ): String {
        val existingProduct = productService.getProductById(id)
        if (existingProduct != null) {
            val priceValue = price?.takeIf { it.isNotBlank() }?.let {
                try {
                    BigDecimal(it)
                } catch (e: NumberFormatException) {
                    null
                }
            }

            val updatedProduct = existingProduct.copy(
                title = title,
                vendor = vendor?.takeIf { it.isNotBlank() },
                productType = productType?.takeIf { it.isNotBlank() },
                price = priceValue
            )

            productService.saveProduct(updatedProduct)
        }
        return "redirect:/"
    }

    @GetMapping("/search")
    fun searchPage(): String {
        return "search"
    }

    @GetMapping("/search/results")
    fun searchProducts(@RequestParam q: String): String {
        return "fragments/search-results :: results"
    }

    @ModelAttribute("allProducts")
    fun allProducts(): List<Product> {
        return productService.getAllProducts()
    }

    @ModelAttribute("searchResults")
    fun searchResults(@RequestParam(required = false) q: String?): List<Product> {
        return if (q.isNullOrBlank()) {
            emptyList()
        } else {
            productService.searchProducts(q)
        }
    }

    @GetMapping("/products/export")
    fun exportProducts(): ResponseEntity<String> {
        val products = productService.getAllProducts()
        val csv = StringBuilder()
        
        // Add CSV header
        csv.append("ID,Title,Vendor,Product Type,Price,Created At\n")
        
        // Add product data
        products.forEach { product ->
            csv.append("${product.id},")
            csv.append("\"${product.title.replace("\"", "\"\"")}\",")
            csv.append("\"${product.vendor?.replace("\"", "\"\"") ?: ""}\",")
            csv.append("\"${product.productType?.replace("\"", "\"\"") ?: ""}\",")
            csv.append("${product.price ?: ""},")
            csv.append("\"${product.createdAt?.format(DateTimeFormatter.ISO_LOCAL_DATE_TIME) ?: ""}\"\n")
        }
        
        val headers = HttpHeaders()
        headers.contentType = MediaType.TEXT_PLAIN
        headers.setContentDispositionFormData("attachment", "products_${LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd_HH-mm-ss"))}.csv")
        
        return ResponseEntity.ok()
            .headers(headers)
            .body(csv.toString())
    }
} 