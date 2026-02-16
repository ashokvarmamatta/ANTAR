package com.ashes.dev.works.system.core.internals.antar.data.repository

import android.content.Context
import android.graphics.ImageFormat
import android.hardware.camera2.CameraCharacteristics
import android.hardware.camera2.CameraManager
import android.os.Build
import android.util.Log
import android.util.Size
import java.util.Locale

class ResolutionHunter(private val context: Context) {

    private val manager = context.getSystemService(Context.CAMERA_SERVICE) as CameraManager
    private val TAG = "ResolutionHunter"

    data class DebugResult(
        val finalMegapixels: String,
        val finalWidth: Int,
        val finalHeight: Int,
        val winningMethod: String,
        val fullLogReport: String // <--- The detailed log is here
    )

    fun findMaxResolution(cameraId: String): DebugResult {
        var maxPixelCount: Long = 0
        var maxW = 0
        var maxH = 0
        var winningMethod = "Standard (Binned)"

        // We will build a massive string log here
        val report = StringBuilder()
        report.append("\n=== RESOLUTION HUNT FOR CAMERA ID: $cameraId ===\n")

        try {
            val chars = manager.getCameraCharacteristics(cameraId)

            // --- BASELINE: Standard JPEG ---
            val standardMap = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
            val standardJpeg = standardMap?.getOutputSizes(ImageFormat.JPEG)?.maxByOrNull { it.width * it.height }
            if (standardJpeg != null) {
                val mp = calculateMP(standardJpeg)
                report.append("  [Base] Standard JPEG: ${standardJpeg.width}x${standardJpeg.height} ($mp MP)\n")

                // Set as initial baseline
                maxPixelCount = (standardJpeg.width.toLong() * standardJpeg.height.toLong())
                maxW = standardJpeg.width
                maxH = standardJpeg.height
            } else {
                report.append("  [Base] Standard JPEG: NULL (Error)\n")
            }

            // --- METHOD 1: Ultra High-Res Map (API 31+) ---
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                try {
                    val highResMap = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP_MAXIMUM_RESOLUTION)
                    val highResJpeg = highResMap?.getOutputSizes(ImageFormat.JPEG)?.maxByOrNull { it.width * it.height }

                    if (highResJpeg != null) {
                        val mp = calculateMP(highResJpeg)
                        report.append("  [Method 1] Ultra Hi-Res API: ${highResJpeg.width}x${highResJpeg.height} ($mp MP) -> FOUND!\n")

                        val pixels = highResJpeg.width.toLong() * highResJpeg.height.toLong()
                        if (pixels > maxPixelCount) {
                            maxPixelCount = pixels
                            maxW = highResJpeg.width
                            maxH = highResJpeg.height
                            winningMethod = "Method 1 (Hi-Res API)"
                        }
                    } else {
                        report.append("  [Method 1] Ultra Hi-Res API: Not supported or returned null\n")
                    }
                } catch (e: Exception) {
                    report.append("  [Method 1] Error: ${e.message}\n")
                }
            } else {
                report.append("  [Method 1] Skipped (Device is older than Android 12)\n")
            }

            // --- METHOD 2: RAW / YUV Size Scrape ---
            val rawSize = standardMap?.getOutputSizes(ImageFormat.RAW_SENSOR)?.maxByOrNull { it.width * it.height }
            if (rawSize != null) {
                val mp = calculateMP(rawSize)
                report.append("  [Method 2] RAW_SENSOR Format: ${rawSize.width}x${rawSize.height} ($mp MP)\n")

                val pixels = rawSize.width.toLong() * rawSize.height.toLong()
                if (pixels > maxPixelCount) {
                    maxPixelCount = pixels
                    maxW = rawSize.width
                    maxH = rawSize.height
                    winningMethod = "Method 2 (RAW Format)"
                }
            } else {
                report.append("  [Method 2] RAW_SENSOR: Not supported\n")
            }

            // --- METHOD 3: Physical Camera Iteration ---
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                val physicalIds = chars.physicalCameraIds
                report.append("  [Method 3] Physical Cameras Found: ${physicalIds.size} (IDs: $physicalIds)\n")

