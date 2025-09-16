package com.hassanjamil.hqibla

import android.location.Location
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import java.util.Locale
import kotlin.math.abs

/**
 * A composable Qibla compass that reacts to the current azimuth and optionally a Qibla bearing.
 *
 * @param modifier Modifier applied to the compass container. Apply padding/size/rotation using this.
 * @param azimuthDegrees Heading of the device in degrees where 0° represents magnetic North.
 * @param qiblaDirectionDegrees Bearing towards Kaaba in degrees if available.
 * @param dialPainter Painter for the dial background image.
 * @param indicatorPainter Painter used for the Qibla indicator.
 * @param dialTint Optional tint applied to the dial.
 * @param indicatorTint Optional tint applied to the Qibla indicator.
 * @param backgroundColor Background color for the compass container.
 * @param borderColor Color of the outer border.
 * @param borderWidth Border width for the compass container.
 * @param showNorthMarker Whether to render a marker at the north direction.
 * @param markerColor Color of the north marker.
 * @param markerContentColor Color of the north marker content.
 * @param location Optional device location.
 * @param animationDurationMillis Duration for dial and indicator animations in milliseconds, default is 320.
 * @param infoContent Optional content rendered on top of the compass. Defaults to [QiblaCompassDefaults.InfoPanel].
 * @param showInfoPanel Whether to display the information panel overlay on the compass.
 */
