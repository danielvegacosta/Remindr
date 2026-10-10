@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.ChevronLeft
import androidx.compose.material.icons.outlined.ChevronRight
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vega.remindr.model.Birthday
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JTextStyle
import java.time.LocalDate
import java.time.Year
import java.time.YearMonth

@Composable
internal fun CalendarScreen(birthdays: List<Birthday>, onOpen: (Birthday) -> Unit) {
    val cs = MaterialTheme.colorScheme
    var currentMonth by remember { mutableStateOf(YearMonth.now()) }
    var selectedDate by remember { mutableStateOf(LocalDate.now()) }
    val today = remember { LocalDate.now() }
    val byDay = remember(birthdays, currentMonth.year) {
        birthdays.groupBy {
            val day = minOf(
                it.birthDate.dayOfMonth,
                it.birthDate.month.length(Year.of(currentMonth.year).isLeap)
            )

            it.birthDate.monthValue * 100 + day
        }
    }
    val monthCount = birthdays.count { it.birthDate.monthValue == currentMonth.monthValue }
    val monthName = currentMonth.month.getDisplayName(JTextStyle.FULL, PtBr).replaceFirstChar { it.uppercase() }

    fun goTo(month: YearMonth) {
        currentMonth = month
        selectedDate = if (month == YearMonth.now()) LocalDate.now() else month.atDay(1)
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LargeTitle(title = "Calendário", subtitle = "Visualize melhor os aniversários")

        Box(
            modifier = Modifier
                .padding(horizontal = 20.dp)
                .fillMaxWidth()
                .clip(CardShape)
                .background(Brush.verticalGradient(listOf(cs.primary.copy(alpha = 0.12f), cs.surfaceContainerLow)))
                .border(1.dp, hairline(), CardShape)
        ) {
            Column(Modifier.padding(16.dp)) {
                Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                    Column(Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.Bottom) {
                            Text(
                                monthName,
                                color = cs.onSurface,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                letterSpacing = (-0.4).sp,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                            Spacer(Modifier.width(5.dp))
                            Text(
                                currentMonth.year.toString(),
                                color = cs.onSurfaceVariant,
                                fontSize = 21.sp,
                                letterSpacing = (-0.3).sp,
                                maxLines = 1
                            )
                        }
                        Text(
                            text = when (monthCount) {
                                0 -> "Nenhum aniversário neste mês"
                                1 -> "1 aniversário neste mês"
                                else -> "$monthCount aniversários neste mês"
                            },
                            color = cs.onSurfaceVariant,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 2.dp)
                        )
                    }
                    if (currentMonth != YearMonth.now()) {
                        SoftPill("Hoje") { goTo(YearMonth.now()) }
                        Spacer(Modifier.width(8.dp))
                    }
                    CircleIconButton(
                        icon = Icons.Outlined.ChevronLeft,
                        description = "Mês anterior",
                        onClick = { goTo(currentMonth.minusMonths(1)) },
                        size = 34.dp,
                        iconSize = 18.dp
                    )
                    Spacer(Modifier.width(6.dp))
                    CircleIconButton(
                        icon = Icons.Outlined.ChevronRight,
                        description = "Próximo mês",
                        onClick = { goTo(currentMonth.plusMonths(1)) },
                        size = 34.dp,
                        iconSize = 18.dp
                    )
                }

                Row(Modifier.fillMaxWidth().padding(top = 18.dp, bottom = 8.dp)) {
                    listOf("D", "S", "T", "Q", "Q", "S", "S").forEach { d ->
                        Text(
                            text = d,
                            color = cs.onSurfaceVariant,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.SemiBold,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.weight(1f)
                        )
                    }
                }

                AnimatedContent(
                    targetState = currentMonth,
                    transitionSpec = { fadeIn(tween(220)) togetherWith fadeOut(tween(160)) },
                    label = "MonthGrid"
                ) { month ->
                    Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                        monthCells(month).chunked(7).forEach { week ->
                            Row(Modifier.fillMaxWidth()) {
                                week.forEach { day ->
                                    if (day == null) {
                                        Spacer(Modifier.weight(1f).aspectRatio(1f))
                                    } else {
                                        val date = month.atDay(day)
                                        val people = byDay[date.monthValue * 100 + date.dayOfMonth].orEmpty()
                                        CalendarDay(
                                            day = day,
                                            people = people,
                                            selected = date == selectedDate,
                                            isToday = date == today,
                                            modifier = Modifier.weight(1f)
                                        ) { selectedDate = date }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        Spacer(Modifier.height(24.dp))

        AnimatedContent(
            targetState = selectedDate,
            transitionSpec = { fadeIn(tween(200)) togetherWith fadeOut(tween(150)) },
            label = "DayList",
            modifier = Modifier.fillMaxWidth()
        ) { date ->
            val list = byDay[date.monthValue * 100 + date.dayOfMonth].orEmpty()
            val weekdayShort = date.dayOfWeek.getDisplayName(JTextStyle.SHORT, PtBr).removeSuffix(".")
            val title = if (date == today) {
                "Hoje, " + date.format(DateTimeFormatter.ofPattern("d 'de' MMMM", PtBr))
            } else {
                date.format(DateTimeFormatter.ofPattern("EEEE, d 'de' MMMM", PtBr)).replaceFirstChar { it.uppercase() }
            }
            Column(modifier = Modifier.fillMaxWidth()) {
                DaySummaryCard(
                    date = date,
                    title = title,
                    weekdayShort = weekdayShort,
                    birthdayCount = list.size,
                    accentColors = list.firstOrNull()?.let { avatarColors(it.name) }
                )

                if (list.isNotEmpty()) {
                    Spacer(Modifier.height(12.dp))
                    SurfaceCard(modifier = Modifier.padding(horizontal = 20.dp)) {
                        list.forEachIndexed { index, b ->
                            BirthdayRow(b, rowSubtitle(b, false), index < list.lastIndex) { onOpen(b) }
                        }
                    }
                }
            }
        }
        Spacer(Modifier.navigationBarsPadding().height(150.dp))
    }
}

@Composable
internal fun CalendarDay(
    day: Int,
    people: List<Birthday>,
    selected: Boolean,
    isToday: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val has = people.isNotEmpty()
    val shape = TileShape
    val accent = people.firstOrNull()?.let { avatarColors(it.name).first }
    val background by animateColorAsState(
        targetValue = when {
            selected && accent != null -> accent
            selected -> cs.primary
            accent != null -> accent.copy(alpha = 0.18f)
            else -> cs.onSurface.copy(alpha = 0.05f)
        },
        animationSpec = tween(180),
        label = "dayBackground"
    )
    val borderModifier = when {
        selected && accent != null -> Modifier
        selected -> Modifier
        isToday -> Modifier.border(1.5.dp, cs.primary, shape)
        accent != null -> Modifier.border(1.dp, accent.copy(alpha = 0.55f), shape)
        else -> Modifier
    }
    Box(
        modifier = modifier
            .aspectRatio(1f)
            .padding(2.dp)
            .clip(shape)
            .background(background)
            .then(borderModifier)
            .clickable(onClick = onClick),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = day.toString(),
                color = when {
                    selected -> Color.White
                    accent != null -> accent
                    else -> cs.onSurface
                },
                fontSize = 14.sp,
                fontWeight = if (selected || isToday || has) FontWeight.Bold else FontWeight.Normal
            )
            if (has) {
                Row(
                    modifier = Modifier.padding(top = 3.dp),
                    horizontalArrangement = Arrangement.spacedBy(3.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    people.take(3).forEach { birthday ->
                        Box(
                            Modifier
                                .size(5.dp)
                                .clip(CircleShape)
                                .background(
                                    if (selected) Color.White
                                    else avatarColors(birthday.name).first
                                )
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun DaySummaryCard(
    date: LocalDate,
    title: String,
    weekdayShort: String,
    birthdayCount: Int,
    accentColors: Pair<Color, Color>?
) {
    val cs = MaterialTheme.colorScheme
    val summaryBrush = when {
        accentColors != null -> Brush.linearGradient(
            listOf(
                accentColors.first.copy(alpha = 0.22f),
                accentColors.second.copy(alpha = 0.10f),
                cs.surfaceContainerLow
            )
        )
        else -> Brush.linearGradient(
            listOf(cs.primary.copy(alpha = 0.10f), cs.surfaceContainerLow)
        )
    }
    val badgeBrush = if (accentColors != null) {
        Brush.linearGradient(listOf(accentColors.first, accentColors.second))
    } else {
        brandBrush()
    }
    val borderColor = accentColors?.first?.copy(alpha = 0.35f) ?: hairline()

    Surface(
        shape = CardShape,
        color = Color.Transparent,
        border = BorderStroke(1.dp, borderColor),
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .background(summaryBrush)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier
                    .size(60.dp)
                    .clip(RoundedCornerShape(18.dp))
                    .background(badgeBrush),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = weekdayShort,
                    color = Color.White.copy(alpha = 0.88f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    date.dayOfMonth.toString(),
                    color = Color.White,
                    fontSize = 23.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = "DIA SELECIONADO",
                    color = accentColors?.first ?: cs.primary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.7.sp
                )
                Text(
                    text = title,
                    color = cs.onSurface,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.2).sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.padding(top = 2.dp)
                )
                Text(
                    text = when (birthdayCount) {
                        0 -> "Sem aniversários neste dia"
                        1 -> "1 aniversário"
                        else -> "$birthdayCount aniversários"
                    },
                    color = cs.onSurfaceVariant,
                    fontSize = 14.sp,
                    modifier = Modifier.padding(top = 2.dp)
                )
            }
        }
    }
}
