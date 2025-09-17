package com.hassanjamil.hqibla

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext

private const val DEFAULT_SENSOR_SMOOTHING = 0.97f

/**
 * Provides a [State] that reflects the device azimuth relative to magnetic north while the caller is in composition.
 * Returns `null` when the required rotation or fallback sensors are unavailable or permission is missing.
 *
 * @param sensorDelay Sensor delay used for registration. Defaults to [SensorManager.SENSOR_DELAY_GAME].
 */
@Composable
fun rememberCompassAzimuth(sensorDelay: Int = SensorManager.SENSOR_DELAY_GAME): State<Float?> {
    val context = LocalContext.current
    val azimuth = remember { mutableStateOf<Float?>(null) }

    DisposableEffect(context, sensorDelay) {
        val sensorManager = context.sensorManagerOrNull()
            ?: return@DisposableEffect onDispose {}

        val rotationSensor = sensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR)
        val accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        val magnetometer = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)

        if (rotationSensor == null && (accelerometer == null || magnetometer == null)) {
            azimuth.value = null
            return@DisposableEffect onDispose {}
        }

        val rotationMatrix = FloatArray(9)
        val orientationAngles = FloatArray(3)
        val accelerometerReading = FloatArray(3)
        val magnetometerReading = FloatArray(3)

        var hasAccelerometer = false
        var hasMagnetometer = false

        val listener = object : SensorEventListener {
            override fun onSensorChanged(event: SensorEvent) {
                if (event.sensor.type == Sensor.TYPE_ROTATION_VECTOR) {
                    SensorManager.getRotationMatrixFromVector(rotationMatrix, event.values)
                    SensorManager.getOrientation(rotationMatrix, orientationAngles)
                    val azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat().normalizeDegrees()
                    azimuth.value = azimuthDegrees
                    return
                }

                when (event.sensor.type) {
                    Sensor.TYPE_ACCELEROMETER -> {
                        event.values.copyIntoLowPass(accelerometerReading)
                        hasAccelerometer = true
                    }

                    Sensor.TYPE_MAGNETIC_FIELD -> {
                        event.values.copyIntoLowPass(magnetometerReading)
                        hasMagnetometer = true
                    }
                }

                if (hasAccelerometer && hasMagnetometer) {
                    val success = SensorManager.getRotationMatrix(
                        rotationMatrix,
                        null,
                        accelerometerReading,
                        magnetometerReading
                    )
                    if (success) {
                        SensorManager.getOrientation(rotationMatrix, orientationAngles)
                        val azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat().normalizeDegrees()
                        azimuth.value = azimuthDegrees
                    }
                }
            }

            override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) = Unit
        }

        if (rotationSensor != null) {
            sensorManager.registerListener(listener, rotationSensor, sensorDelay)
        } else {
            sensorManager.registerListener(listener, accelerometer, sensorDelay)
            sensorManager.registerListener(listener, magnetometer, sensorDelay)
        }

        onDispose {
            sensorManager.unregisterListener(listener)
        }
    }

    return azimuth
}

private fun Context.sensorManagerOrNull(): SensorManager? =
    getSystemService(Context.SENSOR_SERVICE) as? SensorManager

private fun FloatArray.copyIntoLowPass(destination: FloatArray, alpha: Float = DEFAULT_SENSOR_SMOOTHING) {
    for (index in indices) {
        destination[index] = alpha * destination[index] + (1f - alpha) * this[index]
    }
}
