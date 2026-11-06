package com.example

import com.example.data.model.ExpirationStatus
import com.example.data.model.FechaLote
import com.example.util.DateUtils
import org.junit.Assert.assertEquals
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testDateUtils_daysUntilAndStatus() {
        // Today + 5 days with retiro 2 and alerta 3 -> EN_REGLA
        val futureDate = DateUtils.addDaysToToday(5)
        val fecha1 = FechaLote(vencimiento = futureDate, retiroDias = 2, alertaDias = 3)
        assertEquals(ExpirationStatus.EN_REGLA, DateUtils.getStatus(fecha1))

        // Today + 3 days with retiro 2 and alerta 3 -> ALERTA
        val alertDate = DateUtils.addDaysToToday(3)
        val fecha2 = FechaLote(vencimiento = alertDate, retiroDias = 2, alertaDias = 3)
        assertEquals(ExpirationStatus.ALERTA, DateUtils.getStatus(fecha2))

        // Today + 1 day with retiro 2 and alerta 3 -> RETIRAR
        val retiroDate = DateUtils.addDaysToToday(1)
        val fecha3 = FechaLote(vencimiento = retiroDate, retiroDias = 2, alertaDias = 3)
        assertEquals(ExpirationStatus.RETIRAR, DateUtils.getStatus(fecha3))

        // Past date (-2 days) -> VENCIDO
        val pastDate = DateUtils.addDaysToToday(-2)
        val fecha4 = FechaLote(vencimiento = pastDate, retiroDias = 2, alertaDias = 3)
        assertEquals(ExpirationStatus.VENCIDO, DateUtils.getStatus(fecha4))
    }
}
