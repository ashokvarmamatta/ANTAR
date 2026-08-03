package com.ashes.dev.works.system.core.internals.antar

import org.junit.Assert.assertEquals
import org.junit.Test
import java.util.Locale

class SimpleLogicTest {

    @Test
    fun testPercentageCalculation() {
        val total = 1000f
        val used = 450f
        
        // Simple logic: (used / total) * 100
        val percentage = (used / total) * 100
        
        assertEquals(45f, percentage, 0.1f)
    }

    /**
     * Tests the 'intToIp' logic used in NetworkRepository.
     * This converts an integer (from DHCP info) into a human-readable IP address string.
     */
    @Test
    fun testIntToIpConversion() {
        // 192.168.1.1 in integer format (little-endian)
        val ipAsInt = 16885952 
        
        val result = intToIpHelper(ipAsInt)
        
        assertEquals("192.168.1.1", result)
    }

    /**
     * Tests the 'formatSize' logic used in Storage and System repositories.
     * Verifies it correctly converts bytes to MB/GB.
     */
    @Test
    fun testFormatSize() {
        val fiveGB = 5L * 1024 * 1024 * 1024
        val result = formatSizeHelper(fiveGB)
        
        assertEquals("5.00 GB", result)
    }

    // --- Helpers (Copying logic from repositories to test it in isolation) ---

    private fun intToIpHelper(ipAddress: Int): String {
        return (ipAddress and 0xFF).toString() + "." +
                (ipAddress shr 8 and 0xFF) + "." +
                (ipAddress shr 16 and 0xFF) + "." +
                (ipAddress shr 24 and 0xFF)
    }

    private fun formatSizeHelper(size: Long): String {
        if (size <= 0) return "0 B"
        val suffix = arrayOf("B", "KB", "MB", "GB", "TB")
        var fSize = size.toDouble()
        var i = 0
        while (fSize >= 1024 && i < suffix.size - 1) {
            fSize /= 1024
            i++
        }
        return "%.2f %s".format(Locale.US, fSize, suffix[i])
    }
}
