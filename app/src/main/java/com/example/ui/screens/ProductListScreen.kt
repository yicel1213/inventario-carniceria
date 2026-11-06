package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.LocalOffer
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.ExpirationStatus
import com.example.data.model.ProductEntity
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AlertAmberBg
import com.example.ui.theme.ButcherBorder
import com.example.ui.theme.ButcherDarkBg
import com.example.ui.theme.ButcherRedDark
import com.example.ui.theme.ButcherRedHeader
import com.example.ui.theme.ButcherRedLight
import com.example.ui.theme.ButcherRedPrimary
import com.example.ui.theme.ButcherSurface
import com.example.ui.theme.ColdChamberBlue
import com.example.ui.theme.DisplayMuralGreen
import com.example.ui.theme.ExpiredRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils

@Composable
fun ProductListScreen(
    products: List<ProductEntity>,
    categories: List<CategoryEntity>,
    searchQuery: String,
    onSearchQueryChange: (String) -> Unit,
    onAddCategory: (String) -> Unit,
    onDeleteCategory: (String) -> Unit,
    onIncrementCamara: (Long) -> Unit,
    onDecrementCamara: (Long) -> Unit,
    onIncrementMural: (Long) -> Unit,
    onDecrementMural: (Long) -> Unit,
    onTransferCamaraToMural: (Long) -> Unit,
    onDeleteProduct: (Long, String) -> Unit,
    onNavigateIngresar: () -> Unit
) {
    var showAddCategoryDialog by remember { mutableStateOf(false) }
    var newCategoryName by remember { mutableStateOf("") }
    var productToDelete by remember { mutableStateOf<ProductEntity?>(null) }
    var categoryToDelete by remember { mutableStateOf<String?>(null) }

    // Map to track collapsed categories
    val collapsedCategories = remember { mutableStateMapOf<String, Boolean>() }

    val filteredProducts = remember(products, searchQuery) {
        if (searchQuery.isBlank()) products
        else products.filter {
            it.nombre.contains(searchQuery, ignoreCase = true) ||
            it.categoria.contains(searchQuery, ignoreCase = true)
        }
    }

    // Determine all category names including ones that might have products
    val allCategoryNames = remember(categories, products) {
        val catSet = categories.map { it.nombre }.toMutableSet()
        products.forEach { catSet.add(it.categoria) }
        catSet.sorted()
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ButcherDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            // Header card matching the prototype
            Card(
                colors = CardDefaults.cardColors(containerColor = ButcherSurface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ButcherBorder, RoundedCornerShape(18.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Lista de Productos por Categoría",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Resumen general separado por marca/categoría.",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = { showAddCategoryDialog = true },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButcherRedPrimary,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(12.dp),
                        modifier = Modifier.testTag("btn_nueva_categoria")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Nueva Categoría", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChange,
                placeholder = { Text("Buscar corte o categoría...", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = TextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChange("") }) {
                            Icon(
                                imageVector = Icons.Default.Clear,
                                contentDescription = "Limpiar",
                                tint = TextSecondary
                            )
                        }
                    }
                },
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = ButcherSurface,
                    unfocusedContainerColor = ButcherSurface,
                    focusedBorderColor = ButcherRedPrimary,
                    unfocusedBorderColor = ButcherBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_products_input")
            )
        }

        if (allCategoryNames.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ButcherSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("No hay categorías registradas", color = TextSecondary, fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(12.dp))
                        Button(
                            onClick = { showAddCategoryDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = ButcherRedPrimary)
                        ) {
                            Text("Crear Primera Categoría")
                        }
                    }
                }
            }
        } else {
            items(allCategoryNames) { categoryName ->
                val prodsInCat = filteredProducts.filter { it.categoria == categoryName }
                val isCollapsed = collapsedCategories[categoryName] ?: false

                CategorySectionCard(
                    categoryName = categoryName,
                    productCount = prodsInCat.size,
                    isCollapsed = isCollapsed,
                    onToggleCollapse = {
                        collapsedCategories[categoryName] = !isCollapsed
                    },
                    onDeleteCategory = { categoryToDelete = categoryName },
                    products = prodsInCat,
                    onIncrementCamara = onIncrementCamara,
                    onDecrementCamara = onDecrementCamara,
                    onIncrementMural = onIncrementMural,
                    onDecrementMural = onDecrementMural,
                    onTransferCamaraToMural = onTransferCamaraToMural,
                    onDeleteProduct = { prod -> productToDelete = prod },
                    onNavigateIngresar = onNavigateIngresar
                )
            }
        }
    }

    // Modal Nueva Categoría
    if (showAddCategoryDialog) {
        AlertDialog(
            onDismissRequest = {
                showAddCategoryDialog = false
                newCategoryName = ""
            },
            title = {
                Text(
                    text = "Agregar Nueva Categoría",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                OutlinedTextField(
                    value = newCategoryName,
                    onValueChange = { newCategoryName = it },
                    placeholder = { Text("Nombre de la categoría...") },
                    singleLine = true,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = ButcherRedPrimary,
                        unfocusedBorderColor = ButcherBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("dialog_new_category_input")
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        if (newCategoryName.isNotBlank()) {
                            onAddCategory(newCategoryName.trim())
                            newCategoryName = ""
                            showAddCategoryDialog = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ButcherRedPrimary),
                    modifier = Modifier.testTag("dialog_confirm_add_category")
                ) {
                    Text("Guardar")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        showAddCategoryDialog = false
                        newCategoryName = ""
                    }
                ) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = ButcherSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Confirmation dialog for deleting product
    if (productToDelete != null) {
        val prod = productToDelete!!
        AlertDialog(
            onDismissRequest = { productToDelete = null },
            title = { Text("¿Eliminar producto?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "¿Deseas eliminar '${prod.nombre}' de la categoría '${prod.categoria}'?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteProduct(prod.id, prod.nombre)
                        productToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpiredRed),
                    modifier = Modifier.testTag("confirm_delete_product_btn")
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { productToDelete = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = ButcherSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }

    // Confirmation dialog for deleting category
    if (categoryToDelete != null) {
        val cat = categoryToDelete!!
        AlertDialog(
            onDismissRequest = { categoryToDelete = null },
            title = { Text("¿Eliminar categoría?", color = TextPrimary, fontWeight = FontWeight.Bold) },
            text = {
                Text(
                    "¿Deseas eliminar la categoría '$cat' y todos sus productos?",
                    color = TextSecondary
                )
            },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCategory(cat)
                        categoryToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ExpiredRed),
                    modifier = Modifier.testTag("confirm_delete_category_btn")
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { categoryToDelete = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = ButcherSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}

@Composable
private fun CategorySectionCard(
    categoryName: String,
    productCount: Int,
    isCollapsed: Boolean,
    onToggleCollapse: () -> Unit,
    onDeleteCategory: () -> Unit,
    products: List<ProductEntity>,
    onIncrementCamara: (Long) -> Unit,
    onDecrementCamara: (Long) -> Unit,
    onIncrementMural: (Long) -> Unit,
    onDecrementMural: (Long) -> Unit,
    onTransferCamaraToMural: (Long) -> Unit,
    onDeleteProduct: (ProductEntity) -> Unit,
    onNavigateIngresar: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ButcherSurface),
        shape = RoundedCornerShape(18.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ButcherBorder, RoundedCornerShape(18.dp))
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // Category Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(ButcherRedHeader.copy(alpha = 0.7f))
                    .clickable(onClick = onToggleCollapse)
                    .padding(horizontal = 14.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.LocalOffer,
                        contentDescription = null,
                        tint = ButcherRedLight,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = categoryName,
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = ButcherRedLight
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Box(
                        modifier = Modifier
                            .background(ButcherRedDark, RoundedCornerShape(6.dp))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "$productCount",
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = onDeleteCategory,
                        modifier = Modifier
                            .size(28.dp)
                            .testTag("btn_delete_category_$categoryName")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar Categoría",
                            tint = ButcherRedLight,
                            modifier = Modifier.size(16.dp)
                        )
                    }

                    Icon(
                        imageVector = if (isCollapsed) Icons.Default.KeyboardArrowDown else Icons.Default.KeyboardArrowUp,
                        contentDescription = if (isCollapsed) "Expandir" else "Colapsar",
                        tint = TextSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Category Content
            if (!isCollapsed) {
                if (products.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "No hay productos en esta categoría.",
                            color = TextSecondary,
                            fontSize = 12.sp
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        TextButton(onClick = onNavigateIngresar) {
                            Text("+ Ingresar corte a esta marca", color = ButcherRedPrimary, fontSize = 12.sp)
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        products.forEach { prod ->
                            ProductItemCard(
                                product = prod,
                                onIncrementCamara = { onIncrementCamara(prod.id) },
                                onDecrementCamara = { onDecrementCamara(prod.id) },
                                onIncrementMural = { onIncrementMural(prod.id) },
                                onDecrementMural = { onDecrementMural(prod.id) },
                                onTransferCamaraToMural = { onTransferCamaraToMural(prod.id) },
                                onDelete = { onDeleteProduct(prod) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun ProductItemCard(
    product: ProductEntity,
    onIncrementCamara: () -> Unit,
    onDecrementCamara: () -> Unit,
    onIncrementMural: () -> Unit,
    onDecrementMural: () -> Unit,
    onTransferCamaraToMural: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ButcherDarkBg.copy(alpha = 0.6f)),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ButcherBorder.copy(alpha = 0.7f), RoundedCornerShape(14.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            // Product Name and Delete Action
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = product.nombre,
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp,
                    color = TextPrimary,
                    modifier = Modifier.weight(1f)
                )

                IconButton(
                    onClick = onDelete,
                    modifier = Modifier
                        .size(26.dp)
                        .testTag("delete_prod_${product.id}")
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = ExpiredRed.copy(alpha = 0.8f),
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Stock Steppers for Cámara and Mural
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Cámara Stepper
                StockStepperBadge(
                    label = "Cámara",
                    count = product.stockCamara,
                    color = ColdChamberBlue,
                    onDecrement = onDecrementCamara,
                    onIncrement = onIncrementCamara,
                    modifier = Modifier.weight(1f),
                    testTagPrefix = "cam_${product.id}"
                )

                // Quick transfer button
                if (product.stockCamara > 0) {
                    IconButton(
                        onClick = onTransferCamaraToMural,
                        modifier = Modifier
                            .size(34.dp)
                            .background(ButcherBorder, CircleShape)
                            .testTag("transfer_btn_${product.id}")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = "Transferir 1 caja a Mural",
                            tint = Color.White,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }

                // Mural Stepper
                StockStepperBadge(
                    label = "Mural",
                    count = product.stockMural,
                    color = DisplayMuralGreen,
                    onDecrement = onDecrementMural,
                    onIncrement = onIncrementMural,
                    modifier = Modifier.weight(1f),
                    testTagPrefix = "mur_${product.id}"
                )
            }

            // Expiration lots summary
            if (product.fechas.isNotEmpty()) {
                Spacer(modifier = Modifier.height(10.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Vencimiento(s): ",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = TextSecondary
                    )
                    Row(
                        modifier = Modifier.weight(1f),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        product.fechas.take(3).forEach { fecha ->
                            val status = DateUtils.getStatus(fecha)
                            val (badgeBg, badgeFg) = when (status) {
                                ExpirationStatus.VENCIDO -> Pair(ExpiredRed, Color.White)
                                ExpirationStatus.RETIRAR -> Pair(AlertAmber, Color.Black)
                                ExpirationStatus.ALERTA -> Pair(AlertAmberBg, AlertAmber)
                                ExpirationStatus.EN_REGLA -> Pair(ButcherBorder, TextSecondary)
                            }

                            Box(
                                modifier = Modifier
                                    .background(badgeBg, RoundedCornerShape(6.dp))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = DateUtils.formatForDisplay(fecha.vencimiento),
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = badgeFg
                                )
                            }
                        }
                        if (product.fechas.size > 3) {
                            Text(
                                text = "+${product.fechas.size - 3}",
                                fontSize = 9.sp,
                                color = TextSecondary,
                                modifier = Modifier.align(Alignment.CenterVertically)
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun StockStepperBadge(
    label: String,
    count: Int,
    color: Color,
    onDecrement: () -> Unit,
    onIncrement: () -> Unit,
    modifier: Modifier = Modifier,
    testTagPrefix: String
) {
    Row(
        modifier = modifier
            .background(ButcherSurface, RoundedCornerShape(10.dp))
            .border(1.dp, color.copy(alpha = 0.35f), RoundedCornerShape(10.dp))
            .padding(horizontal = 6.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column {
            Text(label, fontSize = 9.sp, color = TextSecondary)
            Text(
                "$count cjas",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
                color = color
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(ButcherBorder, CircleShape)
                    .clickable(onClick = onDecrement)
                    .testTag("${testTagPrefix}_dec"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Remove,
                    contentDescription = "Menos",
                    tint = TextPrimary,
                    modifier = Modifier.size(12.dp)
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            Box(
                modifier = Modifier
                    .size(24.dp)
                    .background(color.copy(alpha = 0.25f), CircleShape)
                    .clickable(onClick = onIncrement)
                    .testTag("${testTagPrefix}_inc"),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Más",
                    tint = color,
                    modifier = Modifier.size(12.dp)
                )
            }
        }
    }
}
