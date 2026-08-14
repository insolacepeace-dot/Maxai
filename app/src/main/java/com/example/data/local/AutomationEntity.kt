package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "automations")
data class AutomationEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val triggerType: String, // "CHARGING_STARTED", "TIME_OF_DAY", "HEADPHONES_CONNECTED", "BATTERY_LOW"
    val triggerCondition: String, // e.g. "07:00 AM", "true", "20%"
    val actionType: String, // "SPEAK_TEXT", "OPEN_APP", "TOGGLE_TORCH", "SET_VOLUME"
    val actionPayload: String, // e.g. "Good morning Boss!", "com.spotify.music"
    val isEnabled: Boolean = true,
    val lastTriggeredTime: Long = 0
)
