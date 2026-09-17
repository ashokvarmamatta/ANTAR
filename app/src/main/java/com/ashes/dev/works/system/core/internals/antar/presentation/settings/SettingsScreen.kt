package com.ashes.dev.works.system.core.internals.antar.presentation.settings

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.provider.Settings
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Animation
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.DeleteForever
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.PrivacyTip
import androidx.compose.material.icons.outlined.StarRate
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.ashes.dev.works.system.core.internals.antar.R
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.GradientHeaderCard
import com.ashes.dev.works.system.core.internals.antar.core.ui.InfoRow
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.LoadingSkeleton
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.PremiumCard
import com.ashes.dev.works.system.core.internals.antar.core.ui.SectionTitle
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarAccentColors
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarBlue
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarCyan
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarDark
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarGray
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarPurple
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.AntarRed
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.LocalAnimationIntensity
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.bounceClick
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.contentSwap
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.effectsSpec
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.pressScale
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.theme.staggeredEntry
import com.ashes.dev.works.system.core.internals.antar.core.designsystem.component.AdaptiveCardGrid
import com.ashes.dev.works.system.core.internals.antar.core.ui.PrivacySheet
import com.ashes.dev.works.system.core.internals.antar.domain.model.MotionLevel
import com.ashes.dev.works.system.core.internals.antar.domain.model.ThemeMode
import org.koin.androidx.compose.koinViewModel

private const val PRIVACY_POLICY_URL = "https://ashes-dev-works.web.app/antar/"
private const val DEVELOPER_URL = "https://ashokvarma.dev"
private const val PLAY_PACKAGE = "com.ashes.dev.works.system.core.internals.antar"

