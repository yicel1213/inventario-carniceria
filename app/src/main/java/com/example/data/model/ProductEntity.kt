package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class ProductEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val categoria: String,
    val nombre: String,
    val stockCamara: Int = 0,
    val stockMural: Int = 0,
    val fechas: List<FechaLote> = emptyList(),
    val updatedAt: Long = System.currentTimeMillis()
)

data class FechaLote(
    val vencimiento: String, // Format: YYYY-MM-DD
    val retiroDias: Int = 2,
    val alertaDias: Int = 3,
    val loteCode: String? = null,
    val cajas: Int = 0
)

@Entity(tableName = "categories")
data class CategoryEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String
)

@Entity(tableName = "cortes")
data class CorteEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val nombre: String
)

enum class ExpirationStatus {
    VENCIDO,
    RETIRAR,
    ALERTA,
    EN_REGLA
}

data class LotAlertItem(
    val productId: Long,
    val productName: String,
    val category: String,
    val stockCamara: Int,
    val stockMural: Int,
    val fecha: FechaLote,
    val daysRemaining: Int,
    val status: ExpirationStatus
)
