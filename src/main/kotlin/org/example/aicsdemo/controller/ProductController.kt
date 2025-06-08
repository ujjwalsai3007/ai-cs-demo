package org.example.aicsdemo.controller

import org.example.aicsdemo.model.Product
import org.example.aicsdemo.service.ProductService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.*
import java.math.BigDecimal

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
    fun deleteProduct(@PathVariable id: Long, model: Model): String {
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
} 