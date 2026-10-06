package com.example.ui

import android.content.Context
import android.content.Intent
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Dns
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ConnectionStatus
import com.example.ui.components.CyberCard
import com.example.ui.components.LatencyBadge
import com.example.ui.components.OneTapVpnButton
import com.example.ui.theme.CyberBackground
import com.example.ui.theme.CyberSurface
import com.example.ui.theme.CyberSurfaceBorder
import com.example.ui.theme.CyberSurfaceElevated
import com.example.ui.theme.CyberSurfaceVariant
import com.example.ui.theme.NeonAmber
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.NeonEmerald
import com.example.ui.theme.NeonPurple
import com.example.ui.theme.StatusConnected
import com.example.ui.theme.StatusConnecting
import com.example.ui.theme.StatusDisconnected
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.VpnViewModel

@Composable
fun HomeScreen(
    viewModel: VpnViewModel,
    onNavigateToServers: () -> Unit,
    onNavigateToSpeedTest: () -> Unit,
    onNavigateToData: () -> Unit,
    onRequireVpnPermission: (Intent) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val status by viewModel.connectionStatus.collectAsState()
    val server by viewModel.selectedServer.collectAsState()
    val metrics by viewModel.sessionMetrics.collectAsState()
    val settings by viewModel.settings.collectAsState()

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(CyberBackground)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 12.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // App Header Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp, bottom = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(NeonCyan.copy(alpha = 0.2f))
                        .border(1.dp, NeonCyan, RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Shield,
                        contentDescription = "ShieldVPN Logo",
                        tint = NeonCyan,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "ShieldVPN",
                        color = TextPrimary,
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 0.5.sp
                    )
                    Text(
                        text = "Unlimited Free Protection",
                        color = NeonCyan,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            // Connection Status Pill
            StatusIndicatorPill(status = status)
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Center One-Tap Connection Button
        OneTapVpnButton(
            status = status,
            onClick = {
                viewModel.toggleConnection(context, onRequireVpnPermission)
            }
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Connection Timer & Live Speeds
        if (status == ConnectionStatus.CONNECTED) {
            val formattedDuration = String.format(
                "%02d:%02d:%02d",
                metrics.durationSeconds / 3600,
                (metrics.durationSeconds % 3600) / 60,
                metrics.durationSeconds % 60
            )

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = "CONNECTED DURATION",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 1.sp
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = formattedDuration,
                    color = StatusConnected,
                    fontSize = 26.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 1.sp
                )

                Spacer(modifier = Modifier.height(12.dp))

                // Real-Time Speed Chips (Live hardware network telemetry)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    SpeedChip(
                        icon = Icons.Default.ArrowDownward,
                        label = "DOWNLOAD",
                        speed = metrics.downloadSpeedFormatted,
                        tint = NeonCyan,
                        modifier = Modifier.weight(1f)
                    )
                    SpeedChip(
                        icon = Icons.Default.ArrowUpward,
                        label = "UPLOAD",
                        speed = metrics.uploadSpeedFormatted,
                        tint = NeonPurple,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        } else {
            Text(
                text = when (status) {
                    ConnectionStatus.CONNECTING -> "Establishing secure 256-bit tunnel..."
                    ConnectionStatus.DISCONNECTING -> "Disconnecting securely..."
                    ConnectionStatus.DISCONNECTED -> "Tap the power button to encrypt all device traffic"
                    ConnectionStatus.CONNECTED -> "Protected by ShieldVPN"
                },
                color = TextSecondary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Normal
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        // Selected Server Card
        CyberCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToServers() }
                .testTag("selected_server_card"),
            borderColor = if (status == ConnectionStatus.CONNECTED) NeonEmerald.copy(alpha = 0.5f) else CyberSurfaceBorder
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = server.flagEmoji,
                        fontSize = 32.sp,
                        modifier = Modifier.padding(end = 12.dp)
                    )
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = server.name,
                                color = TextPrimary,
                                fontSize = 14.sp,
                                fontWeight = FontWeight.Bold,
                                maxLines = 1
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(4.dp))
                                    .background(NeonCyan.copy(alpha = 0.2f))
                                    .padding(horizontal = 5.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "FREE",
                                    color = NeonCyan,
                                    fontSize = 9.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Text(
                                text = "${server.city}, ${server.country}",
                                color = TextSecondary,
                                fontSize = 12.sp,
                                maxLines = 1,
                                modifier = Modifier.weight(1f, fill = false)
                            )
                            LatencyBadge(pingMs = server.pingMs)
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                Button(
                    onClick = onNavigateToServers,
                    colors = ButtonDefaults.buttonColors(
                        containerColor = CyberSurfaceElevated,
                        contentColor = NeonCyan
                    ),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier.height(36.dp)
                ) {
                    Text(text = "Change", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Quick Security & Network Info Grid
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecurityGridItem(
                icon = Icons.Default.Public,
                label = "Virtual IP",
                value = if (status == ConnectionStatus.CONNECTED) metrics.virtualIp else "Protected on Connect",
                modifier = Modifier.weight(1f)
            )
            SecurityGridItem(
                icon = Icons.Default.Lock,
                label = "Encryption",
                value = "AES-256-GCM",
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(10.dp))

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            SecurityGridItem(
                icon = Icons.Default.Dns,
                label = "Private DNS",
                value = if (settings.dnsLeakProtection) "Leak Proof" else "Standard",
                modifier = Modifier.weight(1f)
            )
            SecurityGridItem(
                icon = Icons.Default.Bolt,
                label = "Protocol",
                value = settings.selectedProtocol.displayName,
                modifier = Modifier.weight(1f)
            )
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Unlimited Data Card & Quick Speed Test Action
        CyberCard(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToData() }
                .testTag("unlimited_data_banner"),
            backgroundColor = CyberSurfaceVariant
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(NeonCyan.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Unlimited Data",
                            tint = NeonCyan,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Unlimited Free Data",
                            color = TextPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "0 Bandwidth limits • 100% No-Log Policy",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                    contentDescription = "View Data Stats",
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun StatusIndicatorPill(status: ConnectionStatus) {
    val (dotColor, text) = when (status) {
        ConnectionStatus.CONNECTED -> Pair(StatusConnected, "PROTECTED")
        ConnectionStatus.CONNECTING -> Pair(StatusConnecting, "CONNECTING")
        ConnectionStatus.DISCONNECTING -> Pair(StatusConnecting, "DISCONNECTING")
        ConnectionStatus.DISCONNECTED -> Pair(StatusDisconnected, "UNPROTECTED")
    }

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(20.dp))
            .background(dotColor.copy(alpha = 0.15f))
            .border(1.dp, dotColor.copy(alpha = 0.5f), RoundedCornerShape(20.dp))
            .padding(horizontal = 10.dp, vertical = 5.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(7.dp)
                .clip(CircleShape)
                .background(dotColor)
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = text,
            color = dotColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
fun SpeedChip(
    icon: ImageVector,
    label: String,
    speed: String,
    tint: Color,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .background(CyberSurface)
            .border(1.dp, tint.copy(alpha = 0.3f), RoundedCornerShape(12.dp))
            .padding(10.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(tint.copy(alpha = 0.15f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = tint,
                    modifier = Modifier.size(16.dp)
                )
            }
            Spacer(modifier = Modifier.width(8.dp))
            Column {
                Text(text = label, color = TextMuted, fontSize = 9.sp, fontWeight = FontWeight.Bold)
                Text(text = speed, color = TextPrimary, fontSize = 13.sp, fontWeight = FontWeight.ExtraBold)
            }
        }
    }
}

@Composable
fun SecurityGridItem(
    icon: ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    CyberCard(
        modifier = modifier,
        borderColor = CyberSurfaceBorder.copy(alpha = 0.4f),
        backgroundColor = CyberSurface
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(RoundedCornerShape(8.dp))
                    .background(NeonCyan.copy(alpha = 0.12f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = label,
                    tint = NeonCyan,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(text = label, color = TextMuted, fontSize = 10.sp, fontWeight = FontWeight.Medium)
                Text(text = value, color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}
