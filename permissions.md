# ANTAR — Play Store Permission Audit (final)

**App:** Antar - Device info
**Package:** `com.ashes.dev.works.system.core.internals.antar`
**minSdk:** 24 · **targetSdk / compileSdk:** 36
**Last updated:** 2026-05-09

This is the final audit after the second round of cleanup. The Phone permission group, AD_ID, CAMERA, BLUETOOTH_CONNECT, READ_BASIC_PHONE_STATE, and BATTERY_STATS have all been removed from the manifest *and* the Kotlin code. NEARBY_WIFI_DEVICES is now properly gated at runtime on API 33+.

---

## Quick Reference Table

| # | Permission | Protection | Sensitive? | Valid? | Data Safety | Decl. Form | Video | Prominent Disclosure |
|---|---|---|---|---|---|---|---|---|
| 1 | `INTERNET` | Normal (auto) | NO | YES | No (network is implicit) | NO | NO | NO |
| 2 | `ACCESS_NETWORK_STATE` | Normal (auto) | NO | YES | No | NO | NO | NO |
| 3 | `ACCESS_WIFI_STATE` | Normal (auto) | NO | YES | No | NO | NO | NO |
| 4 | `ACCESS_FINE_LOCATION` | Dangerous (runtime) | YES | YES | YES (Location, precise) | NO | NO | YES |
| 5 | `ACCESS_COARSE_LOCATION` | Dangerous (runtime) | YES | YES | YES (Location, approx.) | NO | NO | YES (paired with FINE) |
| 6 | `NEARBY_WIFI_DEVICES` (`neverForLocation`) | Dangerous (runtime, API 33+) | YES | YES | YES (Connectivity info) | NO | NO | YES |
| 7 | `QUERY_ALL_PACKAGES` | Normal (install-time) | **YES (HIGH)** | YES | YES (App activity → installed apps) | **REQUIRED** | **REQUIRED** | NO (install-time, justified in form) |

**Sensitive legend:** YES (HIGH) = needs Play Console Sensitive Permissions Declaration form & video · YES = sensitive runtime, needs Data Safety + Prominent Disclosure · NO = normal/auto.

**Manifest is down from 14 → 7 permissions.** No more unused entries.

---

## Per-Permission Detail

### 1. `INTERNET`
- **Protection:** Normal (auto-granted at install).
- **Why needed:** Required by `Geocoder.getFromLocation(...)` for reverse-geocoding the user's coordinates into an address on the Location screen.
- **Where used:**
  - `app/src/main/java/.../data/repository/LocationRepositoryImpl.kt:180` — `geocoder.getFromLocation(latitude, longitude, 1)`
- **Valid?** YES. No runtime gate required.
- **Play Store:** No declaration. No video. No Data Safety entry (network use is implicit).

### 2. `ACCESS_NETWORK_STATE`
- **Protection:** Normal.
- **Why needed:** Reading active connection type and link properties.
- **Where used:**
  - `app/src/main/java/.../data/repository/NetworkRepositoryImpl.kt:15-21` — `ConnectivityManager.activeNetworkInfo`, `getLinkProperties(...)` (lines 15, 17, 21)
- **Valid?** YES.

### 3. `ACCESS_WIFI_STATE`
- **Protection:** Normal.
- **Why needed:** Reading current Wi-Fi connection info, DHCP info, and Wi-Fi enabled state.
- **Where used:**
  - `app/src/main/java/.../data/repository/NetworkRepositoryImpl.kt:18-22` — `wifiManager.connectionInfo`, `dhcpInfo`
  - `app/src/main/java/.../data/repository/NetworkRepositoryImpl.kt:51` — `wifiManager.isWifiEnabled`
  - `app/src/main/java/.../data/repository/NetworkRepositoryImpl.kt:100` — `wifiManager.scanResults` (gated by FINE_LOCATION + NEARBY_WIFI_DEVICES)
- **Valid?** YES.

### 4. `ACCESS_FINE_LOCATION`
- **Protection:** Dangerous (runtime).
- **Why needed:** GPS positioning, satellite GNSS status, NMEA messages on the Location screen, and Wi-Fi-security-type lookup on the Network screen.
- **Where used:**
  - `app/src/main/java/.../data/repository/LocationRepositoryImpl.kt:104-119` — runtime check **before** `requestLocationUpdates` / `registerGnssStatusCallback` / `addNmeaListener`
  - `app/src/main/java/.../data/repository/LocationRepositoryImpl.kt:121-142` — protected calls behind the check
  - `app/src/main/java/.../data/repository/NetworkRepositoryImpl.kt:89-99` — runtime check before `wifiManager.scanResults` (line 100)
  - `app/src/main/java/.../presentation/screens/LocationScreen.kt:35-40` — `rememberMultiplePermissionsState(...)`
  - `app/src/main/java/.../presentation/screens/NetworkScreen.kt:28-34` — first entry in the multi-permission request
