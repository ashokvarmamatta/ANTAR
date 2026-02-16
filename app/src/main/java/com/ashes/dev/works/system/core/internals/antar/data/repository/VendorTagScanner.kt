package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.util.Log
import java.lang.reflect.Field
import java.lang.reflect.Modifier

class VendorTagScanner(private val context: Context) {

    private val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val TAG = "ANTAR_VENDOR_TAGS"

    fun scanForHiddenKeys(cameraId: String) {
        Log.d(TAG, "\n=== STARTING VENDOR TAG SCAN FOR CAMERA $cameraId ===")

        try {
            val chars = manager.getCameraCharacteristics(cameraId)
            val allKeys = chars.keys

            // 1. Scan Standard Keys first to see if any look "suspicious" or relate to resolution
            Log.d(TAG, "--- Checking Public Keys for 'Mode' or 'Resolution' ---")
            for (key in allKeys) {
                if (key.name.contains("remosaic", ignoreCase = true) ||
                    key.name.contains("ultra", ignoreCase = true) ||
                    key.name.contains("super", ignoreCase = true) ||
                    key.name.contains("binning", ignoreCase = true)) {

                    val value = chars.get(key)
                    Log.d(TAG, "  [PUBLIC] Found Suspicious Key: ${key.name} = $value")
                }
            }

            // 2. REFLECTION: Scan for Hidden Static Fields in CameraCharacteristics
            // Xiaomi/Samsung often hide "public static final Key<...>" fields in the class itself
            Log.d(TAG, "--- Reflection Scan for Hidden Static Keys ---")
            val fields = CameraCharacteristics::class.java.declaredFields
            for (field in fields) {
                if (Modifier.isStatic(field.modifiers)) {
                    try {
                        field.isAccessible = true
                        if (field.type == CameraCharacteristics.Key::class.java) {
                            val key = field.get(null) as CameraCharacteristics.Key<*>

                            // Filter for vendor-specific names (often start with "com." or "xiaomi.")
                            if (key.name.contains("xiaomi", ignoreCase = true) ||
                                key.name.contains("mi", ignoreCase = true) ||
                                key.name.contains("remosaic", ignoreCase = true)) {

                                val value = chars.get(key)
                                Log.d(TAG, "  [HIDDEN] Found Secret Key: ${key.name} = $value")
                            }
                        }
                    } catch (e: Exception) {
                        // Ignore reflection errors
                    }
                }
            }

            // 3. Brute Force Tag ID Scan (The "Deep" Scan)
            // Vendor tags are often just integer IDs starting at 0x80000000.
            // We can't easily guess them, but if the device supports Camera2VendorTags, we can list them.
            // (This is advanced and might return nothing on some ROMs)

        } catch (e: Exception) {
            Log.e(TAG, "Error scanning vendor tags", e)
        }

        Log.d(TAG, "=== SCAN COMPLETE ===")
    }
}