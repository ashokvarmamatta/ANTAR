# ANTAR — Play Console Declaration & Data Safety Pack (final)

**App:** Antar - Device info
**Package:** `com.ashes.dev.works.system.core.internals.antar`
**Last updated:** 2026-05-09

Final declaration pack. The Phone permission group, AD_ID, CAMERA, BLUETOOTH_CONNECT, READ_BASIC_PHONE_STATE, and BATTERY_STATS have been deleted from the manifest *and* the code. NEARBY_WIFI_DEVICES is now properly runtime-checked on API 33+. Only one Sensitive Permissions Declaration form remains: `QUERY_ALL_PACKAGES`.

---

## Summary — What to Submit Where

| Permission | Decl. form? | Data Safety? | Prominent Disclosure? | Demo video? |
|---|---|---|---|---|
| `INTERNET` | — | — | — | — |
| `ACCESS_NETWORK_STATE` | — | — | — | — |
| `ACCESS_WIFI_STATE` | — | — | — | — |
| `ACCESS_FINE_LOCATION` | — | **YES** (Location → Precise) | **YES** | — |
| `ACCESS_COARSE_LOCATION` | — | **YES** (Location → Approximate) | YES (paired with FINE) | — |
| `NEARBY_WIFI_DEVICES` | — | **YES** (Connectivity info) | YES (Network-screen disclosure) | — |
| `QUERY_ALL_PACKAGES` | **YES — All apps** | **YES** (App activity → installed apps) | — (install-time, justify in form) | **YES** |

Total declaration forms: **1**.
Total demo videos: **1**.

---

## Declaration 1 — `QUERY_ALL_PACKAGES`

**Where:** Play Console → Policy → App content → Sensitive permissions and APIs → **All apps (`QUERY_ALL_PACKAGES`)**.

**Approved use case to select:** *Discover a user's installed apps for purposes of identifying the user's awareness of those apps* — closest match for a device-info utility that lists every package. If the form lists "App browser / inventory", choose that. If neither is present in the form (Google rotates the wording), choose "Other" and use the text below verbatim.

### Form fields — copy/paste

**Core functionality (3-5 sentences):**
> ANTAR's Apps screen presents a complete inventory of every package installed on the device, sorted alphabetically. For each entry it shows the user-facing name, package name, version name, target SDK, native-library architecture (32-bit / 64-bit), and a system-app flag. This inventory is the screen's entire purpose — users open it to audit what is on their phone, identify unfamiliar packages, and check target-SDK compliance for security and compatibility reasons. Filtering down to a fixed `<queries>` list is impossible because the user expects to see **everything**, including packages not known at compile time.

**Why alternative APIs cannot be used:**
> The Android 11 `<queries>` element only returns matches against packages or intents declared in the manifest at build time. ANTAR cannot enumerate "everything the user has installed" through `<queries>`. `LauncherApps.getActivityList()` returns only launcher activities and would miss services, providers, and apps with no launcher (a meaningful subset that users explicitly want to inspect). `QUERY_ALL_PACKAGES` is the only API that returns the complete set required for the feature.

**Install-time vs runtime:**
> Install-time. The permission is granted automatically and there is no user-facing prompt. The Apps screen itself is the user's signal of intent — they navigate to it explicitly to see the list.

**What the app does with the data:**
> Each package's name, version, target SDK, and architecture is read **on demand** when the Apps screen is opened, held in a Compose `LazyColumn` for the duration of the screen, and discarded when the user navigates away. No package list, package name, app name, or icon is stored on disk, written to a database, sent to a server, shared with another app, or used for advertising / analytics / tracking. The app makes zero outbound HTTP requests carrying any package data.

**Short declaration summary (1-2 sentences):**
> ANTAR's Apps screen displays a complete on-device inventory of installed packages so users can audit what is installed on their phone. The list is shown locally and never transmitted.

---

## Data Safety Form — full table

Submit at **Play Console → Policy → App content → Data safety**. ANTAR's net answers:
- **Does your app collect or share any required user data types?** **Yes** — for the items listed below.
- **Is all of the user data collected by your app encrypted in transit?** N/A — no data is transmitted off-device. Tick "Yes — data is encrypted in transit" because it's the truthful answer when nothing leaves the device.
- **Do you provide a way for users to request that their data be deleted?** N/A — nothing is collected to a server. Tick "Yes — users can request deletion" with a note that "no data is stored off-device, so there is nothing to delete remotely; users can revoke runtime permissions in Settings to stop on-device collection."

