@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import android.provider.Settings
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vega.remindr.model.Birthday

@Composable
internal fun MainContainer(
    birthdays: List<Birthday>,
    onAdd: () -> Unit,
    onOpen: (Birthday) -> Unit,
    onSecurity: () -> Unit,
    onThemes: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onEnableNotifications: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val navBottom = bottomInset()
    var currentTab by remember { mutableStateOf(MainTab.Home) }

    Box(modifier = Modifier.fillMaxSize().imePadding()) {
        AnimatedContent(
            targetState = currentTab,
            transitionSpec = { fadeIn(tween(250)) togetherWith fadeOut(tween(200)) },
            label = "TabTransitions",
            modifier = Modifier.fillMaxSize()
        ) { tab ->
            when (tab) {
                MainTab.Calendar -> CalendarScreen(birthdays, onOpen)
                MainTab.Home -> HomeScreen(birthdays, onAdd, onOpen, onEnableNotifications)
                MainTab.Settings -> SettingsScreen(birthdays.size, onSecurity, onThemes, onImport, onExport, onEnableNotifications)
            }
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(140.dp + navBottom)
                .background(Brush.verticalGradient(listOf(Color.Transparent, cs.background)))
        )

        Row(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            FloatingTabBar(
                selectedTab = currentTab,
                onTabSelected = { currentTab = it },
                modifier = Modifier.weight(1f)
            )
            AnimatedVisibility(
                visible = currentTab != MainTab.Settings,
                enter = fadeIn(tween(200)) + expandHorizontally(tween(250)),
                exit = fadeOut(tween(150)) + shrinkHorizontally(tween(200))
            ) {
                Row {
                    Spacer(Modifier.width(12.dp))
                    Surface(
                        onClick = onAdd,
                        shape = CircleShape,
                        color = cs.primary,
                        shadowElevation = 8.dp,
                        modifier = Modifier.size(54.dp)
                    ) {
                        Box(
                            modifier = Modifier.background(brandBrush()),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Outlined.Add, "Adicionar aniversário", tint = cs.onPrimary, modifier = Modifier.size(26.dp))
                        }
                    }
                }
            }
        }
    }
}

@Composable
internal fun FloatingTabBar(
    selectedTab: MainTab,
    onTabSelected: (MainTab) -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme

    Surface(
        shape = CircleShape,
        color = cs.surfaceContainerHigh,
        shadowElevation = 8.dp,
        border = BorderStroke(1.dp, hairline()),
        modifier = modifier.height(62.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxSize().padding(6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            MainTab.entries.forEach { tab ->
                val isSelected = tab == selectedTab
                val tabWeight by animateFloatAsState(
                    targetValue = if (isSelected) 2.3f else 1f,
                    animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
                    label = "tabWeight"
                )
                val pill by animateColorAsState(
                    targetValue = if (isSelected) cs.primary.copy(alpha = 0.16f) else Color.Transparent,
                    animationSpec = tween(220),
                    label = "tabPill"
                )
                val tint by animateColorAsState(
                    targetValue = if (isSelected) cs.primary else cs.onSurfaceVariant.copy(alpha = 0.75f),
                    animationSpec = tween(220),
                    label = "tabTint"
                )
                Row(
                    modifier = Modifier
                        .weight(tabWeight)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .background(pill)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onTabSelected(tab) }
                        ),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = getTabIcon(tab, isSelected),
                        contentDescription = tab.label(),
                        tint = tint,
                        modifier = Modifier.size(24.dp)
                    )
                    AnimatedVisibility(
                        visible = isSelected,
                        enter = fadeIn(tween(220, delayMillis = 60)) + expandHorizontally(tween(260)),
                        exit = fadeOut(tween(100)) + shrinkHorizontally(tween(200))
                    ) {
                        Text(
                            text = tab.label(),
                            color = tint,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            maxLines = 1,
                            softWrap = false,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }
        }
    }
}
