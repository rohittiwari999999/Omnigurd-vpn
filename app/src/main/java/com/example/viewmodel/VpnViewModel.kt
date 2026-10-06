package com.example.viewmodel

import android.content.Context
import android.content.Intent
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.ServerDataSource
import com.example.data.SpeedTestEngine
import com.example.data.SpeedTestProgress
import com.example.model.ConnectionStatus
import com.example.model.ServerRegion
import com.example.model.SpeedTestPhase
import com.example.model.SpeedTestResult
import com.example.model.VpnProtocol
import com.example.model.VpnServer
import com.example.model.VpnSessionMetrics
import com.example.model.VpnSettings
import com.example.vpn.VpnController
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class VpnViewModel : ViewModel() {

    private val speedTestEngine = SpeedTestEngine()

    // State from VpnController
    val connectionStatus: StateFlow<ConnectionStatus> = VpnController.status
    val selectedServer: StateFlow<VpnServer> = VpnController.selectedServer
    val sessionMetrics: StateFlow<VpnSessionMetrics> = VpnController.sessionMetrics
    val settings: StateFlow<VpnSettings> = VpnController.settings
    val todayDownloadedBytes: StateFlow<Long> = VpnController.todayDownloadedBytes
    val todayUploadedBytes: StateFlow<Long> = VpnController.todayUploadedBytes

    // Server list state
    private val _servers = MutableStateFlow<List<VpnServer>>(ServerDataSource.initialServers)
    val servers: StateFlow<List<VpnServer>> = _servers.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _selectedRegion = MutableStateFlow(ServerRegion.ALL)
    val selectedRegion: StateFlow<ServerRegion> = _selectedRegion.asStateFlow()

    private val _isRefreshingPings = MutableStateFlow(false)
    val isRefreshingPings: StateFlow<Boolean> = _isRefreshingPings.asStateFlow()

    // Filtered server list
    val filteredServers: StateFlow<List<VpnServer>> = combine(
        _servers,
        _searchQuery,
        _selectedRegion
    ) { serverList, query, region ->
        serverList.filter { server ->
            val matchesRegion = region == ServerRegion.ALL || server.region == region
            val matchesQuery = query.isBlank() ||
                server.name.contains(query, ignoreCase = true) ||
                server.country.contains(query, ignoreCase = true) ||
                server.city.contains(query, ignoreCase = true)
            matchesRegion && matchesQuery
        }.sortedWith(
            compareByDescending<VpnServer> { it.isFavorite }
                .thenBy { it.pingMs }
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), ServerDataSource.initialServers)

    // Speed test state
    private val _speedTestState = MutableStateFlow(SpeedTestProgress(phase = SpeedTestPhase.IDLE))
    val speedTestState: StateFlow<SpeedTestProgress> = _speedTestState.asStateFlow()

    private val _speedTestHistory = MutableStateFlow<List<SpeedTestResult>>(
        listOf(
            SpeedTestResult(
                id = "history-1",
                timestamp = System.currentTimeMillis() - 3600_000 * 2,
                pingMs = 21,
                jitterMs = 2,
                downloadMbps = 94.6f,
                uploadMbps = 42.1f,
                serverName = "Germany (Frankfurt #1)",
                ipAddress = "185.220.101.5"
            ),
            SpeedTestResult(
                id = "history-2",
                timestamp = System.currentTimeMillis() - 3600_000 * 24,
                pingMs = 25,
                jitterMs = 3,
                downloadMbps = 88.2f,
                uploadMbps = 36.4f,
                serverName = "US East (New York #1)",
                ipAddress = "198.51.100.42"
            )
        )
    )
    val speedTestHistory: StateFlow<List<SpeedTestResult>> = _speedTestHistory.asStateFlow()

    private var speedTestJob: Job? = null

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun onRegionSelected(region: ServerRegion) {
        _selectedRegion.value = region
    }

    fun toggleFavorite(serverId: String) {
        _servers.value = _servers.value.map {
            if (it.id == serverId) it.copy(isFavorite = !it.isFavorite) else it
        }
    }

    fun selectServer(server: VpnServer) {
        VpnController.selectServer(server)
    }

    fun smartConnect(context: Context, onRequireVpnPermission: (Intent) -> Unit) {
        // Pick best server (lowest ping with lowest load)
        val bestServer = _servers.value.minByOrNull { it.pingMs * 0.6 + it.loadPercent * 0.4 }
            ?: _servers.value.first()
        selectServer(bestServer)
        toggleConnection(context, onRequireVpnPermission)
    }

    init {
        viewModelScope.launch {
            refreshServerPings()
        }
    }

    fun refreshServerPings() {
        if (_isRefreshingPings.value) return
        viewModelScope.launch {
            _isRefreshingPings.value = true
            val liveServers = ServerDataSource.fetchLiveVpnGateServers()
            val baseList = if (liveServers.isNotEmpty()) {
                (liveServers + _servers.value).distinctBy { it.ipAddress }
            } else {
                _servers.value
            }

            val updated = baseList.map { server ->
                val newPing = ServerDataSource.probeServerPing(server)
                server.copy(pingMs = newPing)
            }
            _servers.value = updated
            _isRefreshingPings.value = false
        }
    }

    /**
     * One-tap Connect button action
     */
    fun toggleConnection(context: Context, onRequireVpnPermission: (Intent) -> Unit) {
        when (connectionStatus.value) {
            ConnectionStatus.DISCONNECTED -> {
                val prepareIntent = VpnController.prepareVpn(context)
                if (prepareIntent != null) {
                    onRequireVpnPermission(prepareIntent)
                } else {
                    VpnController.startVpn(context)
                }
            }
            ConnectionStatus.CONNECTED -> {
                VpnController.stopVpn(context)
            }
            ConnectionStatus.CONNECTING -> {
                VpnController.stopVpn(context)
            }
            ConnectionStatus.DISCONNECTING -> {
                // wait for disconnection
            }
        }
    }

    fun onVpnPermissionGranted(context: Context) {
        VpnController.startVpn(context)
    }

    fun startSpeedTest() {
        if (speedTestJob?.isActive == true) return
        val currentServer = selectedServer.value

        speedTestJob = viewModelScope.launch {
            speedTestEngine.runSpeedTest(currentServer.name, currentServer.ipAddress).collect { progress ->
                _speedTestState.value = progress
                if (progress.phase == SpeedTestPhase.COMPLETED && progress.finalResult != null) {
                    _speedTestHistory.value = listOf(progress.finalResult) + _speedTestHistory.value.take(9)
                }
            }
        }
    }

    fun resetSpeedTest() {
        speedTestJob?.cancel()
        _speedTestState.value = SpeedTestProgress(phase = SpeedTestPhase.IDLE)
    }

    fun clearSpeedTestHistory() {
        _speedTestHistory.value = emptyList()
    }

    // Settings actions
    fun setKillSwitch(enabled: Boolean) {
        VpnController.updateSettings { it.copy(killSwitchEnabled = enabled) }
    }

    fun setDnsLeakProtection(enabled: Boolean) {
        VpnController.updateSettings { it.copy(dnsLeakProtection = enabled) }
    }

    fun setProtocol(protocol: VpnProtocol) {
        VpnController.updateSettings { it.copy(selectedProtocol = protocol) }
    }

    fun setAutoConnectOnWifi(enabled: Boolean) {
        VpnController.updateSettings { it.copy(autoConnectOnWifi = enabled) }
    }

    fun setSplitTunneling(enabled: Boolean) {
        VpnController.updateSettings { it.copy(splitTunnelingEnabled = enabled) }
    }
}
