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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
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
import com.example.data.model.ExpirationStatus
import com.example.data.model.ProductEntity
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AlertAmberBg
import com.example.ui.theme.ButcherBorder
import com.example.ui.theme.ButcherDarkBg
import com.example.ui.theme.ButcherSurface
import com.example.ui.theme.DisplayMuralBg
import com.example.ui.theme.DisplayMuralGreen
import com.example.ui.theme.ExpiredRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils

@Composable
fun MuralScreen(
    products: List<ProductEntity>,
    totalMuralBoxes: Int,
    onIncrementMural: (Long) -> Unit,
    onDecrementMural: (Long) -> Unit,
    onNavigateIngresar: () -> Unit,
    onNavigateAlertas: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }

    val muralProducts = remember(products, searchQuery) {
        products.filter { it.stockMural > 0 }.filter {
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
                                imageVector = Icons.Default.Storefront,
                                contentDescription = null,
                                tint = DisplayMuralGreen,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Stock en Mural",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Control de exhibición y venta al público.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(DisplayMuralBg, RoundedCornerShape(12.dp))
                            .border(1.dp, DisplayMuralGreen.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$totalMuralBoxes cjas",
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = DisplayMuralGreen
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
                placeholder = { Text("Filtrar en mural por corte o marca...", color = TextSecondary, fontSize = 13.sp) },
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
                    focusedBorderColor = DisplayMuralGreen,
                    unfocusedBorderColor = ButcherBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_mural_input")
            )
        }

        if (muralProducts.isEmpty()) {
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
                            imageVector = Icons.Default.Storefront,
                            contentDescription = null,
                            tint = DisplayMuralGreen.copy(alpha = 0.6f),
                            modifier = Modifier.size(40.dp)
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (searchQuery.isNotBlank()) "No se encontraron cortes en mural con ese filtro."
                                   else "No hay stock en vitrina/mural actualmente.",
                            color = TextSecondary,
                            fontSize = 13.sp
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onNavigateIngresar,
                            colors = ButtonDefaults.buttonColors(containerColor = DisplayMuralGreen),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Text("Ingresar Stock a Mural", color = ButcherDarkBg, fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        } else {
            items(muralProducts, key = { it.id }) { prod ->
                val hasUrgentLot = prod.fechas.any {
                    val s = DateUtils.getStatus(it)
                    s == ExpirationStatus.VENCIDO || s == ExpirationStatus.RETIRAR
                }

                Card(
                    colors = CardDefaults.cardColors(containerColor = ButcherSurface),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(
                            1.dp,
                            if (hasUrgentLot) AlertAmber else ButcherBorder,
                            RoundedCornerShape(16.dp)
                        )
                        .testTag("mural_item_${prod.id}")
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = prod.nombre,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp,
                                    color = TextPrimary
                                )
                                if (hasUrgentLot) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Box(
                                        modifier = Modifier
                                            .background(AlertAmberBg, RoundedCornerShape(6.dp))
                                            .border(1.dp, AlertAmber, RoundedCornerShape(6.dp))
                                            .clickable(onClick = onNavigateAlertas)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "RETIRAR",
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = AlertAmber
                                        )
                                    }
                                }
                            }
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
                                    color = DisplayMuralGreen.copy(alpha = 0.85f),
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
                                    .clickable { onDecrementMural(prod.id) }
                                    .testTag("mural_dec_${prod.id}"),
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
                                text = "${prod.stockMural} cjas",
                                color = DisplayMuralGreen,
                                fontWeight = FontWeight.Bold,
                                fontSize = 14.sp
                            )

                            Spacer(modifier = Modifier.width(8.dp))

                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(DisplayMuralBg, CircleShape)
                                    .clickable { onIncrementMural(prod.id) }
                                    .testTag("mural_inc_${prod.id}"),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Add,
                                    contentDescription = "Más",
                                    tint = DisplayMuralGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
