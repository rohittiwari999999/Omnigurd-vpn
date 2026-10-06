package com.example.model

enum class ConnectionStatus {
    DISCONNECTED,
    CONNECTING,
    CONNECTED,
    DISCONNECTING
}

data class VpnSessionMetrics(
    val durationSeconds: Long = 0,
    val downloadSpeedMbps: Float = 0f,
    val uploadSpeedMbps: Float = 0f,
    val downloadSpeedFormatted: String = "0.0 KB/s",
    val uploadSpeedFormatted: String = "0.0 KB/s",
    val sessionDownloadedBytes: Long = 0,
    val sessionUploadedBytes: Long = 0,
    val virtualIp: String = "192.168.1.1",
    val encryptionType: String = "AES-256-GCM / ChaCha20"
)

data class VpnSettings(
    val killSwitchEnabled: Boolean = false,
    val dnsLeakProtection: Boolean = true,
    val selectedProtocol: VpnProtocol = VpnProtocol.WIREGUARD,
    val autoConnectOnWifi: Boolean = true,
    val splitTunnelingEnabled: Boolean = false,
    val bypassedPackages: Set<String> = emptySet()
)
