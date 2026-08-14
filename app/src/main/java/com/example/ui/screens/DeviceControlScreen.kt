package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bluetooth
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.FlashlightOn
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Undo
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material.icons.filled.VolumeDown
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material.icons.filled.Wifi
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CommandType
import com.example.ui.components.GlassCard
import com.example.ui.components.HapticFeedbackHelper
import com.example.ui.components.ParticleBackground
import com.example.ui.theme.CorePulseViolet
import com.example.ui.theme.ElectricBlue
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.SpaceDarkBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.TarunViewModel

private data class ControlItem(
    val icon: ImageVector,
    val title: String,
    val subtitle: String,
    val commandType: CommandType,
    val voicePrompt: String,
    val accentColor: Color
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DeviceControlScreen(
    viewModel: TarunViewModel,
    onBack: () -> Unit
) {
    val context = LocalContext.current

    val controls = listOf(
        ControlItem(Icons.Default.FlashlightOn, "Flashlight / Torch", "Toggle hardware torch", CommandType.TOGGLE_TORCH, "Torch on karo", NeonCyan),
        ControlItem(Icons.Default.VolumeUp, "Volume Up", "Increase media volume", CommandType.VOLUME_UP, "Volume badhao", HologramTeal),
        ControlItem(Icons.Default.VolumeDown, "Volume Down", "Decrease media volume", CommandType.VOLUME_DOWN, "Volume kam karo", HologramTeal),
        ControlItem(Icons.Default.Wifi, "Wi-Fi Settings", "Open Wi-Fi panel", CommandType.OPEN_WIFI_SETTINGS, "WiFi settings kholo", ElectricBlue),
        ControlItem(Icons.Default.Bluetooth, "Bluetooth Settings", "Open Bluetooth panel", CommandType.OPEN_BLUETOOTH_SETTINGS, "Bluetooth settings kholo", ElectricBlue),
        ControlItem(Icons.Default.CameraAlt, "Camera", "Launch system camera", CommandType.OPEN_CAMERA, "Camera open karo", CorePulseViolet),
        ControlItem(Icons.Default.Alarm, "Set Alarm", "Set 7:00 AM Alarm", CommandType.SET_ALARM, "7 baje ka alarm lagao", NeonCyan),
        ControlItem(Icons.Default.Undo, "Back Action", "Accessibility back gesture", CommandType.NAVIGATE_BACK, "Peeche jao", HologramTeal),
        ControlItem(Icons.Default.Home, "Home Screen", "Go to device launcher", CommandType.NAVIGATE_HOME, "Home screen par jao", ElectricBlue),
        ControlItem(Icons.Default.ViewCarousel, "Recent Tasks", "Open recent apps", CommandType.OPEN_RECENTS, "Recent tasks dikhao", CorePulseViolet),
        ControlItem(Icons.Default.Phone, "Phone Dialer", "Open dialer", CommandType.MAKE_CALL, "Phone dialer kholo", NeonCyan),
        ControlItem(Icons.Default.Settings, "Device Settings", "Open Android settings", CommandType.OPEN_SETTINGS, "Settings kholo", ElectricBlue)
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "DEVICE CONTROL CENTER",
                        color = NeonCyan,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp,
                        fontFamily = FontFamily.Monospace
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = NeonCyan
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SpaceDarkBackground)
            )
        },
        containerColor = SpaceDarkBackground,
        modifier = Modifier.testTag("device_control_screen")
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF0A1424),
                            SpaceDarkBackground
                        )
                    )
                )
        ) {
            ParticleBackground(particleCount = 20)

            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(controls.size) { index ->
                    val item = controls[index]
                    DeviceControlCard(
                        item = item,
                        onClick = {
                            HapticFeedbackHelper.performClickHaptic(context)
                            viewModel.triggerQuickCommand(item.commandType, item.voicePrompt)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun DeviceControlCard(
    item: ControlItem,
    onClick: () -> Unit
) {
    GlassCard(
        modifier = Modifier.fillMaxWidth(),
        borderGlow = true,
        contentPadding = 12.dp
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(44.dp)
                    .background(item.accentColor.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = item.icon,
                    contentDescription = item.title,
                    tint = item.accentColor,
                    modifier = Modifier.size(24.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = item.title,
                color = TextPrimary,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center
            )

            Text(
                text = item.subtitle,
                color = TextSecondary,
                fontSize = 10.sp,
                textAlign = TextAlign.Center,
                lineHeight = 13.sp
            )

            Spacer(modifier = Modifier.height(10.dp))

            Button(
                onClick = onClick,
                modifier = Modifier.fillMaxWidth().height(36.dp),
                colors = ButtonDefaults.buttonColors(containerColor = item.accentColor.copy(alpha = 0.25f))
            ) {
                Text(
                    text = "EXECUTE",
                    color = item.accentColor,
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    fontFamily = FontFamily.Monospace
                )
            }
        }
    }
}
