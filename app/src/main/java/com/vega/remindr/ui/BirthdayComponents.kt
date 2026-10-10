@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vega.remindr.model.Birthday

@Composable
internal fun BrandTitle(count: Int) {
    BrandWordmark(width = 180.dp, height = 36.dp)
}

@Composable
internal fun HeroCard(birthday: Birthday, onClick: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val isDark = cs.background.luminance() < 0.5f
    val days = birthday.daysUntil()

    val containerBrush = if (isDark) {
        Brush.linearGradient(listOf(lerp(cs.primary, Color.Black, 0.78f), lerp(cs.primary, Color.Black, 0.52f)))
    } else {
        Brush.linearGradient(listOf(lerp(cs.primary, Color.White, 0.88f), lerp(cs.primary, Color.White, 0.70f)))
    }
    val nameColor = if (isDark) Color.White else lerp(cs.primary, Color.Black, 0.72f)
    val accent = if (isDark) lerp(cs.primary, Color.White, 0.55f) else lerp(cs.primary, Color.Black, 0.10f)
    val dateColor = if (isDark) Color.White.copy(alpha = 0.65f) else lerp(cs.primary, Color.Black, 0.55f).copy(alpha = 0.80f)
    val borderColor = if (isDark) Color.White.copy(alpha = 0.08f) else cs.primary.copy(alpha = 0.22f)
    val frameColor = if (isDark) Color.White.copy(alpha = 0.92f) else Color.White
    val chevronColor = if (isDark) Color.White.copy(alpha = 0.6f) else nameColor.copy(alpha = 0.6f)

    val parts = birthday.name.trim().split(Regex("\\s+"), limit = 2)
    val firstName = parts.first()
    val surname = parts.getOrNull(1).orEmpty()
    val nameText = buildAnnotatedString {
        withStyle(SpanStyle(fontWeight = FontWeight.Bold)) { append(firstName.uppercase(PtBr)) }
        if (surname.isNotEmpty()) {
            append(" ")
            withStyle(SpanStyle(fontWeight = FontWeight.Light)) { append(surname.uppercase(PtBr)) }
        }
    }
    val timeLeft = when (days) {
        0L -> "É hoje!"
        1L -> "É amanhã"
        else -> "Faltam $days dias"
    }
    val timeText = buildAnnotatedString {
        withStyle(SpanStyle(color = accent, fontWeight = FontWeight.SemiBold)) { append(timeLeft) }
        withStyle(SpanStyle(color = dateColor)) {
            append("  ·  " + shortDate(birthday.upcomingDate()))
        }
    }

    Box(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 8.dp)
            .fillMaxWidth()
            .clip(CardShape)
            .background(containerBrush)
            .border(1.dp, borderColor, CardShape)
            .clickable(onClick = onClick)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 22.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(92.dp)
                    .clip(AvatarShape)
                    .background(frameColor)
                    .padding(3.dp),
                contentAlignment = Alignment.Center
            ) {
                PersonAvatar(name = birthday.name, size = 86.dp)
            }
            Spacer(Modifier.width(18.dp))
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = nameText,
                    color = nameColor,
                    fontSize = 20.sp,
                    lineHeight = 26.sp,
                    letterSpacing = 1.2.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = timeText,
                    fontSize = 14.sp,
                    letterSpacing = 0.3.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Spacer(Modifier.width(8.dp))
            Icon(
                Icons.Outlined.ChevronRight,
                contentDescription = null,
                tint = chevronColor,
                modifier = Modifier.size(24.dp)
            )
        }
    }
}

