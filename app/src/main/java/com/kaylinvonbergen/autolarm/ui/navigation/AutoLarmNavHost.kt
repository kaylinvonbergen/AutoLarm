
package com.kaylinvonbergen.autolarm.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.unit.dp
import com.kaylinvonbergen.autolarm.ui.screens.info.InfoScreen

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
import androidx.compose.runtime.saveable.rememberSaveable

import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel

import com.kaylinvonbergen.autolarm.data.local.AutoLarmDatabase
import com.kaylinvonbergen.autolarm.data.repository.TaskRepositoryImpl
import com.kaylinvonbergen.autolarm.ui.screens.tasks.TasksViewModel

import com.kaylinvonbergen.autolarm.data.repository.AlarmRepositoryImpl
import com.kaylinvonbergen.autolarm.ui.screens.alarms.AlarmsViewModel
import com.kaylinvonbergen.autolarm.ui.screens.alarms.AlarmsScreen

import androidx.compose.foundation.layout.safeDrawingPadding
import androidx.compose.foundation.layout.WindowInsets

import android.app.NotificationManager

import android.Manifest
import android.content.Context
import android.content.Intent
import android.os.Build
import android.provider.Settings
import android.net.Uri

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts

import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner

import com.kaylinvonbergen.autolarm.alarm.AlarmScheduler
import com.kaylinvonbergen.autolarm.alarm.AlarmReceiver
import com.kaylinvonbergen.autolarm.alarm.AlarmRingingService
import com.kaylinvonbergen.autolarm.domain.usecase.CalculateWakeUpTimeUseCase

import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.collectLatest


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


    var lastMainRoute by rememberSaveable {
        mutableStateOf(AutoLarmRoutes.ALARMS)
    }

// Remember the most recently visited main screen.
    LaunchedEffect(currentRoute) {
        if (
            currentRoute == AutoLarmRoutes.ALARMS ||
            currentRoute == AutoLarmRoutes.TASKS
        ) {
            lastMainRoute = currentRoute
        }
    }


    // Determine which main screen is selected.
    val selectedScreen: MainScreen? = when (currentRoute) {
        AutoLarmRoutes.TASKS -> MainScreen.TASKS
        AutoLarmRoutes.ALARMS -> MainScreen.ALARMS
        else -> null
    }

    // Only display the bottom bar on primary screens.
    val showHomeButton =
        currentRoute == AutoLarmRoutes.INFO ||
                currentRoute == AutoLarmRoutes.SETTINGS


    // Application context for database initialization.
    val context = LocalContext.current.applicationContext

    // Create the repository using our Room database.
    val taskRepository = remember(context) {
        val database = AutoLarmDatabase.getDatabase(context)

        TaskRepositoryImpl(
            taskDao = database.taskDao()
        )
    }

// Obtain the TasksViewModel.
    val tasksViewModel: TasksViewModel = viewModel(
        factory = TasksViewModel.factory(taskRepository)
    )

// Observe saved tasks.
    val tasks by tasksViewModel.tasks.collectAsStateWithLifecycle()


    // Controls the task creation dialog.
    var showCreateTaskDialog by remember {
        mutableStateOf(false)
    }


    val alarmRepository = remember(context) {
        AlarmRepositoryImpl(
            AutoLarmDatabase.getDatabase(context)
        )
    }

    val alarmsViewModel: AlarmsViewModel = viewModel(
        factory = AlarmsViewModel.factory(alarmRepository)
    )

    val alarms by alarmsViewModel.alarms.collectAsStateWithLifecycle()


// --------------------------------------------------
// ANDROID ALARM SCHEDULING — PHASE 1
// --------------------------------------------------

    val alarmScheduler = remember(context) {
        AlarmScheduler(context)
    }

    val lifecycleOwner = LocalLifecycleOwner.current

// Refresh scheduling after returning from Android Settings.
    var permissionRefresh by remember {
        mutableIntStateOf(0)
    }

    var requestedFullScreenPermission by rememberSaveable {
        mutableStateOf(false)
    }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                permissionRefresh++
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

// Track whether we've already shown the permission flows.
    var requestedExactPermission by rememberSaveable {
        mutableStateOf(false)
    }

    var requestedNotificationPermission by rememberSaveable {
        mutableStateOf(false)
    }

    val notificationPermissionLauncher =
        rememberLauncherForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) {
            permissionRefresh++
        }

// Register the notification channel.
    LaunchedEffect(context) {
        AlarmRingingService.createNotificationChannel(context)
    }

