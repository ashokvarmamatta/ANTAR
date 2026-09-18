package com.ashes.dev.works.system.core.internals.antar

import org.junit.Assert.assertTrue
import org.junit.Test
import java.io.File

/**
 * Where UI code lives:
 * - a screen's own composables sit in `presentation/<screen>/components/`; `XxxScreen.kt` keeps only
 *   the entry composable and its state roots (`*Screen`, `*Content`, `*Granted`)
 * - one screen never imports another screen's components; shared ones go to `presentation/components/`
 * - composables live only under `presentation/` (plus the theme); `MainActivity` hosts the root and
 *   holds no UI of its own.
 */
class PresentationLayoutTest {

    private val appPackage = "com.ashes.dev.works.system.core.internals.antar"
    private val sourceRoot = File("src/main/java/${appPackage.replace('.', '/')}")
    private val presentation = File(sourceRoot, "presentation")
    private val composable = Regex("""@Composable\s+(?:(?:private|internal)\s+)?fun\s+(?:[\w.]+\.)?(\w+)""")
    private val stateRoot = Regex("(Screen|Content|Granted)$")

    private fun kotlinFiles(dir: File) = dir.walkTopDown().filter { it.isFile && it.extension == "kt" }.toList()

    @Test
    fun screenFiles_holdOnlyTheScreenAndItsStateRoots() {
        val violations = presentation.walkTopDown()
            .filter { it.isFile && it.name.endsWith("Screen.kt") && it.parentFile.name != "components" }
            .flatMap { file ->
                composable.findAll(file.readText()).map { it.groupValues[1] }
                    .filter { it.first().isUpperCase() } // lower-case composables return values, not UI
                    .filterNot { stateRoot.containsMatchIn(it) }
                    .map { "${file.parentFile.name}/${file.name}: $it belongs in ${file.parentFile.name}/components/" }
            }.toList()
        assertTrue(violations.joinToString("\n"), violations.isEmpty())
    }

    @Test
    fun screens_doNotImportAnotherScreensComponents() {
        val importRegex = Regex("""^import $appPackage\.presentation\.(\w+)\.components\.""", RegexOption.MULTILINE)
        val violations = kotlinFiles(presentation).flatMap { file ->
            val ownFeature = file.relativeTo(presentation).path.replace('\\', '/').substringBefore('/')
            importRegex.findAll(file.readText()).map { it.groupValues[1] }
                .filter { it != ownFeature }
                .map { "${file.relativeTo(presentation)} imports $it/components — move the component to presentation/components/" }
                .toList()
        }
        assertTrue(violations.joinToString("\n"), violations.isEmpty())
    }

    @Test
    fun composables_liveOnlyInPresentationOrTheme() {
        val theme = File(sourceRoot, "core/designsystem/theme")
        val violations = kotlinFiles(sourceRoot)
            .filterNot { it.startsWith(presentation) || it.startsWith(theme) }
            .filter { composable.containsMatchIn(it.readText()) }
            .map { it.relativeTo(sourceRoot).path }
        assertTrue("Composables outside presentation/:\n" + violations.joinToString("\n"), violations.isEmpty())
    }

    @Test
    fun mainActivity_onlyHostsTheRoot() {
        val text = File(sourceRoot, "MainActivity.kt").readText()
        val forbidden = listOf("NavHost", "NavGraph", "rememberNavController", "BackHandler", "Crossfade", "ANTARTheme")
            .filter { text.contains(it) }
        assertTrue("MainActivity must only host AntarRoot; found: $forbidden", forbidden.isEmpty())
        assertTrue("MainActivity must call AntarRoot", text.contains("AntarRoot("))
    }
}
