package com.hassanjamil.hqibla

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationListener
import android.location.LocationManager
import android.os.Build
import android.os.Bundle
import android.os.Looper
import androidx.annotation.RequiresPermission
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat

private const val DEFAULT_LOCATION_MIN_TIME = 1_000L
private const val DEFAULT_LOCATION_MIN_DISTANCE = 1f

/**
 * Convenience composable that subscribes to location updates while the caller has been granted
 * location permission. When permission is revoked, the last known location is cleared.
 */
@SuppressLint("MissingPermission")
@Composable
fun rememberLocationState(
    hasLocationPermission: Boolean,
    minTimeBetweenUpdatesMillis: Long = DEFAULT_LOCATION_MIN_TIME,
    minDistanceBetweenUpdatesMeters: Float = DEFAULT_LOCATION_MIN_DISTANCE,
    locationProviders: List<String> = listOf(
        LocationManager.GPS_PROVIDER,
        LocationManager.NETWORK_PROVIDER
    )
): State<Location?> {
    val context = LocalContext.current
    val locationState = remember { mutableStateOf<Location?>(null) }

    DisposableEffect(context, hasLocationPermission, minTimeBetweenUpdatesMillis, minDistanceBetweenUpdatesMeters, locationProviders) {
        if (!hasLocationPermission) {
            locationState.value = null
            return@DisposableEffect onDispose {}
        }

        val locationManager = context.locationManagerOrNull()
            ?: return@DisposableEffect onDispose {}

        val listener = object : LocationListener {
            override fun onLocationChanged(location: Location) {
                locationState.value = location
            }

            override fun onProviderEnabled(provider: String) = Unit

            override fun onProviderDisabled(provider: String) = Unit

            @Deprecated("Deprecated in Android 13")
            override fun onStatusChanged(provider: String?, status: Int, extras: Bundle?) = Unit
        }

        val looper = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            context.mainLooper
        } else {
            Looper.getMainLooper()
        }

        val registeredProviders = locationProviders.filter { provider ->
            locationManager.isProviderEnabled(provider)
        }

        if (registeredProviders.isEmpty()) {
            locationState.value = locationManager.bestLastKnownLocation(locationProviders)
            return@DisposableEffect onDispose {}
        }

        locationState.value = locationManager.bestLastKnownLocation(registeredProviders)

        val registrationResult = runCatching {
            registeredProviders.forEach { provider ->
                locationManager.requestLocationUpdates(
                    provider,
                    minTimeBetweenUpdatesMillis,
                    minDistanceBetweenUpdatesMeters,
                    listener,
                    looper
                )
            }
        }

        if (registrationResult.isFailure) {
            locationState.value = locationManager.bestLastKnownLocation(locationProviders)
            return@DisposableEffect onDispose {}
        }

        onDispose {
            locationManager.removeUpdates(listener)
        }
    }

    return locationState
}

fun Context.hasLocationPermission(): Boolean {
    val fineGranted = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    val coarseGranted = ContextCompat.checkSelfPermission(
        this,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    return fineGranted || coarseGranted
}

@RequiresPermission(anyOf = [Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION])
private fun LocationManager.bestLastKnownLocation(providers: List<String>): Location? {
    var bestLocation: Location? = null
    for (provider in providers) {
        runCatching { getLastKnownLocation(provider) }
            .getOrNull()
            ?.let { location ->
                if (bestLocation == null || location.time > (bestLocation?.time ?: 0L)) {
                    bestLocation = location
                }
            }
    }
    return bestLocation
}

private fun Context.locationManagerOrNull(): LocationManager? =
    getSystemService(Context.LOCATION_SERVICE) as? LocationManager
