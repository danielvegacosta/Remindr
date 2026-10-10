@file:OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)

package com.vega.remindr.ui

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.CalendarMonth
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.NotificationsActive
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.scale
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
internal fun IntroScreen(onContinue: () -> Unit) {
    val cs = MaterialTheme.colorScheme
    var shown by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { shown = true }

    val headerReveal by animateFloatAsState(
        targetValue = if (shown) 1f else 0f,
        animationSpec = tween(600, easing = FastOutSlowInEasing),
        label = "introHeaderReveal"
    )

    BoxWithConstraints(
        modifier = Modifier
            .fillMaxSize()
            .background(cs.background)
    ) {
        val roomy = (maxHeight - topInset() - bottomInset()) >= 700.dp

        BrandAmbient()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .navigationBarsPadding()
                .padding(horizontal = 28.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(if (roomy) 40.dp else 20.dp))

            Column(
                modifier = Modifier
                    .alpha(headerReveal)
                    .scale(0.94f + 0.06f * headerReveal),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                BrandWordmark(width = 210.dp, height = 48.dp)
                Spacer(Modifier.height(12.dp))
                BrandAccentBar()
            }

            Spacer(Modifier.height(if (roomy) 32.dp else 18.dp))

            Reveal(modifier = Modifier.fillMaxWidth(), delayMillis = 140) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Bem-vindo ao Remindr!",
                        color = cs.onBackground,
                        fontSize = 30.sp,
                        lineHeight = 36.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = (-0.8).sp,
                        textAlign = TextAlign.Center
                    )
                    Spacer(Modifier.height(8.dp))
                    Text(
                        text = "Nunca mais esqueça um dia importante",
                        color = cs.onSurfaceVariant,
                        fontSize = 16.sp,
                        lineHeight = 22.sp,
                        textAlign = TextAlign.Center
                    )
                }
            }

            Spacer(Modifier.weight(0.6f))

            Column(Modifier.fillMaxWidth()) {
                Reveal(modifier = Modifier.fillMaxWidth(), delayMillis = 260) {
                    FeatureRow(Icons.Outlined.NotificationsActive, cs.primary, "Lembretes no dia certo", "Uma notificação de cada aniversário.")
                }
                FeatureDivider()
                Reveal(modifier = Modifier.fillMaxWidth(), delayMillis = 500) {
                    FeatureRow(Icons.Outlined.CalendarMonth, cs.secondary, "Calendário completo", "Todas as datas do ano em um só lugar.")
                }
                FeatureDivider()
                Reveal(modifier = Modifier.fillMaxWidth(), delayMillis = 380) {
                    FeatureRow(Icons.Outlined.Lock, cs.tertiary, "Protegido", "Ninguém poderá ver suas anotações ou dados")
                }
            }

            Spacer(Modifier.weight(1f))

            Reveal(modifier = Modifier.fillMaxWidth(), delayMillis = 620) {
                PrimaryButton(
                    label = "Começar",
                    onClick = onContinue,
                    modifier = Modifier.fillMaxWidth(),
                    height = 58.dp,
                    fontSize = 17.sp
                )
            }
            Spacer(Modifier.height(20.dp))
        }
    }
}
