@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.outlined.ChevronRight
import androidx.compose.material.icons.outlined.Download
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material.icons.outlined.Palette
import androidx.compose.material.icons.outlined.Security
import androidx.compose.material.icons.outlined.Upload
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun SettingsScreen(
    count: Int,
    onSecurity: () -> Unit,
    onThemes: () -> Unit,
    onImport: () -> Unit,
    onExport: () -> Unit,
    onNotifications: () -> Unit
) {
    val cs = MaterialTheme.colorScheme
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        LargeTitle(title = "Ajustes", subtitle = "Personalize o Remindr do seu jeito")

        Column(Modifier.padding(horizontal = 20.dp)) {
            SectionTitle("Geral")
            SurfaceCard {
                SettingsItem(Icons.Outlined.Security, cs.primary, "Privacidade", "PIN e biometria", true, onSecurity)
                SettingsItem(Icons.Outlined.Palette, cs.tertiary, "Aparência", "Modo claro, escuro e cores", true, onThemes)
                SettingsItem(
                    Icons.Outlined.NotificationsActive, cs.secondary, "Avisos",
                    "Lembretes no dia de cada aniversário", false, onNotifications, badge = "Ativar"
                )
            }

            Spacer(Modifier.height(24.dp))
            SectionTitle("Backup e dados")
            SurfaceCard {
                SettingsItem(Icons.Outlined.Upload, cs.primary, "Importar", "Restaurar de um arquivo .txt", true, onImport)
                SettingsItem(Icons.Outlined.Download, cs.tertiary, "Exportar", "Salvar um arquivo de backup", false, onExport)
            }

            Spacer(Modifier.height(36.dp))
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrandWordmark(width = 120.dp, height = 26.dp, modifier = Modifier.alpha(0.5f))
                Spacer(Modifier.height(10.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Spacer(Modifier.width(6.dp))
                    Text(
                        text = "Versão 1.0.0",
                        color = cs.onSurfaceVariant.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }
        }
        Spacer(Modifier.navigationBarsPadding().height(150.dp))
    }
}

@Composable
internal fun SettingsItem(
    icon: ImageVector,
    accent: Color,
    title: String,
    subtitle: String,
    showDivider: Boolean,
    onClick: () -> Unit,
    badge: String? = null
) {
    val cs = MaterialTheme.colorScheme
    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick)
                .padding(horizontal = 20.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconTile(icon, accent)
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(title, color = cs.onSurface, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
                Text(subtitle, color = cs.onSurfaceVariant, fontSize = 13.sp, modifier = Modifier.padding(top = 1.dp))
            }
            if (badge != null) {
                Spacer(Modifier.width(8.dp))
                Surface(shape = CircleShape, color = cs.primary.copy(alpha = 0.14f)) {
                    Text(
                        text = badge,
                        color = cs.primary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold,
                        maxLines = 1,
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
                    )
                }
            }
            Spacer(Modifier.width(6.dp))
            Icon(
                Icons.Outlined.ChevronRight,
                null,
                tint = cs.onSurfaceVariant.copy(alpha = 0.7f),
                modifier = Modifier.size(20.dp)
            )
        }
        if (showDivider) {
            HorizontalDivider(
                modifier = Modifier.padding(start = 78.dp, end = 20.dp),
                thickness = 0.5.dp,
                color = hairline()
            )
        }
    }
}

@Composable
internal fun FeatureRow(icon: ImageVector, accent: Color, title: String, subtitle: String) {
    val cs = MaterialTheme.colorScheme
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        IconTile(icon, accent)
        Spacer(Modifier.width(16.dp))
        Column(Modifier.weight(1f)) {
            Text(title, color = cs.onBackground, fontSize = 16.sp, fontWeight = FontWeight.SemiBold)
            Text(
                text = subtitle,
                color = cs.onSurfaceVariant,
                fontSize = 13.sp,
                lineHeight = 18.sp,
                modifier = Modifier.padding(top = 1.dp)
            )
        }
    }
}

@Composable
internal fun FeatureDivider() {
    HorizontalDivider(
        modifier = Modifier.padding(start = 58.dp, top = 14.dp, bottom = 14.dp),
        thickness = 1.dp,
        color = hairline()
    )
}
