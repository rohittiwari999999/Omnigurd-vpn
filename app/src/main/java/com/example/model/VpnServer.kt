package com.example.model

enum class ServerRegion(val displayName: String) {
    ALL("All Regions"),
    EUROPE("Europe"),
    AMERICAS("Americas"),
    ASIA_PACIFIC("Asia Pacific")
}

enum class VpnProtocol(val displayName: String, val description: String) {
    WIREGUARD("WireGuard", "Fastest & modern crypto (Recommended)"),
    OPENVPN_UDP("OpenVPN (UDP)", "High speed streaming & gaming"),
    OPENVPN_TCP("OpenVPN (TCP)", "Reliable firewall bypass"),
    STEALTH("Stealth Camouflage", "Obfuscated anti-censorship tunnel")
}

data class VpnServer(
    val id: String,
    val name: String,
    val country: String,
    val city: String,
    val countryCode: String,
    val flagEmoji: String,
    val ipAddress: String,
    val pingMs: Int,
    val loadPercent: Int,
    val region: ServerRegion,
    val protocol: VpnProtocol = VpnProtocol.WIREGUARD,
    val isFree: Boolean = true,
    val isFavorite: Boolean = false,
    val features: List<String> = listOf("P2P Allowed", "Streaming", "No Logs")
)
