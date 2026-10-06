package com.example.model

data class SpeedTestResult(
    val id: String,
    val timestamp: Long,
    val pingMs: Int,
    val jitterMs: Int,
    val downloadMbps: Float,
    val uploadMbps: Float,
    val serverName: String,
    val ipAddress: String
)

enum class SpeedTestPhase {
    IDLE,
    PING,
    DOWNLOAD,
    UPLOAD,
    COMPLETED
}
