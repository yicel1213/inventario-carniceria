package com.example.data.db

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.CategoryEntity
import com.example.data.model.CorteEntity
import com.example.data.model.ProductEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY categoria ASC, nombre ASC")
    fun getAllProducts(): Flow<List<ProductEntity>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): ProductEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: ProductEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<ProductEntity>)

    @Update
    suspend fun updateProduct(product: ProductEntity)

    @Delete
    suspend fun deleteProduct(product: ProductEntity)

    @Query("DELETE FROM products WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM products WHERE categoria = :categoria")
    suspend fun deleteByCategory(categoria: String)

    @Query("UPDATE products SET stockCamara = :camara, stockMural = :mural, updatedAt = :time WHERE id = :id")
    suspend fun updateStock(id: Long, camara: Int, mural: Int, time: Long = System.currentTimeMillis())
}

@Dao
interface CategoryDao {
    @Query("SELECT * FROM categories ORDER BY nombre ASC")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCategory(category: CategoryEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(categories: List<CategoryEntity>)

    @Delete
    suspend fun deleteCategory(category: CategoryEntity)

    @Query("DELETE FROM categories WHERE nombre = :nombre")
    suspend fun deleteByNombre(nombre: String)
}

@Dao
interface CorteDao {
    @Query("SELECT * FROM cortes ORDER BY nombre ASC")
    fun getAllCortes(): Flow<List<CorteEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCorte(corte: CorteEntity): Long

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertAll(cortes: List<CorteEntity>)

    @Delete
    suspend fun deleteCorte(corte: CorteEntity)
}
