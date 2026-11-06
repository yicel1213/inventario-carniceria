package com.example.data.repository

import com.example.data.db.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.CorteEntity
import com.example.data.model.FechaLote
import com.example.data.model.ProductEntity
import com.example.util.DateUtils
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first

class StockRepository(private val db: AppDatabase) {
    private val productDao = db.productDao()
    private val categoryDao = db.categoryDao()
    private val corteDao = db.corteDao()

    val allProducts: Flow<List<ProductEntity>> = productDao.getAllProducts()
    val allCategories: Flow<List<CategoryEntity>> = categoryDao.getAllCategories()
    val allCortes: Flow<List<CorteEntity>> = corteDao.getAllCortes()

    suspend fun initializeSeedDataIfNeeded() {
        val existingCategories = categoryDao.getAllCategories().first()
        if (existingCategories.isEmpty()) {
            val defaultCats = listOf("Farmer", "Pampa Verde", "El Origen", "Campo Osorno")
            categoryDao.insertAll(defaultCats.map { CategoryEntity(nombre = it) })
        }

        val existingCortes = corteDao.getAllCortes().first()
        if (existingCortes.isEmpty()) {
            val defaultCortes = listOf(
                "Lomo Vetado",
                "Lomo Liso",
                "Asado Carnicero",
                "Sobrecostilla",
                "Punta Ganso",
                "Entraña",
                "Costillar",
                "Huachalomo",
                "Abastero"
            )
            corteDao.insertAll(defaultCortes.map { CorteEntity(nombre = it) })
        }

        val existingProducts = productDao.getAllProducts().first()
        if (existingProducts.isEmpty()) {
            val sampleProducts = listOf(
                ProductEntity(
                    categoria = "Farmer",
                    nombre = "Lomo Vetado",
                    stockCamara = 8,
                    stockMural = 4,
                    fechas = listOf(
                        FechaLote(
                            vencimiento = DateUtils.addDaysToToday(2),
                            retiroDias = 2,
                            alertaDias = 4,
                            loteCode = "LT-041",
                            cajas = 4
                        ),
                        FechaLote(
                            vencimiento = DateUtils.addDaysToToday(7),
                            retiroDias = 2,
                            alertaDias = 3,
                            loteCode = "LT-058",
                            cajas = 8
                        )
                    )
                ),
                ProductEntity(
                    categoria = "Pampa Verde",
                    nombre = "Sobrecostilla",
                    stockCamara = 12,
                    stockMural = 3,
                    fechas = listOf(
                        FechaLote(
                            vencimiento = DateUtils.addDaysToToday(1),
                            retiroDias = 2,
                            alertaDias = 3,
                            loteCode = "LT-033",
                            cajas = 3
                        ),
                        FechaLote(
                            vencimiento = DateUtils.addDaysToToday(8),
                            retiroDias = 2,
                            alertaDias = 3,
                            loteCode = "LT-060",
                            cajas = 12
                        )
                    )
                ),
                ProductEntity(
                    categoria = "Campo Osorno",
                    nombre = "Punta Ganso",
                    stockCamara = 6,
                    stockMural = 2,
                    fechas = listOf(
                        FechaLote(
                            vencimiento = DateUtils.addDaysToToday(0),
                            retiroDias = 2,
                            alertaDias = 3,
                            loteCode = "LT-029",
                            cajas = 2
                        ),
                        FechaLote(
                            vencimiento = DateUtils.addDaysToToday(5),
                            retiroDias = 2,
                            alertaDias = 3,
                            loteCode = "LT-052",
                            cajas = 6
                        )
                    )
                ),
                ProductEntity(
                    categoria = "El Origen",
                    nombre = "Asado Carnicero",
                    stockCamara = 10,
                    stockMural = 5,
                    fechas = listOf(
                        FechaLote(
                            vencimiento = DateUtils.addDaysToToday(4),
                            retiroDias = 2,
                            alertaDias = 3,
                            loteCode = "LT-049",
                            cajas = 5
                        )
                    )
                ),
                ProductEntity(
                    categoria = "Farmer",
                    nombre = "Lomo Liso",
                    stockCamara = 7,
                    stockMural = 3,
                    fechas = listOf(
                        FechaLote(
                            vencimiento = DateUtils.addDaysToToday(6),
                            retiroDias = 2,
                            alertaDias = 3,
                            loteCode = "LT-065",
                            cajas = 10
                        )
                    )
                )
            )
            productDao.insertAll(sampleProducts)
        }
    }

    suspend fun saveProduct(product: ProductEntity): Long {
        return productDao.insertProduct(product)
    }

    suspend fun updateProduct(product: ProductEntity) {
        productDao.updateProduct(product)
    }

    suspend fun deleteProduct(id: Long) {
        productDao.deleteById(id)
    }

    suspend fun deleteCategory(categoryName: String) {
        categoryDao.deleteByNombre(categoryName)
        productDao.deleteByCategory(categoryName)
    }

    suspend fun addCategory(nombre: String) {
        val trimmed = nombre.trim()
        if (trimmed.isNotBlank()) {
            categoryDao.insertCategory(CategoryEntity(nombre = trimmed))
        }
    }

    suspend fun addCorte(nombre: String) {
        val trimmed = nombre.trim()
        if (trimmed.isNotBlank()) {
            corteDao.insertCorte(CorteEntity(nombre = trimmed))
        }
    }

    suspend fun updateStock(productId: Long, camara: Int, mural: Int) {
        val clampedCamara = camara.coerceAtLeast(0)
        val clampedMural = mural.coerceAtLeast(0)
        productDao.updateStock(productId, clampedCamara, clampedMural)
    }

    suspend fun transferCamaraToMural(productId: Long, boxesToTransfer: Int) {
        val product = productDao.getProductById(productId) ?: return
        if (product.stockCamara <= 0 || boxesToTransfer <= 0) return
        val transferQty = boxesToTransfer.coerceAtMost(product.stockCamara)
        val newCamara = product.stockCamara - transferQty
        val newMural = product.stockMural + transferQty
        productDao.updateStock(productId, newCamara, newMural)
    }
}
