package com.example.engine

import com.example.data.model.CommandType
import com.example.data.model.DeviceCommand

class ActionValidator(private val permissionManager: PermissionManager) {

    sealed class ValidationResult {
        object Allowed : ValidationResult()
        data class PermissionRequired(val permissionName: String, val openAction: () -> Unit) : ValidationResult()
        data class Denied(val reason: String) : ValidationResult()
    }

    fun validate(command: DeviceCommand): ValidationResult {
        return when (command.type) {
            CommandType.READ_NOTIFICATION, CommandType.REPLY_NOTIFICATION -> {
                if (!permissionManager.isNotificationAccessGranted()) {
                    ValidationResult.PermissionRequired("Notification Access") {
                        permissionManager.openNotificationListenerSettings()
                    }
                } else {
                    ValidationResult.Allowed
                }
            }

            CommandType.NAVIGATE_BACK, CommandType.NAVIGATE_HOME, CommandType.OPEN_RECENTS -> {
                if (!permissionManager.isAccessibilityServiceEnabled()) {
                    ValidationResult.PermissionRequired("Accessibility Service") {
                        permissionManager.openAccessibilitySettings()
                    }
                } else {
                    ValidationResult.Allowed
                }
            }

            CommandType.MAKE_CALL -> {
                if (!permissionManager.isContactsPermissionGranted()) {
                    ValidationResult.PermissionRequired("Contacts Permission") {
                        permissionManager.openAppSettings()
                    }
                } else {
                    ValidationResult.Allowed
                }
            }

            CommandType.OPEN_APP, CommandType.TOGGLE_TORCH, CommandType.VOLUME_UP,
            CommandType.VOLUME_DOWN, CommandType.SET_ALARM, CommandType.OPEN_SETTINGS,
            CommandType.OPEN_WIFI_SETTINGS, CommandType.OPEN_BLUETOOTH_SETTINGS,
            CommandType.OPEN_CAMERA, CommandType.CONVERSATION_ONLY, CommandType.DRAFT_WHATSAPP,
            CommandType.PLAY_MUSIC, CommandType.PAUSE_MUSIC, CommandType.NEXT_TRACK,
            CommandType.PREV_TRACK, CommandType.ANALYZE_SCREEN, CommandType.GET_WEATHER -> {
                ValidationResult.Allowed
            }

            CommandType.UNKNOWN -> ValidationResult.Denied("Unsupported command.")
        }
    }
}
