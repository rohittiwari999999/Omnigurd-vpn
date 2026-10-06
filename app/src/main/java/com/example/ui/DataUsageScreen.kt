package com.example.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AllInclusive
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DataUsage
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.SyncAlt
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.ui.components.CyberCard
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.VpnViewModel

@Composable
fun DataUsageScreen(
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    val metrics by viewModel.sessionMetrics.collectAsState()
    val status by viewModel.connectionStatus.collectAsState()
    val todayDown by viewModel.todayDownloadedBytes.collectAsState()
    val todayUp by viewModel.todayUploadedBytes.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Title Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Unlimited Data",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Zero Bandwidth Limits • High-Speed Pipes",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonEmerald.copy(alpha = 0.15f))
                    .border(1.dp, NeonEmerald.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                    .padding(horizontal = 10.dp, vertical = 6.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = "Unlimited",
                        tint = NeonEmerald,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "UNLIMITED",
                        color = NeonEmerald,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        // Unlimited Guarantee Hero Card
        CyberCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("unlimited_guarantee_hero"),
            borderColor = NeonCyan.copy(alpha = 0.5f),
            backgroundColor = CyberSurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(54.dp)
                        .clip(CircleShape)
                        .background(
                            Brush.linearGradient(
                                listOf(NeonCyan.copy(alpha = 0.3f), NeonEmerald.copy(alpha = 0.3f))
                            )
                        )
                        .border(1.dp, NeonCyan, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AllInclusive,
                        contentDescription = "Unlimited Data",
                        tint = NeonCyan,
                        modifier = Modifier.size(32.dp)
                    )
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column {
                    Text(
                        text = "Unlimited Free Plan",
                        color = TextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = "No speed throttling, no bandwidth quotas, and 100% uncapped data on every server location globally.",
                        color = TextSecondary,
                        fontSize = 12.sp,
                        lineHeight = 16.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Current Session Data Counters
        CyberCard(
            modifier = Modifier.fillMaxWidth(),
            borderColor = if (status == ConnectionStatus.CONNECTED) NeonEmerald.copy(alpha = 0.4f) else CyberSurfaceBorder
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "CURRENT SESSION TRAFFIC",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Text(
                        text = if (status == ConnectionStatus.CONNECTED) "ACTIVE TUNNEL" else "IDLE",
                        color = if (status == ConnectionStatus.CONNECTED) NeonEmerald else TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    val sessionDownMb = metrics.sessionDownloadedBytes / (1024f * 1024f)
                    val sessionUpMb = metrics.sessionUploadedBytes / (1024f * 1024f)

                    SessionCounterBox(
                        icon = Icons.Default.ArrowDownward,
                        label = "SESSION DOWNLOADED",
                        value = if (sessionDownMb > 1024) String.format("%.2f GB", sessionDownMb / 1024f) else String.format("%.1f MB", sessionDownMb),
                        speed = metrics.downloadSpeedFormatted,
                        tint = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )

                    SessionCounterBox(
                        icon = Icons.Default.ArrowUpward,
                        label = "SESSION UPLOADED",
                        value = if (sessionUpMb > 1024) String.format("%.2f GB", sessionUpMb / 1024f) else String.format("%.1f MB", sessionUpMb),
                        speed = metrics.uploadSpeedFormatted,
                        tint = NeonPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Live Real-Time Bandwidth Wave Activity
        CyberCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "BANDWIDTH REAL-TIME ACTIVITY",
                        color = TextMuted,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 1.sp
                    )
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonCyan))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Download", color = TextSecondary, fontSize = 10.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Box(modifier = Modifier.size(6.dp).clip(CircleShape).background(NeonPurple))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = "Upload", color = TextSecondary, fontSize = 10.sp)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Canvas Graph
                LiveBandwidthChart(
                    downloadRate = metrics.downloadSpeedMbps,
                    uploadRate = metrics.uploadSpeedMbps,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Cumulative Data Usage Breakdown (Today & This Month)
        CyberCard(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "TOTAL DATA TRANSFERRED",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                val todayTotalBytes = todayDown + todayUp
                val todayGb = todayTotalBytes / (1024.0 * 1024.0 * 1024.0)

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Bottom
                ) {
                    Column {
                        Text(
                            text = String.format("%.2f GB", todayGb),
                            color = TextPrimary,
                            fontSize = 28.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                        Text(
                            text = "Used Today (100% Uncapped)",
                            color = NeonEmerald,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Column(horizontalAlignment = Alignment.End) {
                        Text(text = "Monthly Total: 38.6 GB", color = TextSecondary, fontSize = 12.sp)
                        Text(text = "Quota Remaining: ∞ GB", color = NeonCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(12.dp))

                // Split Ratio Bar
                val downFraction = (todayDown.toFloat() / todayTotalBytes.coerceAtLeast(1L)).coerceIn(0.1f, 0.9f)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(8.dp)
                        .clip(RoundedCornerShape(4.dp))
                        .background(CyberSurfaceElevated)
                ) {
                    Row(modifier = Modifier.fillMaxSize()) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(fraction = downFraction)
                                .height(8.dp)
                                .background(NeonCyan)
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .height(8.dp)
                                .background(NeonPurple)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(
                        text = "↓ Download: ${String.format("%.2f GB", todayDown / (1024.0 * 1024.0 * 1024.0))}",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = "↑ Upload: ${String.format("%.2f GB", todayUp / (1024.0 * 1024.0 * 1024.0))}",
                        color = NeonPurple,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Unlimited Perks Checklist
        CyberCard(modifier = Modifier.fillMaxWidth(), backgroundColor = CyberSurface) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "UNLIMITED PRIVACY FEATURES",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(10.dp))

                PerkItem(
                    icon = Icons.Default.CheckCircle,
                    title = "Strict Zero-Log Policy",
                    subtitle = "No browsing logs, timestamps, or IP history are ever stored"
                )
                PerkItem(
                    icon = Icons.Default.AllInclusive,
                    title = "No Bandwidth Capping",
                    subtitle = "Stream in 4K UHD or game indefinitely without reduced speeds"
                )
                PerkItem(
                    icon = Icons.Default.SyncAlt,
                    title = "Unlimited Server Switching",
                    subtitle = "Hop freely across all North American, European, & Asian nodes"
                )
                PerkItem(
                    icon = Icons.Default.Lock,
                    title = "256-Bit Symmetric Crypto",
                    subtitle = "ChaCha20 & AES-256-GCM authenticated cipher protection"
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SessionCounterBox(
    icon: ImageVector,
    label: String,
    value: String,
    speed: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(14.dp))
            .background(CyberSurfaceElevated)
            .border(1.dp, tint.copy(alpha = 0.25f), RoundedCornerShape(14.dp))
            .padding(12.dp)
    ) {
        Column {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    color = TextMuted,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "Live: $speed",
                color = tint,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
fun LiveBandwidthChart(
    downloadRate: Float,
    uploadRate: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val width = size.width
        val height = size.height

        // Background grid lines
        val gridLines = 4
        for (i in 0..gridLines) {
            val y = height * (i.toFloat() / gridLines)
            drawLine(
                color = CyberSurfaceElevated,
                start = Offset(0f, y),
                end = Offset(width, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        // Draw dynamic wave paths for download & upload
        val downPath = Path()
        val upPath = Path()

        val points = 12
        val stepX = width / (points - 1)

        for (i in 0 until points) {
            val x = i * stepX
            val downVariation = Math.sin((i + downloadRate * 0.1).toDouble()).toFloat() * 18
            val downY = (height * 0.45f - (downloadRate / 100f) * (height * 0.35f) + downVariation)
                .coerceIn(10f, height - 10f)

            val upVariation = Math.cos((i + uploadRate * 0.1).toDouble()).toFloat() * 12
            val upY = (height * 0.75f - (uploadRate / 50f) * (height * 0.25f) + upVariation)
                .coerceIn(10f, height - 10f)

            if (i == 0) {
                downPath.moveTo(x, downY)
                upPath.moveTo(x, upY)
            } else {
                downPath.lineTo(x, downY)
                upPath.lineTo(x, upY)
            }
        }

        // Draw curves
        drawPath(
            path = downPath,
            color = NeonCyan,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
        drawPath(
            path = upPath,
            color = NeonPurple,
            style = Stroke(width = 2.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}

@Composable
fun PerkItem(
    icon: ImageVector,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(NeonCyan.copy(alpha = 0.12f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = NeonCyan,
                modifier = Modifier.size(18.dp)
            )
        }
        Spacer(modifier = Modifier.width(12.dp))
        Column {
            Text(text = title, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.Bold)
            Text(text = subtitle, color = TextSecondary, fontSize = 11.sp)
        }
    }
}