- **Valid?** YES. Runtime gate confirmed at every call site.
- **Play Store:** No declaration form. Data Safety: location precise, ephemeral, on-device. Requires **Prominent Disclosure**.

### 5. `ACCESS_COARSE_LOCATION`
- **Protection:** Dangerous (runtime).
- **Why needed:** Required to be requested alongside FINE on Android 12+ when FINE is requested; provides approximate-location fallback if user grants only Coarse.
- **Where used:**
  - `app/src/main/java/.../presentation/screens/LocationScreen.kt:37` — `Manifest.permission.ACCESS_COARSE_LOCATION` in the multi-permission request
- **Valid?** YES.

### 6. `NEARBY_WIFI_DEVICES` (with `android:usesPermissionFlags="neverForLocation"`)
- **Protection:** Dangerous (runtime, Android 13+ / API 33+).
- **Why needed:** On API 33+, `WifiManager.getScanResults()` requires this permission. The `neverForLocation` flag attests that scan results are not used to derive location, which keeps this out of the Location declarations.
- **Where used:**
  - `app/src/main/java/.../data/repository/NetworkRepositoryImpl.kt:95-98` — runtime check (gated on `Build.VERSION.SDK_INT >= TIRAMISU`) before `wifiManager.scanResults` at line 100
  - `app/src/main/java/.../presentation/screens/NetworkScreen.kt:28-33` — added to `rememberMultiplePermissionsState` only when `SDK_INT >= TIRAMISU`
- **Valid?** YES. Runtime check now in place.
- **Play Store:** No declaration form. Data Safety: connectivity info / device or other IDs (BSSIDs of nearby APs). Requires **Prominent Disclosure** (covered by the Network-screen disclosure).

### 7. `QUERY_ALL_PACKAGES`
- **Protection:** Normal (install-time, but Play Store treats as **HIGH-SENSITIVITY**).
- **Why needed:** Enumerate **every** installed package on the device for the Apps screen.
- **Where used:**
  - `app/src/main/java/.../data/repository/AppsRepositoryImpl.kt:13` — `packageManager.getInstalledPackages(0)`
- **Valid?** YES at code level. `tools:ignore="QueryAllPackagesPermission"` suppresses the lint warning only — it does **not** affect Play review.
- **Play Store:** **Sensitive Permissions Declaration form REQUIRED.** **Demo video REQUIRED.** This is the **only** form / video this app needs. Justification text and video script are in `permission_declarations.md` and `permission_video_guide.md`.

---

## Issues Found

### Outstanding
*None.* Every manifest entry has a real call site and a runtime gate where one is required.

### Resolved (already applied)
| # | What was done | Result |
|---|---|---|
| R1 | Removed `CAMERA`, `BLUETOOTH_CONNECT`, `READ_BASIC_PHONE_STATE`, `READ_PHONE_STATE`, `READ_PHONE_NUMBERS`, `AD_ID`, `BATTERY_STATS` from `AndroidManifest.xml` | Manifest down to 7 entries from 14 |
| R2 | Removed `AdvertisingIdClient` import + call + `_deviceFlow` plumbing from `DeviceRepositoryImpl.kt` | No more advertising-ID collection |
| R3 | Removed `googleAdvertisingId` field from `Device.kt`, `FakeDeviceRepository.kt`, `DeviceScreen.kt` | UI no longer renders that row |
| R4 | Removed `play-services-ads-identifier` dependency from `app/build.gradle.kts` and `gradle/libs.versions.toml` | Smaller APK, no Google AdID surface |
| R5 | `NetworkRepositoryImpl.kt` rewritten to use only unprivileged `TelephonyManager` APIs | SIM card now populated via `simOperatorName`, `simCountryIso`, `simOperator` (MCC+MNC), `simCarrierId`, `networkOperatorName`, `isNetworkRoaming`, `phoneCount` — none of which require READ_PHONE_STATE |
| R6 | `DeviceRepositoryImpl.kt` no longer calls `Build.getSerial()` or `dataNetworkType` | Hardware Serial reports "Restricted by Android" on API 26+; Network Type reports Connected/Disconnected |
| R7 | `NetworkScreen.kt` permission request reduced; "Phone number" InfoRow removed from both SIM cards | No more Phone-permission dialogs |
| R8 | `BATTERY_STATS` removed from manifest | Lint clean; Battery screen unaffected (uses `BatteryManager` + `ACTION_BATTERY_CHANGED` which need no permission) |
| R9 | `NEARBY_WIFI_DEVICES` added to runtime request and `checkSelfPermission` block, both gated on `SDK_INT >= TIRAMISU` | Wi-Fi security type now works correctly on API 33+ |

