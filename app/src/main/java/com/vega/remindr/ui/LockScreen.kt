@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.delay

@Composable
internal fun LockWordmarkBlock(reveal: Float, showBar: Boolean) {
    Column(
        modifier = Modifier
            .alpha(reveal)
            .scale(0.94f + 0.06f * reveal),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        BrandWordmark(width = 200.dp, height = 44.dp)
        if (showBar) {
            Spacer(Modifier.height(12.dp))
            BrandAccentBar()
        }
    }
}

@Composable
internal fun LockScreen(
    hasPin: Boolean,
    biometricEnabled: Boolean,
    onPin: (String) -> Unit,
    onBiometric: () -> Unit,
    onForgot: () -> Unit,
    onEnter: () -> Unit,
    pinInputEnabled: Boolean = true,
    lockoutRemainingMillis: Long = 0L
) {
    val cs = MaterialTheme.colorScheme
    var pin by remember { mutableStateOf("") }
    var shown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { shown = true }

    LaunchedEffect(pin) {
        if (pin.length == 4) {
            delay(200)
            onPin(pin)
            pin = ""
        }
    }

    val headerReveal by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "lockHeaderReveal"
    )
    val reveal by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(600, delayMillis = 140, easing = FastOutSlowInEasing),
        label = "lockReveal"
    )

    val unprotected = !hasPin && !biometricEnabled
    val subtitle = when {
        hasPin && biometricEnabled -> "Digite seu PIN ou use a digital"
        hasPin -> "Digite seu PIN para continuar"
        biometricEnabled -> "Use sua digital para continuar"
        else -> "Nunca mais esqueça o aniversário de alguém importante."
    }

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
    ) {
        val roomy = (maxHeight - topInset() - bottomInset()) >= 720.dp

        BrandAmbient()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            if (hasPin) {
                Spacer(Modifier.height(if (roomy) 28.dp else 16.dp))
                LockWordmarkBlock(reveal = headerReveal, showBar = roomy)
                Spacer(Modifier.height(if (roomy) 24.dp else 12.dp))
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .alpha(reveal)
                    .offset(y = ((1f - reveal) * 28f).dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                if (unprotected) {
                    Spacer(Modifier.weight(1f))
                    LockWordmarkBlock(reveal = headerReveal, showBar = true)
                    Spacer(Modifier.height(24.dp))
                    LockTitle(subtitle)
                    Spacer(Modifier.height(28.dp))
                    SurfaceCard {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconTile(Icons.Outlined.Security, cs.primary)
                            Spacer(Modifier.width(14.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Proteja seus aniversários",
                                    color = cs.onSurface,
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Ative um PIN ou a digital em Ajustes › Privacidade.",
                                    color = cs.onSurfaceVariant,
                                    fontSize = 13.sp,
                                    lineHeight = 18.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    PrimaryButton(
                        label = "Entrar",
                        onClick = onEnter,
                        modifier = Modifier.fillMaxWidth(),
                        height = 56.dp,
                        fontSize = 17.sp
                    )
                    Spacer(Modifier.height(20.dp))
                } else if (hasPin) {
                    if (roomy) {
                        Spacer(Modifier.height(18.dp))
                    }
                    LockTitle(if (pinInputEnabled) subtitle else "PIN temporariamente bloqueado")
                    if (!pinInputEnabled) {
                        val secondsRemaining = ((lockoutRemainingMillis + 999L) / 1_000L).coerceAtLeast(0L)
                        Text(
                            "Tente novamente em ${secondsRemaining / 60}:${(secondsRemaining % 60).toString().padStart(2, '0')}.",
                            color = cs.error,
                            fontSize = 14.sp,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(top = 8.dp)
                        )
                    }
                    Spacer(Modifier.height(if (roomy) 22.dp else 16.dp))
                    PinBoxes(filled = pin.length, enabled = pinInputEnabled)
                    Spacer(Modifier.height(14.dp))
                    if (!biometricEnabled) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text("Esqueceu o PIN? ", color = cs.onSurfaceVariant, fontSize = 14.sp)
                            Text(
                                text = "Redefinir",
                                color = cs.primary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                textDecoration = TextDecoration.Underline,
                                modifier = Modifier.clickable(onClick = onForgot).padding(4.dp)
                            )
                        }
                    }

                    Spacer(Modifier.weight(1f))

                    FlatNumpad(
                        onNumber = { if (pinInputEnabled && pin.length < 4) pin += it },
                        onDelete = { if (pinInputEnabled && pin.isNotEmpty()) pin = pin.dropLast(1) },
                        keysEnabled = pinInputEnabled,
                        modifier = Modifier.offset(y = 12.dp),
                        leftSlot = {
                            if (biometricEnabled) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(20.dp))
                                        .clickable(onClick = onBiometric),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(Icons.Outlined.Fingerprint, "Usar digital", tint = cs.primary, modifier = Modifier.size(32.dp))
                                }
                            }
                        }
                    )
                    Spacer(Modifier.height(20.dp))
                } else {
                    Spacer(Modifier.weight(1f))
                    LockWordmarkBlock(reveal = headerReveal, showBar = true)
                    Spacer(Modifier.height(24.dp))
                    LockTitle(subtitle)
                    Spacer(Modifier.height(32.dp))
                    Surface(
                        onClick = onBiometric,
                        shape = CardShape,
                        color = cs.surfaceContainerLow,
                        border = BorderStroke(1.dp, hairline()),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(18.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(60.dp)
                                    .clip(AvatarShape)
                                    .background(brandBrush()),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(Icons.Outlined.Fingerprint, "Usar digital", tint = cs.onPrimary, modifier = Modifier.size(32.dp))
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(
                                    "Desbloquear com a digital",
                                    color = cs.onSurface,
                                    fontSize = 17.sp,
                                    fontWeight = FontWeight.SemiBold
                                )
                                Text(
                                    "Toque para autenticar",
                                    color = cs.onSurfaceVariant,
                                    fontSize = 14.sp,
                                    modifier = Modifier.padding(top = 2.dp)
                                )
                            }
                            Icon(
                                Icons.Outlined.ChevronRight,
                                null,
                                tint = cs.onSurfaceVariant.copy(alpha = 0.7f),
                                modifier = Modifier.size(22.dp)
                            )
                        }
                    }
                    Spacer(Modifier.weight(1f))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Outlined.Lock,
                            null,
                            tint = cs.onSurfaceVariant.copy(alpha = 0.7f),
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(Modifier.width(6.dp))
                        Text(
                            "Seus dados ficam só neste aparelho",
                            color = cs.onSurfaceVariant.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                    Spacer(Modifier.height(24.dp))
                }
            }
        }
    }
}

