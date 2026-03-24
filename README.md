# ANTAR — Device Info & Analytics

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-00897B?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Min_SDK-24-brightgreen?style=for-the-badge" />
</p>

**ANTAR** is a comprehensive Android device information and analytics application that surfaces deep hardware metrics, real-time system monitoring, and detailed sensor data — all in a beautiful, dark-themed UI built entirely with **Jetpack Compose**.

---

## Screenshots

<p align="center">
  <img src="screenshots/01_dashboard.jpg" width="200" />
  <img src="screenshots/03_system.jpg" width="200" />
  <img src="screenshots/05_battery.jpg" width="200" />
  <img src="screenshots/07_location.jpg" width="200" />
</p>
<p align="center">
  <img src="screenshots/08_network.jpg" width="200" />
  <img src="screenshots/10_display.jpg" width="200" />
  <img src="screenshots/11_sensors.jpg" width="200" />
  <img src="screenshots/12_apps.jpg" width="200" />
</p>

---

## Features

### Dashboard
- **RAM Monitor** — Real-time memory usage with animated circular progress (used/total/free)
- **Internal Storage** — Storage breakdown with visual progress bar
- **Power Source** — Battery percentage, charging state, temperature, voltage
- **Processor Info** — Chipset name, core count, clock speed
- **Sensor Count** — Total available hardware sensors
- **App Count** — Installed applications with update detection
- **System Health** — Overall device health score with uptime tracking

### Device Information
- Complete device model, manufacturer, brand, and hardware details
- Build fingerprint, product name, board, and bootloader info

### System
- Android version, API level, build number, security patch level
- Baseband version, kernel info, root detection
- System-as-Root and Seamless Update support detection

### CPU
- Processor architecture (ARM, x86)
- Core count, clock speeds (min/max/current)
- CPU governor and scaling driver info
- Real-time per-core frequency monitoring

### Battery
- **Battery Health** — Cycle count tracking with circular gauge
- **Real-time Graph** — Live mAh discharge/charge curve
- **Detailed Metrics** — Health percentage, temperature, voltage, charger type
- Expandable battery metrics panel

### Location
- **GPS Coordinates** — Latitude, longitude with reverse geocoding
- **Satellite Tracking** — Beidou, NavStar GPS, GLONASS, QZSS, Galileo, IRNSS counts
- **Position Details** — Altitude, speed, PDOP, TTFF, horizontal/vertical DOP

### Network
- **WiFi** — SSID, BSSID, IP (v4/v6), gateway, DNS, link speed, frequency, security
- **Cellular** — Dual SIM detection, carrier info, signal strength
- **Public IP** detection with one-tap lookup
- **Data Usage** monitoring

### Display
- Screen resolution, physical size, DPI, refresh rate
- HDR capability detection
- Display bucket classification (mdpi/hdpi/xhdpi/xxhdpi)
- Brightness mode and screen timeout

### Sensors
- Complete sensor inventory (44+ sensor types)
- Accelerometer, gyroscope, magnetometer, proximity, light
- Vendor, power consumption, and resolution per sensor

### Apps
- Installed application count
- System vs user app breakdown
- Update availability detection

---

## Tech Stack

| Layer | Technology |
|-------|-----------|
| **Language** | Kotlin 100% |
| **UI** | Jetpack Compose + Material 3 |
| **Architecture** | MVVM + Clean Architecture |
| **DI** | Koin |
| **Async** | Kotlin Coroutines + Flow |
| **Data** | Android SensorManager, TelephonyManager, BatteryManager, WifiManager, LocationManager |
| **Charts** | Custom Compose Canvas drawing for battery graphs and circular gauges |
| **Navigation** | Tab-based with horizontal pager |
| **Min SDK** | 24 (Android 7.0) |
| **Target SDK** | 35 (Android 15) |

---

## Architecture



---

## Key Technical Highlights

- **Real-time Data Streams** — Uses  for reactive updates across all system metrics
- **Dual SIM Detection** — Complex  +  handling for multi-SIM devices
- **Custom Canvas Charts** — Battery discharge curve and circular gauges drawn with Compose Canvas API
- **Dynamic Permissions** — Runtime permission handling for location, phone state, and usage stats
- **Lifecycle-Aware** — All sensor listeners and location callbacks bound to lifecycle to prevent leaks
- **Efficient Polling** — Configurable update intervals to balance freshness vs battery impact

---

## Build & Run



1. Open in **Android Studio** (Hedgehog or later)
2. Sync Gradle dependencies
3. Run on device or emulator (API 24+)

---

## Author

**Matta Ashok Varma** — Senior Android Developer

- [GitHub](https://github.com/ashokvarmamatta)
- [LinkedIn](https://www.linkedin.com/in/ashokvarmamatta)
- [Portfolio](https://ashokvarmamatta.github.io/portfolio/)
