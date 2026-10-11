
package com.kaylinvonbergen.autolarm.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp

import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.NavGraph.Companion.findStartDestination

import com.kaylinvonbergen.autolarm.ui.components.AutoLarmTopBar
import com.kaylinvonbergen.autolarm.ui.components.AutoLarmBottomBar
import com.kaylinvonbergen.autolarm.ui.components.MainScreen

import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import com.kaylinvonbergen.autolarm.domain.model.Task
import com.kaylinvonbergen.autolarm.ui.screens.tasks.TasksScreen

import kotlinx.coroutines.launch

/**
 * Application navigation destinations.
 */
object AutoLarmRoutes {
    const val ALARMS = "alarms"
    const val TASKS = "tasks"
    const val SETTINGS = "settings"
    const val INFO = "info"
}

/**
 * Main application navigation container.
 *
 * Manages:
 * - Top navigation bar
 * - Bottom navigation bar
 * - Screen navigation
 * - GitHub link
 * - Add button behavior
 */
@Composable
fun AutoLarmNavHost(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController()
) {
    val uriHandler = LocalUriHandler.current
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // Observe the currently displayed screen.
    val backStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = backStackEntry?.destination?.route

    // Determine which main screen is selected.
    val selectedScreen = when (currentRoute) {
        AutoLarmRoutes.TASKS -> MainScreen.TASKS
        else -> MainScreen.ALARMS
    }

    // Only display the bottom bar on primary screens.
    val showBottomBar = currentRoute == AutoLarmRoutes.ALARMS ||
            currentRoute == AutoLarmRoutes.TASKS ||
            currentRoute == null

    // Temporary in-memory task storage.
    // This will be replaced by a ViewModel and Room database.
    val tasks = remember { mutableStateListOf<Task>() }

    // Controls the task creation dialog.
    var showCreateTaskDialog by remember {
        mutableStateOf(false)
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),

        // Shared top bar
        topBar = {
            AutoLarmTopBar(
                onInfoClick = {
                    navController.navigate(AutoLarmRoutes.INFO) {
                        launchSingleTop = true
                    }
                },

                onGitHubClick = {
                    uriHandler.openUri(
                        "https://github.com/kaylinvonbergen/AutoLarm"
                    )
                },

                onSettingsClick = {
                    navController.navigate(AutoLarmRoutes.SETTINGS) {
                        launchSingleTop = true
                    }
                }
            )
        },

        // Shared bottom bar
        bottomBar = {
            if (showBottomBar) {
                AutoLarmBottomBar(
                    selectedScreen = selectedScreen,

                    onAlarmsClick = {
                        navController.navigateToMainScreen(
                            AutoLarmRoutes.ALARMS
                        )
                    },

                    onTasksClick = {
                        navController.navigateToMainScreen(
                            AutoLarmRoutes.TASKS
                        )
                    },

                    onAddClick = {
                        when (currentRoute) {
                            AutoLarmRoutes.TASKS -> {
                                showCreateTaskDialog = true
                            }

                            AutoLarmRoutes.ALARMS -> {
                                coroutineScope.launch {
                                    snackbarHostState.showSnackbar(
                                        "Create alarm - coming soon"
                                    )
                                }
                            }
                        }
                    }
                )
            }
        },

        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        }

    ) { innerPadding ->

        // Screen content
        NavHost(
            navController = navController,
            startDestination = AutoLarmRoutes.ALARMS,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {

            composable(AutoLarmRoutes.ALARMS) {
                PlaceholderScreen("Alarms")
            }


            composable(AutoLarmRoutes.TASKS) {
                TasksScreen(
                    tasks = tasks,

                    showCreateDialog = showCreateTaskDialog,

                    onDismissCreateDialog = {
                        showCreateTaskDialog = false
                    },

                    onCreateTask = { newTask ->
                        tasks.add(newTask)
                    },

                    onUpdateTask = { updatedTask ->
                        val index = tasks.indexOfFirst {
                            it.id == updatedTask.id
                        }

                        if (index != -1) {
                            tasks[index] = updatedTask
                        }
                    },

                    onDeleteTask = { task ->
                        tasks.removeAll { it.id == task.id }
                    }
                )
            }


            composable(AutoLarmRoutes.SETTINGS) {
                PlaceholderScreen("Settings")
            }

            composable(AutoLarmRoutes.INFO) {
                PlaceholderScreen("Information")
            }
        }
    }
}

/**
 * Navigate between the two primary screens.
 *
 * Preserves screen state where possible and avoids
 * creating unnecessary duplicate destinations.
 */
private fun NavHostController.navigateToMainScreen(
    route: String
) {
    navigate(route) {
        popUpTo(graph.findStartDestination().id) {
            saveState = true
        }

        launchSingleTop = true
        restoreState = true
    }
}

/**
 * Temporary screen until the real screen implementations
 * are ready.
 */
@Composable
private fun PlaceholderScreen(
    name: String
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "$name Screen",
            style = MaterialTheme.typography.headlineMedium
        )
    }
}
