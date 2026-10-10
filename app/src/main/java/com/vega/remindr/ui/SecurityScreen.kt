@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Fingerprint
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun SecurityScreen(
    hasPin: Boolean,
    biometricEnabled: Boolean,
    onBack: () -> Unit,
    onCreatePin: () -> Unit,
    onRemovePin: () -> Unit,
    onBiometricChange: (Boolean) -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(Modifier.fillMaxSize().background(cs.background)) {
        ScreenTopBar(title = "Privacidade", onBack = onBack)
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 20.dp)
        ) {
            Spacer(Modifier.height(8.dp))
            Text(
                text = "Escolha como proteger seus aniversários sempre que abrir o app.",
                color = cs.onSurfaceVariant,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                modifier = Modifier.padding(horizontal = 4.dp)
            )
            Spacer(Modifier.height(20.dp))
            SurfaceCard {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTile(Icons.Outlined.Lock, cs.primary)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Acesso com PIN", color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = if (hasPin) "Ativado" else "Desativado",
                            color = if (hasPin) cs.primary else cs.onSurfaceVariant,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                    if (hasPin) {
                        OutlinedButton(
                            onClick = onRemovePin,
                            shape = CircleShape,
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = cs.error),
                            border = BorderStroke(1.dp, cs.error.copy(alpha = 0.4f))
                        ) { Text("Remover", fontWeight = FontWeight.SemiBold) }
                    } else {
                        PrimaryButton(
                            label = "Configurar",
                            onClick = onCreatePin,
                            height = 40.dp,
                            fontSize = 14.sp
                        )
                    }
                }
                HorizontalDivider(
                    modifier = Modifier.padding(start = 78.dp, end = 20.dp),
                    thickness = 0.5.dp,
                    color = hairline()
                )
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconTile(Icons.Outlined.Fingerprint, cs.tertiary)
                    Spacer(Modifier.width(16.dp))
                    Column(Modifier.weight(1f)) {
                        Text("Impressão digital", color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                        Text(
                            text = "Desbloqueie com a biometria do aparelho",
                            color = cs.onSurfaceVariant,
                            fontSize = 13.sp,
                            modifier = Modifier.padding(top = 1.dp)
                        )
                    }
                    Switch(checked = biometricEnabled, onCheckedChange = onBiometricChange)
                }
            }
            Spacer(Modifier.navigationBarsPadding())
        }
    }
}
