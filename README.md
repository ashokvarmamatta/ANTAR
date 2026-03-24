# 📱 ANTAR — Device Info & Analytics

<p align="center">
  <img src="https://img.shields.io/badge/Platform-Android-3DDC84?style=for-the-badge&logo=android&logoColor=white" />
  <img src="https://img.shields.io/badge/Kotlin-100%25-7F52FF?style=for-the-badge&logo=kotlin&logoColor=white" />
  <img src="https://img.shields.io/badge/Jetpack_Compose-UI-4285F4?style=for-the-badge&logo=jetpackcompose&logoColor=white" />
  <img src="https://img.shields.io/badge/Architecture-MVVM-00897B?style=for-the-badge" />
  <img src="https://img.shields.io/badge/Min_SDK-24-brightgreen?style=for-the-badge" />
  <br/>
  <a href="https://github.com/ashokvarmamatta/ANTAR/releases/download/v1.0.0/ANTAR-v1.0.0.apk">
    <img src="https://img.shields.io/badge/Download_APK-v1.0.0-00D4AA?style=for-the-badge&logo=android&logoColor=white" />
  </a>
</p>

<p align="center">
  <b>Your phone has secrets. ANTAR reveals them all.</b><br/>
  <sub>A comprehensive device analytics app that digs deep into every sensor, chip, and signal on your Android device.</sub>
</p>

---

## 📸 Screenshots

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

## ✨ Features

### 🏠 Dashboard — Everything at a Glance
> One screen. Every vital metric. Zero clutter.
- 🧠 **RAM Monitor** — Animated circular gauge showing real-time memory (used / total / free)
- 💾 **Storage Meter** — How full is your phone? Visual progress bar tells you instantly
- 🔋 **Power Source** — Battery %, charging state, temperature, voltage — all live
- ⚡ **Processor** — Chipset name, core count, clock speed
- 📡 **Sensors** — 44+ hardware sensors detected and cataloged
- 📦 **Apps** — Total installed count with update detection
- 💚 **System Health** — Overall score + uptime tracking

### 📋 Device — Know Your Hardware
> Every detail about the metal in your hands.
- Model, manufacturer, brand, board, bootloader
- Build fingerprint, product name, hardware revision

### ⚙️ System — Under the Hood
> What Android version? What security patch? Is it rooted?
- 🤖 Android version, API level, codename (e.g., Vanilla Ice Cream)
- 🔒 Security patch level, baseband version
- 🔍 Root detection, System-as-Root, Seamless Updates support

### 🧮 CPU — Real-Time Processor Intel
> Watch your cores work in real-time.
- Architecture (ARM64, x86), core count
- Min / Max / Current clock speeds per core
- CPU governor, scaling driver, thermal info

### 🔋 Battery — Health Deep Dive
> Is your battery dying? ANTAR knows before you do.
- 🔄 **Cycle Count** — Beautiful circular gauge showing battery wear
- 📈 **Live Graph** — Real-time mAh discharge/charge curve
- 🌡️ Health %, temperature, voltage, charger type
- Expandable detailed metrics panel

### 📍 Location — GPS & Satellites
> Track every satellite your phone can see.
- 🛰️ **Satellite Tracking** — Beidou, NavStar, GLONASS, QZSS, Galileo, IRNSS
- 📌 Lat/Long with reverse geocoding to street address
- Altitude, speed, PDOP, TTFF, DOP values

### 🌐 Network — WiFi & Cellular Deep Scan
> Your network has no secrets from ANTAR.
- 📶 WiFi — SSID, BSSID, IP (v4 + v6), gateway, DNS, link speed, frequency
- 📱 Cellular — Dual SIM detection, carrier, signal strength
- 🌍 Public IP lookup with one tap
- WPA2/WPA3 security detection

### 🖥️ Display — Screen Specs
> Know every pixel.
- Resolution (1220x2712), physical size, DPI
- 🎮 120Hz refresh rate detection, HDR support
- Brightness mode, screen timeout, display bucket

### 🔬 Sensors — Full Hardware Inventory
> 44+ sensors. Every single one documented.
- Accelerometer, gyroscope, magnetometer, proximity, light
- Vendor, power consumption, resolution per sensor

### 📦 Apps — What's Installed
> Quick audit of everything on your device.
- Total count, system vs user breakdown
- Update availability flags

---

## 🏗️ Tech Stack

| Layer | Technology |
|-------|-----------|
| 🗣️ **Language** | Kotlin 100% |
| 🎨 **UI** | Jetpack Compose + Material 3 |
| 🏛️ **Architecture** | MVVM + Clean Architecture |
| 💉 **DI** | Koin |
| ⚡ **Async** | Kotlin Coroutines + Flow |
| 📊 **Charts** | Custom Compose Canvas (battery graphs, circular gauges) |
| 🧭 **Navigation** | Tab-based horizontal pager |
| 📱 **Min SDK** | 24 (Android 7.0) |
| 🎯 **Target SDK** | 35 (Android 15) |

---

## 🧬 Architecture

```
app/
 ui/
   dashboard/       → 🏠 System overview cards
   device/          → 📋 Hardware details
   system/          → ⚙️ OS & build info
   cpu/             → 🧮 Processor monitoring
   battery/         → 🔋 Health & live graphs
   location/        → 📍 GPS & satellites
   network/         → 🌐 WiFi & cellular
   storage/         → 💾 Storage breakdown
   display/         → 🖥️ Screen specs
   sensors/         → 🔬 Sensor inventory
   apps/            → 📦 App management
 data/
   repository/      → 📂 Data access layer
   model/           → 📄 Data models
 di/                → 💉 Koin modules
 utils/             → 🔧 Extensions & helpers
```

---

## 🔥 Key Technical Highlights

- ⚡ **Real-time StateFlow Streams** — Reactive updates across ALL system metrics, no polling lag
- 📱 **Dual SIM Detection** — Complex TelephonyManager + SubscriptionManager for multi-SIM devices
- 🎨 **Custom Canvas Charts** — Battery curves & circular gauges hand-drawn with Compose Canvas API
- 🔐 **Dynamic Permissions** — Graceful handling of location, phone state, usage stats permissions
- 🧹 **Zero Leaks** — All sensor listeners & location callbacks lifecycle-bound
- 🔋 **Battery Efficient** — Smart polling intervals that respect your battery

---

## 🚀 Quick Start

```bash
git clone https://github.com/ashokvarmamatta/ANTAR.git
```

1. Open in **Android Studio** (Hedgehog or later)
2. Sync Gradle
3. Run on device (API 24+)

Or just [**download the APK**](https://github.com/ashokvarmamatta/ANTAR/releases/download/v1.0.0/ANTAR-v1.0.0.apk) and install!

---

## 👨‍💻 Author

**Matta Ashok Varma** — Senior Android Developer

[![GitHub](https://img.shields.io/badge/GitHub-181717?style=flat-square&logo=github&logoColor=white)](https://github.com/ashokvarmamatta)
[![LinkedIn](https://img.shields.io/badge/LinkedIn-0077B5?style=flat-square&logo=linkedin&logoColor=white)](https://www.linkedin.com/in/ashokvarmamatta)
[![Portfolio](https://img.shields.io/badge/Portfolio-00D4AA?style=flat-square&logo=googlechrome&logoColor=white)](https://ashokvarmamatta.github.io/portfolio/)