@Composable
fun SettingsScreen(
    navController: NavController,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    var showPrivacySheet by rememberSaveable { mutableStateOf(false) }
    val context = LocalContext.current

    if (showPrivacySheet) {
        PrivacySheet(
            onDismiss = { showPrivacySheet = false },
            onReadPolicy = {
                showPrivacySheet = false
                openUrl(context, PRIVACY_POLICY_URL)
            }
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Fixed header owns the top inset; only the grid below scrolls.
        Surface(color = MaterialTheme.colorScheme.background) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Top + WindowInsetsSides.Horizontal))
                    .padding(horizontal = 8.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                val backSource = remember { MutableInteractionSource() }
                IconButton(
                    onClick = { navController.popBackStack() },
                    interactionSource = backSource,
                    modifier = Modifier.pressScale(backSource)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                        contentDescription = stringResource(R.string.common_back),
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = stringResource(R.string.settings_title),
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.ExtraBold,
                    color = MaterialTheme.colorScheme.primary,
                    letterSpacing = 2.sp,
                    modifier = Modifier.padding(start = 4.dp)
                )
            }
        }

        AnimatedContent(
            targetState = uiState,
            contentKey = { it::class },
            transitionSpec = LocalAnimationIntensity.current.contentSwap(),
            modifier = Modifier.weight(1f),
            label = "settingsState"
        ) { state ->
            when (state) {
                SettingsUiState.Loading -> LoadingSkeleton()
                is SettingsUiState.Content -> SettingsContent(
                    state = state,
                    viewModel = viewModel,
                    onShowPrivacy = { showPrivacySheet = true }
                )
            }
        }

        // Pinned outside the scrollable so it can never scroll out of reach.
        val content = uiState as? SettingsUiState.Content
        val applySource = remember { MutableInteractionSource() }
        Button(
            onClick = viewModel::apply,
            enabled = content?.hasChanges == true,
            interactionSource = applySource,
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .windowInsetsPadding(WindowInsets.safeDrawing.only(WindowInsetsSides.Horizontal))
                .padding(horizontal = 16.dp, vertical = 12.dp)
                .pressScale(applySource),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            )
        ) {
            Text(
                text = stringResource(R.string.settings_apply),
                fontWeight = FontWeight.Bold,
                color = if (content?.hasChanges == true) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun SettingsContent(
    state: SettingsUiState.Content,
    viewModel: SettingsViewModel,
    onShowPrivacy: () -> Unit
) {
    val context = LocalContext.current
    val versionName = remember(context) { readVersionName(context) }
    val draft = state.draft
    val noBrowser = stringResource(R.string.settings_no_browser)
    val noAppSettings = stringResource(R.string.settings_cannot_open_app_settings)

    AdaptiveCardGrid {
        item(key = "header", span = StaggeredGridItemSpan.FullLine) {
            GradientHeaderCard(modifier = Modifier.staggeredEntry(0)) {
                Column(modifier = Modifier.padding(24.dp)) {
                    Text(
                        text = stringResource(R.string.settings_brand),
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    )
                    Text(
                        text = stringResource(R.string.app_name),
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = stringResource(R.string.settings_version, versionName),
                        style = MaterialTheme.typography.bodySmall,
                        color = AntarGray
                    )
                }
            }
        }

        item(key = "theme") {
            PremiumCard(modifier = Modifier.staggeredEntry(1)) {
                SectionTitle(title = R.string.settings_section_theme, icon = Icons.Outlined.Palette, accentColor = AntarCyan)

                SegmentedChoice(
                    options = listOf(
                        ThemeMode.SYSTEM to R.string.settings_theme_system,
                        ThemeMode.LIGHT to R.string.settings_theme_light,
                        ThemeMode.DARK to R.string.settings_theme_dark
                    ),
                    selected = draft.themeMode,
                    accent = AntarCyan,
                    onSelected = viewModel::selectThemeMode
                )

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                    Spacer(modifier = Modifier.height(16.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = stringResource(R.string.settings_dynamic_colors),
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                            Text(
                                text = stringResource(R.string.settings_dynamic_colors_summary),
                                style = MaterialTheme.typography.bodySmall,
                                color = AntarGray
                            )
                        }
                        val switchSource = remember { MutableInteractionSource() }
                        Switch(
                            checked = draft.dynamicColors,
                            onCheckedChange = viewModel::selectDynamicColors,
                            interactionSource = switchSource,
                            modifier = Modifier.pressScale(switchSource),
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = AntarCyan,
                                checkedTrackColor = AntarCyan.copy(alpha = 0.5f)
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    text = stringResource(R.string.settings_accent_color),
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = stringResource(
                        if (draft.dynamicColors) R.string.settings_accent_disabled_hint else R.string.settings_accent_hint
                    ),
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray
                )
                Spacer(modifier = Modifier.height(12.dp))
                AccentColorRow(
                    selectedIndex = draft.accentIndex,
                    enabled = !draft.dynamicColors,
                    onSelected = viewModel::selectAccent
                )
            }
        }

        item(key = "motion") {
            PremiumCard(modifier = Modifier.staggeredEntry(2)) {
                SectionTitle(title = R.string.settings_section_motion, icon = Icons.Outlined.Animation, accentColor = AntarBlue)
                Text(
                    text = stringResource(R.string.settings_motion_summary),
                    style = MaterialTheme.typography.bodySmall,
                    color = AntarGray,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                SegmentedChoice(
                    options = listOf(
                        MotionLevel.LOW to R.string.settings_motion_low,
                        MotionLevel.MEDIUM to R.string.settings_motion_medium,
                        MotionLevel.HIGH to R.string.settings_motion_high
                    ),
                    selected = draft.motionLevel,
                    accent = AntarBlue,
                    onSelected = viewModel::selectMotionLevel
                )
            }
        }

        item(key = "privacy") {
            PremiumCard(modifier = Modifier.staggeredEntry(3)) {
                SectionTitle(title = R.string.settings_section_privacy, icon = Icons.Outlined.PrivacyTip, accentColor = AntarPurple)
                SettingsRow(
                    icon = Icons.Outlined.PrivacyTip,
                    accent = AntarPurple,
                    title = R.string.settings_privacy_policy,
                    subtitle = PRIVACY_POLICY_URL.removePrefix("https://"),
                    onClick = { openUrl(context, PRIVACY_POLICY_URL, noBrowser) }
                )
                SettingsRow(
                    icon = Icons.Outlined.DeleteForever,
                    accent = AntarRed,
                    title = R.string.settings_data_deletion,
                    subtitle = stringResource(R.string.settings_data_deletion_summary),
                    onClick = { openAppInfoSettings(context, noAppSettings) }
                )
                SettingsRow(
                    icon = Icons.Outlined.Info,
                    accent = AntarCyan,
                    title = R.string.settings_what_collected,
                    subtitle = stringResource(R.string.settings_what_collected_summary),
                    onClick = onShowPrivacy
                )
            }
        }

        item(key = "about") {
            PremiumCard(modifier = Modifier.staggeredEntry(4)) {
                SectionTitle(title = R.string.settings_section_about, icon = Icons.Outlined.Info)
                InfoRow(R.string.settings_label_app_name, stringResource(R.string.app_name))
                InfoRow(R.string.settings_label_version, versionName)
                Spacer(modifier = Modifier.height(8.dp))
                SettingsRow(
                    icon = Icons.Outlined.StarRate,
                    accent = AntarPurple,
                    title = R.string.settings_rate_app,
                    subtitle = stringResource(R.string.settings_rate_app_summary),
                    onClick = { rateApp(context, noBrowser) }
                )
                Spacer(modifier = Modifier.height(8.dp))
                SettingsRow(
                    icon = Icons.Outlined.Info,
                    accent = AntarCyan,
                    title = R.string.settings_developer,
                    subtitle = DEVELOPER_URL.removePrefix("https://"),
                    onClick = { openUrl(context, DEVELOPER_URL, noBrowser) }
                )
            }
        }
    }
}

/** A row of equal segments; tapping one only ticks it (Apply commits). */
@Composable
private fun <T> SegmentedChoice(
    options: List<Pair<T, Int>>,
    selected: T,
    accent: Color,
    onSelected: (T) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(4.dp),
        horizontalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        val intensity = LocalAnimationIntensity.current
        options.forEach { (value, label) ->
            val isSelected = selected == value
            val background by animateColorAsState(
                if (isSelected) accent.copy(alpha = 0.15f) else Color.Transparent,
                intensity.effectsSpec(),
                label = "segmentBackground"
            )
            val shape = RoundedCornerShape(8.dp)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(shape)
                    .background(background)
                    .border(0.5.dp, if (isSelected) accent.copy(alpha = 0.4f) else Color.Transparent, shape)
                    .bounceClick { onSelected(value) }
                    .padding(vertical = 10.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = stringResource(label),
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                    color = if (isSelected) accent else MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1
                )
            }
        }
    }
}

@Composable
private fun AccentColorRow(
    selectedIndex: Int,
    enabled: Boolean,
    onSelected: (Int) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        AntarAccentColors.forEachIndexed { index, color ->
            val isSelected = index == selectedIndex
            Box(
                modifier = Modifier
                    .weight(1f)
                    .aspectRatio(1f)
                    .clip(CircleShape)
                    .background(color.copy(alpha = if (enabled) 1f else 0.35f))
                    .border(
                        width = if (isSelected) 2.5.dp else 0.dp,
                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else Color.Transparent,
                        shape = CircleShape
                    )
                    .then(if (enabled) Modifier.bounceClick { onSelected(index) } else Modifier),
                contentAlignment = Alignment.Center
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Outlined.Check,
                        contentDescription = stringResource(R.string.settings_accent_selected),
                        tint = AntarDark,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingsRow(
    icon: ImageVector,
    accent: Color,
    @StringRes title: Int,
    subtitle: String,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(12.dp))
            .bounceClick(pressedScale = 0.98f, onClick = onClick)
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(accent.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(imageVector = icon, contentDescription = null, tint = accent, modifier = Modifier.size(20.dp))
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = stringResource(title),
                style = MaterialTheme.typography.bodyLarge,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = AntarGray,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
        }
        Icon(
            imageVector = Icons.Outlined.ChevronRight,
            contentDescription = null,
            tint = AntarGray,
            modifier = Modifier.size(20.dp)
        )
    }
}

private fun openUrl(context: Context, url: String, noBrowserMessage: String? = null) {
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url)).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        if (noBrowserMessage != null) Toast.makeText(context, noBrowserMessage, Toast.LENGTH_SHORT).show()
    }
}

private fun rateApp(context: Context, noBrowserMessage: String) {
    // The base package name is used explicitly: debug builds carry a ".debug" suffix that does not
    // exist on the Play Store.
    val goToMarket = Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=$PLAY_PACKAGE")).apply {
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_NO_HISTORY or Intent.FLAG_ACTIVITY_MULTIPLE_TASK)
    }
    try {
        context.startActivity(goToMarket)
    } catch (_: ActivityNotFoundException) {
        openUrl(context, "https://play.google.com/store/apps/details?id=$PLAY_PACKAGE", noBrowserMessage)
    }
}

private fun openAppInfoSettings(context: Context, failureMessage: String) {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", context.packageName, null)
        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    }
    try {
        context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
        Toast.makeText(context, failureMessage, Toast.LENGTH_SHORT).show()
    }
}

private fun readVersionName(context: Context): String = try {
    val pm = context.packageManager
    val info = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        pm.getPackageInfo(context.packageName, android.content.pm.PackageManager.PackageInfoFlags.of(0))
    } else {
        @Suppress("DEPRECATION")
        pm.getPackageInfo(context.packageName, 0)
    }
    info.versionName.orEmpty()
} catch (_: Exception) {
    ""
}
