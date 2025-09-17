package com.hassanjamil.hqibla

import android.location.Location
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.remember

/**
 * Returns a [State] containing the Qibla direction calculated from the given [location].
 */
@Composable
fun rememberQiblaDirection(location: Location?): State<Float?> {
    return remember(location) {
        derivedStateOf {
            location?.let { calculateQiblaDirection(it.latitude, it.longitude) }
        }
    }
}
