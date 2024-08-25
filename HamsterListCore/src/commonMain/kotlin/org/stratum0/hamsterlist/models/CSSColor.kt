package org.stratum0.hamsterlist.models

import io.github.oshai.kotlinlogging.KotlinLogging

private val logger = KotlinLogging.logger {}

sealed class CSSColor {
    companion object {
        private const val BASE_16 = 16
        operator fun invoke(input: String): CSSColor? {
            return if (Regex(RGBAColor.PATTERN).matches(input)) {
                RGBAColor(input)
            } else if (Regex(HSLColor.PATTERN).matches(input)) {
                HSLColor(input)
            } else {
                null
            }
        }
    }

    data class RGBAColor(
        val red: Int,
        val green: Int,
        val blue: Int,
        val alpha: Int
    ) : CSSColor() {
        companion object {
            const val PATTERN = "^#[0-9a-fA-F]{6}\$"

            @Suppress("TooGenericExceptionCaught")
            operator fun invoke(input: String): RGBAColor? {
                return try {
                    val rgbBytes = input
                        .drop(1)
                        .chunked(2)
                        .map { it.toInt(BASE_16) }
                    RGBAColor(
                        red = rgbBytes[0],
                        green = rgbBytes[1],
                        blue = rgbBytes[2],
                        alpha = 0xFF
                    )
                } catch (e: Exception) {
                    logger.error(e) { "failed to parse RBG color from $input" }
                    null
                }
            }
        }
    }

    data class HSLColor(
        val hue: Double,
        val saturation: Double,
        val lightness: Double
    ) : CSSColor() {
        companion object{
            private const val PREFIX_COUNT = 4
            private const val HUNDRED_PERCENT = 100.0
            const val PATTERN = "^hsl\\(\\d+(\\.\\d+)?, \\d+(\\.\\d+)?%, \\d+(.?\\d+)?%\\)\$"

            @Suppress("TooGenericExceptionCaught")
            operator fun invoke(input: String): HSLColor? {
                return try {
                    val hslInput = input
                        .substring(PREFIX_COUNT, input.length - 1)
                        .filter { it != '%' }
                        .split(", ")
                        .map { it.toDouble() }
                    HSLColor(
                        hslInput[0],
                        hslInput[1] / HUNDRED_PERCENT,
                        hslInput[2] / HUNDRED_PERCENT
                    )
                } catch (e: Exception) {
                    logger.error(e) { "failed to parse HSL color from $input" }
                    null
                }
            }
        }
    }
}