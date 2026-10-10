@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.asPaddingValues
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.exclude
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.ime
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBars
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import com.vega.remindr.R

@Composable
internal fun hairline(): Color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f)

@Composable
internal fun topInset(): Dp = WindowInsets.statusBars.asPaddingValues().calculateTopPadding()

@Composable
internal fun bottomInset(): Dp =
    WindowInsets.navigationBars.exclude(WindowInsets.ime).asPaddingValues().calculateBottomPadding()

@Composable
internal fun brandBrush(): Brush {
    val cs = MaterialTheme.colorScheme
    return Brush.linearGradient(listOf(cs.primary, lerp(cs.primary, cs.tertiary, 0.45f)))
}

@Composable
internal fun BrandWordmark(width: Dp, height: Dp, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val isDark = cs.background.luminance() < 0.5f
    Image(
        painter = painterResource(id = R.drawable.title_white),
        contentDescription = "Remindr",
        contentScale = ContentScale.Fit,
        colorFilter = ColorFilter.tint(if (isDark) Color.White else Color.Black),
        modifier = modifier
            .width(width)
            .height(height)
    )
}

@Composable
internal fun BrandAccentBar() {
    Box(
        modifier = Modifier
            .width(44.dp)
            .height(4.dp)
            .clip(CircleShape)
            .background(brandBrush())
    )
}

@Composable
internal fun BrandAmbient(modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    val top = topInset()
    val bottom = bottomInset()
    val infinite = rememberInfiniteTransition(label = "ambient")
    val glow by infinite.animateFloat(
        initialValue = 0.16f,
        targetValue = 0.28f,
        animationSpec = infiniteRepeatable(
            animation = tween(3400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "ambientGlow"
    )
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(380.dp + top)
                .background(
                    Brush.verticalGradient(
                        listOf(
                            cs.primary.copy(alpha = 0.12f),
                            cs.tertiary.copy(alpha = 0.04f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .fillMaxWidth()
                .height(520.dp)
                .offset(y = (-190).dp + top)
                .background(
                    Brush.radialGradient(
                        listOf(
                            cs.primary.copy(alpha = glow),
                            cs.tertiary.copy(alpha = glow * 0.4f),
                            Color.Transparent
                        )
                    )
                )
        )
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .height(300.dp + bottom)
                .background(
                    Brush.verticalGradient(
                        listOf(Color.Transparent, cs.primary.copy(alpha = 0.10f))
                    )
                )
        )
    }
}

@Composable
internal fun Reveal(
    modifier: Modifier = Modifier,
    delayMillis: Int = 0,
    content: @Composable () -> Unit
) {
    var shown by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { shown = true }
    val progress by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(durationMillis = 560, delayMillis = delayMillis, easing = FastOutSlowInEasing),
        label = "reveal"
    )
    Box(
        modifier = modifier
            .alpha(progress)
            .offset(y = ((1f - progress) * 24f).dp)
    ) { content() }
}

@Composable
internal fun dialogContainer(): Color {
    val cs = MaterialTheme.colorScheme
    return if (cs.background.luminance() < 0.5f) cs.surfaceContainerHigh else Color.White
}

@Composable
internal fun dialogBorder(): Color {
    val cs = MaterialTheme.colorScheme
    return if (cs.background.luminance() < 0.5f) hairline() else cs.outlineVariant
}

@Composable
internal fun SurfaceCard(
    modifier: Modifier = Modifier,
    shape: Shape = CardShape,
    content: @Composable ColumnScope.() -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        shape = shape,
        color = cs.surfaceContainerLow,
        border = BorderStroke(1.dp, hairline()),
        modifier = modifier.fillMaxWidth()
    ) {
        Column(content = content)
    }
}

@Composable
internal fun PrimaryButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null,
    height: Dp = 56.dp,
    fontSize: TextUnit = 16.sp
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = cs.primary,
        contentColor = cs.onPrimary,
        modifier = modifier.height(height)
    ) {
        Row(
            modifier = Modifier.background(brandBrush()).padding(horizontal = 22.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            if (icon != null) {
                Icon(icon, null, tint = cs.onPrimary, modifier = Modifier.size(20.dp))
                Spacer(Modifier.width(8.dp))
            }
            Text(label, color = cs.onPrimary, fontSize = fontSize, fontWeight = FontWeight.Bold, maxLines = 1)
        }
    }
}

