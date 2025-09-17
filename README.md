# hjQiblaCompass

Modern Android library that renders a customizable Qibla compass using Kotlin and Jetpack Compose. It provides lifecycle-aware sensor and location helpers together with a ready-to-use compass composable that you can drop into your app.

## Features
- 100% Kotlin + Jetpack Compose implementation; no legacy activities or XML views.
- Lifecycle-aware sensor reader with graceful fallbacks when rotation-vector sensors are missing.
- Location helper with permission-aware updates and automatic last-known-location fallback.
- Highly customizable UI: dial/indicator painters, color tints, info overlays, marker visibility, and animation duration.
- Sample app demonstrating permission flow, dual compass styles, and live status cards.

## Get Started
### 1. Add the dependency
Make sure your project is using **Android Gradle Plugin 8.2+**, **Kotlin 1.9+**, and has Compose enabled.

```kotlin
// settings.gradle
pluginManagement {
    repositories {
        google()
        mavenCentral()
    }
}
```

```kotlin
// build.gradle (project)
allprojects {
    repositories {
        google()
        mavenCentral()
    }
}
```

```kotlin
// module-level build.gradle(.kts)
dependencies {
    implementation("io.github.hassaanjamil:qibla:1.0.0")
}
```

> ⚠️ The library is built with Compose. Ensure your module has `buildFeatures { compose = true }` and the appropriate `composeOptions` configured.

### 2. Request permissions (if you need automatic location)
The helper `rememberLocationState` expects that at least coarse or fine location permission has been granted. Handle the runtime permission flow in your composable or activity.

### 3. Render the compass
```kotlin
@Composable
fun QiblaScreen() {
    val context = LocalContext.current
    var hasLocationPermission by remember { mutableStateOf(context.hasLocationPermission()) }
    val permissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { result ->
        hasLocationPermission = result[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            result[Manifest.permission.ACCESS_COARSE_LOCATION] == true
    }

    val azimuthState = rememberCompassAzimuth()
    val locationState = rememberLocationState(hasLocationPermission)
    val qiblaDirection = rememberQiblaDirection(locationState.value).value

    QiblaCompass(
        azimuthDegrees = azimuthState.value,
        qiblaDirectionDegrees = qiblaDirection,
        location = locationState.value,
        animationDurationMillis = 260,
        dialTint = MaterialTheme.colorScheme.primary.copy(alpha = 0.15f),
        indicatorTint = MaterialTheme.colorScheme.secondary
    )
}
```

### Customization Options
`QiblaCompass` exposes several parameters so you can brand the experience:

- `dialPainter` / `indicatorPainter`: Replace default resources with your own `Painter` instances.
- `dialTint`, `indicatorTint`, `backgroundColor`, `borderColor`, `markerColor`, `markerContentColor`.
- `animationDurationMillis`: Control how fast the indicator reacts to sensor updates.
- `showNorthMarker`: Toggle the top “N” badge.
- `infoContent`: Provide custom overlay content; the default shows heading, Qibla direction, bearing difference, and coordinates.
- `showInfoPanel`: Hide or show the overlay entirely.

For location helpers:
- `rememberLocationState` lets you configure min update time/distance and providers list.

## Sample App
The `app` module contains a Compose-based sample that demonstrates permission handling and two styling approaches. Launch it to see the compass in action.

```bash
./gradlew :app:assembleDebug
```

## Contributing
Pull requests and issues are welcome! Please open a discussion if you have suggestions for new customization points or sensor improvements.

## License
Apache License 2.0 – see [LICENSE](LICENSE) for details.
