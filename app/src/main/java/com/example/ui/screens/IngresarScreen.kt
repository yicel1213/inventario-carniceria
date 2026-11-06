package com.example.ui.screens

import android.app.DatePickerDialog
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
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CategoryEntity
import com.example.data.model.CorteEntity
import com.example.data.model.FechaLote
import com.example.ui.theme.AlertAmber
import com.example.ui.theme.ButcherBorder
import com.example.ui.theme.ButcherDarkBg
import com.example.ui.theme.ButcherRedDark
import com.example.ui.theme.ButcherRedLight
import com.example.ui.theme.ButcherRedPrimary
import com.example.ui.theme.ButcherSurface
import com.example.ui.theme.ColdChamberBlue
import com.example.ui.theme.DisplayMuralGreen
import com.example.ui.theme.ExpiredRed
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.util.DateUtils
import java.util.Calendar

data class FechaFormState(
    var vencimiento: String = "",
    var retiroDias: String = "2",
    var alertaDias: String = "3",
    var loteCode: String = ""
)

@Composable
fun IngresarScreen(
    categories: List<CategoryEntity>,
    cortes: List<CorteEntity>,
    onSaveProduct: (String, String, Int, Int, List<FechaLote>) -> Unit
) {
    val context = LocalContext.current

    var selectedCategory by remember { mutableStateOf("") }
    var customCategory by remember { mutableStateOf("") }
    var categoryExpanded by remember { mutableStateOf(false) }

    var selectedCorte by remember { mutableStateOf("") }
    var customCorte by remember { mutableStateOf("") }
    var corteExpanded by remember { mutableStateOf(false) }

    var ubicacion by remember { mutableStateOf("Camara") } // "Camara", "Mural", "Ambos"
    var stockCamaraText by remember { mutableStateOf("0") }
    var stockMuralText by remember { mutableStateOf("0") }

    val fechasList = remember {
        mutableStateListOf(
            FechaFormState(
                vencimiento = DateUtils.addDaysToToday(5),
                retiroDias = "2",
                alertaDias = "3"
            )
        )
    }

    var errorMessage by remember { mutableStateOf<String?>(null) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(ButcherDarkBg)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 80.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
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
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Ingresar Nuevo Lote / Producto",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                    Text(
                        text = "Complete los datos de stock, categoría y fechas.",
                        fontSize = 11.sp,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 2.dp)
                    )
                }
            }
        }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ButcherSurface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ButcherBorder, RoundedCornerShape(18.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // CATEGORÍA / MARCA
                    Column {
                        Text(
                            text = "Categoría / Marca",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = if (selectedCategory == "OTRO") "Otro (Escribir nueva...)" else selectedCategory,
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Seleccione categoría...", color = TextSecondary, fontSize = 13.sp) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Desplegar",
                                        tint = TextSecondary
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = ButcherDarkBg,
                                    unfocusedContainerColor = ButcherDarkBg,
                                    focusedBorderColor = ButcherRedPrimary,
                                    unfocusedBorderColor = ButcherBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { categoryExpanded = true }
                                    .testTag("select_categoria")
                            )

                            DropdownMenu(
                                expanded = categoryExpanded,
                                onDismissRequest = { categoryExpanded = false },
                                modifier = Modifier
                                    .background(ButcherSurface)
                                    .border(1.dp, ButcherBorder, RoundedCornerShape(8.dp))
                            ) {
                                categories.forEach { cat ->
                                    DropdownMenuItem(
                                        text = { Text(cat.nombre, color = TextPrimary, fontSize = 13.sp) },
                                        onClick = {
                                            selectedCategory = cat.nombre
                                            categoryExpanded = false
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("Otro (Escribir nueva...)", color = ButcherRedPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                                    onClick = {
                                        selectedCategory = "OTRO"
                                        categoryExpanded = false
                                    }
                                )
                            }
                        }

                        if (selectedCategory == "OTRO") {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customCategory,
                                onValueChange = { customCategory = it },
                                placeholder = { Text("Ej. Marca Propia / Premium", color = TextSecondary) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = ButcherDarkBg,
                                    unfocusedContainerColor = ButcherDarkBg,
                                    focusedBorderColor = ButcherRedPrimary,
                                    unfocusedBorderColor = ButcherBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_otra_categoria")
                            )
                        }
                    }

                    // NOMBRE DEL CORTE
                    Column {
                        Text(
                            text = "Nombre del Corte",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Box(modifier = Modifier.fillMaxWidth()) {
                            OutlinedTextField(
                                value = if (selectedCorte == "OTRO") "Otro (Escribir nuevo corte...)" else selectedCorte,
                                onValueChange = {},
                                readOnly = true,
                                placeholder = { Text("Seleccione corte...", color = TextSecondary, fontSize = 13.sp) },
                                trailingIcon = {
                                    Icon(
                                        imageVector = Icons.Default.KeyboardArrowDown,
                                        contentDescription = "Desplegar",
                                        tint = TextSecondary
                                    )
                                },
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = ButcherDarkBg,
                                    unfocusedContainerColor = ButcherDarkBg,
                                    focusedBorderColor = ButcherRedPrimary,
                                    unfocusedBorderColor = ButcherBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { corteExpanded = true }
                                    .testTag("select_corte")
                            )

                            DropdownMenu(
                                expanded = corteExpanded,
                                onDismissRequest = { corteExpanded = false },
                                modifier = Modifier
                                    .background(ButcherSurface)
                                    .border(1.dp, ButcherBorder, RoundedCornerShape(8.dp))
                            ) {
                                cortes.forEach { cor ->
                                    DropdownMenuItem(
                                        text = { Text(cor.nombre, color = TextPrimary, fontSize = 13.sp) },
                                        onClick = {
                                            selectedCorte = cor.nombre
                                            corteExpanded = false
                                        }
                                    )
                                }
                                DropdownMenuItem(
                                    text = { Text("Otro (Escribir nuevo corte...)", color = ButcherRedPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp) },
                                    onClick = {
                                        selectedCorte = "OTRO"
                                        corteExpanded = false
                                    }
                                )
                            }
                        }

                        if (selectedCorte == "OTRO") {
                            Spacer(modifier = Modifier.height(8.dp))
                            OutlinedTextField(
                                value = customCorte,
                                onValueChange = { customCorte = it },
                                placeholder = { Text("Ej. Entraña, Filete, Plateada...", color = TextSecondary) },
                                singleLine = true,
                                shape = RoundedCornerShape(12.dp),
                                colors = OutlinedTextFieldDefaults.colors(
                                    focusedContainerColor = ButcherDarkBg,
                                    unfocusedContainerColor = ButcherDarkBg,
                                    focusedBorderColor = ButcherRedPrimary,
                                    unfocusedBorderColor = ButcherBorder,
                                    focusedTextColor = TextPrimary,
                                    unfocusedTextColor = TextPrimary
                                ),
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .testTag("input_otro_corte")
                            )
                        }
                    }

                    // UBICACIÓN DE STOCK
                    Column {
                        Text(
                            text = "Ubicación de Stock",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = TextPrimary
                        )
                        Spacer(modifier = Modifier.height(6.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("Camara" to "Cámara", "Mural" to "Mural", "Ambos" to "Ambos").forEach { (key, label) ->
                                val isSelected = ubicacion == key
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .background(
                                            if (isSelected) ButcherRedDark else ButcherDarkBg,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .border(
                                            1.dp,
                                            if (isSelected) ButcherRedPrimary else ButcherBorder,
                                            RoundedCornerShape(10.dp)
                                        )
                                        .clickable { ubicacion = key }
                                        .padding(vertical = 10.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        fontSize = 12.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) Color.White else TextSecondary
                                    )
                                }
                            }
                        }
                    }

                    // STOCK COUNTS INPUTS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        if (ubicacion == "Camara" || ubicacion == "Ambos") {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Stock Cámara (Cajas)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = ColdChamberBlue
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = stockCamaraText,
                                    onValueChange = { stockCamaraText = it.filter { ch -> ch.isDigit() } },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = ButcherDarkBg,
                                        unfocusedContainerColor = ButcherDarkBg,
                                        focusedBorderColor = ColdChamberBlue,
                                        unfocusedBorderColor = ButcherBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_stock_camara")
                                )
                            }
                        }

                        if (ubicacion == "Mural" || ubicacion == "Ambos") {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = "Stock Mural (Cajas)",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = DisplayMuralGreen
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                OutlinedTextField(
                                    value = stockMuralText,
                                    onValueChange = { stockMuralText = it.filter { ch -> ch.isDigit() } },
                                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                                    shape = RoundedCornerShape(12.dp),
                                    colors = OutlinedTextFieldDefaults.colors(
                                        focusedContainerColor = ButcherDarkBg,
                                        unfocusedContainerColor = ButcherDarkBg,
                                        focusedBorderColor = DisplayMuralGreen,
                                        unfocusedBorderColor = ButcherBorder,
                                        focusedTextColor = TextPrimary,
                                        unfocusedTextColor = TextPrimary
                                    ),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .testTag("input_stock_mural")
                                )
                            }
                        }
                    }
                }
            }
        }

        // FECHAS Y VENCIMIENTOS SECTION (Hasta 10)
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = ButcherSurface),
                shape = RoundedCornerShape(18.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .border(1.dp, ButcherBorder, RoundedCornerShape(18.dp))
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "FECHAS Y VENCIMIENTOS",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = ButcherRedLight
                            )
                            Text(
                                text = "Lotes y alertas (${fechasList.size} de 10)",
                                fontSize = 10.sp,
                                color = TextSecondary
                            )
                        }

                        Button(
                            onClick = {
                                if (fechasList.size < 10) {
                                    fechasList.add(
                                        FechaFormState(
                                            vencimiento = DateUtils.addDaysToToday(5),
                                            retiroDias = "2",
                                            alertaDias = "3"
                                        )
                                    )
                                }
                            },
                            enabled = fechasList.size < 10,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = ButcherBorder,
                                contentColor = TextPrimary
                            ),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.testTag("btn_agregar_fecha")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Añadir Fecha", fontSize = 11.sp)
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    fechasList.forEachIndexed { index, fechaState ->
                        FechaRowItem(
                            index = index,
                            state = fechaState,
                            canDelete = fechasList.size > 1,
                            onDelete = { fechasList.removeAt(index) },
                            onPickDate = {
                                val cal = Calendar.getInstance()
                                DatePickerDialog(
                                    context,
                                    { _, year, month, dayOfMonth ->
                                        val pickedDate = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth)
                                        fechaState.vencimiento = pickedDate
                                    },
                                    cal.get(Calendar.YEAR),
                                    cal.get(Calendar.MONTH),
                                    cal.get(Calendar.DAY_OF_MONTH)
                                ).show()
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
                }
            }
        }

        // Error message if any
        if (errorMessage != null) {
            item {
                Text(
                    text = errorMessage!!,
                    color = ExpiredRed,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        // SUBMIT BUTTON
        item {
            Button(
                onClick = {
                    val finalCategory = if (selectedCategory == "OTRO") customCategory.trim() else selectedCategory.trim()
                    val finalCorte = if (selectedCorte == "OTRO") customCorte.trim() else selectedCorte.trim()

                    if (finalCategory.isBlank()) {
                        errorMessage = "Por favor seleccione o escriba una categoría."
                        return@Button
                    }
                    if (finalCorte.isBlank()) {
                        errorMessage = "Por favor seleccione o escriba un corte."
                        return@Button
                    }

                    val camaraCount = if (ubicacion == "Camara" || ubicacion == "Ambos") stockCamaraText.toIntOrNull() ?: 0 else 0
                    val muralCount = if (ubicacion == "Mural" || ubicacion == "Ambos") stockMuralText.toIntOrNull() ?: 0 else 0

                    val lotes = fechasList.mapNotNull { f ->
                        if (f.vencimiento.isNotBlank()) {
                            FechaLote(
                                vencimiento = f.vencimiento,
                                retiroDias = f.retiroDias.toIntOrNull() ?: 2,
                                alertaDias = f.alertaDias.toIntOrNull() ?: 3,
                                loteCode = f.loteCode.ifBlank { null }
                            )
                        } else null
                    }

                    errorMessage = null
                    onSaveProduct(finalCategory, finalCorte, camaraCount, muralCount, lotes)
                },
                colors = ButtonDefaults.buttonColors(containerColor = ButcherRedPrimary),
                shape = RoundedCornerShape(14.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .testTag("btn_guardar_producto")
            ) {
                Text(
                    text = "Guardar Producto",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
            }
        }
    }
}

@Composable
private fun FechaRowItem(
    index: Int,
    state: FechaFormState,
    canDelete: Boolean,
    onDelete: () -> Unit,
    onPickDate: () -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = ButcherDarkBg.copy(alpha = 0.7f)),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, ButcherBorder.copy(alpha = 0.5f), RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Fecha #${index + 1}",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = ButcherRedLight
                )
                if (canDelete) {
                    IconButton(onClick = onDelete, modifier = Modifier.size(24.dp)) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Eliminar fecha",
                            tint = ExpiredRed,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Expiration Date picker field
            Text(text = "¿Cuándo vence? (YYYY-MM-DD)", fontSize = 10.sp, color = TextSecondary)
            Spacer(modifier = Modifier.height(2.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = state.vencimiento,
                    onValueChange = { state.vencimiento = it },
                    placeholder = { Text("YYYY-MM-DD", color = TextSecondary, fontSize = 12.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(10.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = ButcherSurface,
                        unfocusedContainerColor = ButcherSurface,
                        focusedBorderColor = ButcherRedPrimary,
                        unfocusedBorderColor = ButcherBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier
                        .weight(1f)
                        .testTag("fecha_input_$index")
                )

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = onPickDate,
                    modifier = Modifier
                        .background(ButcherBorder, RoundedCornerShape(10.dp))
                        .size(48.dp)
                        .testTag("btn_pick_date_$index")
                ) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = "Elegir fecha",
                        tint = ButcherRedLight,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Presets for days before retire & alert
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Días antes para retirar", fontSize = 10.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = state.retiroDias,
                        onValueChange = { state.retiroDias = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ButcherSurface,
                            unfocusedContainerColor = ButcherSurface,
                            focusedBorderColor = AlertAmber,
                            unfocusedBorderColor = ButcherBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Text(text = "Días antes de alerta", fontSize = 10.sp, color = TextSecondary)
                    Spacer(modifier = Modifier.height(2.dp))
                    OutlinedTextField(
                        value = state.alertaDias,
                        onValueChange = { state.alertaDias = it.filter { ch -> ch.isDigit() } },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        shape = RoundedCornerShape(10.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = ButcherSurface,
                            unfocusedContainerColor = ButcherSurface,
                            focusedBorderColor = AlertAmber,
                            unfocusedBorderColor = ButcherBorder,
                            focusedTextColor = TextPrimary,
                            unfocusedTextColor = TextPrimary
                        ),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }
    }
}
