@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vega.remindr.model.Birthday
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle as JTextStyle

@Composable
internal fun DetailsScreen(birthday: Birthday, onBack: () -> Unit, onEdit: () -> Unit, onDelete: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    val days = birthday.daysUntil()
    val age = birthday.upcomingAge()
    val weekday = birthday.upcomingDate().dayOfWeek
        .getDisplayName(JTextStyle.SHORT, PtBr)
        .removeSuffix(".")
        .replaceFirstChar { it.uppercase() }
    val statusText = when (days) {
        0L -> "Hoje é o aniversário"
        1L -> "É amanhã"
        else -> "Faltam $days dias"
    }

    Column(Modifier.fillMaxSize().background(cs.background)) {
        ScreenTopBar(
            title = "",
            onBack = onBack,
            trailing = { CircleIconButton(Icons.Outlined.Edit, "Editar", onEdit) }
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(12.dp))
            PersonAvatar(name = birthday.name, size = 112.dp)
            Spacer(Modifier.height(20.dp))
            Text(
                text = birthday.name,
                color = cs.onBackground,
                fontSize = 30.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = (-0.6).sp,
                textAlign = TextAlign.Center,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = birthday.birthDate.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", PtBr)),
                color = cs.onSurfaceVariant,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
            Spacer(Modifier.height(16.dp))
            Surface(shape = CircleShape, color = if (days == 0L) cs.primary else cs.primary.copy(alpha = 0.14f)) {
                Text(
                    text = statusText,
                    color = if (days == 0L) cs.onPrimary else cs.primary,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(horizontal = 18.dp, vertical = 9.dp)
                )
            }

            Spacer(Modifier.height(28.dp))
            SurfaceCard {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 18.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    StatCell("Faltam", days.toString(), if (days == 1L) "dia" else "dias", Modifier.weight(1f))
                    Box(Modifier.width(0.5.dp).height(36.dp).background(hairline()))
                    StatCell("Completa", if (age > 0) age.toString() else "—", if (age > 0) yearsWord(age) else "", Modifier.weight(1f))
                    Box(Modifier.width(0.5.dp).height(36.dp).background(hairline()))
                    StatCell("Cai em", weekday, "", Modifier.weight(1f))
                }
            }

            Spacer(Modifier.height(16.dp))
            SurfaceCard {
                Column(Modifier.fillMaxWidth().padding(20.dp)) {
                    Text("Anotações", color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                    Spacer(Modifier.height(8.dp))
                    if (birthday.notes.isBlank()) {
                        Text(
                            text = "Nada por aqui ainda. Toque em editar para guardar ideias de presente.",
                            color = cs.onSurfaceVariant,
                            fontSize = 15.sp,
                            lineHeight = 22.sp
                        )
                    } else {
                        Text(
                            text = birthday.notes,
                            color = cs.onSurface.copy(alpha = 0.85f),
                            fontSize = 16.sp,
                            lineHeight = 24.sp
                        )
                    }
                }
            }

            Spacer(Modifier.height(24.dp))
            Surface(
                onClick = onDelete,
                shape = CircleShape,
                color = cs.error.copy(alpha = 0.10f),
                border = BorderStroke(1.dp, cs.error.copy(alpha = 0.25f)),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Row(Modifier.fillMaxSize(), Arrangement.Center, Alignment.CenterVertically) {
                    Icon(Icons.Outlined.Delete, null, tint = cs.error, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Apagar registro", color = cs.error, fontSize = 15.sp, fontWeight = FontWeight.SemiBold)
                }
            }
            Spacer(Modifier.navigationBarsPadding().height(32.dp))
        }
    }
}

@Composable
internal fun StatCell(label: String, value: String, unit: String, modifier: Modifier = Modifier) {
    val cs = MaterialTheme.colorScheme
    Column(modifier = modifier, horizontalAlignment = Alignment.CenterHorizontally) {
        Text(label, color = cs.onSurfaceVariant, fontSize = 13.sp)
        Spacer(Modifier.height(4.dp))
        Row(verticalAlignment = Alignment.Bottom) {
            Text(value, color = cs.onSurface, fontSize = 26.sp, fontWeight = FontWeight.Bold, letterSpacing = (-0.5).sp)
            if (unit.isNotEmpty()) {
                Text(
                    text = unit,
                    color = cs.onSurfaceVariant,
                    fontSize = 13.sp,
                    modifier = Modifier.padding(start = 3.dp, bottom = 4.dp)
                )
            }
        }
    }
}