@Composable
internal fun LockTitle(subtitle: String) {
    val cs = MaterialTheme.colorScheme
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = "Bem-vindo de volta",
            color = cs.onBackground,
            fontSize = 30.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.8).sp,
            textAlign = TextAlign.Center
        )
        Spacer(Modifier.height(6.dp))
        Text(
            text = subtitle,
            color = cs.onSurfaceVariant,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
internal fun PinBoxes(filled: Int, enabled: Boolean, box: Dp = 58.dp, gap: Dp = 14.dp) {
    val cs = MaterialTheme.colorScheme
    val shape = RoundedCornerShape(18.dp)
    val idleBorder = hairline()
    Row(
        horizontalArrangement = Arrangement.spacedBy(gap),
        modifier = Modifier.alpha(if (enabled) 1f else 0.45f)
    ) {
        for (i in 0 until 4) {
            val on = i < filled
            val active = enabled && i == filled
            val bg by animateColorAsState(
                targetValue = if (on) cs.primary else cs.surfaceContainerHigh,
                animationSpec = tween(150),
                label = "pinBoxBg"
            )
            val boxScale by animateFloatAsState(
                targetValue = if (on) 1.06f else 1f,
                animationSpec = spring(dampingRatio = 0.5f),
                label = "pinBoxScale"
            )
            Box(
                modifier = Modifier
                    .size(box)
                    .scale(boxScale)
                    .clip(shape)
                    .background(bg)
                    .border(if (active) 2.dp else 1.dp, if (active) cs.primary else idleBorder, shape),
                contentAlignment = Alignment.Center
            ) {
                if (on) {
                    Box(
                        Modifier
                            .size(12.dp)
                            .clip(CircleShape)
                            .background(cs.onPrimary)
                    )
                }
            }
        }
    }
}

@Composable
internal fun FlatNumpad(
    onNumber: (String) -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier,
    keyHeight: Dp = 60.dp,
    keysEnabled: Boolean = true,
    leftSlot: @Composable () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    val rows = listOf(listOf("1", "2", "3"), listOf("4", "5", "6"), listOf("7", "8", "9"))
    Column(modifier = modifier.fillMaxWidth()) {
        rows.forEach { row ->
            Row(Modifier.fillMaxWidth()) {
                row.forEach { key ->
                    FlatKey(Modifier.weight(1f), keyHeight, { onNumber(key) }, enabled = keysEnabled) {
                        Text(key, color = cs.onSurface, fontSize = 30.sp, fontWeight = FontWeight.Medium)
                    }
                }
            }
        }
        Row(Modifier.fillMaxWidth()) {
            Box(Modifier.weight(1f).height(keyHeight), contentAlignment = Alignment.Center) { leftSlot() }
            FlatKey(Modifier.weight(1f), keyHeight, { onNumber("0") }, enabled = keysEnabled) {
                Text("0", color = cs.onSurface, fontSize = 30.sp, fontWeight = FontWeight.Medium)
            }
            FlatKey(Modifier.weight(1f), keyHeight, onDelete, enabled = keysEnabled) {
                Icon(
                    Icons.AutoMirrored.Outlined.ArrowBack,
                    "Apagar",
                    tint = cs.onSurfaceVariant,
                    modifier = Modifier.size(26.dp)
                )
            }
        }
    }
}

@Composable
internal fun FlatKey(
    modifier: Modifier,
    height: Dp,
    onClick: () -> Unit,
    enabled: Boolean = true,
    content: @Composable () -> Unit
) {
    Box(
        modifier = modifier
            .height(height)
            .clip(RoundedCornerShape(20.dp))
            .alpha(if (enabled) 1f else 0.35f)
            .clickable(enabled = enabled, onClick = onClick),
        contentAlignment = Alignment.Center
    ) { content() }
}
