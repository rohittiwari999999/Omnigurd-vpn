package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PowerSettingsNew
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.ripple
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.GaugeEnd
import com.example.ui.theme.GaugeMid
import com.example.ui.theme.GaugeStart
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonEmeraldGlow
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusConnecting
import com.example.ui.theme.StatusDisconnected
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun CyberCard(
    modifier: Modifier = Modifier,
    borderColor: Color = CyberSurfaceBorder,
    backgroundColor: Color = CyberSurface,
    content: @Composable () -> Unit
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(20.dp))
            .border(1.dp, borderColor, RoundedCornerShape(20.dp)),
        color = backgroundColor,
        tonalElevation = 4.dp
    ) {
        content()
    }
}

/**
 * Big One-Tap Power Button with animated glowing rings and state transitions
 */
@Composable
fun OneTapVpnButton(
    status: ConnectionStatus,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_alpha"
    )

    val (glowColor, buttonColor, iconColor) = when (status) {
        ConnectionStatus.CONNECTED -> Triple(NeonEmerald, NeonEmerald, CyberBackground)
        ConnectionStatus.CONNECTING -> Triple(NeonAmber, NeonAmber, CyberBackground)
        ConnectionStatus.DISCONNECTING -> Triple(NeonAmber, CyberSurfaceElevated, NeonAmber)
        ConnectionStatus.DISCONNECTED -> Triple(NeonCyan, CyberSurfaceElevated, NeonCyan)
    }

    Box(
        modifier = modifier.size(220.dp),
        contentAlignment = Alignment.Center
    ) {
        // Outer pulsing ring when connected or connecting
        if (status == ConnectionStatus.CONNECTED || status == ConnectionStatus.CONNECTING) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .scale(pulseScale)
                    .clip(CircleShape)
                    .background(glowColor.copy(alpha = pulseAlpha))
            )
        }

        // Secondary glow ring
        Box(
            modifier = Modifier
                .size(175.dp)
                .clip(CircleShape)
                .border(
                    width = 2.dp,
                    color = glowColor.copy(alpha = if (status == ConnectionStatus.CONNECTED) 0.8f else 0.3f),
                    shape = CircleShape
                )
        )

        // Main touch button
        Box(
            modifier = Modifier
                .size(150.dp)
                .clip(CircleShape)
                .background(
                    Brush.radialGradient(
                        colors = listOf(
                            buttonColor.copy(alpha = if (status == ConnectionStatus.CONNECTED) 0.95f else 0.4f),
                            CyberSurface
                        )
                    )
                )
                .border(2.dp, glowColor, CircleShape)
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(bounded = true, color = glowColor),
                    onClick = onClick
                )
                .testTag("one_tap_vpn_button"),
            contentAlignment = Alignment.Center
        ) {
            if (status == ConnectionStatus.CONNECTING || status == ConnectionStatus.DISCONNECTING) {
                CircularProgressIndicator(
                    modifier = Modifier.size(90.dp),
                    color = glowColor,
                    strokeWidth = 3.dp
                )
            }

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PowerSettingsNew,
                    contentDescription = "VPN Power Button",
                    tint = if (status == ConnectionStatus.CONNECTED) CyberBackground else glowColor,
                    modifier = Modifier.size(54.dp)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = when (status) {
                        ConnectionStatus.CONNECTED -> "CONNECTED"
                        ConnectionStatus.CONNECTING -> "CONNECTING"
                        ConnectionStatus.DISCONNECTING -> "STOPPING"
                        ConnectionStatus.DISCONNECTED -> "TAP TO CONNECT"
                    },
                    color = if (status == ConnectionStatus.CONNECTED) CyberBackground else TextPrimary,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }
    }
}

/**
 * Latency badge with color coding
 */
