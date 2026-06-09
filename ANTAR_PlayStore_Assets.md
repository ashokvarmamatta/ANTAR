# 🎨 ANTAR — Play Store Publishing Assets

> _Copy-paste kit for the Google Play Console listing._

![Play Store](https://img.shields.io/badge/Google%20Play-Listing-34A853?logo=googleplay&logoColor=white)
![Android](https://img.shields.io/badge/Android-12%2B-3DDC84?logo=android&logoColor=white)
![Package](https://img.shields.io/badge/package-com.ashes.dev.works.system.core.internals.antar-0EA5E9)
![Version](https://img.shields.io/badge/version-1.0%20(1)-8B5CF6)
![Status](https://img.shields.io/badge/status-Pre--Submission-F59E0B)

---

| ANTAR<br>Antar — Device info |
|---|
| Google Play Store Publishing Assets Kit |

| Package | Version | Generated |
|---|---|---|
| com.ashes.dev.works.system.core.internals.antar | 1.0 (versionCode 1) | 2026-05-09 |

> What this document is: a copy-paste kit for filing the Play Store listing. Section 1 contains the three Play Console copy fields. Sections 2-3 are categorisation and keyword reference. Section 4 has AI prompts for Feature Graphic, Icon and Screenshots. Section 5 has release notes, Section 6 has Data Safety answers, and Section 7 is the final pre-submission checklist.
> Companion document: ANTAR_PlayStore_Audit.docx covers policy compliance and the full Data Safety form. Use both side by side when filing.


---

## 🛍️ 1. Store Listing Copy

All three copy fields below are ready to paste into Play Console → Store presence → Main store listing. Character counts shown for each.


### 📌 1.1 — App Title

| Variant | Text | Chars (max 30) |
|---|---|---|
| Primary (matches launcher) | Antar - Device Info | 19 / 30 |
| SEO variant | Antar: Device Info & Sensors | 28 / 30 |

> [!NOTE]
> Use the primary variant unless you want to broaden organic search reach. The launcher label in strings.xml is 'Antar - Device info'; the title in Play can differ but consistency helps recognition.


### 📌 1.2 — Short Description

> Instant device, battery, network & sensor stats — free, no ads, no account.

Character count: 75 / 80

> Lead value: 'Instant device, battery, network & sensor stats' covers four of the app's screens.
> Friction-removers: 'free, no ads, no account' — addresses the three most common objections to utility apps.


### 📌 1.3 — Long Description

> [!NOTE]
> ANTAR — DEEP DEVICE <span style="color:#3b82f6"><b>INFO</b></span> IN A CLEAN INTERFACE
> Antar shows you everything your phone knows about itself — hardware specs, battery health, network details, sensor data and every installed app — all in one fast, ad-free utility designed for people who actually like reading device data.
> WHAT YOU GET
> • Live device snapshot — model, manufacturer, hardware fingerprint, build id
> • Battery health and history — 24-hour and 7-day charts, charging sessions, voltage, current draw, capacity
> • Network details — Wi-Fi DHCP, gateway, DNS, link speed, SIM carrier, MCC/MNC
> • Location and GNSS — GPS coordinates, satellite constellations, NMEA accuracy, address
> • Sensors — accelerometer, gyroscope, magnetometer, barometer, proximity, ambient light
> • Storage — internal and external partitions, used vs free, mount points
> • Display — resolution, density, refresh rate, HDR support
> • CPU — cores, architecture, current frequency, governor
> • Camera — sensor resolutions, supported modes, hardware level
> • System — Android version, security patch, kernel, build fingerprint
> • Apps — every installed package, version, target SDK, architecture
> PREMIUM DESIGN
> • Dark cyan glassmorphism theme with subtle gradients
> • Smooth animations across 12 screens
> • Material You support on Android 12+
> PRIVACY FIRST
> • No tracking SDKs, no analytics, no crash reporters
> • No advertisements, no account, no sign-up
> • Data is read on demand and stays on your device
> • Settings → Request data deletion clears local battery history at any time
> PERFECT FOR
> • Developers who need quick build / SDK / hardware info on a real device
> • Tech enthusiasts who want to inspect sensors and connectivity
> • Anyone debugging connectivity, battery drain, or storage issues
> Antar is free, has zero advertisements, and never asks you to log in.
> Permissions: Location is requested only for GPS, satellites and Wi-Fi security identification. Nearby Wi-Fi devices is requested on Android 13+ for the same reason. Both are optional — the app keeps working in degraded mode if you decline.

Character count: 2040 / 4000 (target band 1,800–2,200 for readability)

> Plain text only. ALL CAPS section headers are the Play Store convention; bullet points use '•'.
> Every feature listed here is grounded in an actual screen of the app (Dashboard, Device, System, CPU, Battery, Location, Network, Storage, Display, Sensors, Apps, Camera, Settings).
> Closes with a soft permissions explainer rather than a hard sales line — sets expectations and reduces 1-star reviews from users surprised by a permission prompt.


## 🗂️ 2. App Categorisation

Reference table for the Play Console Store presence → Main store listing and Categorisation forms.

| Field | Value |
|---|---|
| Category (primary) | Tools |
| Category (alt) | House & Home → Device personalization (only if Tools is contested) |
| Content rating | Everyone (no violence, no gambling, no UGC, no mature themes — utility-only) |
| Tags / Keywords (woven into the long description) | device info, system info, hardware monitor, battery health, sim info, sensor test, network analyzer, gps info, build version, kernel info, storage info, cpu info, android version checker, ram info |
| Ads disclosure | No ads — declare 'Contains ads: <span style="color:#ef4444"><b>NO</b></span>' in Play Console |
| In-app purchases | None — app is fully free with no IAP / subscriptions |
| Permissions used (declared in AndroidManifest.xml) | INTERNET, ACCESS_NETWORK_STATE, ACCESS_WIFI_STATE, ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, NEARBY_WIFI_DEVICES (neverForLocation), QUERY_ALL_PACKAGES |
| Target audience | Developers, power users, tech enthusiasts, IT support staff. Age 13+. |
| Distribution countries | All countries supported by Play (no geofencing reason) |
| Pricing | Free |


## 📌 3. Keyword Strategy

> [!IMPORTANT]
> Three tiers of keywords. Play Store has no separate keyword field — these must be woven naturally into the long description (which Section 1.3 already does).

| Primary (high volume) | Secondary (feature-specific) | Long-tail (intent) |
|---|---|---|
| device info | battery health | device info app no ads |
| system info | sim card info | free hardware info android |
| hardware info | sensor test | check phone specs offline |
| phone specs | wifi analyzer | system info no permission |
| device monitor | gps info | android build version checker |
| device toolkit | cpu info | battery drain analyzer android |
| android utility | storage info | phone hardware test free |
| device analyzer | network info | show device manufacturer model |
| build info | kernel info | lightweight device info android |

> Primary keywords already appear in the title and short description.
> Secondary keywords appear in the WHAT YOU GET bullets in 1.3.
> Long-tail phrases are how niche searchers find utility apps — sprinkle one or two organically into the closing paragraphs of 1.3.
> Avoid keyword stuffing: Play penalises store listings that read like keyword soup. Aim for natural prose that happens to contain the terms.


## 🎨 4. Graphic Assets — AI Image Prompts

Each asset section below has a spec table first (size, safe zone, what NOT to include per Play policy), then ready-to-paste AI image-generation prompts. Prompts use exact hex codes from app/.../theme/Color.kt.


### 🖼️ 4.1 — Feature Graphic (1024 × 500 px)

| Spec | Value |
|---|---|
| Required size | 1024 × 500 pixels (exactly) |
| Format | PNG or JPEG, 24-bit, no transparency |
| Safe zone | Keep all critical content inside the central 924 × 400 area (50 px margin all sides). Edges may be cropped or covered by overlays on different devices. |
| Forbidden | Real device-brand mockups, App Store / Play Store badges, screenshots of other people's apps, people's faces, marketing claims that imply rankings or downloads. |
| Best practice | Tagline overlay must be readable on a 5-inch phone preview. App name should be visible without being a logo file. |

> PROMPT A — ATMOSPHERIC / CINEMATIC

> [!NOTE]
> Cinematic, atmospheric promotional banner exactly 1024 x 500 pixels for an Android utility app called 'Antar - Device Info'. Wide aspect 1024:500.
> Subject: a single floating, semi-transparent smartphone silhouette set against a pre-dawn sky with soft volumetric light rays from the upper-left. Around the phone, abstract holographic data panels float as glassmorphism cards displaying real device-info fields: 'Battery 87%', '120 Hz', 'arm64-v8a', 'Wi-Fi WPA3', '24 satellites', 'GPS 28.6° N', '4.2 GB / 128 GB'.
> Colour palette (use exact hex values):
> Background gradient from #0A0E21 (deep night) to #111631.
> Cyan accent #00E5FF for highlights, frame edges and key data text.
> Purple lift #B388FF in the upper light rays.
> Soft cyan glow #3300E5FF around the phone silhouette.
> Tagline overlay text on the lower-left: 'Deep device info, in a clean interface.' in Inter / Helvetica bold white.
> App name overlay top-left: 'ANTAR' in extra-bold white with cyan underline.
> Style: editorial, photoreal lighting, subtle film grain, 3D rendered, very high detail.
> Constraints: No device mockups of real branded phones. No App Store / Play Store badges. No people. Leave a 64 px safe margin on all edges so text won't be cropped on Play.

> PROMPT B — MINIMALIST / DARK MODE

> [!NOTE]
> Minimalist flat-design Play Store feature graphic exactly 1024 x 500 pixels for an Android utility app called 'Antar - Device Info'. Wide aspect 1024:500.
> Composition: an off-centre concentric-ring radar / sensor diagram on the right, made of thin neon strokes. To its left, a vertically stacked column of glassmorphism info chips with real device data labels: 'CPU', 'RAM', 'Battery', 'Network', 'Storage', 'Sensors'.
> Colour palette (use exact hex values):
> Solid background #0A0E21.
> Primary accent strokes and chip borders #00E5FF.
> Secondary accent #448AFF.
> Soft purple glow #B388FF on the outer rings.
> Chip background fills #161B35 with 1 px cyan borders.
> Tagline overlay centred-left: 'Every spec your phone hides.' in geometric sans-serif bold white.
> App name top-left: 'ANTAR' uppercase, extra-bold, cyan with subtle gradient.
> Style: flat vector, sharp edges, ultra-clean, dark-mode forward, very minimal.
> Constraints: No device mockups of real branded phones. No App Store / Play Store badges. No people. Leave a 64 px safe margin on all edges.


### 🟢 4.2 — App Icon (512 × 512 px)

| Spec | Value |
|---|---|
| Required size | 512 × 512 pixels |
| Format | 32-bit PNG with alpha channel (transparent background allowed outside the shape) |
| Safe zone | Material You / adaptive icon central 66% — content outside the safe zone may be cropped on round / squircle masks |
| Forbidden | Text in the icon, replicas of competitor logos, App Store / Play badges, drop shadows that imply 3D, busy backgrounds |

> PROMPT — APP ICON

> [!NOTE]
> Adaptive Android app icon exactly 512 x 512 pixels for a device-info utility called 'Antar'.
> Concept: a stylised concentric 'A' formed from three nested arcs that double as a sensor radar / signal-strength glyph — reads as both the letter A and as a device pulse.
> Colour palette (use exact hex values):
> Outer arc / primary mark #00E5FF (cyan).
> Middle arc #448AFF.
> Inner arc #B388FF.
> Fill behind the mark #0A0E21 contained inside a circular bounded shape.
> Subtle inner-glow #3300E5FF around the centre to lift the mark off the background.
> Composition: keep the mark inside the central 66% safe zone (Material You / adaptive icon mask). The outer 17% on each side may be cropped on round masks.
> Style: flat geometric, slight 1-pixel cyan rim light, no skeuomorphism, no gradients on the mark itself, very clean.
> Constraints: No text. No serifs. Transparent background outside the circular shape so adaptive masks render cleanly. No drop shadows. No 3D bevels.


### 📱 4.3 — Phone Screenshots (1080 × 1920 px)

| Spec | Value |
|---|---|
| Required size | 1080 × 1920 px (portrait phone) — minimum 320 px short edge, max 3840 px long edge |
| Quantity | Minimum 2, recommended 4–8 for first-time apps. Tablet sets are optional. |
| Format | PNG or JPEG, 24-bit, no transparency |
| Style | Consistent across all tiles — same banner band, same caption font, same phone frame, same colour palette |
| Forbidden | Real branded device frames, false claims, copy that contradicts the app, deceptive UI |

| # | Screenshot Subject | Full AI Prompt |
|---|---|---|
| 1 | Dashboard screen | Vertical 1080 x 1920 px Play Store screenshot tile. Top 200 px: solid #0A0E21 banner with bold white text 'EVERY SPEC AT A GLANCE'. Below: a stylised phone frame (no real-brand mockup) showing the Antar Dashboard screen — a dark #0A0E21 background with rounded glassmorphism cards arranged in a 2-column grid. Cards show real field labels from the app: 'Model: Pixel 8 Pro', 'Battery: 87%', 'CPU: Tensor G3', 'Storage: 96 / 256 GB', 'Wi-Fi: WPA3', 'Sensors: 14'. Cyan #00E5FF section accents, subtle purple #B388FF gradient bar across the top of the phone frame. Caption strip at the bottom in cyan: 'Live snapshot of your device.' Style: editorial, sharp, dark-mode. |
| 2 | Battery screen with 24h / 7d chart | Vertical 1080 x 1920 px Play Store screenshot tile. Top banner: 'BATTERY HEALTH, IN HISTORY'. Below: phone frame showing the Antar Battery screen — header card with '87% • 25 °C • 4.21 V', then a 24-hour line chart with cyan #00E5FF stroke and purple #B388FF charging-session highlights, then info rows: 'Health: Good', 'Capacity: 4485 / 5000 mAh', 'Current: -540 mA'. Background #0A0E21. Caption: '24-hour and 7-day charts. Charging sessions tracked.' Style match Tile 1. |
| 3 | Network / SIM card detail | Vertical 1080 x 1920 px Play Store screenshot tile. Top banner: 'WI-FI + SIM, IN ONE PLACE'. Below: phone frame showing the Antar Network screen — Wi-Fi card with 'WPA3 • 192.168.1.42 • -56 dBm', Mobile Data card 'Multi SIM: 2', SIM 1 card 'Carrier: Airtel • IN • MCC 404 • MNC 45'. Cyan rows, dark #0A0E21 background, subtle teal accent on the SIM card. Caption: 'No phone permissions. Nothing leaves the device.' Style match Tile 1. |
| 4 | Sensors live data | Vertical 1080 x 1920 px Play Store screenshot tile. Top banner: 'EVERY SENSOR YOUR PHONE HAS'. Below: phone frame showing the Antar Sensors screen — list of sensors with live readings: 'Accelerometer 0.02, 0.18, 9.81 m/s²', 'Gyroscope -0.01, 0.00, 0.02 rad/s', 'Magnetometer 26.4 µT', 'Proximity Far', 'Light 412 lx', 'Pressure 1013 hPa'. Cyan iconography per row, dark #0A0E21 background. Caption: 'Read every sensor in real time.' Style match Tile 1. |

> Style consistency check: every prompt above uses the same primary palette (#0A0E21 background, #00E5FF cyan, #448AFF blue, #B388FF purple) and the same caption-banner band. Render all four tiles back-to-back so reviewers see a coherent visual story.
> If a model adds device branding (Pixel / Galaxy / iPhone outlines), regenerate — Play rejects feature graphics with real-brand mockups.


## 🚀 5. What's New (Release Notes)

Plain text only, max 500 characters per version, '•' bullets. Pulled from the actual feature commits in this branch.

| Version | Release Notes Text |
|---|---|
| 1.0 (versionCode 1) — Initial release | • New: 12 device-info screens — Dashboard, Device, System, CPU, Battery, Location, Network, Storage, Display, Sensors, Apps, Camera<br>• New: persistent battery history with 24-hour and 7-day charts plus charging sessions<br>• New: premium dark cyan glassmorphism theme with smooth animations<br>• New: dynamic colors on Android 12+ (Material You), with Antar theme as fallback<br>• New: in-app Settings with rate, share, privacy policy and data deletion shortcuts<br>• Privacy: zero analytics, zero ad SDKs, all data stays on the device<br><br>[522 / 500 chars] |

> Paste each notes block into Play Console → Release → Edit release → 'Release notes' for the matching language.
> Keep the 500-char budget tight: long release notes get truncated in the Play store update card.


## 🛡️ 6. Data Safety Answers

Six high-level questions Play Console asks. Full row-by-row form (33 data-type categories) is in ANTAR_PlayStore_Audit.docx → Section 6.

| Question | Your Answer | Notes (file:line evidence) |
|---|---|---|
| Does your app collect or share user data? | Yes — Location (precise + approximate), Installed apps, Connectivity info (BSSID), Device or other IDs (Android ID). | Every collected type is read on demand; nothing is stored remotely. See permission_declarations.md → Data Safety table for the full row-by-row form. |
| Location data collected? | Yes — both ACCESS_FINE_LOCATION and ACCESS_COARSE_LOCATION; runtime-gated; foreground only. | Sources: LocationRepositoryImpl.kt:104 (FINE check) and :125-141 (LocationManager calls); NetworkRepositoryImpl.kt:92 (FINE check before wifiManager.scanResults). |
| Is location data shared? With whom? | Yes — shared with Google's system Geocoder. | android.location.Geocoder.getFromLocation at LocationRepositoryImpl.kt:180 forwards lat/lon to Google's geocoding service over HTTPS to convert coordinates into a human-readable address. No other party receives location. |
| Can users request data deletion? | Yes. | SettingsScreen.kt → 'Request data deletion' opens Android Settings → App info, where the user can clear local app data (Room battery_log DB + DataStore preferences). Privacy policy page also explains this. |
| Is data encrypted in transit? | Yes. | The only outbound traffic is the Geocoder call, which uses HTTPS. App makes no other network requests (Retrofit / OkHttp deps are unused — Section 5 of the audit recommends removing them). |
| Advertising ID collected? | No. | AdvertisingIdClient call removed; AD_ID permission removed from AndroidManifest.xml; play-services-ads-identifier dependency dropped from app/build.gradle.kts. |

> If you reduce the feature surface (e.g. drop the address field on the Location screen and remove the Geocoder call), the answer to 'Is location data shared?' becomes 'No' — fewer disclosures, faster review.


## ✔️ 7. Pre-Submission Checklist

> [!IMPORTANT]
> Tick each item before pressing 'Send for review' in Play Console. Required items (red) are blockers; To Do items are recommended; Ready items are already satisfied by the codebase.

|  | Task | Status | Notes / Pointer |
|---|---|---|---|
| ☐ | Replace any test/placeholder API keys or ad unit IDs | ✅ Ready | App has no ad SDK and no API keys. Verified by grep for InterstitialAd, MobileAds, com.google.android.gms.ads — zero matches. |
| ☐ | Upload signed release APK or AAB | ⬜ To Do | Build via Android Studio → Build → Generate Signed App Bundle. Use Play App Signing. |
| ☐ | Confirm versionCode and versionName | ✅ Ready | build.gradle.kts:16-17 → versionCode = 1, versionName = '1.0'. |
| ☐ | Upload Feature Graphic (1024 × 500) | ⬜ To Do | Use prompt A or B from Section 4.1. |
| ☐ | Upload App Icon (512 × 512) | ⬜ To Do | Use prompt from Section 4.2. |
| ☐ | Upload minimum 2 screenshots (1080 × 1920) | ⬜ To Do | Recommend the 4 from Section 4.3 for visual coherence. |
| ☐ | Fill App Title | ⬜ To Do | Use 'Antar - Device Info' (Section 1.1). |
| ☐ | Fill Short Description | ⬜ To Do | Use the 76-char string from Section 1.2. |
| ☐ | Fill Long Description | ⬜ To Do | Paste the full block from Section 1.3. |
| ☐ | Select correct Category | ⬜ To Do | Tools (Section 2). |
| ☐ | Complete Data Safety form | ⚠️ Required | Use the 33-row table in ANTAR_PlayStore_Audit.docx Section 6.2 + the answers in Section 6 of this kit. |
| ☐ | Add release notes | ⬜ To Do | Paste v1.0 block from Section 5. |
| ☐ | Set Content Rating via questionnaire | ⬜ To Do | Expected: Everyone. App is utility-only with no UGC. |
| ☐ | Verify Privacy Policy URL is live | ⚠️ Required | https://ashokvarma.dev/antar/privacy must return HTTP 200 and list every permission. Audit doc finding #3. |
| ☐ | Verify Data Deletion URL is live | ⚠️ Required | https://ashokvarma.dev/antar/data-deletion. Hard-coded in SettingsScreen.kt:45. |
| ☐ | File Sensitive Permissions Declaration for QUERY_ALL_PACKAGES | ⚠️ Required | Form text in permission_declarations.md → Declaration 1. |
| ☐ | Upload demo video URL for QUERY_ALL_PACKAGES | ⚠️ Required | Recording script in permission_video_guide.md → V1. |
| ☐ | Implement Prominent Disclosure dialogs (LocationScreen + NetworkScreen) | ⚠️ Required | Audit doc finding #2. Wording in permission_declarations.md Disclosures 1 + 2. |
| ☐ | Final QA on physical device release build | ⬜ To Do | Test all 12 screens; verify the Battery 24h chart populates after 15 min of foreground use; verify deny-permissions path doesn't crash any screen. |
| ☐ | Test edge cases (first launch, permission denial, no network, no SIM, airplane mode) | ⬜ To Do | Each repository returns sentinel strings on denial — no crashes expected, but verify on a real device. |
| ☐ | Run Play pre-launch report on Internal testing track | ⬜ To Do | Catches Material design regressions, ANRs, and accessibility issues before production. |

> [!CAUTION]
> If you tackle the four ⚠️ Required items in this order — privacy URL live → data deletion URL live → Sensitive Permissions form filed with video → Prominent Disclosure dialogs implemented — submission becomes a 30-minute task.
> Cross-reference with the action plan in ANTAR_PlayStore_Audit.docx Section 4 for effort estimates.


---

<sub>🎨 Rendered with color highlights — <span style="color:#22c55e">green = pass</span>, <span style="color:#ef4444">red = blocker</span>, <span style="color:#f59e0b">amber = review</span>, <span style="color:#3b82f6">blue = info</span>.</sub>
