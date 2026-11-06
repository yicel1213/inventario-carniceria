package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AcUnit
import androidx.compose.material.icons.filled.AllInbox
import androidx.compose.material.icons.filled.Storefront
import androidx.compose.material.icons.filled.WarningAmber
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.ButcherBorder
import com.example.ui.theme.ButcherSurface
import com.example.ui.theme.ColdChamberBlue
import com.example.ui.theme.DisplayMuralGreen
import com.example.ui.theme.ExpiredRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.viewmodel.Screen

@Composable
fun QuickStatsBanner(
    totalBoxes: Int,
    camaraBoxes: Int,
    muralBoxes: Int,
    alertCount: Int,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        StatCard(
            title = "Total",
            count = "$totalBoxes",
            unit = "cajas",
            icon = Icons.Default.AllInbox,
            accentColor = TextPrimary,
            modifier = Modifier
                .weight(1f)
                .clickable { onNavigate(Screen.LISTA) }
                .testTag("stat_card_total")
        )

        StatCard(
            title = "Cámara",
            count = "$camaraBoxes",
            unit = "cajas",
            icon = Icons.Default.AcUnit,
            accentColor = ColdChamberBlue,
            modifier = Modifier
                .weight(1f)
                .clickable { onNavigate(Screen.CAMARA) }
                .testTag("stat_card_camara")
        )

        StatCard(
            title = "Mural",
            count = "$muralBoxes",
            unit = "cajas",
            icon = Icons.Default.Storefront,
            accentColor = DisplayMuralGreen,
            modifier = Modifier
                .weight(1f)
                .clickable { onNavigate(Screen.MURAL) }
                .testTag("stat_card_mural")
        )

        StatCard(
            title = "Alertas",
            count = "$alertCount",
            unit = "lotes",
            icon = Icons.Default.WarningAmber,
            accentColor = if (alertCount > 0) ExpiredRed else AlertAmber,
            modifier = Modifier
                .weight(1f)
                .clickable { onNavigate(Screen.ALERTAS) }
                .testTag("stat_card_alertas")
        )
    }
}

@Composable
private fun StatCard(
    title: String,
    count: String,
    unit: String,
    icon: ImageVector,
    accentColor: Color,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .background(ButcherSurface, RoundedCornerShape(14.dp))
            .border(1.dp, ButcherBorder, RoundedCornerShape(14.dp))
            .padding(vertical = 10.dp, horizontal = 6.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = count,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = accentColor
        )
        Text(
            text = title,
            fontSize = 10.sp,
            color = TextSecondary,
            maxLines = 1
        )
    }
}