@Composable
fun LatencyBadge(pingMs: Int, modifier: Modifier = Modifier) {
    val (color, text) = when {
        pingMs < 30 -> Pair(NeonEmerald, "$pingMs ms")
        pingMs < 70 -> Pair(NeonAmber, "$pingMs ms")
        else -> Pair(StatusDisconnected, "$pingMs ms")
    }

    Row(
        modifier = modifier
            .clip(RoundedCornerShape(8.dp))
            .background(color.copy(alpha = 0.15f))
            .border(1.dp, color.copy(alpha = 0.4f), RoundedCornerShape(8.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .clip(CircleShape)
                .background(color)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = color,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            maxLines = 1,
            softWrap = false
        )
    }
}

/**
 * Server Load Bar
 */
@Composable
fun ServerLoadBar(loadPercent: Int, modifier: Modifier = Modifier) {
    val loadColor = when {
        loadPercent < 40 -> NeonEmerald
        loadPercent < 70 -> NeonAmber
        else -> StatusDisconnected
    }

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = "Server Load", color = TextMuted, fontSize = 10.sp)
            Text(
                text = "$loadPercent%",
                color = loadColor,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold
            )
        }
        Spacer(modifier = Modifier.height(3.dp))
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(4.dp)
                .clip(RoundedCornerShape(2.dp))
                .background(CyberSurfaceElevated)
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(fraction = (loadPercent / 100f).coerceIn(0.05f, 1f))
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(loadColor)
            )
        }
    }
}

/**
 * Real-Time Speedometer Canvas Gauge
 */
@Composable
fun SpeedometerGauge(
    speedMbps: Float,
    maxSpeedMbps: Float = 150f,
    modifier: Modifier = Modifier
) {
    val normalized = (speedMbps / maxSpeedMbps).coerceIn(0f, 1f)
    val sweepAngle = 240f
    val startAngle = 150f

    Box(
        modifier = modifier.size(260.dp),
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val strokeWidth = 16.dp.toPx()
            val diameter = size.minDimension - strokeWidth * 2
            val arcSize = Size(diameter, diameter)
            val topLeft = Offset(
                (size.width - diameter) / 2,
                (size.height - diameter) / 2
            )

            // Background Track
            drawArc(
                color = CyberSurfaceElevated,
                startAngle = startAngle,
                sweepAngle = sweepAngle,
                useCenter = false,
                topLeft = topLeft,
                size = arcSize,
                style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
            )

            // Active Glowing Speed Arc
            val activeSweep = sweepAngle * normalized
            if (activeSweep > 0) {
                drawArc(
                    brush = Brush.sweepGradient(
                        colors = listOf(GaugeStart, GaugeMid, GaugeEnd)
                    ),
                    startAngle = startAngle,
                    sweepAngle = activeSweep,
                    useCenter = false,
                    topLeft = topLeft,
                    size = arcSize,
                    style = Stroke(width = strokeWidth, cap = StrokeCap.Round)
                )
            }

            // Draw ticks around the dial
            val radius = diameter / 2
            val center = Offset(size.width / 2, size.height / 2)
            val totalTicks = 20
            for (i in 0..totalTicks) {
                val tickFraction = i.toFloat() / totalTicks
                val angleDeg = startAngle + tickFraction * sweepAngle
                val angleRad = Math.toRadians(angleDeg.toDouble())

                val tickLength = if (i % 5 == 0) 14.dp.toPx() else 8.dp.toPx()
                val innerR = radius - strokeWidth / 2 - tickLength - 4.dp.toPx()
                val outerR = radius - strokeWidth / 2 - 4.dp.toPx()

                val startX = center.x + innerR * cos(angleRad).toFloat()
                val startY = center.y + innerR * sin(angleRad).toFloat()
                val endX = center.x + outerR * cos(angleRad).toFloat()
                val endY = center.y + outerR * sin(angleRad).toFloat()

                val tickColor = if (tickFraction <= normalized) NeonCyan else TextMuted.copy(alpha = 0.4f)
                drawLine(
                    color = tickColor,
                    start = Offset(startX, startY),
                    end = Offset(endX, endY),
                    strokeWidth = if (i % 5 == 0) 2.dp.toPx() else 1.dp.toPx(),
                    cap = StrokeCap.Round
                )
            }
        }

        // Center Digital Readout
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = String.format("%.1f", speedMbps),
                color = TextPrimary,
                fontSize = 44.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = (-1).sp
            )
            Text(
                text = "Mbps",
                color = NeonCyan,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }
    }
}
