package com.example.ui.navigation

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.data.model.AssistantState
import com.example.ui.components.FuturisticOrb
import com.example.ui.screens.AboutScreen
import com.example.ui.screens.AppSettingsScreen
import com.example.ui.screens.AutomationScreen
import com.example.ui.screens.BuildExportScreen
import com.example.ui.screens.ChatScreen
import com.example.ui.screens.ConversationHistoryScreen
import com.example.ui.screens.CreativityScreen
import com.example.ui.screens.DeveloperModeScreen
import com.example.ui.screens.DeviceControlScreen
import com.example.ui.screens.GeminiSettingsScreen
import com.example.ui.screens.MainAiScreen
import com.example.ui.screens.MemoryScreen
import com.example.ui.screens.NotificationCenterScreen
import com.example.ui.screens.OnboardingScreen
import com.example.ui.screens.PermissionCenterScreen
import com.example.ui.screens.PrivacyScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.screens.RoutinesScreen
import com.example.ui.screens.SkillsScreen
import com.example.ui.screens.SplashScreen
import com.example.ui.screens.VoiceSettingsScreen
import com.example.ui.screens.WhatsAppAgentScreen
import com.example.ui.theme.AlertRed
import com.example.ui.theme.Blue600
import com.example.ui.theme.CorePulseViolet
import com.example.ui.theme.Cyan400
import com.example.ui.theme.Cyan500
import com.example.ui.theme.DeepSapphire
import com.example.ui.theme.GlassBorder
import com.example.ui.theme.GlassSurfaceDark
import com.example.ui.theme.GlassWhite10
import com.example.ui.theme.GlassWhite5
import com.example.ui.theme.HologramTeal
import com.example.ui.theme.NeonCyan
import com.example.ui.theme.Slate100
import com.example.ui.theme.Slate400
import com.example.ui.theme.Slate500
import com.example.ui.theme.SpaceDarkBackground
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VoidBlack
import com.example.viewmodel.TarunViewModel
import kotlinx.coroutines.launch

