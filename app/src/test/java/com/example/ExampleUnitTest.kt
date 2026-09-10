package com.example

import com.example.data.model.DateUtils
import com.example.data.model.SimpleDate
import com.example.data.model.SimpleYearMonth
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun testTiffinBillAndSavingsCalculation() {
        val pricePerTiffin = 50.0
        val totalDays = 30
        val tiffinsReceived = 25
        val leaveDays = 5

        // Formula: Tiffin Cost = Number of Present Days × ₹50
        val amountPayable = tiffinsReceived * pricePerTiffin
        assertEquals(1250.0, amountPayable, 0.001)

        // Formula: Saved Money = Number of Absent Days × ₹50
        val moneySaved = leaveDays * pricePerTiffin
        assertEquals(250.0, moneySaved, 0.001)

        // Carry forward: August savings (250) + September savings (300) = 550
        val augustSavings = 250.0
        val septemberSavings = 300.0
        val totalCarriedSavings = augustSavings + septemberSavings
        assertEquals(550.0, totalCarriedSavings, 0.001)
    }

    @Test
    fun testDateUtilsDaysInMonth() {
        assertEquals(30, DateUtils.daysInMonth(2026, 9)) // Sep 2026 has 30 days
        assertEquals(31, DateUtils.daysInMonth(2026, 8)) // Aug 2026 has 31 days
        assertEquals(28, DateUtils.daysInMonth(2026, 2)) // Feb 2026 has 28 days
        assertEquals(29, DateUtils.daysInMonth(2024, 2)) // Feb 2024 (leap year) has 29 days
    }

    @Test
    fun testDateUtilsNavigation() {
        val sep2026 = SimpleYearMonth(2026, 9)
        assertEquals(SimpleYearMonth(2026, 8), sep2026.previous())
        assertEquals(SimpleYearMonth(2026, 10), sep2026.next())

        val jan2026 = SimpleYearMonth(2026, 1)
        assertEquals(SimpleYearMonth(2025, 12), jan2026.previous())

        val dec2026 = SimpleYearMonth(2026, 12)
        assertEquals(SimpleYearMonth(2027, 1), dec2026.next())
    }
}