| Data type | Collected? | Shared? | Optional? | Purpose | Ephemeral? | Processed on-device only? |
|---|---|---|---|---|---|---|
| **Location → Approximate location** | Yes | No | Yes (user grants COARSE) | App functionality (display GPS info to user) | **Yes** (held in memory, dropped when screen closes) | **Yes** |
| **Location → Precise location** | Yes | No | Yes (user grants FINE) | App functionality | **Yes** | **Yes** |
| **App activity → Installed apps** *(via `QUERY_ALL_PACKAGES`)* | Yes | No | No | App functionality (Apps screen inventory) | **Yes** | **Yes** |
| **Connectivity info → Connected Wi-Fi info** *(via `wifiManager.connectionInfo` + `scanResults`, API 33+ via `NEARBY_WIFI_DEVICES`)* | Yes | No | Yes (user grants Location + Nearby on API 33+) | App functionality (Network screen — BSSID, security type, link speed) | **Yes** | **Yes** |
| **Device or other IDs → Android ID** *(`Settings.Secure.ANDROID_ID`)* | Yes | No | No | App functionality (display device identifiers) | **Yes** | **Yes** |

**Removed compared with the previous declaration:**
- ❌ Personal info → Phone number — *no longer collected (READ_PHONE_NUMBERS removed)*
- ❌ Device or other IDs → Device IDs (IMEI / serial via `Build.getSerial`) — *no longer collected (READ_PHONE_STATE removed)*
- ❌ Device or other IDs → Advertising ID — *no longer collected (`AdvertisingIdClient` call removed; `AD_ID` permission removed)*

---

## Prominent Disclosure Strings

These dialogs are required by Play before the system permission prompt is shown for any sensitive runtime permission. Implement as a Material `AlertDialog` (or equivalent) before calling `launchMultiplePermissionRequest()`.

### Disclosure 1 — Location (`ACCESS_FINE_LOCATION` + `ACCESS_COARSE_LOCATION`) on Location screen

**Where to attach:** before the `Button(onClick = { locationPermissionsState.launchMultiplePermissionRequest() }, ...)` call at `LocationScreen.kt:202`.

**Title:** Location permission

**Body:**
> ANTAR uses your device's precise and approximate location only to display GPS coordinates, satellite/GNSS status, NMEA accuracy, and the address of your current position on the Location screen.
>
> Your location is read on demand, displayed only inside the app, and is **never** transmitted to ANTAR's servers, stored on disk, used for advertising, or shared with any third party.
>
> You can revoke this permission at any time from Android Settings → Apps → ANTAR → Permissions.

**Buttons:** *Continue* (calls `launchMultiplePermissionRequest()`) and *Not now*.

### Disclosure 2 — Network-screen permissions (Location + Nearby Wi-Fi devices on API 33+)

**Where to attach:** before the `Button(onClick = { networkPermissionsState.launchMultiplePermissionRequest() }, ...)` call at `NetworkScreen.kt:~167`.

**Title:** Network permissions

**Body:**
> The Network screen needs the following to read the security type of the Wi-Fi you're currently connected to:
>
> • **Location** — Android exposes Wi-Fi scan results only to apps holding location.
> • **Nearby Wi-Fi devices** *(Android 13+ only)* — required by Android 13 to scan Wi-Fi networks. ANTAR uses this only to identify the network you're already connected to; it is **not** used to derive your location (the manifest sets `neverForLocation`).
>
> Both permissions are used solely to display "WPA2", "WPA3", "WEP", "Open", etc. on the Network screen. Nothing is transmitted, stored, or shared.

**Buttons:** *Continue* and *Not now*.

---

## Pre-submission Checklist

Tick each box before pressing "Submit for review" in Play Console.

### Code hygiene
- [ ] Implemented Prominent Disclosure dialog 1 before `LocationScreen.kt:202` button click.
- [ ] Implemented Prominent Disclosure dialog 2 before `NetworkScreen.kt:~167` button click.
- [x] `NEARBY_WIFI_DEVICES` runtime request and `checkSelfPermission` block in place, gated on `SDK_INT >= TIRAMISU`.
- [ ] Verified `tcpdump` / a network proxy shows no telephony, location, package-list, or device-id data leaving the device on a fresh install.

### Play Console
- [ ] Filled in **All apps (`QUERY_ALL_PACKAGES`)** declaration with the text from Declaration 1 above.
- [ ] Uploaded **demo video URL** for `QUERY_ALL_PACKAGES` (see `permission_video_guide.md`).
- [ ] Filled in **Data safety** form using the table above. Marked location, installed apps, connectivity info, and Android ID as collected; marked all rows as *not shared*, *processed on-device only*, *ephemeral*.
- [ ] Privacy policy URL is publicly reachable, lists every permission above, and is linked in the Play Console store listing.
- [ ] Privacy policy explicitly states: "ANTAR does not collect or transmit your phone number, IMEI, hardware serial, or Google Advertising ID. Earlier versions read these values; the current version does not request the underlying permissions."

### Sanity check
- [ ] Re-read each "Where Used" file:line entry in `permissions.md` and confirmed each line is still accurate against the current codebase.
- [ ] Confirmed the privacy policy text matches the Data Safety form (Play rejects mismatches).
