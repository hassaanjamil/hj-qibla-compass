package com.hassanjamil.hqibla.sample

import android.Manifest
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.hassanjamil.hqibla.sample.ui.theme.HjQiblaCompassTheme
import com.hassanjamil.hqibla.QiblaCompass
import com.hassanjamil.hqibla.QiblaCompassInfo
import com.hassanjamil.hqibla.bearingDifference
import com.hassanjamil.hqibla.hasLocationPermission
import com.hassanjamil.hqibla.rememberCompassAzimuth
import com.hassanjamil.hqibla.rememberLocationState
import com.hassanjamil.hqibla.rememberQiblaDirection
import com.hassanjamil.hqibla.toCardinalDirection
import kotlin.math.absoluteValue
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            HjQiblaCompassTheme {
                SampleApp()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun SampleApp() {
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }

    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { result ->
        hasLocationPermission = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    val azimuthState = rememberCompassAzimuth()
    val locationState = rememberLocationState(hasLocationPermission)
    val location = locationState.value
    val qiblaDirectionState = rememberQiblaDirection(location)
    val qiblaDirection = qiblaDirectionState.value
    val azimuth = azimuthState.value
    val bearingDelta = if (azimuth != null && qiblaDirection != null) {
        bearingDifference(azimuth, qiblaDirection)
    } else null

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            CenterAlignedTopAppBar(
                title = { Text(text = stringResource(id = R.string.app_name)) },
                colors = TopAppBarDefaults.centerAlignedTopAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                ),
                actions = {
                    IconButton(onClick = {
                        if (!hasLocationPermission) {
                            permissionLauncher.launch(
                                arrayOf(
                                    Manifest.permission.ACCESS_FINE_LOCATION,
                                    Manifest.permission.ACCESS_COARSE_LOCATION
                                )
                            )
                        }
                    }) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_location),
                            contentDescription = stringResource(id = R.string.request_permission)
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            PermissionSection(
                hasPermission = hasLocationPermission,
                onRequestPermission = {
                    permissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            )

            QiblaCompass(
                modifier = Modifier.size(280.dp),
                azimuthDegrees = azimuth,
                qiblaDirectionDegrees = qiblaDirection,
                location = location,
                animationDurationMillis = 260,
                showInfoPanel = false
            )

            QiblaCompass(
                modifier = Modifier.size(280.dp),
                azimuthDegrees = azimuth,
                qiblaDirectionDegrees = qiblaDirection,
                dialTint = MaterialTheme.colorScheme.primary.copy(alpha = 0.2f),
                indicatorTint = MaterialTheme.colorScheme.secondary,
                backgroundColor = MaterialTheme.colorScheme.surface,
                borderColor = MaterialTheme.colorScheme.primary,
                markerColor = MaterialTheme.colorScheme.secondary,
                markerContentColor = MaterialTheme.colorScheme.onSecondary,
                location = location,
                animationDurationMillis = 160,
//                infoContent = { info -> CustomCompassInfo(info = info) }
            )

            StatusCard(
                azimuth = azimuth,
                qiblaDirection = qiblaDirection,
                bearingDelta = bearingDelta,
                hasLocationPermission = hasLocationPermission,
                hasLocationFix = location != null
            )
        }
    }
}

@Composable
private fun PermissionSection(
    hasPermission: Boolean,
    onRequestPermission: () -> Unit
) {
    val message = if (hasPermission) {
        stringResource(id = R.string.permission_granted_message)
    } else {
        stringResource(id = R.string.permission_required_message)
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium
            )
            if (!hasPermission) {
                Button(onClick = onRequestPermission) {
                    Text(text = stringResource(id = R.string.request_permission))
                }
            }
        }
    }
}

@Composable
private fun CustomCompassInfo(info: QiblaCompassInfo) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Card(
            modifier = Modifier.padding(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.9f))
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = stringResource(id = R.string.custom_style_title),
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
                info.qiblaDirection?.let { direction ->
                    val formatted = String.format(Locale.ENGLISH, "%.0f°", direction)
                    Text(
                        text = stringResource(id = R.string.qibla_direction_format, formatted, direction.toCardinalDirection()),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
                info.azimuth?.let { heading ->
                    Text(
                        text = String.format(Locale.ENGLISH, stringResource(id = R.string.device_heading_format), heading),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                }
            }
        }
    }
}

@Composable
private fun StatusCard(
    azimuth: Float?,
    qiblaDirection: Float?,
    bearingDelta: Float?,
    hasLocationPermission: Boolean,
    hasLocationFix: Boolean
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = stringResource(id = R.string.live_status_title),
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            InfoRow(
                label = stringResource(id = R.string.status_heading),
                value = azimuth?.let { String.format(Locale.ENGLISH, "%.1f°", it) }
                    ?: stringResource(id = R.string.status_pending)
            )
            InfoRow(
                label = stringResource(id = R.string.status_qibla_direction),
                value = qiblaDirection?.let {
                    String.format(Locale.ENGLISH, "%.1f° (%s)", it, it.toCardinalDirection())
                }
                    ?: stringResource(id = R.string.status_pending)
            )
            InfoRow(
                label = stringResource(id = R.string.status_bearing_difference),
                value = bearingDelta?.let { String.format(Locale.ENGLISH, "%.1f°", it.absoluteValue) }
                    ?: stringResource(id = R.string.status_pending)
            )
            InfoRow(
                label = stringResource(id = R.string.status_permission),
                value = if (hasLocationPermission) stringResource(id = R.string.status_granted) else stringResource(id = R.string.status_denied)
            )
            InfoRow(
                label = stringResource(id = R.string.status_location_fix),
                value = if (hasLocationFix) stringResource(id = R.string.status_available) else stringResource(id = R.string.status_pending)
            )
        }
    }
}

@Composable
private fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            textAlign = TextAlign.End
        )
    }
}
