package com.example.service

import android.accessibilityservice.AccessibilityService
import android.view.accessibility.AccessibilityEvent
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class TarunAccessibilityService : AccessibilityService() {

    companion object {
        private var instance: TarunAccessibilityService? = null

        private val _isServiceEnabled = MutableStateFlow(false)
        val isServiceEnabled: StateFlow<Boolean> = _isServiceEnabled.asStateFlow()

        fun performGlobalAction(action: Int): Boolean {
            return instance?.performGlobalAction(action) ?: false
        }

        fun performBack(): Boolean {
            return performGlobalAction(GLOBAL_ACTION_BACK)
        }

        fun performHome(): Boolean {
            return performGlobalAction(GLOBAL_ACTION_HOME)
        }

        fun performRecents(): Boolean {
            return performGlobalAction(GLOBAL_ACTION_RECENTS)
        }

        fun isConnected(): Boolean = instance != null
    }

    override fun onServiceConnected() {
        super.onServiceConnected()
        instance = this
        _isServiceEnabled.value = true
    }

    override fun onAccessibilityEvent(event: AccessibilityEvent?) {
        // Safe accessibility events
    }

    override fun onInterrupt() {
        // Interrupted
    }

    override fun onDestroy() {
        super.onDestroy()
        instance = null
        _isServiceEnabled.value = false
    }
}
