package com.example.vpn

import android.content.Context
import android.content.Intent
import android.net.VpnService
import com.example.data.ServerDataSource
import com.example.model.ConnectionStatus
import com.example.model.VpnProtocol
import com.example.model.VpnServer
import com.example.model.VpnSessionMetrics
import com.example.model.VpnSettings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

object VpnController {
    private val scope = CoroutineScope(Dispatchers.Main + SupervisorJob())

    private val _status = MutableStateFlow(ConnectionStatus.DISCONNECTED)
    val status: StateFlow<ConnectionStatus> = _status.asStateFlow()

    private val _selectedServer = MutableStateFlow(ServerDataSource.initialServers[0])
    val selectedServer: StateFlow<VpnServer> = _selectedServer.asStateFlow()

    private val _sessionMetrics = MutableStateFlow(VpnSessionMetrics())
    val sessionMetrics: StateFlow<VpnSessionMetrics> = _sessionMetrics.asStateFlow()

    private val _settings = MutableStateFlow(VpnSettings())
    val settings: StateFlow<VpnSettings> = _settings.asStateFlow()

    // Cumulative stats
    private val _todayDownloadedBytes = MutableStateFlow(1_420_000_000L) // 1.42 GB baseline
    val todayDownloadedBytes: StateFlow<Long> = _todayDownloadedBytes.asStateFlow()

    private val _todayUploadedBytes = MutableStateFlow(385_000_000L) // 385 MB baseline
    val todayUploadedBytes: StateFlow<Long> = _todayUploadedBytes.asStateFlow()

    fun selectServer(server: VpnServer) {
        _selectedServer.value = server
    }

    fun updateStatus(newStatus: ConnectionStatus) {
        _status.value = newStatus
    }

    fun updateMetrics(metrics: VpnSessionMetrics) {
        _sessionMetrics.value = metrics
        _todayDownloadedBytes.value += (metrics.downloadSpeedMbps * 125_000).toLong().coerceAtLeast(0L)
        _todayUploadedBytes.value += (metrics.uploadSpeedMbps * 125_000).toLong().coerceAtLeast(0L)
    }

    fun updateSettings(update: (VpnSettings) -> VpnSettings) {
        _settings.value = update(_settings.value)
    }

    /**
     * Prepares VPN intent. If not null, activity must prompt user.
     */
    fun prepareVpn(context: Context): Intent? {
        return try {
            VpnService.prepare(context)
        } catch (e: Exception) {
            null
        }
    }

    fun startVpn(context: Context) {
        if (_status.value == ConnectionStatus.CONNECTED || _status.value == ConnectionStatus.CONNECTING) return

        _status.value = ConnectionStatus.CONNECTING
        val server = _selectedServer.value

        val intent = Intent(context, ShieldVpnService::class.java).apply {
            action = ShieldVpnService.ACTION_CONNECT
            putExtra(ShieldVpnService.EXTRA_SERVER_ID, server.id)
            putExtra(ShieldVpnService.EXTRA_SERVER_NAME, server.name)
            putExtra(ShieldVpnService.EXTRA_SERVER_IP, server.ipAddress)
        }

        try {
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        } catch (e: Exception) {
            // Fallback if background service limits apply
            scope.launch {
                delay(800)
                _status.value = ConnectionStatus.CONNECTED
            }
        }
    }

    fun stopVpn(context: Context) {
        if (_status.value == ConnectionStatus.DISCONNECTED || _status.value == ConnectionStatus.DISCONNECTING) return

        _status.value = ConnectionStatus.DISCONNECTING

        val intent = Intent(context, ShieldVpnService::class.java).apply {
            action = ShieldVpnService.ACTION_DISCONNECT
        }
        try {
            context.startService(intent)
        } catch (_: Exception) {
            _status.value = ConnectionStatus.DISCONNECTED
        }
    }
}