### Working as intended
| # | Item | File:line |
|---|---|---|
| W1 | FINE_LOCATION runtime gate before `requestLocationUpdates` | `LocationRepositoryImpl.kt:104-119` |
| W2 | FINE_LOCATION + NEARBY_WIFI_DEVICES (API 33+) runtime gate before `wifiManager.scanResults` | `NetworkRepositoryImpl.kt:89-99` |
| W3 | Compose permission request UI for Location + Network screens | `LocationScreen.kt:35`, `NetworkScreen.kt:28-33` |
| W4 | SIM 1 card shows what's reachable without phone perms | `NetworkScreen.kt:114-127`, populated by `NetworkRepositoryImpl.kt:24-86` |
| W5 | SIM 2 card hidden when no per-slot data is available | `NetworkScreen.kt:130` |
| W6 | Battery screen functions without `BATTERY_STATS` | `BatteryRepositoryImpl.kt:28-194`, `BatteryLogWorker.kt:25-42` |

### Architecture notes
- The Network screen now shows **slot count** (`telephonyManager.phoneCount`) under "Multi SIM" and a **default-SIM card** under "SIM 1". Per-slot enumeration (separate SIM 1 / SIM 2 cards) is impossible without `READ_PHONE_STATE`; the existing `network.sim2Name == "- - -"` guard hides the SIM 2 card cleanly.
- All telephony reads are unprivileged: `simOperatorName`, `simCountryIso`, `simOperator`, `simCarrierId` (API 28+), `networkOperatorName`, `isNetworkRoaming`, `simState`, `phoneCount`.
- The Battery screen uses `BatteryManager` (`getIntProperty(BATTERY_PROPERTY_CURRENT_NOW)`, `BATTERY_PROPERTY_CHARGE_COUNTER`) and `Intent.ACTION_BATTERY_CHANGED` extras. Neither requires any permission.
- There is no foreground service, no notification, no accessibility service, no VPN, and no notification-listener service. None of those Play declaration forms apply.

---

## Prominent Disclosure Requirements

| Permission(s) | Where to show | Purpose |
|---|---|---|
| `ACCESS_FINE_LOCATION` + `ACCESS_COARSE_LOCATION` | `LocationScreen.kt` — before `launchMultiplePermissionRequest()` at line 202 | Location display |
| `ACCESS_FINE_LOCATION` + `NEARBY_WIFI_DEVICES` (API 33+) | `NetworkScreen.kt` — before `launchMultiplePermissionRequest()` at line ~167 | Wi-Fi security type lookup |

**Suggested Location dialog text:**
> "ANTAR uses your precise location only to show GPS coordinates, satellite status, and the security type of the Wi-Fi network you're connected to. Location data stays on your device and is never sent off-device or shared with third parties."

(See `permission_declarations.md` for the copy-paste-ready versions.)

---

## What goes where on Google Play Console

| Permission | Console location |
|---|---|
| `QUERY_ALL_PACKAGES` | Policy → App content → **Sensitive permissions and APIs** → **All apps (`QUERY_ALL_PACKAGES`)** |
| `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION` | Policy → App content → **Data safety** → Location → Approximate / Precise |
| `NEARBY_WIFI_DEVICES` | Policy → App content → **Data safety** → Connectivity info |
| `INTERNET`, `ACCESS_NETWORK_STATE`, `ACCESS_WIFI_STATE` | Nothing required |

---

## Unused Permissions Check

| Manifest entry | Has matching code? | Verdict |
|---|---|---|
| `INTERNET` | YES (`Geocoder.getFromLocation`) | KEEP |
| `ACCESS_NETWORK_STATE` | YES | KEEP |
| `ACCESS_WIFI_STATE` | YES | KEEP |
| `ACCESS_FINE_LOCATION` | YES (LocationManager + scanResults) | KEEP |
| `ACCESS_COARSE_LOCATION` | YES (paired with FINE in request) | KEEP |
| `NEARBY_WIFI_DEVICES` | YES (`scanResults` on API 33+, runtime-gated) | KEEP |
| `QUERY_ALL_PACKAGES` | YES (`packageManager.getInstalledPackages(0)`) | KEEP — declare on Console |

**Zero unused entries.**

---

## Sensitive count summary

- **Total permissions in manifest:** 7
- **Sensitive (Data Safety needed):** 4 — FINE/COARSE LOCATION, NEARBY_WIFI_DEVICES, QUERY_ALL_PACKAGES
- **High-sensitivity (Sensitive Permissions Declaration form REQUIRED):** **1** — QUERY_ALL_PACKAGES
- **Demo videos to record:** **1** — QUERY_ALL_PACKAGES
- **UNUSED:** **0**
- **NEEDS FIX:** **0**
