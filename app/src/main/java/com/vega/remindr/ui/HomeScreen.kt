@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Celebration
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import com.vega.remindr.model.Birthday
import java.time.LocalDate
import kotlinx.coroutines.delay

@Composable
internal fun HomeScreen(
    birthdays: List<Birthday>,
    onAdd: () -> Unit,
    onOpen: (Birthday) -> Unit,
    onEnableNotifications: () -> Unit
) {
    var filter by remember { mutableStateOf(HomeFilter.Upcoming) }
    var searching by remember { mutableStateOf(false) }
    var query by remember { mutableStateOf("") }
    val focus = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val navBottom = bottomInset()
    val today = LocalDate.now()

    LaunchedEffect(searching) {
        if (searching) {
            delay(180)
            runCatching { focus.requestFocus() }
        }
    }

    val visible = remember(birthdays, filter, query, today) {
        val q = query.trim()
        val base = if (q.isEmpty()) birthdays else birthdays.filter { it.name.contains(q, ignoreCase = true) }
        when (filter) {
            HomeFilter.Upcoming -> base.sortedBy { it.daysUntil() }
            HomeFilter.Month -> filterBirthdaysForMonth(base, today)
                .sortedBy { it.birthDate.dayOfMonth }
            HomeFilter.Alphabetical -> base.sortedBy { it.name.lowercase() }
        }
    }

    val hero = if (filter == HomeFilter.Upcoming && query.isBlank() && visible.isNotEmpty()) visible.first() else null
    val rest = if (hero != null) visible.drop(1) else visible

    Column(modifier = Modifier.fillMaxSize()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(start = 20.dp, end = 20.dp, top = 16.dp, bottom = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.weight(1f).height(56.dp),
                contentAlignment = Alignment.CenterStart
            ) {
                this@Row.AnimatedVisibility(
                    visible = !searching,
                    enter = fadeIn(tween(260, delayMillis = 120)) + slideInHorizontally(tween(320)) { -it / 4 },
                    exit = fadeOut(tween(160)) + slideOutHorizontally(tween(260)) { -it / 4 }
                ) {
                    BrandTitle(count = birthdays.size)
                }
                this@Row.AnimatedVisibility(
                    visible = searching,
                    enter = expandHorizontally(
                        animationSpec = tween(340, easing = FastOutSlowInEasing),
                        expandFrom = Alignment.End
                    ) + fadeIn(tween(220)),
                    exit = shrinkHorizontally(
                        animationSpec = tween(260, easing = FastOutSlowInEasing),
                        shrinkTowards = Alignment.End
                    ) + fadeOut(tween(160))
                ) {
                    SearchPill(
                        query = query,
                        onQueryChange = { query = it },
                        focusRequester = focus,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
            if (birthdays.isNotEmpty()) {
                Spacer(Modifier.width(12.dp))
                CircleIconButton(
                    icon = if (searching) Icons.Outlined.Close else Icons.Outlined.Search,
                    description = if (searching) "Fechar busca" else "Buscar",
                    onClick = {
                        if (searching) {
                            focusManager.clearFocus()
                            query = ""
                        }
                        searching = !searching
                    }
                )
            }
        }

        if (birthdays.isNotEmpty()) {
            SegmentedControl(
                options = HomeFilter.entries.map { it.label },
                selectedIndex = filter.ordinal,
                onSelect = { filter = HomeFilter.entries[it] },
                modifier = Modifier.padding(horizontal = 20.dp)
            )
            Spacer(Modifier.height(8.dp))
        }

        when {
            birthdays.isEmpty() -> EmptyState(
                icon = Icons.Outlined.Celebration,
                title = "Nenhum aniversário ainda",
                message = "Adicione o primeiro e o Remindr avisa você no dia de cada comemoração.",
                modifier = Modifier.weight(1f),
                actionLabel = "Adicionar aniversário",
                onAction = onAdd,
                secondaryLabel = "Ativar lembretes",
                onSecondary = onEnableNotifications
            )
            visible.isEmpty() -> EmptyState(
                icon = Icons.Outlined.Search,
                title = "Nada por aqui",
                message = if (query.isNotBlank()) "Nenhum nome parecido com \"${query.trim()}\"." else "Ninguém faz aniversário neste mês.",
                modifier = Modifier.weight(1f)
            )
            else -> LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentPadding = PaddingValues(top = 4.dp, bottom = 150.dp + navBottom)
            ) {
                if (hero != null) {
                    item(key = "hero") { HeroCard(hero) { onOpen(hero) } }
                }
                if (filter == HomeFilter.Upcoming) {
                    val groups = rest.groupBy { groupLabel(it.daysUntil()) }
                    groups.forEach { (label, list) ->
                        item(key = "group_$label") { GroupHeader(label, list.size) }
                        itemsIndexed(list, key = { _, b -> b.id }) { index, b ->
                            BirthdayRow(b, rowSubtitle(b, true), index < list.lastIndex) { onOpen(b) }
                        }
                    }
                } else {
                    itemsIndexed(rest, key = { _, b -> b.id }) { index, b ->
                        BirthdayRow(b, rowSubtitle(b, true), index < rest.lastIndex) { onOpen(b) }
                    }
                }
            }
        }
    }
}


internal fun filterBirthdaysForMonth(
    birthdays: List<Birthday>,
    today: LocalDate
): List<Birthday> = birthdays.filter {
    it.birthDate.monthValue == today.monthValue && it.birthDate.dayOfMonth >= today.dayOfMonth
}
