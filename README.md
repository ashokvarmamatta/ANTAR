<p align="center">
  <img src="docs/icon.webp" width="120" alt="ANTAR app icon: a satellite over a dark dial" />
</p>

<h1 align="center">ANTAR</h1>

<p align="center">
  <b>Every chip, sensor and signal on your Android phone, on one offline dashboard.</b><br/>
  12 tabs: CPU, battery, GPS satellites, sensors, display, storage, network, camera, installed apps and more. No account, no ads, no internet permission.
</p>

<p align="center">
  <a href="https://antar.ashokvarma.dev"><img alt="Website" src="https://img.shields.io/badge/Website-antar.ashokvarma.dev-00D4AA?style=for-the-badge&logo=googlechrome&logoColor=white"></a>
  <a href="https://github.com/ashokvarmamatta/ANTAR/releases/latest"><img alt="Latest release" src="https://img.shields.io/github/v/release/ashokvarmamatta/ANTAR?style=for-the-badge&label=Release"></a>
  <img alt="Android 7.0+" src="https://img.shields.io/badge/Android-7.0%2B-3DDC84?style=for-the-badge&logo=android&logoColor=white">
  <img alt="Kotlin and Jetpack Compose" src="https://img.shields.io/badge/Kotlin-Compose-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white">
</p>

<p align="center"><b>Website:</b> <a href="https://antar.ashokvarma.dev">antar.ashokvarma.dev</a></p>

---

## What it does

| Tab | What you see |
|---|---|
| **Dashboard** | RAM and internal-storage usage, battery level/temperature/voltage, processor, sensor count, installed-app count, uptime |
| **Device** | model, manufacturer, board, hardware, Android device ID, build fingerprint, operator |
| **System** | Android version and codename, API level, security patch, baseband, root check, system-as-root, seamless updates, dynamic partitions, uptime |
| **CPU** | SoC name, core count, frequency range, ABIs, instruction-set features |
| **Battery** | live level ring, charge cycles, live mAh graph, 24 h / 7 day history, charging sessions, design vs. estimated capacity |
| **Location** | coordinates, address, satellites per constellation (GPS, GLONASS, Galileo, BeiDou, QZSS, IRNSS, SBAS), PDOP/HDOP/VDOP, accuracy |
| **Network** | Wi-Fi and SIM details, IP, DNS, gateway, Wi-Fi security type |
| **Storage** | RAM, internal storage and `/system` partition usage |
| **Display** | resolution, physical size, refresh rate, HDR, density bucket, brightness mode, timeout |
| **Sensors** | every hardware sensor with type, vendor and power draw |
| **Apps** | installed apps (All / System / User) with version and target API, search |
| **Camera** | per-camera megapixels, active array, RAW support, pixel-binning status, hardware level |

Settings: theme (system/light/dark), dynamic colours, accent colour, animation intensity, privacy summary.

## Install

