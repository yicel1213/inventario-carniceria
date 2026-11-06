package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.db.AppDatabase
import com.example.data.model.CategoryEntity
import com.example.data.model.CorteEntity
import com.example.data.model.ExpirationStatus
import com.example.data.model.FechaLote
import com.example.data.model.LotAlertItem
import com.example.data.model.ProductEntity
import com.example.data.repository.StockRepository
import com.example.util.DateUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class Screen {
    MENU,
    LISTA,
    CAMARA,
    MURAL,
    INGRESAR,
    ALERTAS
}

enum class AlertFilter {
    ALL,
    VENCIDOS,
    RETIRAR,
    ALERTA
}

class StockViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: StockRepository

    init {
        val db = AppDatabase.getInstance(application)
        repository = StockRepository(db)
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    private val _currentScreen = MutableStateFlow(Screen.MENU)
    val currentScreen: StateFlow<Screen> = _currentScreen.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedCategoryFilter = MutableStateFlow<String?>(null)
    val selectedCategoryFilter: StateFlow<String?> = _selectedCategoryFilter.asStateFlow()

    private val _alertFilter = MutableStateFlow(AlertFilter.ALL)
    val alertFilter: StateFlow<AlertFilter> = _alertFilter.asStateFlow()

    private val _snackbarMessage = MutableStateFlow<String?>(null)
    val snackbarMessage: StateFlow<String?> = _snackbarMessage.asStateFlow()

    val products: StateFlow<List<ProductEntity>> = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories: StateFlow<List<CategoryEntity>> = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val cortes: StateFlow<List<CorteEntity>> = repository.allCortes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Aggregated stats
    val totalCamaraBoxes: StateFlow<Int> = products.map { list ->
        list.sumOf { it.stockCamara }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalMuralBoxes: StateFlow<Int> = products.map { list ->
        list.sumOf { it.stockMural }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val totalBoxes: StateFlow<Int> = combine(totalCamaraBoxes, totalMuralBoxes) { c, m ->
        c + m
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    // All active lot alerts
    val allAlerts: StateFlow<List<LotAlertItem>> = products.map { list ->
        val alertItems = mutableListOf<LotAlertItem>()
        for (prod in list) {
            for (fecha in prod.fechas) {
                if (fecha.vencimiento.isNotBlank()) {
                    val days = DateUtils.daysUntil(fecha.vencimiento)
                    val status = DateUtils.getStatus(fecha)
                    // Consider alert if expired, within withdrawal days, or within alert days
                    if (status != ExpirationStatus.EN_REGLA) {
                        alertItems.add(
                            LotAlertItem(
                                productId = prod.id,
                                productName = prod.nombre,
                                category = prod.categoria,
                                stockCamara = prod.stockCamara,
                                stockMural = prod.stockMural,
                                fecha = fecha,
                                daysRemaining = days,
                                status = status
                            )
                        )
                    }
                }
            }
        }
        alertItems.sortedWith(compareBy({ it.daysRemaining }, { it.productName }))
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val urgentAlertCount: StateFlow<Int> = allAlerts.map { list ->
        list.count { it.status == ExpirationStatus.VENCIDO || it.status == ExpirationStatus.RETIRAR }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    val filteredAlerts: StateFlow<List<LotAlertItem>> = combine(allAlerts, _alertFilter) { alerts, filter ->
        when (filter) {
            AlertFilter.ALL -> alerts
            AlertFilter.VENCIDOS -> alerts.filter { it.status == ExpirationStatus.VENCIDO }
            AlertFilter.RETIRAR -> alerts.filter { it.status == ExpirationStatus.RETIRAR }
            AlertFilter.ALERTA -> alerts.filter { it.status == ExpirationStatus.ALERTA }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun navigateTo(screen: Screen) {
        _currentScreen.value = screen
    }

    fun goHome() {
        _currentScreen.value = Screen.MENU
    }

    fun setSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun setCategoryFilter(category: String?) {
        _selectedCategoryFilter.value = category
    }

    fun setAlertFilter(filter: AlertFilter) {
        _alertFilter.value = filter
    }

    fun showSnackbar(message: String) {
        _snackbarMessage.value = message
    }

    fun clearSnackbar() {
        _snackbarMessage.value = null
    }

    fun incrementCamara(productId: Long) {
        viewModelScope.launch {
            val prod = products.value.find { it.id == productId } ?: return@launch
            repository.updateStock(productId, prod.stockCamara + 1, prod.stockMural)
        }
    }

    fun decrementCamara(productId: Long) {
        viewModelScope.launch {
            val prod = products.value.find { it.id == productId } ?: return@launch
            if (prod.stockCamara > 0) {
                repository.updateStock(productId, prod.stockCamara - 1, prod.stockMural)
            }
        }
    }

    fun incrementMural(productId: Long) {
        viewModelScope.launch {
            val prod = products.value.find { it.id == productId } ?: return@launch
            repository.updateStock(productId, prod.stockCamara, prod.stockMural + 1)
        }
    }

    fun decrementMural(productId: Long) {
        viewModelScope.launch {
            val prod = products.value.find { it.id == productId } ?: return@launch
            if (prod.stockMural > 0) {
                repository.updateStock(productId, prod.stockCamara, prod.stockMural - 1)
            }
        }
    }

    fun transferCamaraToMural(productId: Long, count: Int = 1) {
        viewModelScope.launch {
            val prod = products.value.find { it.id == productId } ?: return@launch
            if (prod.stockCamara <= 0) {
                showSnackbar("No hay stock en cámara para transferir")
                return@launch
            }
            val qty = count.coerceAtMost(prod.stockCamara)
            repository.transferCamaraToMural(productId, qty)
            showSnackbar("Transferidas $qty caja(s) de Cámara a Mural (${prod.nombre})")
        }
    }

    fun saveProduct(
        categoria: String,
        nombre: String,
        stockCamara: Int,
        stockMural: Int,
        fechas: List<FechaLote>
    ) {
        viewModelScope.launch {
            val catTrimmed = categoria.trim().ifBlank { "General" }
            val nomTrimmed = nombre.trim().ifBlank { "Corte" }

            repository.addCategory(catTrimmed)
            repository.addCorte(nomTrimmed)

            val newProd = ProductEntity(
                categoria = catTrimmed,
                nombre = nomTrimmed,
                stockCamara = stockCamara.coerceAtLeast(0),
                stockMural = stockMural.coerceAtLeast(0),
                fechas = fechas
            )
            repository.saveProduct(newProd)
            showSnackbar("¡Producto '$nomTrimmed' guardado con éxito!")
            navigateTo(Screen.LISTA)
        }
    }

    fun deleteProduct(id: Long, productName: String) {
        viewModelScope.launch {
            repository.deleteProduct(id)
            showSnackbar("Producto '$productName' eliminado")
        }
    }

    fun addCategory(nombre: String) {
        viewModelScope.launch {
            repository.addCategory(nombre)
            showSnackbar("Categoría '$nombre' agregada")
        }
    }

    fun deleteCategory(nombre: String) {
        viewModelScope.launch {
            repository.deleteCategory(nombre)
            showSnackbar("Categoría '$nombre' y sus productos eliminados")
        }
    }

    fun addCorte(nombre: String) {
        viewModelScope.launch {
            repository.addCorte(nombre)
            showSnackbar("Corte '$nombre' agregado")
        }
    }

    fun buildInventorySummaryText(): String {
        val prods = products.value
        val sb = StringBuilder()
        sb.appendLine("🥩 INVENTARIO CARNICERÍA - REPORTE DE STOCK")
        sb.appendLine("Fecha: ${DateUtils.formatForDisplay(DateUtils.getTodayString())}")
        sb.appendLine("Total Cajas: ${totalBoxes.value} (Cámara: ${totalCamaraBoxes.value} | Mural: ${totalMuralBoxes.value})")
        sb.appendLine("Alertas activas: ${allAlerts.value.size}")
        sb.appendLine("------------------------------------")
        val grouped = prods.groupBy { it.categoria }
        for ((cat, list) in grouped) {
            sb.appendLine("\n🏷️ $cat:")
            for (p in list) {
                val dates = if (p.fechas.isEmpty()) "Sin fechas" else p.fechas.joinToString { it.vencimiento }
                sb.appendLine("  • ${p.nombre}: Cám: ${p.stockCamara} cjas | Mur: ${p.stockMural} cjas | Vence: $dates")
            }
        }
        if (allAlerts.value.isNotEmpty()) {
            sb.appendLine("\n⚠️ ALERTAS DE VENCIMIENTO:")
            for (a in allAlerts.value) {
                val statusLabel = when (a.status) {
                    ExpirationStatus.VENCIDO -> "¡VENCIDO!"
                    ExpirationStatus.RETIRAR -> "RETIRAR DE MURAL"
                    ExpirationStatus.ALERTA -> "PRÓXIMO A VENCER"
                    else -> "EN REGLA"
                }
                sb.appendLine("  • [${statusLabel}] ${a.productName} (${a.category}) - Vence: ${a.fecha.vencimiento} (Quedan ${a.daysRemaining} días)")
            }
        }
        return sb.toString()
    }
}
