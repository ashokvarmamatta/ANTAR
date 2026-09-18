package com.ashes.dev.works.system.core.internals.antar

import com.ashes.dev.works.system.core.internals.antar.core.common.NO_VALUE
import com.ashes.dev.works.system.core.internals.antar.presentation.components.InfoRowDefaults
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * presentation/components is ANTAR's shared component library: it must stay copyable into
 * another app. It may use Compose, Material, Accompanist and the design system's motion helpers,
 * but never ANTAR's resources, brand colours or any feature, data, domain or DI code.
 */
class ComponentLibraryBoundaryTest {

    private val appPackage = "com.ashes.dev.works.system.core.internals.antar"
    private val componentDir =
        File("src/main/java/${appPackage.replace('.', '/')}/presentation/components")

    private val allowedAppImports = setOf(
        "$appPackage.presentation.components.",
        "$appPackage.core.designsystem.theme.pressScale",
        "$appPackage.core.designsystem.theme.shimmer",
        "$appPackage.core.designsystem.theme.spatialSpec",
        "$appPackage.core.designsystem.theme.LocalAnimationIntensity"
    )

    private val forbiddenInBody = listOf(
        Regex("""\bR\.(string|drawable|color|dimen|plurals)\."""),
        Regex("""\bstringResource\("""),
        Regex("""\b(Static)?Antar[A-Z][A-Za-z]*\b"""),
        Regex("""\bGradient(Start|Mid|End)\b"""),
        Regex("""\bGlow(Cyan|Purple)\b""")
    )

    private fun sources(): List<File> =
        componentDir.listFiles { f -> f.extension == "kt" }.orEmpty().toList()

    @Test
    fun componentPackage_exists() {
        assertTrue("component package not found at ${componentDir.absolutePath}", sources().isNotEmpty())
    }

    @Test
    fun components_importNoAppCodeOutsideTheLibrary() {
        val violations = sources().flatMap { file ->
            file.readLines()
                .filter { it.startsWith("import $appPackage.") }
                .map { it.removePrefix("import ").trim() }
                .filterNot { imported -> allowedAppImports.any { imported.startsWith(it) } }
                .map { "${file.name}: $it" }
        }
        assertTrue("App imports in the component library:\n" + violations.joinToString("\n"), violations.isEmpty())
    }

    @Test
    fun components_useNoAppResourcesOrBrandColours() {
        val violations = sources().flatMap { file ->
            file.readLines().withIndex()
                .filterNot { (_, line) -> line.trimStart().startsWith("import ") }
                .flatMap { (index, line) ->
                    forbiddenInBody.mapNotNull { rule ->
                        rule.find(line)?.let { "${file.name}:${index + 1}: ${it.value}" }
                    }
                }
        }
        assertTrue("App resources or brand colours in the component library:\n" + violations.joinToString("\n"), violations.isEmpty())
    }

    @Test
    fun missingValuePlaceholder_matchesTheAppConstant() {
        assertEquals(NO_VALUE, InfoRowDefaults.MissingValue)
    }
}
