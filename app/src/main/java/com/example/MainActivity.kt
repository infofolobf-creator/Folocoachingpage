package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.CatalogScreen
import com.example.ui.screens.CourseDetailScreen
import com.example.ui.screens.ToolsScreen
import com.example.ui.screens.ProfileScreen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.FoloViewModel

sealed class Screen {
    object Home : Screen()
    object Catalog : Screen()
    data class CourseDetail(val moduleId: String) : Screen()
    object Tools : Screen()
    object Profile : Screen()
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                val viewModel: FoloViewModel = viewModel()
                var currentScreen by remember { mutableStateOf<Screen>(Screen.Home) }
                var toolsInitialTab by remember { mutableStateOf(0) }
                var quizModuleId by remember { mutableStateOf<String?>(null) }

                val progresses by viewModel.progresses.collectAsState()
                val isOffline by viewModel.isOfflineMode.collectAsState()

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        NavigationBar(
                            modifier = Modifier.testTag("app_bottom_bar")
                        ) {
                            NavigationBarItem(
                                selected = currentScreen is Screen.Home,
                                onClick = {
                                    currentScreen = Screen.Home
                                    quizModuleId = null
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Home,
                                        contentDescription = "Accueil"
                                    )
                                },
                                label = { Text("Accueil") },
                                modifier = Modifier.testTag("tab_home")
                            )

                            NavigationBarItem(
                                selected = currentScreen is Screen.Catalog || currentScreen is Screen.CourseDetail,
                                onClick = {
                                    currentScreen = Screen.Catalog
                                    quizModuleId = null
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.School,
                                        contentDescription = "Catalogue"
                                    )
                                },
                                label = { Text("Formations") },
                                modifier = Modifier.testTag("tab_formations")
                            )

                            NavigationBarItem(
                                selected = currentScreen is Screen.Tools,
                                onClick = {
                                    toolsInitialTab = 0
                                    currentScreen = Screen.Tools
                                    // if active quiz clicked again, reset it
                                    quizModuleId = null
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Explore,
                                        contentDescription = "Outils"
                                    )
                                },
                                label = { Text("Outils") },
                                modifier = Modifier.testTag("tab_outils")
                            )

                            NavigationBarItem(
                                selected = currentScreen is Screen.Profile,
                                onClick = {
                                    currentScreen = Screen.Profile
                                    quizModuleId = null
                                },
                                icon = {
                                    Icon(
                                        imageVector = Icons.Default.Person,
                                        contentDescription = "Profil"
                                    )
                                },
                                label = { Text("Profil") },
                                modifier = Modifier.testTag("tab_profil")
                            )
                        }
                    }
                ) { innerPadding ->
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(innerPadding)
                    ) {
                        when (currentScreen) {
                            is Screen.Home -> {
                                HomeScreen(
                                    viewModel = viewModel,
                                    onNavigateToDetail = { id ->
                                        currentScreen = Screen.CourseDetail(id)
                                    },
                                    onNavigateToTools = { tabIndex ->
                                        toolsInitialTab = tabIndex
                                        currentScreen = Screen.Tools
                                    }
                                )
                            }
                            is Screen.Catalog -> {
                                CatalogScreen(
                                    viewModel = viewModel,
                                    onNavigateToDetail = { id ->
                                        currentScreen = Screen.CourseDetail(id)
                                    }
                                )
                            }
                            is Screen.CourseDetail -> {
                                val detailId = (currentScreen as Screen.CourseDetail).moduleId
                                CourseDetailScreen(
                                    moduleId = detailId,
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        currentScreen = Screen.Catalog
                                    },
                                    onNavigateToQuiz = { qId ->
                                        quizModuleId = qId
                                        currentScreen = Screen.Tools
                                    }
                                )
                            }
                            is Screen.Tools -> {
                                ToolsScreen(
                                    viewModel = viewModel,
                                    quizModuleId = quizModuleId,
                                    initialTab = toolsInitialTab,
                                    onQuizClosed = {
                                        quizModuleId = null
                                        currentScreen = Screen.Catalog
                                    }
                                )
                            }
                            is Screen.Profile -> {
                                ProfileScreen(viewModel = viewModel)
                            }
                        }
                    }
                }
            }
        }
    }
}
