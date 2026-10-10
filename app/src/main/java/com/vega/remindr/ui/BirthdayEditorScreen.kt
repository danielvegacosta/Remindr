@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material.icons.outlined.Event
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.material3.SelectableDates
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.vega.remindr.model.Birthday
import java.time.format.DateTimeFormatter
import java.time.Instant
import java.time.LocalDate
import java.time.temporal.ChronoUnit
import java.time.ZoneOffset

@Composable
internal fun EditorScreen(birthday: Birthday?, onBack: () -> Unit, onSave: (Birthday) -> Unit) {
    val cs = MaterialTheme.colorScheme
    var name by remember(birthday) { mutableStateOf(birthday?.name.orEmpty()) }
    var notes by remember(birthday) { mutableStateOf(birthday?.notes.orEmpty()) }
    var date by remember(birthday) { mutableStateOf(birthday?.birthDate) }
    val today = LocalDate.now()
    var calendarOpen by remember { mutableStateOf(false) }
    var validationError by remember { mutableStateOf(false) }
    var nameFocused by remember { mutableStateOf(false) }
    var notesFocused by remember { mutableStateOf(false) }

    val nameInvalid = validationError && name.isBlank()
    val dateInvalid = validationError &&
            (date == null || date?.isAfter(today) == true)
    val hint = date?.let { d ->
        when (val n = ChronoUnit.DAYS.between(LocalDate.now(), nextOccurrence(d))) {
            0L -> "O aniversário é hoje"
            1L -> "O aniversário é amanhã"
            else -> "Faltam $n dias para o aniversário"
        }
    } ?: "Preencha os dados abaixo"

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
            .imePadding()
    ) {
        ScreenTopBar(title = if (birthday == null) "Novo aniversariante" else "Editar registro", onBack = onBack)

        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(8.dp))
            Spacer(Modifier.height(8.dp))
            if (name.isNotBlank()) {
                PersonAvatar(name = name, size = 108.dp)
            } else {
                Box(
                    modifier = Modifier
                        .size(108.dp)
                        .clip(AvatarShape)
                        .background(cs.surfaceContainerHigh)
                        .border(1.dp, hairline(), AvatarShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(Icons.Outlined.Person, null, tint = cs.onSurfaceVariant, modifier = Modifier.size(48.dp))
                }
            }
            Spacer(Modifier.height(16.dp))
            Surface(shape = CircleShape, color = cs.primary.copy(alpha = 0.12f)) {
                Row(
                    modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(hint, color = cs.primary, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                }
            }

            Spacer(Modifier.height(24.dp))
            FieldCard(label = "Nome", icon = Icons.Outlined.Person, focused = nameFocused, error = nameInvalid) {
                Box(Modifier.fillMaxWidth()) {
                    if (name.isEmpty()) {
                        Text("Nome completo", color = cs.onSurfaceVariant.copy(alpha = 0.5f), fontSize = 18.sp)
                    }
                    BasicTextField(
                        value = name,
                        onValueChange = { name = it; validationError = false },
                        singleLine = true,
                        textStyle = TextStyle(color = cs.onSurface, fontSize = 18.sp, fontWeight = FontWeight.SemiBold),
                        cursorBrush = SolidColor(cs.primary),
                        keyboardOptions = KeyboardOptions(
                            capitalization = KeyboardCapitalization.Words,
                            imeAction = ImeAction.Done
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { nameFocused = it.isFocused }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))
            FieldCard(
                label = "Data de nascimento",
                icon = Icons.Outlined.Event,
                focused = false,
                error = dateInvalid,
                modifier = Modifier.clip(FieldShape).clickable { calendarOpen = true }
            ) {
                Text(
                    text = date?.format(DateTimeFormatter.ofPattern("d 'de' MMMM 'de' yyyy", PtBr)) ?: "Selecionar data",
                    color = if (date != null) cs.onSurface else cs.onSurfaceVariant.copy(alpha = 0.5f),
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }

            Spacer(Modifier.height(12.dp))
            FieldCard(label = "Anotações", icon = Icons.Outlined.Edit, focused = notesFocused, error = false) {
                Box(Modifier.fillMaxWidth()) {
                    if (notes.isEmpty()) {
                        Text(
                            "Ideias de presente, gostos, lembretes…",
                            color = cs.onSurfaceVariant.copy(alpha = 0.5f),
                            fontSize = 16.sp
                        )
                    }
                    BasicTextField(
                        value = notes,
                        onValueChange = { notes = it },
                        minLines = 3,
                        textStyle = TextStyle(color = cs.onSurface, fontSize = 16.sp, lineHeight = 24.sp),
                        cursorBrush = SolidColor(cs.primary),
                        modifier = Modifier
                            .fillMaxWidth()
                            .onFocusChanged { notesFocused = it.isFocused }
                    )
                }
            }

            if (nameInvalid || dateInvalid) {
                Text(
                    text = when {
                        nameInvalid && dateInvalid -> "Informe o nome e a data de nascimento."
                        nameInvalid -> "Informe o nome."
                        else -> "Escolha a data de nascimento."
                    },
                    color = cs.error,
                    fontSize = 13.sp,
                    modifier = Modifier.align(Alignment.Start).padding(start = 8.dp, top = 12.dp)
                )
            }
            Spacer(Modifier.height(24.dp))
        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 14.dp)
                .navigationBarsPadding()
        ) {
            PrimaryButton(
                label = if (birthday == null) "Adicionar aniversariante" else "Salvar alterações",
                onClick = {
                    val invalidDate = date == null || date?.isAfter(today) == true

                    if (name.isBlank() || invalidDate) {
                        validationError = true
                    } else {
                        onSave(
                            Birthday(
                                birthday?.id ?: 0L,
                                name.trim(),
                                date!!,
                                notes.trim()
                            )
                        )
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                height = 56.dp,
                fontSize = 17.sp
            )
        }
    }

    if (calendarOpen) {
        val selectableDates = remember(today) {
            object : SelectableDates {

                override fun isSelectableDate(utcTimeMillis: Long): Boolean {
                    val selectedDate = Instant
                        .ofEpochMilli(utcTimeMillis)
                        .atZone(ZoneOffset.UTC)
                        .toLocalDate()

                    return !selectedDate.isAfter(today)
                }

                override fun isSelectableYear(year: Int): Boolean {
                    return year <= today.year
                }
            }
        }
        val picker = rememberDatePickerState(
            initialSelectedDateMillis = date
                ?.atStartOfDay(ZoneOffset.UTC)
                ?.toInstant()
                ?.toEpochMilli(),
            selectableDates = selectableDates
        )
        val pickerColors = DatePickerDefaults.colors(containerColor = dialogContainer())
        DatePickerDialog(
            onDismissRequest = { calendarOpen = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        picker.selectedDateMillis?.let {
                            val selectedDate = Instant
                                .ofEpochMilli(it)
                                .atZone(ZoneOffset.UTC)
                                .toLocalDate()

                            if (!selectedDate.isAfter(today)) {
                                date = selectedDate
                                validationError = false
                                calendarOpen = false
                            } else {
                                validationError = true
                            }
                        } ?: run {
                            calendarOpen = false
                        }
                    }
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { calendarOpen = false },
                    colors = ButtonDefaults.textButtonColors(contentColor = cs.onSurfaceVariant)
                ) { Text("Cancelar") }
            },
            colors = pickerColors
        ) {
            DatePicker(
                state = picker,
                colors = pickerColors,
                showModeToggle = false
            )
        }
    }
}

@Composable
internal fun FieldCard(
    label: String,
    icon: ImageVector,
    focused: Boolean,
    error: Boolean,
    modifier: Modifier = Modifier,
    content: @Composable ColumnScope.() -> Unit
) {
    val cs = MaterialTheme.colorScheme
    val idleBorder = hairline()
    val borderColor by animateColorAsState(
        targetValue = when {
            error -> cs.error
            focused -> cs.primary
            else -> idleBorder
        },
        animationSpec = tween(160),
        label = "fieldBorder"
    )
    Surface(
        shape = FieldShape,
        color = cs.surfaceContainerLow,
        border = BorderStroke(if (focused || error) 1.5.dp else 1.dp, borderColor),
        modifier = modifier.fillMaxWidth()
    ) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.Top) {
            IconTile(icon, if (error) cs.error else cs.primary)
            Spacer(Modifier.width(14.dp))
            Column(Modifier.weight(1f)) {
                Text(
                    text = label,
                    color = if (error) cs.error else cs.onSurfaceVariant,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Spacer(Modifier.height(4.dp))
                content()
            }
        }
    }
}