@Composable
fun TarunNavHost(
    viewModel: TarunViewModel,
    navController: NavHostController = rememberNavController()
) {
    val appSettings by viewModel.appSettings.collectAsState()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    val isTopLevelScreen = PrimaryBottomNavScreens.any { it.route == currentRoute }
    val showBottomBar = isTopLevelScreen && currentRoute != TarunScreen.Splash.route && currentRoute != TarunScreen.Onboarding.route
    val isDrawerEnabled = currentRoute != TarunScreen.Splash.route && currentRoute != TarunScreen.Onboarding.route

    ModalNavigationDrawer(
        drawerState = drawerState,
        gesturesEnabled = isDrawerEnabled,
        drawerContent = {
            if (isDrawerEnabled) {
                ModalDrawerSheet(
                    drawerContainerColor = VoidBlack,
                    drawerShape = RoundedCornerShape(topEnd = 24.dp, bottomEnd = 24.dp),
                    modifier = Modifier.width(300.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color(0xFF0C1322),
                                        VoidBlack
                                    )
                                )
                            )
                            .padding(20.dp)
                            .verticalScroll(rememberScrollState())
                    ) {
                        // Drawer Header
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            FuturisticOrb(
                                state = AssistantState.SPEAKING,
                                audioAmplitude = 0.2f,
                                size = 48.dp
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = "TARUN AI",
                                    color = Cyan500,
                                    fontSize = 18.sp,
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 2.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                                Text(
                                    text = "Boss: ${appSettings.bossTitle}",
                                    color = Slate400,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))
                        HorizontalDivider(color = GlassWhite10)
                        Spacer(modifier = Modifier.height(12.dp))

                        DrawerScreens.forEach { screen ->
                            val isSelected = currentRoute == screen.route
                            NavigationDrawerItem(
                                icon = {
                                    if (screen.icon != null) {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title,
                                            tint = if (isSelected) VoidBlack else Cyan500
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 13.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                        color = if (isSelected) VoidBlack else Slate100
                                    )
                                },
                                selected = isSelected,
                                onClick = {
                                    scope.launch { drawerState.close() }
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(TarunScreen.MainAi.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                shape = RoundedCornerShape(14.dp),
                                colors = NavigationDrawerItemDefaults.colors(
                                    selectedContainerColor = Cyan500,
                                    unselectedContainerColor = Color.Transparent
                                ),
                                modifier = Modifier.padding(vertical = 2.dp)
                            )
                        }
                    }
                }
            }
        }
    ) {
        Scaffold(
            bottomBar = {
                if (showBottomBar) {
                    NavigationBar(
                        containerColor = GlassSurfaceDark,
                        contentColor = NeonCyan,
                        tonalElevation = 8.dp
                    ) {
                        PrimaryBottomNavScreens.forEach { screen ->
                            val selected = currentRoute == screen.route
                            NavigationBarItem(
                                icon = {
                                    if (screen.icon != null) {
                                        Icon(
                                            imageVector = screen.icon,
                                            contentDescription = screen.title,
                                            tint = if (selected) NeonCyan else Slate400
                                        )
                                    }
                                },
                                label = {
                                    Text(
                                        text = screen.title,
                                        fontSize = 10.sp,
                                        fontWeight = if (selected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (selected) NeonCyan else Slate400
                                    )
                                },
                                selected = selected,
                                onClick = {
                                    if (currentRoute != screen.route) {
                                        navController.navigate(screen.route) {
                                            popUpTo(TarunScreen.MainAi.route) {
                                                saveState = true
                                            }
                                            launchSingleTop = true
                                            restoreState = true
                                        }
                                    }
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    selectedIconColor = NeonCyan,
                                    selectedTextColor = NeonCyan,
                                    indicatorColor = GlassWhite10,
                                    unselectedIconColor = Slate400,
                                    unselectedTextColor = Slate400
                                )
                            )
                        }
                    }
                }
            },
            containerColor = SpaceDarkBackground
        ) { innerPadding ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                NavHost(
                    navController = navController,
                    startDestination = TarunScreen.Splash.route
                ) {
                    composable(TarunScreen.Splash.route) {
                        SplashScreen(
                            onSplashFinished = {
                                val destination = if (appSettings.onboarded) TarunScreen.MainAi.route else TarunScreen.Onboarding.route
                                navController.navigate(destination) {
                                    popUpTo(TarunScreen.Splash.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    composable(TarunScreen.Onboarding.route) {
                        OnboardingScreen(
                            viewModel = viewModel,
                            onFinish = {
                                navController.navigate(TarunScreen.MainAi.route) {
                                    popUpTo(TarunScreen.Onboarding.route) { inclusive = true }
                                }
                            }
                        )
                    }

                    // 6 Primary Hubs
                    composable(TarunScreen.MainAi.route) {
                        MainAiScreen(
                            viewModel = viewModel,
                            onOpenDrawer = { scope.launch { drawerState.open() } },
                            onNavigateToHistory = { navController.navigate(TarunScreen.History.route) }
                        )
                    }

                    composable(TarunScreen.Chat.route) {
                        ChatScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.navigate(TarunScreen.MainAi.route) }
                        )
                    }

                    composable(TarunScreen.WhatsAppAgent.route) {
                        WhatsAppAgentScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.navigate(TarunScreen.MainAi.route) }
                        )
                    }

                    composable(TarunScreen.Routines.route) {
                        RoutinesScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.navigate(TarunScreen.MainAi.route) }
                        )
                    }

                    composable(TarunScreen.Creativity.route) {
                        CreativityScreen(
                            viewModel = viewModel,
                            onNavigateBack = { navController.navigate(TarunScreen.MainAi.route) }
                        )
                    }

                    composable(TarunScreen.Profile.route) {
                        ProfileScreen(
                            viewModel = viewModel,
                            onNavigateToGemini = { navController.navigate(TarunScreen.GeminiSettings.route) },
                            onNavigateToVoice = { navController.navigate(TarunScreen.VoiceSettings.route) },
                            onNavigateToPermissions = { navController.navigate(TarunScreen.PermissionCenter.route) },
                            onNavigateToMemory = { navController.navigate(TarunScreen.Memory.route) },
                            onNavigateToExport = { navController.navigate(TarunScreen.BuildExport.route) },
                            onNavigateToPrivacy = { navController.navigate(TarunScreen.Privacy.route) },
                            onNavigateToDeveloper = { navController.navigate(TarunScreen.DeveloperMode.route) },
                            onNavigateToAbout = { navController.navigate(TarunScreen.About.route) },
                            onNavigateBack = { navController.navigate(TarunScreen.MainAi.route) }
                        )
                    }

                    // Secondary Sub-Screens
                    composable(TarunScreen.DeviceControl.route) {
                        DeviceControlScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.History.route) {
                        ConversationHistoryScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.NotificationCenter.route) {
                        NotificationCenterScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.Skills.route) {
                        SkillsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.Memory.route) {
                        MemoryScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.Automation.route) {
                        AutomationScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.DeveloperMode.route) {
                        DeveloperModeScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.PermissionCenter.route) {
                        PermissionCenterScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.VoiceSettings.route) {
                        VoiceSettingsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.GeminiSettings.route) {
                        GeminiSettingsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.AppSettings.route) {
                        AppSettingsScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.Privacy.route) {
                        PrivacyScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.BuildExport.route) {
                        BuildExportScreen(
                            viewModel = viewModel,
                            onBack = { navController.popBackStack() }
                        )
                    }

                    composable(TarunScreen.About.route) {
                        AboutScreen(
                            onBack = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}