@Composable
internal fun GroupHeader(label: String, count: Int) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(start = 20.dp, end = 20.dp, top = 20.dp, bottom = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            color = cs.onBackground,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = (-0.2).sp,
            modifier = Modifier.weight(1f)
        )
        Surface(shape = CircleShape, color = cs.surfaceContainerHigh) {
            Text(
                text = count.toString(),
                color = cs.onSurfaceVariant,
                fontSize = 12.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
internal fun BirthdayRow(
    birthday: Birthday,
    subtitle: String,
    showDivider: Boolean,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            PersonAvatar(name = birthday.name, size = 54.dp)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = birthday.name,
                    color = cs.onBackground,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subtitle,
                    color = cs.onSurfaceVariant,
                    fontSize = 14.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
            Spacer(Modifier.width(12.dp))
            DaysPill(birthday.daysUntil())
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 88.dp, end = 20.dp),
                thickness = 0.5.dp,
                color = hairline()
            )
        }
    }
}

@Composable
internal fun DaysPill(days: Long) {
    val cs = MaterialTheme.colorScheme
    val label = when (days) {
        0L -> "Hoje"
        1L -> "Amanhã"
        else -> "$days dias"
    }
    val container = when {
        days == 0L -> cs.primary
        days <= 7L -> cs.primary.copy(alpha = 0.16f)
        else -> cs.surfaceContainerHigh
    }
    val content = when {
        days == 0L -> cs.onPrimary
        days <= 7L -> cs.primary
        else -> cs.onSurfaceVariant
    }
    Surface(shape = CircleShape, color = container) {
        Text(
            text = label,
            color = content,
            fontSize = 13.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}

@Composable
internal fun SegmentedControl(
    options: List<String>,
    selectedIndex: Int,
    onSelect: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    val thumbBrush = brandBrush()
    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .background(cs.surfaceContainerLow)
            .border(1.dp, hairline(), CircleShape)
            .padding(4.dp)
    ) {
        val segmentWidth = maxWidth / options.size
        val thumbOffset by animateDpAsState(
            targetValue = segmentWidth * selectedIndex,
            animationSpec = spring(dampingRatio = 0.8f, stiffness = Spring.StiffnessMediumLow),
            label = "segmentThumb"
        )
        Box(
            modifier = Modifier
                .offset(x = thumbOffset)
                .width(segmentWidth)
                .fillMaxHeight()
                .clip(CircleShape)
                .background(thumbBrush)
        )
        Row(modifier = Modifier.fillMaxSize()) {
            options.forEachIndexed { index, label ->
                val selected = index == selectedIndex
                val textColor by animateColorAsState(
                    targetValue = if (selected) cs.onPrimary else cs.onSurfaceVariant,
                    animationSpec = tween(180),
                    label = "segmentText"
                )
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxHeight()
                        .clip(CircleShape)
                        .clickable(
                            interactionSource = remember { MutableInteractionSource() },
                            indication = null,
                            onClick = { onSelect(index) }
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = label,
                        color = textColor,
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1
                    )
                }
            }
        }
    }
}

@Composable
internal fun SearchPill(
    query: String,
    onQueryChange: (String) -> Unit,
    focusRequester: FocusRequester,
    modifier: Modifier = Modifier
) {
    val cs = MaterialTheme.colorScheme
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(48.dp)
            .clip(CircleShape)
            .background(cs.surfaceContainerHigh)
            .border(1.dp, hairline(), CircleShape)
            .padding(horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Outlined.Search, null, tint = cs.onSurfaceVariant, modifier = Modifier.size(20.dp))
        Spacer(Modifier.width(10.dp))
        Box(Modifier.weight(1f), contentAlignment = Alignment.CenterStart) {
            if (query.isEmpty()) {
                Text("Buscar por nome", color = cs.onSurfaceVariant.copy(alpha = 0.6f), fontSize = 16.sp, maxLines = 1, softWrap = false)
            }
            BasicTextField(
                value = query,
                onValueChange = onQueryChange,
                singleLine = true,
                textStyle = TextStyle(color = cs.onSurface, fontSize = 16.sp),
                cursorBrush = SolidColor(cs.primary),
                modifier = Modifier.fillMaxWidth().focusRequester(focusRequester)
            )
        }
    }
}
