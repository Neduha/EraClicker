package com.example.eraclicker.util

import java.text.DecimalFormat
import kotlin.math.floor
import kotlin.math.log10
import kotlin.math.pow

object NumberFormatter {

    private val suffixes = arrayOf(
        "",    // Base
        "K",   // Thousand  (10^3)
        "M",   // Million   (10^6)
        "B",   // Billion   (10^9)
        "T",   // Trillion  (10^12)
        "Q",   // Quadrillion (10^15)
        "Qi",  // Quintillion (10^18)
        "Sx",  // Sextillion  (10^21)
        "Sp",  // Septillion  (10^24)
        "Oc",  // Octillion   (10^27)
        "No",  // Nonillion   (10^30)
        "Dc"   // Decillion   (10^33)
    )

    private val decimalFormat = DecimalFormat("0.##")
    private val wholeNumberFormat = DecimalFormat("#,##0")

    fun format(number: Long): String {
        if (number < 0) return "-" + format(-number)
        if (number < 1_000_000_000L) {
            return wholeNumberFormat.format(number)
        }

        val valueB  = 1_000_000_000L              // 10^9
        val valueT  = 1_000_000_000_000L          // 10^12
        val valueQ  = 1_000_000_000_000_000L      // 10^15
        val valueQi = 1_000_000_000_000_000_000L  // 10^18
        val valueSx = 1.0E21                      // (10^21)
        val valueSp = 1.0E24                      // (10^24)
        val valueOc = 1.0E27                      // (10^27)
        val valueNo = 1.0E30                      // (10^30)
        val valueDc = 1.0E33                      // (10^33)

        val numberAsDouble = number.toDouble()

        return when {
            numberAsDouble >= valueDc -> "${decimalFormat.format(numberAsDouble / valueDc)} Dc"
            numberAsDouble >= valueNo -> "${decimalFormat.format(numberAsDouble / valueNo)} No"
            numberAsDouble >= valueOc -> "${decimalFormat.format(numberAsDouble / valueOc)} Oc"
            numberAsDouble >= valueSp -> "${decimalFormat.format(numberAsDouble / valueSp)} Sp"
            numberAsDouble >= valueSx -> "${decimalFormat.format(numberAsDouble / valueSx)} Sx"
            number >= valueQi -> "${decimalFormat.format(numberAsDouble / valueQi)} Qi"
            number >= valueQ  -> "${decimalFormat.format(numberAsDouble / valueQ)} Q"
            number >= valueT  -> "${decimalFormat.format(numberAsDouble / valueT)} T"
            number >= valueB  -> "${decimalFormat.format(numberAsDouble / valueB)} B"
            else -> wholeNumberFormat.format(number)
        }
    }

    fun formatGeneric(number: Long): String {
        if (number == 0L) return "0"
        if (number < 0) return "-" + formatGeneric(-number)
        if (number < 1000) return number.toString()

        val tier = floor(log10(number.toDouble()) / 3).toInt()

        if (tier == 0) {
            return number.toString()
        }
        if (tier >= suffixes.size) {

            val largestSuffixIndex = suffixes.size - 1
            val largestSuffix = suffixes[largestSuffixIndex]
            val valueForLargestSuffix = 1000.0.pow(largestSuffixIndex.toDouble())
            return "${decimalFormat.format(number.toDouble() / valueForLargestSuffix)}$largestSuffix"

        }

        val suffix = suffixes[tier]
        val value = 1000.0.pow(tier.toDouble())
        val scaledNumber = number.toDouble() / value

        return "${decimalFormat.format(scaledNumber)}$suffix"
    }
}