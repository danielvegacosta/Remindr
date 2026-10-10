@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
internal fun RemindrDialog(popup: Popup, onDismiss: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = dialogContainer(),
            border = BorderStroke(1.dp, dialogBorder()),
            modifier = Modifier.padding(horizontal = 28.dp).fillMaxWidth().widthIn(max = 420.dp)
        ) {
            Column(Modifier.padding(24.dp)) {
                Box(
                    modifier = Modifier
                        .size(52.dp)
                        .clip(AvatarShape)
                        .background(if (popup.destructive) cs.errorContainer else cs.primaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (popup.destructive) Icons.Outlined.Delete else Icons.Outlined.Info,
                        contentDescription = null,
                        tint = if (popup.destructive) cs.onErrorContainer else cs.onPrimaryContainer,
                        modifier = Modifier.size(26.dp)
                    )
                }
                Text(
                    text = popup.title,
                    color = cs.onSurface,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp,
                    modifier = Modifier.padding(top = 20.dp)
                )
                Text(
                    text = popup.message,
                    color = cs.onSurfaceVariant,
                    fontSize = 15.sp,
                    lineHeight = 22.sp,
                    modifier = Modifier.padding(top = 8.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 24.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    if (popup.destructive) {
                        FilledTonalButton(
                            onClick = onDismiss,
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.filledTonalButtonColors(
                                containerColor = cs.onSurface.copy(alpha = 0.08f),
                                contentColor = cs.onSurface
                            )
                        ) { Text("Cancelar", fontSize = 15.sp, fontWeight = FontWeight.SemiBold) }
                        Button(
                            onClick = { onDismiss(); popup.onConfirm() },
                            modifier = Modifier.weight(1f).height(48.dp),
                            shape = CircleShape,
                            colors = ButtonDefaults.buttonColors(containerColor = cs.error, contentColor = cs.onError)
                        ) { Text(popup.confirmLabel, fontSize = 15.sp, fontWeight = FontWeight.Bold) }
                    } else {
                        PrimaryButton(
                            label = popup.confirmLabel,
                            onClick = { onDismiss(); popup.onConfirm() },
                            modifier = Modifier.weight(1f),
                            height = 48.dp,
                            fontSize = 15.sp
                        )
                    }
                }
            }
        }
    }
}

@Composable
internal fun PinDialog(mode: PinMode, onDismiss: () -> Unit, onSaved: (String) -> Unit) {
    val cs = MaterialTheme.colorScheme
    var pin by remember { mutableStateOf("") }
    var confirmation by remember { mutableStateOf("") }
    var step by remember { mutableStateOf(1) }
    var mismatch by remember { mutableStateOf(false) }

    fun digit(d: String) {
        if (step == 1) {
            if (pin.length < 4) {
                pin += d
                mismatch = false
                if (pin.length == 4) step = 2
            }
        } else if (confirmation.length < 4) {
            confirmation += d
            if (confirmation.length == 4) {
                if (confirmation == pin) {
                    onSaved(pin)
                } else {
                    pin = ""
                    confirmation = ""
                    step = 1
                    mismatch = true
                }
            }
        }
    }

    fun erase() {
        if (step == 1 && pin.isNotEmpty()) pin = pin.dropLast(1)
        else if (step == 2 && confirmation.isNotEmpty()) confirmation = confirmation.dropLast(1)
    }

    Dialog(
        onDismissRequest = onDismiss,
        properties = DialogProperties(usePlatformDefaultWidth = false)
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = dialogContainer(),
            border = BorderStroke(1.dp, dialogBorder()),
            modifier = Modifier.padding(horizontal = 20.dp).fillMaxWidth().widthIn(max = 380.dp)
        ) {
            Column(
                modifier = Modifier.padding(horizontal = 20.dp, vertical = 28.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                IconTile(Icons.Outlined.Lock, cs.primary)
                Spacer(Modifier.height(16.dp))
                Text(
                    text = when {
                        step == 2 -> "Confirmar PIN"
                        mode == PinMode.Reset -> "Redefinir PIN"
                        else -> "Criar PIN"
                    },
                    color = cs.onSurface,
                    fontSize = 21.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = (-0.3).sp
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    text = if (step == 1) "Escolha 4 dígitos para proteger o app" else "Digite o mesmo PIN novamente",
                    color = cs.onSurfaceVariant,
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center
                )
                Spacer(Modifier.height(20.dp))
                PinBoxes(
                    filled = if (step == 1) pin.length else confirmation.length,
                    enabled = true,
                    box = 50.dp,
                    gap = 12.dp
                )
                Box(Modifier.height(30.dp), contentAlignment = Alignment.Center) {
                    if (mismatch) {
                        Text("Os PINs não coincidiram. Tente de novo.", color = cs.error, fontSize = 13.sp)
                    }
                }
                FlatNumpad(
                    onNumber = { digit(it) },
                    onDelete = { erase() },
                    keyHeight = 54.dp
                )
            }
        }
    }
}
