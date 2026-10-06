package com.example.ui

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.NetworkCheck
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Sensors
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.SpeedTestPhase
import com.example.model.SpeedTestResult
import com.example.ui.components.CyberCard
import com.example.ui.components.SpeedometerGauge
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
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun SpeedTestScreen(
    viewModel: VpnViewModel,
    modifier: Modifier = Modifier
) {
    val speedTestState by viewModel.speedTestState.collectAsState()
    val speedTestHistory by viewModel.speedTestHistory.collectAsState()
    val selectedServer by viewModel.selectedServer.collectAsState()

    val isTesting = speedTestState.phase != SpeedTestPhase.IDLE && speedTestState.phase != SpeedTestPhase.COMPLETED
    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Top Title
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Real-Time Speed Test",
                    color = TextPrimary,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Testing via ${selectedServer.name}",
                    color = NeonCyan,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(NeonCyan.copy(alpha = 0.15f))
                    .padding(horizontal = 10.dp, vertical = 5.dp)
            ) {
                Text(
                    text = selectedServer.flagEmoji + " " + selectedServer.countryCode,
                    color = TextPrimary,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Center Speedometer Gauge
        SpeedometerGauge(
            speedMbps = speedTestState.currentSpeedMbps,
            maxSpeedMbps = 150f
        )

        Spacer(modifier = Modifier.height(12.dp))

        // Phase Progress Bar & Status Text
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = when (speedTestState.phase) {
                    SpeedTestPhase.IDLE -> "Ready to test connection"
                    SpeedTestPhase.PING -> "Measuring latency & jitter..."
                    SpeedTestPhase.DOWNLOAD -> "Testing download speed..."
                    SpeedTestPhase.UPLOAD -> "Testing upload speed..."
                    SpeedTestPhase.COMPLETED -> "Speed test completed successfully!"
                },
                color = when (speedTestState.phase) {
                    SpeedTestPhase.COMPLETED -> NeonEmerald
                    SpeedTestPhase.IDLE -> TextSecondary
                    else -> NeonAmber
                },
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold
            )

            if (isTesting) {
                Spacer(modifier = Modifier.height(8.dp))
                LinearProgressIndicator(
                    progress = { speedTestState.progressPercent },
                    modifier = Modifier
                        .fillMaxWidth(0.8f)
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = NeonCyan,
                    trackColor = CyberSurfaceElevated
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Live Metric Cards (Ping, Jitter, Download, Upload)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SpeedMetricCard(
                icon = Icons.Default.Sensors,
                label = "PING",
                value = if (speedTestState.pingMs > 0) "${speedTestState.pingMs} ms" else "--",
                tint = NeonCyan,
                modifier = Modifier.weight(1f)
            )
            SpeedMetricCard(
                icon = Icons.Default.NetworkCheck,
                label = "JITTER",
                value = if (speedTestState.jitterMs > 0) "${speedTestState.jitterMs} ms" else "--",
                tint = NeonAmber,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SpeedMetricCard(
                icon = Icons.Default.ArrowDownward,
                label = "DOWNLOAD",
                value = if (speedTestState.downloadSpeedMbps > 0) "${speedTestState.downloadSpeedMbps} Mbps" else "--",
                tint = NeonEmerald,
                modifier = Modifier.weight(1f)
            )
            SpeedMetricCard(
                icon = Icons.Default.ArrowUpward,
                label = "UPLOAD",
                value = if (speedTestState.uploadSpeedMbps > 0) "${speedTestState.uploadSpeedMbps} Mbps" else "--",
                tint = NeonPurple,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Action Button: Start Speed Test
        Button(
            onClick = {
                if (speedTestState.phase == SpeedTestPhase.COMPLETED) {
                    viewModel.resetSpeedTest()
                }
                viewModel.startSpeedTest()
            },
            enabled = !isTesting,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("start_speed_test_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = NeonCyan,
                contentColor = CyberBackground,
                disabledContainerColor = CyberSurfaceElevated,
                disabledContentColor = TextMuted
            ),
            shape = RoundedCornerShape(16.dp)
        ) {
            Icon(
                imageVector = if (speedTestState.phase == SpeedTestPhase.COMPLETED) Icons.Default.Refresh else Icons.Default.PlayArrow,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = when {
                    isTesting -> "TESTING IN PROGRESS..."
                    speedTestState.phase == SpeedTestPhase.COMPLETED -> "TEST AGAIN"
                    else -> "START SPEED TEST"
                },
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                letterSpacing = 1.sp
            )
        }

        // Quality rating banner when completed
        if (speedTestState.phase == SpeedTestPhase.COMPLETED && speedTestState.finalResult != null) {
            Spacer(modifier = Modifier.height(16.dp))
            CyberCard(
                modifier = Modifier.fillMaxWidth(),
                borderColor = NeonEmerald.copy(alpha = 0.5f),
                backgroundColor = CyberSurfaceVariant
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .clip(CircleShape)
                                .background(NeonEmerald)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Connection Quality: Ultra Fast",
                            color = NeonEmerald,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Optimal bandwidth for 4K Ultra-HD streaming, lag-free competitive gaming, and rapid multi-gigabyte downloads.",
                        color = TextSecondary,
                        fontSize = 11.sp
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        // History Section
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = "History",
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = "Recent Test History",
                    color = TextPrimary,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )
            }
            if (speedTestHistory.isNotEmpty()) {
                IconButton(onClick = { viewModel.clearSpeedTestHistory() }) {
                    Icon(
                        imageVector = Icons.Default.DeleteOutline,
                        contentDescription = "Clear History",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        if (speedTestHistory.isEmpty()) {
            Text(
                text = "No speed test records yet. Tap Start Speed Test above.",
                color = TextMuted,
                fontSize = 12.sp,
                modifier = Modifier.padding(vertical = 12.dp)
            )
        } else {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                speedTestHistory.forEach { item ->
                    SpeedHistoryCard(item)
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SpeedMetricCard(
    icon: ImageVector,
    label: String,
    value: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    CyberCard(
        modifier = modifier,
        borderColor = CyberSurfaceBorder.copy(alpha = 0.4f),
        backgroundColor = CyberSurface
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = label,
                    color = TextMuted,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 0.5.sp
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = value,
                color = TextPrimary,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold
            )
        }
    }
}

@Composable
fun SpeedHistoryCard(result: SpeedTestResult) {
    val formatter = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    val dateStr = formatter.format(Date(result.timestamp))

    CyberCard(
        modifier = Modifier.fillMaxWidth(),
        borderColor = CyberSurfaceBorder.copy(alpha = 0.3f),
        backgroundColor = CyberSurface
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = result.serverName,
                    color = TextPrimary,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "$dateStr • Ping ${result.pingMs}ms",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }

            Row(horizontalArrangement = Arrangement.spacedBy(14.dp)) {
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "↓ ${result.downloadMbps}", color = NeonEmerald, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Mbps", color = TextMuted, fontSize = 9.sp)
                }
                Column(horizontalAlignment = Alignment.End) {
                    Text(text = "↑ ${result.uploadMbps}", color = NeonPurple, fontSize = 13.sp, fontWeight = FontWeight.Bold)
                    Text(text = "Mbps", color = TextMuted, fontSize = 9.sp)
                }
            }
        }
    }
}
