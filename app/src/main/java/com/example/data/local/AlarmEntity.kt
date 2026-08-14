package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "alarms")
data class AlarmEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hour: Int,
    val minute: Int,
    val label: String = "Alarm",
    val isEnabled: Boolean = true,
    val daysOfWeek: String = "DAILY", // "DAILY", "ONCE", "WEEKDAYS", "WEEKENDS", "MON,TUE,WED"
    val isVibrate: Boolean = true,
    val soundName: String = "Default Futuristic",
    val createdAt: Long = System.currentTimeMillis()
)
