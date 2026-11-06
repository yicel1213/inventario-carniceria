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
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
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
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.model.ProductEntity
import com.example.ui.theme.ButcherBorder
import com.example.ui.theme.ButcherDarkBg
import com.example.ui.theme.ButcherSurface
import com.example.ui.theme.ColdChamberBg
import com.example.ui.theme.ColdChamberBlue
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils

@Composable
fun CamaraScreen(
    products: List<ProductEntity>,
    totalCamaraBoxes: Int,
    onIncrementCamara: (Long) -> Unit,
    onDecrementCamara: (Long) -> Unit,
    onTransferCamaraToMural: (Long, Int) -> Unit,
    onNavigateIngresar: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var transferProduct by remember { mutableStateOf<ProductEntity?>(null) }
    var transferQty by remember { mutableIntStateOf(1) }

    val camaraProducts = remember(products, searchQuery) {
        products.filter { it.stockCamara > 0 }.filter {
            if (searchQuery.isBlank()) true
            else it.nombre.contains(searchQuery, ignoreCase = true) ||
                 it.categoria.contains(searchQuery, ignoreCase = true)
        }
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
            // Header card
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
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.AcUnit,
                                contentDescription = null,
                                tint = ColdChamberBlue,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Stock en Cámara",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Control rápido del almacenamiento en frío.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(ColdChamberBg, RoundedCornerShape(12.dp))
                            .border(1.dp, ColdChamberBlue.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$totalCamaraBoxes cjas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColdChamberBlue
                        )
                    }
                }
            }
        }

        item {
            // Search field
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = { Text("Filtrar en cámara por corte o marca...", color = TextSecondary, fontSize = 13.sp) },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Buscar",
                        tint = TextSecondary
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { searchQuery = "" }) {
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
                    focusedBorderColor = ColdChamberBlue,
                    unfocusedBorderColor = ButcherBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_camara_input")
            )
        }

        if (camaraProducts.isEmpty()) {
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
                        Icon(
                            imageVector = Icons.Default.AcUnit,
                            contentDescription = null,
                            tint = ColdChamberBlue.copy(alpha = 0.6f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No se encontraron cortes con ese filtro."
                                   else "No hay stock en cámara de frío actualmente.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onNavigateIngresar,
                            colors = ButtonDefaults.buttonColors(containerColor = ColdChamberBlue),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ingresar Stock a Cámara", color = ButcherDarkBg, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(camaraProducts, key = { it.id }) { prod ->
                Card(
                    colors = CardDefaults.cardColors(containerColor = ButcherSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, ButcherBorder, RoundedCornerShape(16.dp))
                        .testTag("camara_item_${prod.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = prod.nombre,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp,
                                color = TextPrimary
                            )
                            Text(
                                text = "Categoría: ${prod.categoria}",
                                fontSize = 11.sp,
                                color = TextSecondary
                            )
                            if (prod.fechas.isNotEmpty()) {
                                val dates = prod.fechas.map { DateUtils.formatForDisplay(it.vencimiento) }.joinToString(", ")
                                Text(
                                    text = "Vence: $dates",
                                    fontSize = 10.sp,
                                    color = ColdChamberBlue.copy(alpha = 0.85f),
                                    maxLines = 1
                                )
                            }
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            // Stepper
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(ButcherBorder, CircleShape)
                                    .clickable { onDecrementCamara(prod.id) }
                                    .testTag("camara_dec_${prod.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Remove,
                                    contentDescription = "Menos",
                                    tint = TextPrimary,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(8.dp))

                            Text(
                                text = "${prod.stockCamara} cjas",
                                color = ColdChamberBlue,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(ColdChamberBg, CircleShape)
                                    .clickable { onIncrementCamara(prod.id) }
                                    .testTag("camara_inc_${prod.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Más",
                                    tint = ColdChamberBlue,
                                    modifier = Modifier.size(14.dp)
                                )
                            }

                            Spacer(modifier = Modifier.width(10.dp))

                            // Transfer button
                            Button(
                                onClick = {
                                    transferProduct = prod
                                    transferQty = 1
                                },
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = ButcherBorder,
                                    contentColor = Color.White
                                ),
                                shape = RoundedCornerShape(10.dp),
                                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 4.dp),
                                modifier = Modifier.testTag("btn_transfer_mural_${prod.id}")
                            ) {
                                Icon(
                                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                                    contentDescription = "Pasar a Mural",
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text("A Mural", fontSize = 10.sp)
                            }
                        }
                    }
                }
            }
        }
    }

    // Dialog for transfer quantity
    if (transferProduct != null) {
        val prod = transferProduct!!
        AlertDialog(
            onDismissRequest = { transferProduct = null },
            title = {
                Text(
                    text = "Trasladar de Cámara a Mural",
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )
            },
            text = {
                Column {
                    Text(
                        text = "Producto: ${prod.nombre} (${prod.categoria})",
                        color = TextPrimary,
                        fontSize = 13.sp
                    )
                    Text(
                        text = "Stock actual en Cámara: ${prod.stockCamara} cajas",
                        color = TextSecondary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Cajas a trasladar a Mural:",
                        fontWeight = FontWeight.SemiBold,
                        color = TextPrimary,
                        fontSize = 12.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ButcherBorder, CircleShape)
                                .clickable { if (transferQty > 1) transferQty-- },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Remove, contentDescription = null, tint = TextPrimary)
                        }

                        Text(
                            text = "$transferQty",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = ColdChamberBlue,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(ColdChamberBg, CircleShape)
                                .clickable { if (transferQty < prod.stockCamara) transferQty++ },
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = ColdChamberBlue)
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onTransferCamaraToMural(prod.id, transferQty)
                        transferProduct = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = ColdChamberBlue),
                    modifier = Modifier.testTag("confirm_transfer_dialog_btn")
                ) {
                    Text("Confirmar Traslado", color = ButcherDarkBg, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { transferProduct = null }) {
                    Text("Cancelar", color = TextSecondary)
                }
            },
            containerColor = ButcherSurface,
            shape = RoundedCornerShape(18.dp)
        )
    }
}
