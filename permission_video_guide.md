# ANTAR — Permission Demo Video Guide (final)

**App:** Antar - Device info
**Last updated:** 2026-05-09

After the cleanup, only **one** Sensitive Permissions Declaration form remains: `QUERY_ALL_PACKAGES`. That's the only video Play will ask for. This file is the recording brief for it, plus optional backup videos in case a reviewer escalates.

---

## Required vs Optional

| # | Video | Reason | Required? | Target length |
|---|---|---|---|---|
| V1 | **`QUERY_ALL_PACKAGES` demo** | High-sensitivity declaration for full installed-app inventory | **REQUIRED** | 30–60 s |
| V2 | Location demo (FINE/COARSE) | Sensitive runtime, but **no declaration form** — no video required | Optional, keep as backup if reviewer asks | 30 s |
| V3 | Network screen demo | Shows that Phone perms are no longer requested and the SIM card now uses unprivileged data | Optional, useful if a reviewer comments on previous-version Phone permissions | 30 s |

> Notes
> - There is **no** video required for `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`, or `NEARBY_WIFI_DEVICES`. Those go in Data Safety only — no Sensitive Permissions Declaration form, no video.
> - `READ_PHONE_STATE`, `READ_PHONE_NUMBERS`, `CAMERA`, `BLUETOOTH_CONNECT`, `READ_BASIC_PHONE_STATE`, `AD_ID`, and `BATTERY_STATS` have all been **removed** from the manifest and the code. No video for any of them.

---

## General recording tips

- Use a real device (or Android Studio Emulator with API 34/35), **not** a desktop screen recorder pointed at a window.
- Resolution ≥ 720p, ≤ 60 s if possible (Play prefers concise demos).
- No background music. No edits / cuts in the middle of the permission flow — the reviewer must see the **uninterrupted** sequence: prominent disclosure → system dialog → feature working.
- Show the entire user gesture: tap, scroll, tap. Do not jump between states.
- Filename suggestion: `antar-query-all-packages.mp4`. Upload to a publicly listed YouTube URL or a public Google Drive link with **anyone-with-the-link** access.

---

## V1 — `QUERY_ALL_PACKAGES` demo (REQUIRED)

**Permission covered:** `QUERY_ALL_PACKAGES`.

**Target length:** 30–60 seconds.

**Pre-recording setup:**
- Device with a realistic spread of installed apps (system + user apps). 60+ packages is ideal — it makes the "we genuinely need everything" case visually obvious.
- Fresh install of ANTAR, or at least navigate from Dashboard so the reviewer sees the entry point.

**Recording script — step by step:**

1. **(0–3 s)** Show the device home screen. Tap the ANTAR launcher icon.
2. **(3–8 s)** Dashboard appears. Tap the **Apps** entry in the navigation.
3. **(8–15 s)** Apps screen renders with the count header (e.g., "147 apps installed") and a long list. **Pause** ~2 s so the reviewer reads the count.
4. **(15–35 s)** Slowly scroll the list from top to bottom. The reviewer must see:
   - User-installed apps (Chrome, WhatsApp, Gmail, etc.).
   - System apps (those marked with a system flag in the UI).
   - Each row showing: app name, package name, version, target SDK, architecture (32-bit / 64-bit / arm64-v8a etc.).
5. **(35–50 s)** Tap into one app row (if the screen supports a detail view) to show that the data is read-only — there's no install, uninstall, modify, or upload action; the screen is purely an inventory display.
6. **(50–60 s)** Optional caption: "ANTAR lists installed packages so users can audit their device. The list is shown locally and never sent off-device."

**What the reviewer must see:**
- The complete inventory rendering (proves it's not a `<queries>`-style narrow list).
- That the screen is **read-only** — no buttons that send/upload/launch the data.
- A representative mix of system + user apps so it's clear the inventory is genuinely all packages.

---

## V2 — Location demo (OPTIONAL backup)

**Permissions covered:** `ACCESS_FINE_LOCATION`, `ACCESS_COARSE_LOCATION`.

**Why optional:** Location does **not** trigger the Sensitive Permissions Declaration form. Play only asks for a video when the form is involved — it isn't here. Record this only if a reviewer escalates.

**Target length:** 30 s.

**Brief script:**
1. Home → ANTAR → Location screen.
2. Empty state shows "Location permission required".
3. Tap **Grant Permission** → Prominent Disclosure dialog (once implemented) → Continue.
4. System dialog → **While using the app**.
5. Screen populates with lat/lon, satellites by constellation, position details, address. Hold for 5 s.
6. Caption: "Location is read on demand and never transmitted."

---

## V3 — Network screen demo (OPTIONAL backup)

**Why optional:** ANTAR previously asked for `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` on this screen. The current version does not. If a reviewer compares the new build to the previous declaration history and asks "why did you drop the Phone permissions?", this video is the answer.

**Target length:** 30 s.

**Brief script:**
1. Home → ANTAR → Network.
2. Empty state shows "Location permission required" — *only* Location, not Phone. Hold for ~3 s so the reviewer reads it.
3. Tap **Grant Permission** → only the Location system dialog appears. Tap **While using the app**. **No phone-permission dialog appears at any point.**
4. Network screen populates: WiFi card, Mobile Data card with "Multi SIM" count, and one **SIM 1** card showing Name, Country ISO, MCC, MNC, Carrier id, Carrier name, Data roaming. Note the absence of a "Phone number" row.
5. Caption: "ANTAR no longer requests Phone permissions. SIM info shown is what the OS exposes without `READ_PHONE_STATE`."

---

## Submission checklist

### Required (must be done before pressing "Submit")
- [ ] **V1** uploaded to YouTube (unlisted is fine, **public-with-link** required) and the URL pasted into the All apps (`QUERY_ALL_PACKAGES`) declaration.
- [ ] V1 starts on the home screen, ends with the Apps screen visibly populated, and shows scrolling through a representative inventory.

### Recommended (do if you have 30 minutes spare)
- [ ] Record **V2** (Location). Keep the URL on hand — paste into a reply if a reviewer asks.
- [ ] Record **V3** (Network) to demonstrate the Phone-permission removal. Especially valuable if a previous submission was already in review with the older permission set.
- [ ] Record a 10-second clip showing **`tcpdump`** or **mitmproxy** logs while the user is on the Network and Apps screens — visual proof of the "no data leaves the device" claim. Not required, but reviewers respond well to it.

---

## Why only 1 video is required (post-cleanup)

Google Play's **Sensitive Permissions and APIs** policy lists exactly which permissions trigger the **Sensitive Permissions Declaration form**. After the cleanup, ANTAR triggers exactly one of them:

- **All apps (`QUERY_ALL_PACKAGES`)** — because the Apps screen calls `packageManager.getInstalledPackages(0)`.

The form has a **"Demonstration video URL"** field. Hence: 1 video.

The previous draft of this guide listed two videos because the old manifest also declared `READ_PHONE_STATE` and `READ_PHONE_NUMBERS` (Phone permission group). Both are now removed from the manifest *and* from the code, so the Phone-permissions video is no longer needed.

Nothing else in the current manifest triggers a Sensitive Permissions Declaration form: location and nearby-Wi-Fi go through Data Safety only, and `INTERNET` / `ACCESS_NETWORK_STATE` / `ACCESS_WIFI_STATE` are normal install-time permissions.

Source: Google Play Console Help → "Use of Permissions and APIs that Access Sensitive Information" (`support.google.com/googleplay/android-developer`).
