package com.example.util

import com.example.data.model.ExpirationStatus
import com.example.data.model.FechaLote
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

object DateUtils {
    private val dateFormat: SimpleDateFormat
        get() = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())

    private val displayFormat: SimpleDateFormat
        get() = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())

    fun getTodayString(): String {
        return dateFormat.format(Date())
    }

    fun daysUntil(targetDateStr: String): Int {
        if (targetDateStr.isBlank()) return 999
        return try {
            val target = dateFormat.parse(targetDateStr) ?: return 999
            val todayCal = Calendar.getInstance().apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val targetCal = Calendar.getInstance().apply {
                time = target
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }
            val diffMillis = targetCal.timeInMillis - todayCal.timeInMillis
            TimeUnit.MILLISECONDS.toDays(diffMillis).toInt()
        } catch (e: Exception) {
            999
        }
    }

    fun getStatus(fecha: FechaLote): ExpirationStatus {
        val days = daysUntil(fecha.vencimiento)
        return when {
            days < 0 -> ExpirationStatus.VENCIDO
            days <= fecha.retiroDias -> ExpirationStatus.RETIRAR
            days <= fecha.alertaDias -> ExpirationStatus.ALERTA
            else -> ExpirationStatus.EN_REGLA
        }
    }

    fun formatForDisplay(dateStr: String): String {
        return try {
            val date = dateFormat.parse(dateStr) ?: return dateStr
            displayFormat.format(date)
        } catch (e: Exception) {
            dateStr
        }
    }

    fun addDaysToToday(days: Int): String {
        val cal = Calendar.getInstance()
        cal.add(Calendar.DAY_OF_YEAR, days)
        return dateFormat.format(cal.time)
    }
}
