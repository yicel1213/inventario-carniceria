package com.example

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.ui.components.ButcherTopBar
import com.example.ui.screens.AlertasScreen
import com.example.ui.screens.CamaraScreen
import com.example.ui.screens.IngresarScreen
import com.example.ui.screens.MenuScreen
import com.example.ui.screens.MuralScreen
import com.example.ui.screens.ProductListScreen
import com.example.ui.theme.ButcherDarkBg
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.Screen
import com.example.ui.viewmodel.StockViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: StockViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme {
                StockCarniceriaApp(
                    viewModel = viewModel,
                    onShareSummary = { summaryText ->
                        val sendIntent = Intent().apply {
                            action = Intent.ACTION_SEND
                            putExtra(Intent.EXTRA_TEXT, summaryText)
                            type = "text/plain"
                        }
                        val shareIntent = Intent.createChooser(sendIntent, "Compartir Inventario Carnicería")
                        startActivity(shareIntent)
                    }
                )
            }
        }
    }
}

@Composable
fun StockCarniceriaApp(
    viewModel: StockViewModel,
    onShareSummary: (String) -> Unit
) {
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val cortes by viewModel.cortes.collectAsStateWithLifecycle()
    val totalBoxes by viewModel.totalBoxes.collectAsStateWithLifecycle()
    val totalCamaraBoxes by viewModel.totalCamaraBoxes.collectAsStateWithLifecycle()
    val totalMuralBoxes by viewModel.totalMuralBoxes.collectAsStateWithLifecycle()
    val allAlerts by viewModel.allAlerts.collectAsStateWithLifecycle()
    val filteredAlerts by viewModel.filteredAlerts.collectAsStateWithLifecycle()
    val urgentAlertCount by viewModel.urgentAlertCount.collectAsStateWithLifecycle()
    val alertFilter by viewModel.alertFilter.collectAsStateWithLifecycle()
    val searchQuery by viewModel.searchQuery.collectAsStateWithLifecycle()
    val snackbarMsg by viewModel.snackbarMessage.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }

    // Display snackbar messages
    LaunchedEffect(snackbarMsg) {
        snackbarMsg?.let { msg ->
            snackbarHostState.showSnackbar(msg)
            viewModel.clearSnackbar()
        }
    }

    // Hardware back button returns to Main Menu
    if (currentScreen != Screen.MENU) {
        BackHandler {
            viewModel.goHome()
        }
    }

    Scaffold(
        modifier = Modifier
            .fillMaxSize()
            .background(ButcherDarkBg),
        topBar = {
            ButcherTopBar(
                currentScreen = currentScreen,
                onNavigateHome = { viewModel.goHome() },
                onShareReport = {
                    val summary = viewModel.buildInventorySummaryText()
                    onShareSummary(summary)
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = ButcherDarkBg
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = { fadeIn() togetherWith fadeOut() },
                label = "screen_transition"
            ) { screen ->
                when (screen) {
                    Screen.MENU -> {
                        MenuScreen(
                            totalBoxes = totalBoxes,
                            camaraBoxes = totalCamaraBoxes,
                            muralBoxes = totalMuralBoxes,
                            alertCount = allAlerts.size,
                            urgentCount = urgentAlertCount,
                            onNavigate = { viewModel.navigateTo(it) },
                            onShareReport = {
                                val summary = viewModel.buildInventorySummaryText()
                                onShareSummary(summary)
                            }
                        )
                    }

                    Screen.LISTA -> {
                        ProductListScreen(
                            products = products,
                            categories = categories,
                            searchQuery = searchQuery,
                            onSearchQueryChange = { viewModel.setSearchQuery(it) },
                            onAddCategory = { viewModel.addCategory(it) },
                            onDeleteCategory = { viewModel.deleteCategory(it) },
                            onIncrementCamara = { viewModel.incrementCamara(it) },
                            onDecrementCamara = { viewModel.decrementCamara(it) },
                            onIncrementMural = { viewModel.incrementMural(it) },
                            onDecrementMural = { viewModel.decrementMural(it) },
                            onTransferCamaraToMural = { viewModel.transferCamaraToMural(it, 1) },
                            onDeleteProduct = { id, name -> viewModel.deleteProduct(id, name) },
                            onNavigateIngresar = { viewModel.navigateTo(Screen.INGRESAR) }
                        )
                    }

                    Screen.CAMARA -> {
                        CamaraScreen(
                            products = products,
                            totalCamaraBoxes = totalCamaraBoxes,
                            onIncrementCamara = { viewModel.incrementCamara(it) },
                            onDecrementCamara = { viewModel.decrementCamara(it) },
                            onTransferCamaraToMural = { id, qty -> viewModel.transferCamaraToMural(id, qty) },
                            onNavigateIngresar = { viewModel.navigateTo(Screen.INGRESAR) }
                        )
                    }

                    Screen.MURAL -> {
                        MuralScreen(
                            products = products,
                            totalMuralBoxes = totalMuralBoxes,
                            onIncrementMural = { viewModel.incrementMural(it) },
                            onDecrementMural = { viewModel.decrementMural(it) },
                            onNavigateIngresar = { viewModel.navigateTo(Screen.INGRESAR) },
                            onNavigateAlertas = { viewModel.navigateTo(Screen.ALERTAS) }
                        )
                    }

                    Screen.INGRESAR -> {
                        IngresarScreen(
                            categories = categories,
                            cortes = cortes,
                            onSaveProduct = { cat, corte, camara, mural, fechas ->
                                viewModel.saveProduct(cat, corte, camara, mural, fechas)
                            }
                        )
                    }

                    Screen.ALERTAS -> {
                        AlertasScreen(
                            alerts = filteredAlerts,
                            allAlertsCount = allAlerts.size,
                            currentFilter = alertFilter,
                            onFilterChange = { viewModel.setAlertFilter(it) },
                            onDecrementMural = { viewModel.decrementMural(it) },
                            onNavigate = { viewModel.navigateTo(it) }
                        )
                    }
                }
            }
        }
    }
}
