package com.example.data.db

import androidx.room.TypeConverter
import com.example.data.model.FechaLote
import org.json.JSONArray
import org.json.JSONObject

class Converters {
    @TypeConverter
    fun fromFechaList(list: List<FechaLote>?): String {
        if (list.isNullOrEmpty()) return "[]"
        val array = JSONArray()
        for (item in list) {
            val obj = JSONObject().apply {
                put("vencimiento", item.vencimiento)
                put("retiroDias", item.retiroDias)
                put("alertaDias", item.alertaDias)
                put("loteCode", item.loteCode ?: "")
                put("cajas", item.cajas)
            }
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toFechaList(data: String?): List<FechaLote> {
        if (data.isNullOrBlank()) return emptyList()
        val list = mutableListOf<FechaLote>()
        try {
            val array = JSONArray(data)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    FechaLote(
                        vencimiento = obj.optString("vencimiento", ""),
                        retiroDias = obj.optInt("retiroDias", 2),
                        alertaDias = obj.optInt("alertaDias", 3),
                        loteCode = obj.optString("loteCode", "").takeIf { it.isNotBlank() },
                        cajas = obj.optInt("cajas", 0)
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }
}