1. **[Download ANTAR.apk](https://github.com/ashokvarmamatta/ANTAR/releases/latest/download/ANTAR.apk)** (latest release) · see what changed in the **[release notes](https://github.com/ashokvarmamatta/ANTAR/releases/latest)** · **[all releases](https://github.com/ashokvarmamatta/ANTAR/releases)**.
2. Open it on your phone and allow installing from this source when Android asks.

Requires Android 7.0 (API 24) or newer. No account needed; works fully offline. Every release is signed with the
same key, so a newer `ANTAR.apk` installs over the previous one.

The APK is a debug build of the `prod` branch. If you want a release build signed with your own key,
[build from source](#build) and run `./gradlew assembleRelease` with your signing config.

## Screenshots

<table>
  <tr>
    <td align="center"><img src="docs/screenshots/01_dashboard.webp" width="200" alt="Dashboard with RAM ring, storage bar, battery card and processor, sensor, app and health tiles"/><br/><sub><b>Dashboard</b></sub></td>
    <td align="center"><img src="docs/screenshots/02_device.webp" width="200" alt="Device tab listing model, manufacturer, board, hardware and identifiers"/><br/><sub><b>Device</b></sub></td>
    <td align="center"><img src="docs/screenshots/03_system.webp" width="200" alt="System tab with Android version, API level, build and security patch"/><br/><sub><b>System</b></sub></td>
    <td align="center"><img src="docs/screenshots/04_cpu.webp" width="200" alt="CPU tab with SoC name, cores, frequency range and instruction set"/><br/><sub><b>CPU</b></sub></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/screenshots/05_battery.webp" width="200" alt="Battery tab with level ring, cycle count, live mAh graph and 24 hour history"/><br/><sub><b>Battery</b></sub></td>
    <td align="center"><img src="docs/screenshots/06_location.webp" width="200" alt="Location tab with satellite counts per constellation; coordinates and address blurred"/><br/><sub><b>Location</b> (position blurred)</sub></td>
    <td align="center"><img src="docs/screenshots/08_storage.webp" width="200" alt="Storage tab with RAM, internal storage and system partition usage"/><br/><sub><b>Storage</b></sub></td>
    <td align="center"><img src="docs/screenshots/09_display.webp" width="200" alt="Display tab with resolution, refresh rate, HDR and DPI"/><br/><sub><b>Display</b></sub></td>
  </tr>
  <tr>
    <td align="center"><img src="docs/screenshots/10_sensors.webp" width="200" alt="Sensors tab listing hardware sensors with vendor and power"/><br/><sub><b>Sensors</b></sub></td>
    <td align="center"><img src="docs/screenshots/11_apps.webp" width="200" alt="Apps tab filtered to system apps with version and target API"/><br/><sub><b>Apps</b></sub></td>
    <td align="center"><img src="docs/screenshots/12_camera.webp" width="200" alt="Camera tab with megapixels, pixel binning notice and sensor details"/><br/><sub><b>Camera</b></sub></td>
    <td></td>
  </tr>
</table>

Captured from v1.5.0 on a POCO X6 Pro 5G (Android 16).

## Permissions & privacy

| Permission | Why it is needed |
|---|---|
| `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` | GPS coordinates, satellite status and the address on the Location tab; Wi-Fi security type on the Network tab |
| `NEARBY_WIFI_DEVICES` (`neverForLocation`) | Wi-Fi details on the Network tab (Android 13+) |
| `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE` | connection type, IP, DNS and gateway |
| `QUERY_ALL_PACKAGES` | the installed-apps list on the Apps tab |

Location and nearby-Wi-Fi are requested only after an in-app explanation, and the installed-apps list is read
only after you accept a one-time disclosure on the Apps tab (until then the dashboard shows `- - -` for the app
count).

The app declares **no `INTERNET` permission**, so nothing it reads can be sent anywhere. What it stores, on the
device only:

- settings, plus a cached copy of the installed-apps list so the Apps tab opens instantly (SharedPreferences)
- a battery log every 15 minutes, kept for 30 days (Room database `antar_db`)

Android Auto Backup is enabled (`allowBackup="true"`), so these files are included in your device's Google
backup. Uninstalling the app deletes them from the phone.

## How it works

### Live battery readings that stop when you leave
`ACTION_BATTERY_CHANGED` only fires when the level or charger changes, but current and power move every second,
so the stream combines the broadcast with a 2-second poll:

<!-- src: app/src/main/java/com/ashes/dev/works/system/core/internals/antar/data/repository/BatteryRepositoryImpl.kt -->
```kotlin
        context.registerReceiver(receiver, IntentFilter(Intent.ACTION_BATTERY_CHANGED))

        // The receiver already pushes an update on every real battery change. We poll only to refresh
        // live current/power/temperature; 2s keeps it responsive while cutting per-tick work ~4x.
        val pollJob = launch {
            while (true) {
                pollBattery()
                delay(2000)
            }
        }

        awaitClose {
            context.unregisterReceiver(receiver)
            pollJob.cancel()
        }
    }.distinctUntilChanged() // Only emit when the Battery data actually changes
        .flowOn(Dispatchers.IO) // PowerProfile reflection, sticky-intent and sysfs reads stay off main
```

The ViewModels share it with `SharingStarted.WhileSubscribed(5000)`, so the receiver and the poll are torn down
5 seconds after the screen is no longer visible.

### Address lookup that never blocks the GPS callbacks
Location updates arrive every 2 seconds plus a GNSS status callback, and reverse geocoding can take seconds on
a slow network. Geocoding runs asynchronously and only after the position moves at least
`GEOCODE_MIN_DISTANCE_M = 25f` metres:

<!-- src: app/src/main/java/com/ashes/dev/works/system/core/internals/antar/data/repository/LocationRepositoryImpl.kt -->
```kotlin
        fun geocodeIfMoved(location: AndroidLocation) {
            val last = lastGeocoded
            if (last != null && last.distanceTo(location) < GEOCODE_MIN_DISTANCE_M) return
            lastGeocoded = location
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                geocoder.getFromLocation(location.latitude, location.longitude, 1) { results ->
                    address = results.firstOrNull()?.getAddressLine(0) ?: "- - -"
                    tryEmitLocation()
                }
            } else {
                launch(Dispatchers.IO) {
```

### 30 days of battery history from a background worker
A WorkManager job, scheduled every 15 minutes in `AntarApp.kt`, stores one reading and trims anything older than
30 days:

<!-- src: app/src/main/java/com/ashes/dev/works/system/core/internals/antar/data/worker/BatteryLogWorker.kt -->
```kotlin
            dao.insert(log)

            // Purge logs older than 30 days
            val thirtyDaysAgo = System.currentTimeMillis() - TimeUnit.DAYS.toMillis(30)
            dao.deleteOlderThan(thirtyDaysAgo)

            Result.success()
        } catch (e: CancellationException) {
            throw e // WorkManager stopped us — that's not a failure to retry
        } catch (e: Exception) {
            // A persistent error (disk full, corrupt DB) must not retry forever every period.
            if (runAttemptCount < MAX_RETRIES) Result.retry() else Result.failure()
        }
```

The Battery tab reads it back as a Room `Flow` for the 24 h and 7 day charts and the charging-session list.

### No package scan before consent
The dashboard's app count comes from the same package query the Apps tab uses, so it waits for the disclosure:

<!-- src: app/src/main/java/com/ashes/dev/works/system/core/internals/antar/data/repository/DashboardRepositoryImpl.kt -->
```kotlin
                // The installed-apps disclosure is shown on the Apps screen; until the user has agreed
                // there, don't enumerate packages at all — not even just to count them.
                if (cachedApps == null && preferences.appsConsentGiven) {
                    cachedApps = "${appsRepository.getAppCount()} apps"
                }
```

## Build

Clone `https://github.com/ashokvarmamatta/ANTAR.git`, then from the project folder:

```bash
./gradlew assembleDebug
./gradlew testDebugUnitTest
```

The debug APK lands in `app/build/outputs/apk/debug/`.

Toolchain: JDK 25 · Gradle 9.6.0 (wrapper) · Android Gradle Plugin 9.4.0 · Kotlin 2.4.20 · compileSdk/targetSdk 37 ·
minSdk 24. No API keys or config files are needed.

Libraries: Jetpack Compose (BOM 2026.09.00) with Material 3 · Navigation Compose 2.10.1 · Koin 4.2.2 ·
Room 2.8.5 (KSP 2.3.12) · WorkManager 2.11.2 · Kotlin Coroutines 1.11.0 · Accompanist Permissions 0.37.3 ·
Core SplashScreen 1.2.0.

Branches: work lands on `test`, moves to `dev`, then `prod`. Every push to `prod` builds `ANTAR.apk` and attaches
it to the GitHub Release tagged `v<versionName>`.

## Structure

88 Kotlin files, 10,037 lines, in `app/src/main/java/com/ashes/dev/works/system/core/internals/antar/`:

| Package | Owns |
|---|---|
| `presentation/screens/` | one Compose screen per tab, the pager host (`MainScreen.kt`), Settings, intro |
| `presentation/viewmodel/` | a ViewModel per screen, exposing `StateFlow`s |
| `presentation/theme/`, `components/` | colours, typography, motion, splash, dialogs, sensor icons |
| `domain/model/`, `domain/repository/` | data classes and repository interfaces per feature |
| `data/repository/` | the Android API readers (BatteryManager, LocationManager, Camera2, PackageManager, `/proc`, sysfs) |
| `data/db/`, `data/worker/` | Room battery log and the WorkManager logger |
| `data/preference/` | settings and the Apps cache |
| `di/AppModule.kt` | Koin wiring |

Tests: 5 unit tests (including a Koin module check) and 3 instrumented Compose tests.

---

<h3 align="center">Matta Ashok Varma</h3>

<p align="center">
  <a href="https://github.com/ashokvarmamatta"><img alt="GitHub" src="https://img.shields.io/badge/GitHub-181717?style=for-the-badge&logo=github&logoColor=white" /></a>
  &nbsp;
  <a href="https://linkedin.com/in/ashokvarmamatta"><img alt="LinkedIn" src="https://img.shields.io/badge/LinkedIn-0077B5?style=for-the-badge&logo=linkedin&logoColor=white" /></a>
  &nbsp;
  <a href="https://ashokvarmamatta.github.io/portfolio/"><img alt="Portfolio" src="https://img.shields.io/badge/Portfolio-00D4AA?style=for-the-badge&logo=googlechrome&logoColor=white" /></a>
</p>
