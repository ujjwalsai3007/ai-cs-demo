package org.example.aicsdemo.repository

import org.example.aicsdemo.model.Product
import org.springframework.jdbc.core.simple.JdbcClient
import org.springframework.stereotype.Repository

@Repository
class ProductRepository(
    private val jdbcClient: JdbcClient,
) {
    fun findAll(): List<Product> =
        jdbcClient
            .sql(
                """
            SELECT id, title, vendor, product_type, price, raw_data, created_at, updated_at 
            FROM products 
            ORDER BY id ASC
        """,
            ).query { rs, _ ->
                Product(
                    id = rs.getLong("id"),
                    title = rs.getString("title"),
                    vendor = rs.getString("vendor"),
                    productType = rs.getString("product_type"),
                    price = rs.getBigDecimal("price"),
                    rawData = rs.getString("raw_data"),
                    createdAt = rs.getTimestamp("created_at")?.toLocalDateTime(),
                    updatedAt = rs.getTimestamp("updated_at")?.toLocalDateTime(),
                )
            }.list()

    fun findById(id: Long): Product? =
        jdbcClient
            .sql(
                """
            SELECT id, title, vendor, product_type, price, raw_data, created_at, updated_at 
            FROM products 
            WHERE id = :id
        """,
            ).param("id", id)
            .query { rs, _ ->
                Product(
                    id = rs.getLong("id"),
                    title = rs.getString("title"),
                    vendor = rs.getString("vendor"),
                    productType = rs.getString("product_type"),
                    price = rs.getBigDecimal("price"),
                    rawData = rs.getString("raw_data"),
                    createdAt = rs.getTimestamp("created_at")?.toLocalDateTime(),
                    updatedAt = rs.getTimestamp("updated_at")?.toLocalDateTime(),
                )
            }.optional()
            .orElse(null)

    fun searchByTitle(title: String): List<Product> =
        jdbcClient
            .sql(
                """
            SELECT id, title, vendor, product_type, price, raw_data, created_at, updated_at 
            FROM products 
            WHERE title ILIKE :title 
            ORDER BY id ASC
        """,
            ).param("title", "%$title%")
            .query { rs, _ ->
                Product(
                    id = rs.getLong("id"),
                    title = rs.getString("title"),
                    vendor = rs.getString("vendor"),
                    productType = rs.getString("product_type"),
                    price = rs.getBigDecimal("price"),
                    rawData = rs.getString("raw_data"),
                    createdAt = rs.getTimestamp("created_at")?.toLocalDateTime(),
                    updatedAt = rs.getTimestamp("updated_at")?.toLocalDateTime(),
                )
            }.list()

    fun save(product: Product): Product =
        if (product.id == null) {
            // Insert new product
            jdbcClient
                .sql(
                    """
                INSERT INTO products (title, vendor, product_type, price, raw_data, created_at, updated_at) 
                VALUES (:title, :vendor, :productType, :price, :rawData, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)
            """,
                ).param("title", product.title)
                .param("vendor", product.vendor)
                .param("productType", product.productType)
                .param("price", product.price)
                .param("rawData", product.rawData)
                .update()

            // For simplicity, fetch the last inserted product
            findAll().first()
        } else {
            // Update existing product
            jdbcClient
                .sql(
                    """
                UPDATE products 
                SET title = :title, vendor = :vendor, product_type = :productType, price = :price, 
                    raw_data = :rawData, updated_at = CURRENT_TIMESTAMP 
                WHERE id = :id
            """,
                ).param("id", product.id)
                .param("title", product.title)
                .param("vendor", product.vendor)
                .param("productType", product.productType)
                .param("price", product.price)
                .param("rawData", product.rawData)
                .update()

            // Return the updated product
            findById(product.id) ?: product
        }

    fun deleteById(id: Long): Boolean {
        val rowsAffected =
            jdbcClient
                .sql("DELETE FROM products WHERE id = :id")
                .param("id", id)
                .update()
        return rowsAffected > 0
    }

    fun count(): Long =
        jdbcClient
            .sql("SELECT COUNT(*) FROM products")
            .query(Long::class.java)
            .single()
}