// Observe real Room emissions instead of the ViewModel's
// initial empty StateFlow values.
    LaunchedEffect(
        alarmRepository,
        taskRepository,
        alarmScheduler,
        permissionRefresh
    ) {
        combine(
            alarmRepository.alarms,
            taskRepository.allTasks
        ) { savedAlarms, savedTasks ->
            savedAlarms to savedTasks
        }.collectLatest { (savedAlarms, savedTasks) ->

            val calculator = CalculateWakeUpTimeUseCase()

            val existingTaskIds = savedTasks
                .map { it.id }
                .toSet()

            val desiredSchedules = savedAlarms
                .filter { it.enabled }
                .associate { alarm ->

                    // Ignore deleted task IDs.
                    val validAlarm = alarm.copy(
                        selectedTaskIds =
                            alarm.selectedTaskIds.filter {
                                it in existingTaskIds
                            }
                    )

                    val result = calculator(
                        validAlarm,
                        savedTasks
                    )

                    alarm.id to result.wakeUpTime
                }

            // Cancel deleted/disabled alarms, and schedule
            // any new or modified wake-up times.
            alarmScheduler.reconcile(desiredSchedules)
        }
    }

// Ask for permissions only if an alarm is enabled.
    LaunchedEffect(alarms, permissionRefresh) {
        if (alarms.none { it.enabled }) {
            return@LaunchedEffect
        }

        if (
            !alarmScheduler.canScheduleExactAlarms() &&
            !requestedExactPermission
        ) {
            requestedExactPermission = true

            try {
                val intent = Intent(
                    Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM
                ).apply {
                    data = Uri.parse(
                        "package:${context.packageName}"
                    )
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }

                context.startActivity(intent)
            } catch (e: Exception) {
                android.util.Log.e(
                    "AutoLarm",
                    "Could not open exact alarm settings",
                    e
                )
            }

            return@LaunchedEffect
        }


        if (Build.VERSION.SDK_INT >= 34) {
            val notificationManager = context.getSystemService(
                Context.NOTIFICATION_SERVICE
            ) as NotificationManager

            if (
                !notificationManager.canUseFullScreenIntent() &&
                !requestedFullScreenPermission
            ) {
                requestedFullScreenPermission = true

                try {
                    val intent = Intent(
                        Settings.ACTION_MANAGE_APP_USE_FULL_SCREEN_INTENT
                    ).apply {
                        data = Uri.parse(
                            "package:${context.packageName}"
                        )
                        addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    }

                    context.startActivity(intent)
                } catch (e: Exception) {
                    android.util.Log.e(
                        "AutoLarm",
                        "Could not open full-screen permission settings",
                        e
                    )
                }

                return@LaunchedEffect
            }
        }


        if (
            Build.VERSION.SDK_INT >= 33 &&
            context.checkSelfPermission(
                Manifest.permission.POST_NOTIFICATIONS
            ) != android.content.pm.PackageManager.PERMISSION_GRANTED &&
            !requestedNotificationPermission
        ) {
            requestedNotificationPermission = true

            notificationPermissionLauncher.launch(
                Manifest.permission.POST_NOTIFICATIONS
            )
        }
    }


    Scaffold(
            modifier = modifier
                .fillMaxSize()
                .safeDrawingPadding(),

            contentWindowInsets = WindowInsets(0, 0, 0, 0),

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
                AutoLarmBottomBar(
                    selectedScreen = selectedScreen,

                    showHomeButton = showHomeButton,


                    onHomeClick = {
                        // Return to the previously visited main screen,
                        // removing Info/Settings screens from the stack.
                        val returned = navController.popBackStack(
                            route = lastMainRoute,
                            inclusive = false
                        )

                        // Fallback if that screen isn't in the back stack.
                        if (!returned) {
                            navController.navigateToMainScreen(lastMainRoute)
                        }
                    },


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
                                // Keep your existing alarm creation logic.
                                if (alarms.size < 5) {
                                    alarmsViewModel.createAlarm()
                                } else {
                                    coroutineScope.launch {
                                        snackbarHostState.showSnackbar(
                                            "You can have a maximum of 5 alarms."
                                        )
                                    }
                                }
                            }
                        }
                    }
                )
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
                AlarmsScreen(
                    alarms = alarms,
                    tasks = tasks,
                    viewModel = alarmsViewModel
                )
            }




            composable(AutoLarmRoutes.TASKS) {
                TasksScreen(
                    tasks = tasks,

                    showCreateDialog = showCreateTaskDialog,

                    onDismissCreateDialog = {
                        showCreateTaskDialog = false
                    },

                    onCreateTask = { newTask ->
                        tasksViewModel.createTask(newTask)
                    },

                    onUpdateTask = { updatedTask ->
                        tasksViewModel.updateTask(updatedTask)
                    },

                    onDeleteTask = { task ->
                        tasksViewModel.deleteTask(task)
                    }
                )
            }



            composable(AutoLarmRoutes.SETTINGS) {
                PlaceholderScreen("Settings")
            }

            composable(AutoLarmRoutes.INFO) {
                InfoScreen()
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