                for (pId in physicalIds) {
                    try {
                        val pChars = manager.getCameraCharacteristics(pId)
                        val pArray = pChars.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)

                        if (pArray != null) {
                            val mp = calculateMP(pArray)
                            report.append("      -> Phys ID $pId Array: ${pArray.width}x${pArray.height} ($mp MP)\n")

                            val pixels = pArray.width.toLong() * pArray.height.toLong()
                            if (pixels > maxPixelCount) {
                                maxPixelCount = pixels
                                maxW = pArray.width
                                maxH = pArray.height
                                winningMethod = "Method 3 (Physical ID $pId)"
                            }
                        }
                    } catch (e: Exception) {
                        report.append("      -> Phys ID $pId: Error reading\n")
                    }
                }
            } else {
                report.append("  [Method 3] Skipped (Device older than Android 9)\n")
            }

            // --- METHOD 4: Hardware Pixel Array Size (The Silicon Truth) ---
            val arraySize = chars.get(CameraCharacteristics.SENSOR_INFO_PIXEL_ARRAY_SIZE)
            if (arraySize != null) {
                val mp = calculateMP(arraySize)
                report.append("  [Method 4] Hardware Active Array: ${arraySize.width}x${arraySize.height} ($mp MP)\n")

                val pixels = arraySize.width.toLong() * arraySize.height.toLong()
                if (pixels > maxPixelCount) {
                    maxPixelCount = pixels
                    maxW = arraySize.width
                    maxH = arraySize.height
                    winningMethod = "Method 4 (HW Active Array)"
                }
            } else {
                report.append("  [Method 4] Hardware Active Array: Null\n")
            }

        } catch (e: Exception) {
            report.append("CRITICAL ERROR: ${e.message}\n")
            Log.e(TAG, "Error analyzing camera $cameraId", e)
        }

        // --- METHOD 5: Brute Force Hidden IDs (Only if still low res) ---
        if (maxPixelCount < 20_000_000 && cameraId == "0") {
            report.append("  [Method 5] Triggering Brute Force (Current max < 20MP)...\n")
            val hiddenResult = bruteForceHiddenIds(report) // Pass report to log attempts
            if (hiddenResult != null && hiddenResult.pixelCount > maxPixelCount) {
                maxPixelCount = hiddenResult.pixelCount
                maxW = hiddenResult.width
                maxH = hiddenResult.height
                winningMethod = "Method 5 (Hidden ID ${hiddenResult.id})"
                report.append("      -> WINNER: Hidden ID ${hiddenResult.id} found with high res!\n")
            } else {
                report.append("      -> Brute Force: No better hidden cameras found.\n")
            }
        } else {
            report.append("  [Method 5] Skipped (Resolution already satisfactory or not Cam 0)\n")
        }

        val mpString = formatMP(maxPixelCount)
        report.append("=== FINAL VERDICT: $mpString ($maxW x $maxH) via $winningMethod ===\n")

        return DebugResult(mpString, maxW, maxH, winningMethod, report.toString())
    }

    private data class HiddenCam(val id: String, val pixelCount: Long, val width: Int, val height: Int)

    private fun bruteForceHiddenIds(report: StringBuilder): HiddenCam? {
        var bestCam: HiddenCam? = null
        // Common hidden IDs: 2, 3, 4 (Xiaomi aux), 20+, 40+ (Samsung raw)
        val candidates = listOf("2", "3", "4", "5", "20", "21", "22", "40", "41", "60", "61")

        report.append("      -> Scanning IDs: $candidates\n")

        for (id in candidates) {
            try {
                // If this throws, the ID doesn't exist
                val chars = manager.getCameraCharacteristics(id)
                val map = chars.get(CameraCharacteristics.SCALER_STREAM_CONFIGURATION_MAP)
                val size = map?.getOutputSizes(ImageFormat.JPEG)?.maxByOrNull { it.width * it.height }

                if (size != null) {
                    val mp = calculateMP(size)
                    report.append("      -> ID $id exists: ${size.width}x${size.height} ($mp MP)\n")

                    val pixels = size.width.toLong() * size.height.toLong()
                    if (bestCam == null || pixels > bestCam!!.pixelCount) {
                        bestCam = HiddenCam(id, pixels, size.width, size.height)
                    }
                }
            } catch (e: Exception) {
                // Ignore
            }
        }
        return bestCam
    }

    private fun calculateMP(size: Size): String {
        val mp = (size.width.toLong() * size.height.toLong()).toDouble() / 1_000_000.0
        return String.format(Locale.US, "%.1f", mp)
    }

    private fun formatMP(pixels: Long): String {
        val mp = pixels.toDouble() / 1_000_000.0
        return when {
            mp > 190 -> "200 MP"
            mp > 100 -> "108 MP"
            mp > 60 -> "64 MP"
            mp > 48 -> "50 MP"
            else -> String.format(Locale.US, "%.1f MP", mp)
        }
    }
}