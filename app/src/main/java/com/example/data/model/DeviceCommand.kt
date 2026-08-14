package com.example.data.model

enum class CommandType {
    OPEN_APP,
    READ_NOTIFICATION,
    REPLY_NOTIFICATION,
    TOGGLE_TORCH,
    VOLUME_UP,
    VOLUME_DOWN,
    SET_ALARM,
    OPEN_SETTINGS,
    OPEN_WIFI_SETTINGS,
    OPEN_BLUETOOTH_SETTINGS,
    OPEN_CAMERA,
    NAVIGATE_BACK,
    NAVIGATE_HOME,
    OPEN_RECENTS,
    MAKE_CALL,
    DRAFT_WHATSAPP,
    PLAY_MUSIC,
    PAUSE_MUSIC,
    NEXT_TRACK,
    PREV_TRACK,
    ANALYZE_SCREEN,
    GET_WEATHER,
    CONVERSATION_ONLY,
    UNKNOWN
}

data class DeviceCommand(
    val type: CommandType,
    val rawQuery: String,
    val targetApp: String? = null,
    val contactName: String? = null,
    val messageBody: String? = null,
    val hour: Int? = null,
    val minute: Int? = null,
    val replyText: String? = null,
    val confidence: Float = 1.0f,
    val confirmationRequired: Boolean = false,
    val conversationalReply: String = ""
)
