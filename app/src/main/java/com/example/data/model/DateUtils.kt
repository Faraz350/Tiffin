package com.example.data.model

import java.util.Calendar
import java.util.Locale

data class SimpleDate(
    val year: Int,
    val month: Int, // 1 to 12
    val day: Int
) : Comparable<SimpleDate> {
    val isoString: String
        get() = String.format(Locale.US, "%04d-%02d-%02d", year, month, day)

    val yearMonthIso: String
        get() = String.format(Locale.US, "%04d-%02d", year, month)

    fun dayOfWeek(): Int {
        // 1 = Monday, 7 = Sunday
        val cal = Calendar.getInstance().apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month - 1)
            set(Calendar.DAY_OF_MONTH, day)
        }
        val dow = cal.get(Calendar.DAY_OF_WEEK)
        // Calendar.SUNDAY = 1, MONDAY = 2, ... SATURDAY = 7
        return if (dow == Calendar.SUNDAY) 7 else dow - 1
    }

    fun displayString(): String {
        return "$day ${DateUtils.shortMonthName(month)} $year"
    }

    fun fullDisplayString(): String {
        val days = arrayOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
        val dowName = days[(dayOfWeek() - 1).coerceIn(0, 6)]
        return "$dowName, $day ${DateUtils.monthName(month)} $year"
    }

    override fun compareTo(other: SimpleDate): Int {
        if (year != other.year) return year.compareTo(other.year)
        if (month != other.month) return month.compareTo(other.month)
        return day.compareTo(other.day)
    }

    companion object {
        fun parse(isoString: String): SimpleDate? {
            val parts = isoString.split("-")
            if (parts.size != 3) return null
            return try {
                SimpleDate(parts[0].toInt(), parts[1].toInt(), parts[2].toInt())
            } catch (e: Exception) {
                null
            }
        }
    }
}

data class SimpleYearMonth(
    val year: Int,
    val month: Int // 1 to 12
) : Comparable<SimpleYearMonth> {
    val isoString: String
        get() = String.format(Locale.US, "%04d-%02d", year, month)

    fun daysInMonth(): Int {
        return DateUtils.daysInMonth(year, month)
    }

    fun displayString(): String {
        return "${DateUtils.monthName(month)} $year"
    }

    fun previous(): SimpleYearMonth {
        return if (month == 1) {
            SimpleYearMonth(year - 1, 12)
        } else {
            SimpleYearMonth(year, month - 1)
        }
    }

    fun next(): SimpleYearMonth {
        return if (month == 12) {
            SimpleYearMonth(year + 1, 1)
        } else {
            SimpleYearMonth(year, month + 1)
        }
    }

    override fun compareTo(other: SimpleYearMonth): Int {
        if (year != other.year) return year.compareTo(other.year)
        return month.compareTo(other.month)
    }

    companion object {
        fun parse(isoString: String): SimpleYearMonth? {
            val parts = isoString.split("-")
            if (parts.size != 2) return null
            return try {
                SimpleYearMonth(parts[0].toInt(), parts[1].toInt())
            } catch (e: Exception) {
                null
            }
        }
    }
}

object DateUtils {
    private val MONTH_NAMES = arrayOf(
        "January", "February", "March", "April", "May", "June",
        "July", "August", "September", "October", "November", "December"
    )

    private val SHORT_MONTH_NAMES = arrayOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    fun monthName(month: Int): String {
        return MONTH_NAMES.getOrElse(month - 1) { "" }
    }

    fun shortMonthName(month: Int): String {
        return SHORT_MONTH_NAMES.getOrElse(month - 1) { "" }
    }

    fun today(): SimpleDate {
        val cal = Calendar.getInstance()
        return SimpleDate(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    fun currentYearMonth(): SimpleYearMonth {
        val t = today()
        return SimpleYearMonth(t.year, t.month)
    }

    fun daysInMonth(year: Int, month: Int): Int {
        return when (month) {
            1, 3, 5, 7, 8, 10, 12 -> 31
            4, 6, 9, 11 -> 30
            2 -> if (isLeapYear(year)) 29 else 28
            else -> 30
        }
    }

    fun isLeapYear(year: Int): Boolean {
        return (year % 4 == 0 && year % 100 != 0) || (year % 400 == 0)
    }

    fun firstDayOfWeek(year: Int, month: Int): Int {
        val firstDate = SimpleDate(year, month, 1)
        return firstDate.dayOfWeek()
    }
}
