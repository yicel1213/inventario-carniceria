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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.FormatListBulleted
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Storefront
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.QuickStatsBanner
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
import com.example.ui.viewmodel.Screen

@Composable
fun MenuScreen(
    totalBoxes: Int,
    camaraBoxes: Int,
    muralBoxes: Int,
    alertCount: Int,
    urgentCount: Int,
    onNavigate: (Screen) -> Unit,
    onShareReport: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ButcherDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            // Quick metrics overview
            QuickStatsBanner(
                totalBoxes = totalBoxes,
                camaraBoxes = camaraBoxes,
                muralBoxes = muralBoxes,
                alertCount = alertCount,
                onNavigate = onNavigate
            )
        }

        item {
            // Grid 2x2 of main module navigation matching the prototype
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuGridItem(
                    title = "Ver lista de productos",
                    subtitle = "Categorías y cortes",
                    icon = Icons.Default.FormatListBulleted,
                    accentColor = ButcherRedPrimary,
                    modifier = Modifier.weight(1f),
                    testTag = "menu_btn_lista",
                    onClick = { onNavigate(Screen.LISTA) }
                )

                MenuGridItem(
                    title = "Cámara",
                    subtitle = "Stock frío ($camaraBoxes cjas)",
                    icon = Icons.Default.AcUnit,
                    accentColor = ColdChamberBlue,
                    modifier = Modifier.weight(1f),
                    testTag = "menu_btn_camara",
                    onClick = { onNavigate(Screen.CAMARA) }
                )
            }
        }

        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MenuGridItem(
                    title = "Mural",
                    subtitle = "Exhibición ($muralBoxes cjas)",
                    icon = Icons.Default.Storefront,
                    accentColor = DisplayMuralGreen,
                    modifier = Modifier.weight(1f),
                    testTag = "menu_btn_mural",
                    onClick = { onNavigate(Screen.MURAL) }
                )

                MenuGridItem(
                    title = "Ingresar productos",
                    subtitle = "Nuevo lote y fechas",
                    icon = Icons.Default.AddCircle,
                    accentColor = ButcherRedPrimary,
                    modifier = Modifier.weight(1f),
                    testTag = "menu_btn_ingresar",
                    onClick = { onNavigate(Screen.INGRESAR) }
                )
            }
        }

        item {
            // Large Alertas y Vencimientos banner
            Card(
                colors = CardDefaults.cardColors(
                    containerColor = if (urgentCount > 0) ButcherRedHeader else ButcherSurface
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(
                        1.dp,
                        if (urgentCount > 0) ExpiredRed else ButcherBorder,
                        RoundedCornerShape(20.dp)
                    )
                    .clickable { onNavigate(Screen.ALERTAS) }
                    .testTag("menu_btn_alertas")
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .background(
                                if (urgentCount > 0) ExpiredRed.copy(alpha = 0.25f) else AlertAmberBg,
                                CircleShape
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.WarningAmber,
                            contentDescription = "Alertas",
                            tint = if (urgentCount > 0) ExpiredRed else AlertAmber,
                            modifier = Modifier.size(26.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(14.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "Alertas y Vencimientos",
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )
                            if (alertCount > 0) {
                                Spacer(modifier = Modifier.width(8.dp))
                                Box(
                                    modifier = Modifier
                                        .background(
                                            if (urgentCount > 0) ExpiredRed else AlertAmber,
                                            RoundedCornerShape(8.dp)
                                        )
                                        .padding(horizontal = 6.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = "$alertCount",
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }
                        }
                        Text(
                            text = if (urgentCount > 0)
                                "¡Hay $urgentCount producto(s) para retirar o vencidos!"
                            else if (alertCount > 0)
                                "Hay $alertCount producto(s) próximos a vencer"
                            else
                                "Todos los productos al día y en regla",
                            fontSize = 12.sp,
                            color = if (urgentCount > 0) ButcherRedLight else TextSecondary
                        )
                    }

                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Ver",
                        tint = TextSecondary
                    )
                }
            }
        }

        item {
            // Quick action card to share daily inventory report
            Card(
                colors = CardDefaults.cardColors(containerColor = ButcherSurface),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ButcherBorder, RoundedCornerShape(16.dp))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Reporte Diario de Stock",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Text(
                            text = "Exportar resumen de inventario por WhatsApp o correo",
                            fontSize = 11.sp,
                            color = TextSecondary
                        )
                    }

                    Button(
                        onClick = onShareReport,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = ButcherRedDark,
                            contentColor = Color.White
                        ),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier.testTag("menu_share_btn")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Exportar", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
private fun MenuGridItem(
    title: String,
    subtitle: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ButcherSurface),
        shape = RoundedCornerShape(20.dp),
        modifier = modifier
            .border(1.dp, ButcherBorder, RoundedCornerShape(20.dp))
            .clickable(onClick = onClick)
            .testTag(testTag)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 22.dp, horizontal = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .background(accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = accentColor,
                    modifier = Modifier.size(28.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = title,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                color = TextPrimary,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = subtitle,
                fontSize = 10.sp,
                color = TextSecondary,
                maxLines = 1
            )
        }
    }
}