@Composable
fun QiblaCompass(
    modifier: Modifier = Modifier,
    azimuthDegrees: Float?,
    qiblaDirectionDegrees: Float?,
    dialPainter: Painter = painterResource(id = R.drawable.dial),
    indicatorPainter: Painter = painterResource(id = R.drawable.qibla),
    dialTint: Color? = null,
    indicatorTint: Color? = null,
    backgroundColor: Color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.9f),
    borderColor: Color = MaterialTheme.colorScheme.outlineVariant,
    borderWidth: Dp = 2.dp,
    showNorthMarker: Boolean = true,
    markerColor: Color = MaterialTheme.colorScheme.primary,
    markerContentColor: Color = MaterialTheme.colorScheme.onPrimary,
    location: Location? = null,
    animationDurationMillis: Int = 320,
    infoContent: (@Composable BoxScope.(QiblaCompassInfo) -> Unit)? = QiblaCompassDefaults.InfoPanel,
    showInfoPanel: Boolean = true
) {
    var dialPrevious by remember { mutableFloatStateOf(0f) }
    var indicatorPrevious by remember { mutableFloatStateOf(0f) }

    val dialTarget = azimuthDegrees?.let { -it.normalizeDegrees() }
    val dialAnimatedTarget = dialTarget?.sanitizeRotation(dialPrevious) ?: dialPrevious
    val dialRotation by animateFloatAsState(
        targetValue = dialAnimatedTarget,
        animationSpec = tween(durationMillis = animationDurationMillis, easing = FastOutSlowInEasing),
        label = "dialRotation"
    )

    LaunchedEffect(dialRotation, dialTarget) {
        if (dialTarget != null) {
            dialPrevious = dialRotation
        }
    }

    val indicatorTarget = if (azimuthDegrees != null && qiblaDirectionDegrees != null) {
        (qiblaDirectionDegrees - azimuthDegrees).normalizeSignedDegrees()
    } else null
    val indicatorAnimatedTarget = if (indicatorTarget != null) indicatorTarget.sanitizeRotation(indicatorPrevious) else indicatorPrevious
    val indicatorRotation by animateFloatAsState(
        targetValue = indicatorAnimatedTarget,
        animationSpec = tween(durationMillis = animationDurationMillis, easing = FastOutSlowInEasing),
        label = "indicatorRotation"
    )

    LaunchedEffect(indicatorRotation, indicatorTarget) {
        if (indicatorTarget != null) {
            indicatorPrevious = indicatorRotation
        }
    }

    val info = remember(azimuthDegrees, qiblaDirectionDegrees, location) {
        val bearingDifference = if (azimuthDegrees != null && qiblaDirectionDegrees != null) {
            bearingDifference(azimuthDegrees, qiblaDirectionDegrees)
        } else null
        QiblaCompassInfo(
            azimuth = azimuthDegrees,
            qiblaDirection = qiblaDirectionDegrees,
            bearingDifference = bearingDifference,
            location = location
        )
    }

    Box(
        modifier = modifier
            .aspectRatio(1f)
            .clip(CircleShape)
            .background(backgroundColor)
            .border(borderWidth, borderColor, CircleShape)
            .padding(12.dp),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = dialPainter,
            contentDescription = null,
            contentScale = ContentScale.FillBounds,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer { rotationZ = dialRotation },
            colorFilter = dialTint?.let { ColorFilter.tint(it) }
        )

        if (qiblaDirectionDegrees != null) {
            Image(
                painter = indicatorPainter,
                contentDescription = null,
                contentScale = ContentScale.FillBounds,
                modifier = Modifier
                    .fillMaxSize()
                    .graphicsLayer { rotationZ = indicatorRotation },
                colorFilter = indicatorTint?.let { ColorFilter.tint(it) }
            )
        }

        if (showNorthMarker) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(top = 20.dp),
                contentAlignment = Alignment.TopCenter
            ) {
                Surface(
                    color = markerColor,
                    contentColor = markerContentColor,
                    shape = RoundedCornerShape(50)
                ) {
                    Text(
                        text = "N",
                        style = MaterialTheme.typography.labelLarge,
                        color = markerContentColor,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        if (showInfoPanel) {
        if (showInfoPanel) {
            infoContent?.invoke(this, info)
        }
    }
}
}

/**
 * Data model describing the values shown on the compass.
 */
data class QiblaCompassInfo(
    val azimuth: Float?,
    val qiblaDirection: Float?,
    val bearingDifference: Float?,
    val location: Location?
)

object QiblaCompassDefaults {
    val InfoPanel: @Composable BoxScope.(QiblaCompassInfo) -> Unit = { info ->
        if (info.azimuth == null && info.qiblaDirection == null) {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = stringResource(id = R.string.qibla_compass_calibrating),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        textAlign = TextAlign.Center
                    )
                }
            }
        } else {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.BottomCenter
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .clip(RoundedCornerShape(18.dp))
                        .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.85f))
                        .padding(horizontal = 20.dp, vertical = 16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    info.qiblaDirection?.let { direction ->
                        val directionText = String.format(
                            Locale.ENGLISH,
                            stringResource(id = R.string.qibla_compass_direction),
                            direction.normalizeDegrees(),
                            direction.toCardinalDirection()
                        )
                        Text(
                            text = directionText,
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface,
                            textAlign = TextAlign.Center
                        )
                    }

                    info.azimuth?.let { heading ->
                        val headingText = String.format(
                            Locale.ENGLISH,
                            stringResource(id = R.string.qibla_compass_heading),
                            heading.normalizeDegrees()
                        )
                        Text(
                            text = headingText,
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 4.dp)
                        )
                    }

                    info.bearingDifference?.let { diff ->
                        val absolute = abs(diff)
                        val turnText = when {
                            absolute < 2f -> stringResource(id = R.string.qibla_compass_aligned)
                            diff > 0 -> String.format(
                                Locale.ENGLISH,
                                stringResource(id = R.string.qibla_compass_turn_right),
                                absolute
                            )
                            else -> String.format(
                                Locale.ENGLISH,
                                stringResource(id = R.string.qibla_compass_turn_left),
                                absolute
                            )
                        }
                        Text(
                            text = turnText,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }

                    info.location?.let { location ->
                        val locationText = String.format(
                            Locale.ENGLISH,
                            "%.4f, %.4f",
                            location.latitude,
                            location.longitude
                        )
                        Text(
                            text = locationText,
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 6.dp)
                        )
                    }
                }
            }
        }
    }
}
