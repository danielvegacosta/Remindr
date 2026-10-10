@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Check
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun ThemesScreen(
    selectedKey: String,
    appearance: AppearancePrefs,
    isDark: Boolean,
    onAppearanceChange: (AppearancePrefs) -> Unit,
    onBack: () -> Unit,
    onSelect: (String) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    var menuOpen by remember { mutableStateOf(false) }
    val selectEnabled = !appearance.followSystem
    val entries = remember { themeEntries() }
    val activeKey = entries.firstOrNull { it.key == selectedKey }?.key ?: defaultThemeKey()

    Column(Modifier.fillMaxSize().background(cs.background)) {
        ScreenTopBar(title = "Aparência", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            SectionTitle("Modo")
            SurfaceCard {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTile(Icons.Outlined.Settings, cs.primary)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Padrão do sistema", color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Segue o tema claro ou escuro do aparelho",
                            color = cs.onSurfaceVariant,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                    Spacer(Modifier.width(8.dp))
                    Switch(
                        checked = appearance.followSystem,
                        onCheckedChange = { on ->
                            onAppearanceChange(
                                if (on) appearance.copy(followSystem = true)
                                else AppearancePrefs(followSystem = false, dark = isDark)
                            )
                        }
                    )
                }
                HorizontalDivider(
                    modifier = Modifier.padding(start = 78.dp, end = 20.dp),
                    thickness = 0.5.dp,
                    color = hairline()
                )
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .alpha(if (selectEnabled) 1f else 0.45f)
                        .padding(20.dp)
                ) {
                    Text("Tema", color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(10.dp))
                    Box(Modifier.fillMaxWidth()) {
                        Surface(
                            onClick = { menuOpen = true },
                            enabled = selectEnabled,
                            shape = RoundedCornerShape(16.dp),
                            color = cs.surfaceContainerHigh,
                            border = BorderStroke(1.dp, hairline()),
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(
                                modifier = Modifier.height(52.dp).padding(horizontal = 16.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isDark) "Tema escuro" else "Tema claro",
                                    color = cs.onSurface,
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Medium,
                                    modifier = Modifier.weight(1f)
                                )
                                Icon(
                                    Icons.Outlined.ChevronRight,
                                    null,
                                    tint = cs.onSurfaceVariant,
                                    modifier = Modifier.size(20.dp).rotate(90f)
                                )
                            }
                        }
                        DropdownMenu(
                            expanded = menuOpen,
                            onDismissRequest = { menuOpen = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Tema claro") },
                                onClick = {
                                    menuOpen = false
                                    onAppearanceChange(AppearancePrefs(followSystem = false, dark = false))
                                }
                            )
                            DropdownMenuItem(
                                text = { Text("Tema escuro") },
                                onClick = {
                                    menuOpen = false
                                    onAppearanceChange(AppearancePrefs(followSystem = false, dark = true))
                                }
                            )
                        }
                    }
                }
            }

            Spacer(Modifier.height(28.dp))
            SectionTitle("Cores")
            entries.chunked(2).forEach { pair ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    pair.forEach { entry ->
                        PaletteCard(
                            entry = entry,
                            isSelected = entry.key == activeKey,
                            modifier = Modifier.weight(1f)
                        ) { onSelect(entry.key) }
                    }
                    if (pair.size == 1) Spacer(Modifier.weight(1f))
                }
                Spacer(Modifier.height(12.dp))
            }
            Spacer(Modifier.navigationBarsPadding().height(24.dp))
        }
    }
}

@Composable
internal fun SectionTitle(text: String) {
    Text(
        text = text,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        fontSize = 14.sp,
        fontWeight = FontWeight.SemiBold,
        modifier = Modifier.padding(start = 4.dp, bottom = 10.dp)
    )
}

@Composable
internal fun PaletteCard(
    entry: ThemeEntry,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val idleBorder = hairline()
    val borderColor by animateColorAsState(
        targetValue = if (isSelected) cs.primary else idleBorder,
        animationSpec = tween(200),
        label = "paletteBorder"
    )
    val chipShape = RoundedCornerShape(12.dp)
    Surface(
        onClick = onClick,
        shape = CardShape,
        color = if (isSelected) lerp(cs.surfaceContainerLow, cs.primary, 0.08f) else cs.surfaceContainerLow,
        border = BorderStroke(if (isSelected) 2.dp else 1.dp, borderColor),
        modifier = modifier
    ) {
        Column(Modifier.padding(12.dp)) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(84.dp)
                    .clip(RoundedCornerShape(16.dp))
                    .background(cs.surfaceContainerHigh)
                    .padding(4.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxSize(),
                    horizontalArrangement = Arrangement.spacedBy(3.dp)
                ) {
                    entry.colors.forEachIndexed { index, color ->
                        Box(
                            Modifier
                                .weight(if (index == 0) 1.6f else 1f)
                                .fillMaxHeight()
                                .clip(chipShape)
                                .background(color)
                                .border(1.dp, cs.outlineVariant.copy(alpha = 0.5f), chipShape)
                        )
                    }
                }
                if (isSelected) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(6.dp)
                            .size(24.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(cs.primary)
                            .border(2.dp, cs.surfaceContainerHigh, RoundedCornerShape(8.dp)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Outlined.Check, "Selecionado", tint = cs.onPrimary, modifier = Modifier.size(14.dp))
                    }
                }
            }
            Spacer(Modifier.height(12.dp))
            Text(
                text = entry.label,
                color = cs.onSurface,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = entry.description,
                color = cs.onSurfaceVariant,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.padding(top = 2.dp)
            )
        }
    }
}