@Composable
internal fun PersonAvatar(name: String, size: Dp, modifier: Modifier = Modifier, round: Boolean = false) {
    val colors = avatarColors(name)
    val shape: Shape = if (round) CircleShape else AvatarShape
    Box(
        modifier = modifier
            .size(size)
            .clip(shape)
            .background(Brush.linearGradient(listOf(colors.first, colors.second))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = name.trim().take(1).uppercase().ifEmpty { "?" },
            color = Color.White,
            fontWeight = FontWeight.Bold,
            fontSize = (size.value * 0.4f).sp
        )
    }
}

@Composable
internal fun CircleIconButton(
    icon: ImageVector,
    description: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    size: Dp = 44.dp,
    iconSize: Dp = 22.dp
) {
    val cs = MaterialTheme.colorScheme
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = cs.surfaceContainerHigh,
        border = BorderStroke(1.dp, hairline()),
        modifier = modifier.size(size)
    ) {
        Box(contentAlignment = Alignment.Center) {
            Icon(icon, description, tint = cs.onSurface, modifier = Modifier.size(iconSize))
        }
    }
}

@Composable
internal fun SoftPill(label: String, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Surface(onClick = onClick, shape = CircleShape, color = cs.primary.copy(alpha = 0.14f)) {
        Text(
            text = label,
            color = cs.primary,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 9.dp)
        )
    }
}

@Composable
internal fun IconTile(icon: ImageVector, accent: Color, modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .size(42.dp)
            .clip(TileShape)
            .background(accent.copy(alpha = 0.16f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(icon, null, tint = accent, modifier = Modifier.size(22.dp))
    }
}

@Composable
internal fun ScreenTopBar(title: String, onBack: () -> Unit, trailing: @Composable () -> Unit = {}) {
    val cs = MaterialTheme.colorScheme
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .height(48.dp)
    ) {
        CircleIconButton(
            icon = Icons.AutoMirrored.Outlined.ArrowBack,
            description = "Voltar",
            onClick = onBack,
            modifier = Modifier.align(Alignment.CenterStart)
        )
        Text(
            text = title,
            color = cs.onBackground,
            fontSize = 17.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.align(Alignment.Center)
        )
        Box(Modifier.align(Alignment.CenterEnd)) { trailing() }
    }
}

@Composable
internal fun LargeTitle(title: String, subtitle: String? = null, trailing: @Composable () -> Unit = {}) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .statusBarsPadding()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(Modifier.weight(1f)) {
            Text(
                text = title,
                color = cs.onBackground,
                fontSize = 32.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.8).sp
            )
            if (subtitle != null) {
                Text(
                    text = subtitle,
                    color = cs.onSurfaceVariant,
                    fontSize = 15.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
        trailing()
    }
}

@Composable
internal fun EmptyState(
    icon: ImageVector,
    title: String,
    message: String,
    modifier: Modifier = Modifier,
    actionLabel: String? = null,
    onAction: () -> Unit = {},
    secondaryLabel: String? = null,
    onSecondary: () -> Unit = {}
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 36.dp)
            .navigationBarsPadding()
            .padding(bottom = 90.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(88.dp)
                .clip(AvatarShape)
                .background(cs.primary.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(icon, null, tint = cs.primary, modifier = Modifier.size(40.dp))
        }
        Spacer(Modifier.height(24.dp))
        Text(
            text = title,
            color = cs.onBackground,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold,
            textAlign = TextAlign.Center,
            letterSpacing = (-0.3).sp
        )
        Spacer(Modifier.height(8.dp))
        Text(
            text = message,
            color = cs.onSurfaceVariant,
            fontSize = 15.sp,
            lineHeight = 22.sp,
            textAlign = TextAlign.Center
        )
        if (actionLabel != null) {
            Spacer(Modifier.height(28.dp))
            PrimaryButton(
                label = actionLabel,
                onClick = onAction,
                modifier = Modifier.fillMaxWidth(),
                icon = Icons.Outlined.Add,
                height = 54.dp
            )
        }
        if (secondaryLabel != null) {
            Spacer(Modifier.height(8.dp))
            TextButton(onClick = onSecondary) {
                Text(secondaryLabel, color = cs.primary, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
            }
        }
    }
}
