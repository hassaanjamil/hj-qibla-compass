package com.hassanjamil.hqibla

import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

private const val KAABA_LATITUDE = 21.422487
private const val KAABA_LONGITUDE = 39.826206

/**
 * Calculates the direction of the Kaaba (Qibla) relative to the given coordinates.
 *
 * @return Bearing in degrees where 0° represents North and values increase clockwise.
 */
fun calculateQiblaDirection(latitude: Double, longitude: Double): Float {
    val kaabaLatRad = Math.toRadians(KAABA_LATITUDE)
    val kaabaLngRad = Math.toRadians(KAABA_LONGITUDE)
    val userLatRad = Math.toRadians(latitude)
    val userLngRad = Math.toRadians(longitude)

    val longDiff = kaabaLngRad - userLngRad
    val y = sin(longDiff) * cos(kaabaLatRad)
    val x = cos(userLatRad) * sin(kaabaLatRad) -
        sin(userLatRad) * cos(kaabaLatRad) * cos(longDiff)

    val bearing = Math.toDegrees(atan2(y, x))
    return bearing.normalizeDegrees()
}

internal fun Double.normalizeDegrees(): Float = ((this + 360.0) % 360.0).toFloat()

internal fun Float.normalizeDegrees(): Float {
    var value = this % 360f
    if (value < 0f) value += 360f
    return value
}

internal fun Float.normalizeSignedDegrees(): Float {
    val normalized = normalizeDegrees()
    return if (normalized > 180f) normalized - 360f else normalized
}

internal fun Float.sanitizeRotation(previous: Float): Float {
    var delta = this - previous
    if (delta > 180f) {
        return this - 360f
    }
    if (delta < -180f) {
        return this + 360f
    }
    return this
}

fun bearingDifference(currentAzimuth: Float, targetAzimuth: Float): Float {
    val diff = (targetAzimuth - currentAzimuth + 540f) % 360f - 180f
    return diff
}

fun Float.toCardinalDirection(): String {
    if (this.isNaN()) return ""
    val directions = listOf("N", "NE", "E", "SE", "S", "SW", "W", "NW")
    val normalized = normalizeDegrees()
    val index = ((normalized + 22.5f) / 45f).toInt() % directions.size
    return directions[index]
}
