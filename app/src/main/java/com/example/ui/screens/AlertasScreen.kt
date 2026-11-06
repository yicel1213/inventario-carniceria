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
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.RemoveCircleOutline
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ExpirationStatus
import com.example.data.model.LotAlertItem
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.AlertAmberBg
import com.example.ui.theme.ButcherBorder
import com.example.ui.theme.ButcherDarkBg
import com.example.ui.theme.ButcherRedLight
import com.example.ui.theme.ButcherSurface
import com.example.ui.theme.ColdChamberBlue
import com.example.ui.theme.DisplayMuralGreen
import com.example.ui.theme.ExpiredRed
import com.example.ui.theme.ExpiredRedBg
import com.example.ui.theme.RetiroOrange
import com.example.ui.theme.RetiroOrangeBg
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.AlertFilter
import com.example.ui.viewmodel.Screen
import com.example.util.DateUtils

@Composable
fun AlertasScreen(
    alerts: List<LotAlertItem>,
    allAlertsCount: Int,
    currentFilter: AlertFilter,
    onFilterChange: (AlertFilter) -> Unit,
    onDecrementMural: (Long) -> Unit,
    onNavigate: (Screen) -> Unit
) {
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
                                imageVector = Icons.Default.WarningAmber,
                                contentDescription = null,
                                tint = AlertAmber,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = "Panel de Alertas",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                        }
                        Text(
                            text = "Productos próximos a retiro o vencimiento.",
                            fontSize = 11.sp,
                            color = TextSecondary,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(
                                if (allAlertsCount > 0) ExpiredRedBg else ButcherBorder,
                                RoundedCornerShape(12.dp)
                            )
                            .padding(horizontal = 10.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "$allAlertsCount alertas",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (allAlertsCount > 0) ExpiredRed else TextSecondary
                        )
                    }
                }
            }
        }

        item {
            // Filter Tabs
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                FilterChipItem(
                    label = "Todas",
                    isSelected = currentFilter == AlertFilter.ALL,
                    onClick = { onFilterChange(AlertFilter.ALL) },
                    modifier = Modifier.weight(1f)
                )
                FilterChipItem(
                    label = "Vencidos",
                    isSelected = currentFilter == AlertFilter.VENCIDOS,
                    onClick = { onFilterChange(AlertFilter.VENCIDOS) },
                    modifier = Modifier.weight(1f)
                )
                FilterChipItem(
                    label = "Para retirar",
                    isSelected = currentFilter == AlertFilter.RETIRAR,
                    onClick = { onFilterChange(AlertFilter.RETIRAR) },
                    modifier = Modifier.weight(1f)
                )
                FilterChipItem(
                    label = "Próximos",
                    isSelected = currentFilter == AlertFilter.ALERTA,
                    onClick = { onFilterChange(AlertFilter.ALERTA) },
                    modifier = Modifier.weight(1f)
                )
            }
        }

        if (alerts.isEmpty()) {
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = ButcherSurface),
                    shape = RoundedCornerShape(18.dp),
                    modifier = Modifier.fillMaxWidth().padding(vertical = 24.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = null,
                            tint = DisplayMuralGreen,
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "No hay alertas en esta categoría",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Todo el stock se encuentra al día y en regla.",
                            fontSize = 12.sp,
                            color = TextSecondary
                        )
                    }
                }
            }
        } else {
            items(alerts, key = { "${it.productId}_${it.fecha.vencimiento}" }) { alertItem ->
                AlertItemCard(
                    item = alertItem,
                    onDecrementMural = { onDecrementMural(alertItem.productId) },
                    onGoToProduct = { onNavigate(Screen.LISTA) }
                )
            }
        }
    }
}

@Composable
private fun FilterChipItem(
    label: String,
    isSelected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(
                if (isSelected) AlertAmberBg else ButcherSurface,
                RoundedCornerShape(10.dp)
            )
            .border(
                1.dp,
                if (isSelected) AlertAmber else ButcherBorder,
                RoundedCornerShape(10.dp)
            )
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
            color = if (isSelected) AlertAmber else TextSecondary
        )
    }
}

@Composable
private fun AlertItemCard(
    item: LotAlertItem,
    onDecrementMural: () -> Unit,
    onGoToProduct: () -> Unit
) {
    val (statusLabel, statusBg, statusFg, statusBorder) = when (item.status) {
        ExpirationStatus.VENCIDO -> Quadruple("¡VENCIDO!", ExpiredRedBg, ExpiredRed, ExpiredRed)
        ExpirationStatus.RETIRAR -> Quadruple("RETIRAR DE MURAL", RetiroOrangeBg, RetiroOrange, RetiroOrange)
        ExpirationStatus.ALERTA -> Quadruple("PRÓXIMO A VENCER", AlertAmberBg, AlertAmber, AlertAmber)
        ExpirationStatus.EN_REGLA -> Quadruple("EN REGLA", ButcherBorder, DisplayMuralGreen, ButcherBorder)
    }

    Card(
        colors = CardDefaults.cardColors(containerColor = ButcherSurface),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, statusBorder.copy(alpha = 0.6f), RoundedCornerShape(16.dp))
            .testTag("alert_card_${item.productId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "${item.productName} (${item.category})",
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp,
                        color = TextPrimary
                    )
                    if (item.fecha.loteCode != null) {
                        Text(
                            text = "Lote: ${item.fecha.loteCode}",
                            fontSize = 10.sp,
                            color = TextSecondary
                        )
                    }
                }

                Box(
                    modifier = Modifier
                        .background(statusBg, RoundedCornerShape(8.dp))
                        .border(1.dp, statusBorder.copy(alpha = 0.5f), RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = statusLabel,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold,
                        color = statusFg
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Expiration countdown
            val daysText = when {
                item.daysRemaining < 0 -> "Venció hace ${-item.daysRemaining} día(s)"
                item.daysRemaining == 0 -> "¡Vence HOY!"
                item.daysRemaining == 1 -> "Queda 1 día"
                else -> "Quedan ${item.daysRemaining} días"
            }

            Text(
                text = "Vence: ${DateUtils.formatForDisplay(item.fecha.vencimiento)} ($daysText)",
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                color = statusFg
            )

            Spacer(modifier = Modifier.height(4.dp))

            // Stock context
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(
                    text = "Stock Cámara: ${item.stockCamara} cjas",
                    fontSize = 11.sp,
                    color = ColdChamberBlue
                )
                Text(
                    text = "Stock Mural: ${item.stockMural} cjas",
                    fontSize = 11.sp,
                    color = DisplayMuralGreen
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Action row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (item.stockMural > 0) {
                    Button(
                        onClick = onDecrementMural,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButcherBorder,
                            contentColor = TextPrimary
                        ),
                        shape = RoundedCornerShape(10.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp),
                        modifier = Modifier.testTag("btn_retirar_mural_${item.productId}")
                    ) {
                        Icon(
                            imageVector = Icons.Default.RemoveCircleOutline,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text("Retirar 1 cja de Mural", fontSize = 11.sp)
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                }

                Button(
                    onClick = onGoToProduct,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ButcherSurface,
                        contentColor = ButcherRedLight
                    ),
                    shape = RoundedCornerShape(10.dp),
                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 6.dp)
                ) {
                    Text("Ver en Lista", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

private data class Quadruple<A, B, C, D>(val first: A, val second: B, val third: C, val fourth: D)
