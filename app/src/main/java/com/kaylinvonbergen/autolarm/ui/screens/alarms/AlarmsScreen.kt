
package com.kaylinvonbergen.autolarm.ui.screens.alarms

import android.app.TimePickerDialog

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material.icons.outlined.Remove
import androidx.compose.material3.*

import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp

import com.kaylinvonbergen.autolarm.domain.model.*
import com.kaylinvonbergen.autolarm.domain.usecase.CalculateWakeUpTimeUseCase
import com.kaylinvonbergen.autolarm.ui.theme.*

import java.time.LocalTime
import java.time.format.DateTimeFormatter
import kotlin.math.roundToInt

private val timeFormat =
    DateTimeFormatter.ofPattern("h:mm a")

@Composable
fun AlarmsScreen(
    alarms: List<Alarm>,
    tasks: List<Task>,
    viewModel: AlarmsViewModel,
    modifier: Modifier = Modifier
) {
    var pendingDelete by remember {
        mutableStateOf<Alarm?>(null)
    }

    val pagerState = rememberPagerState(
        pageCount = { alarms.size }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        if (alarms.isEmpty()) {
            EmptyAlarmsState()
        } else {

            // Carousel indicators
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 8.dp),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                alarms.indices.forEach { index ->
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .size(
                                width = if (
                                    pagerState.currentPage == index
                                ) 20.dp else 8.dp,
                                height = 8.dp
                            )
                            .background(
                                if (
                                    pagerState.currentPage == index
                                ) Color.White else Color.Gray
                            )
                    )
                }
            }

            HorizontalPager(
                state = pagerState,
                modifier = Modifier.weight(1f),
                key = { index -> alarms[index].id }
            ) { page ->
                val alarm = alarms[page]

                AlarmBuilder(
                    alarm = alarm,
                    tasks = tasks,
                    calculation = viewModel.calculate(
                        alarm,
                        tasks
                    ),
                    onChange = viewModel::updateAlarm,
                    onDelete = {
                        pendingDelete = alarm
                    }
                )
            }
        }
    }

    // Delete confirmation
    pendingDelete?.let { alarm ->
        AlertDialog(
            onDismissRequest = {
                pendingDelete = null
            },
            title = {
                Text("Delete Alarm?")
            },
            text = {
                Text("This alarm configuration will be deleted.")
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.deleteAlarm(alarm)
                        pendingDelete = null
                    }
                ) {
                    Text(
                        "Delete",
                        color = MaterialTheme.colorScheme.error
                    )
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        pendingDelete = null
                    }
                ) {
                    Text("Cancel")
                }
            },
            containerColor = NavigationBackground
        )
    }
}

@Composable
private fun EmptyAlarmsState() {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        color = CardBackground,
        shape = RectangleShape,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                "You don't have any alarms!",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                "create a new alarm to continue.",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun AlarmBuilder(
    alarm: Alarm,
    tasks: List<Task>,
    calculation: CalculateWakeUpTimeUseCase.Result,
    onChange: (Alarm) -> Unit,
    onDelete: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(
                start = 16.dp,
                end = 16.dp,
                bottom = 16.dp
            )
    ) {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = CardBackground,
            shape = RectangleShape,
            border = BorderStroke(1.dp, BorderColor)
        ) {
            Column {

                // Section 1: Arrival details
                ArrivalSection(
                    alarm = alarm,
                    onChange = onChange,
                    onDelete = onDelete
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = BorderColor
                )

                // Sections 2 and 3: Task selection
                TaskSelectionSection(
                    alarm = alarm,
                    tasks = tasks,
                    onChange = onChange,
                    modifier = Modifier.weight(1f)
                )

                HorizontalDivider(
                    thickness = 1.dp,
                    color = BorderColor
                )

                // Section 4: Alarm controls
                AlarmControlsSection(
                    alarm = alarm,
                    calculation = calculation,
                    onChange = onChange
                )
            }
        }
    }
}

/**
 * Arrival time, addresses, travel duration,
 * and transportation selection.
 */
@Composable
private fun ArrivalSection(
    alarm: Alarm,
    onChange: (Alarm) -> Unit,
    onDelete: () -> Unit
) {
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(4.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Time to Arrive:",
                style = MaterialTheme.typography.titleLarge,
                color = PrimaryText
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = alarm.arrivalTime.format(timeFormat),
                style = MaterialTheme.typography.titleLarge,
                textDecoration = TextDecoration.Underline,
                color = PrimaryText,
                modifier = Modifier
                    .weight(1f)
                    .clickable {
                        TimePickerDialog(
                            context,
                            { _, hour, minute ->
                                onChange(
                                    alarm.copy(
                                        arrivalTime = LocalTime.of(
                                            hour,
                                            minute
                                        )
                                    )
                                )
                            },
                            alarm.arrivalTime.hour,
                            alarm.arrivalTime.minute,
                            false
                        ).show()
                    }
            )

            Switch(
                checked = alarm.enabled,
                onCheckedChange = {
                    onChange(alarm.copy(enabled = it))
                }
            )

            IconButton(onClick = onDelete) {
                Icon(
                    imageVector = Icons.Outlined.Delete,
                    contentDescription = "Delete alarm",
                    tint = PrimaryText
                )
            }
        }

        // Compact address fields
        CompactTextField(
            label = "Leaving from",
            value = alarm.departureLocation,
            fieldKey = "${alarm.id}_departure",
            onValueChange = {
                onChange(
                    alarm.copy(departureLocation = it)
                )
            }
        )

        CompactTextField(
            label = "Going to",
            value = alarm.destinationLocation,
            fieldKey = "${alarm.id}_destination",
            onValueChange = {
                onChange(
                    alarm.copy(destinationLocation = it)
                )
            }
        )

        // Compact travel duration and transportation row
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(5.dp)
        ) {
            Text(
                "Takes:",
                style = MaterialTheme.typography.titleMedium
            )

            TravelTimeField(
                alarm = alarm,
                onChange = onChange,
                modifier = Modifier.wrapContentWidth()
            )

            Text(
                "by:",
                style = MaterialTheme.typography.titleMedium
            )

            Box(
                modifier = Modifier.weight(1f)
            ) {
                TransportationDropdown(
                    selected = alarm.transportationMode,
                    onSelect = {
                        onChange(
                            alarm.copy(
                                transportationMode = it
                            )
                        )
                    }
                )
            }
        }
    }
}

/**
 * Compact underlined text input used for addresses.
 */

@Composable
private fun CompactTextField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    fieldKey: String
) {
    // Local state is independent of Room emissions.
    var localText by remember(fieldKey) {
        mutableStateOf(value)
    }

    // Track whether the user has changed this field.
    var edited by remember(fieldKey) {
        mutableStateOf(false)
    }

    // Keep the latest callback without restarting the debounce.
    val latestOnValueChange by rememberUpdatedState(onValueChange)

    // Save after the user stops typing for 400ms.
    LaunchedEffect(fieldKey, localText, edited) {
        if (edited) {
            kotlinx.coroutines.delay(400)
            latestOnValueChange(localText)
            edited = false
        }
    }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 2.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "$label:",
            style = MaterialTheme.typography.titleMedium,
            color = PrimaryText
        )

        Spacer(Modifier.width(6.dp))

        BasicTextField(
            value = localText,
            onValueChange = { newText ->
                localText = newText
                edited = true
            },
            singleLine = true,
            textStyle = MaterialTheme.typography.bodyMedium.copy(
                color = PrimaryText,
                textDecoration = TextDecoration.Underline
            ),
            cursorBrush = SolidColor(PrimaryText),
            modifier = Modifier.weight(1f),
            decorationBox = { innerTextField ->
                Box {
                    if (localText.isEmpty()) {
                        Text(
                            text = "Enter $label",
                            style = MaterialTheme.typography.bodyMedium,
                            color = SecondaryText
                        )
                    }

                    innerTextField()
                }
            }
        )
    }
}


/**
 * Compact manual travel duration with numeric validation.
 */
@Composable
private fun TravelTimeField(
    alarm: Alarm,
    onChange: (Alarm) -> Unit,
    modifier: Modifier = Modifier
) {
    // Keep unfinished/invalid input visible until corrected.
    var input by remember(alarm.id) {
        mutableStateOf(
            alarm.travelDurationMinutes.toString()
        )
    }

    val parsed = input.toIntOrNull()
    val invalid = parsed == null || parsed !in 0..1440

    Column(modifier = modifier) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            BasicTextField(
                value = input,
                onValueChange = { text ->
                    input = text

                    val minutes = text.toIntOrNull()

                    if (minutes != null && minutes in 0..1440) {
                        onChange(
                            alarm.copy(
                                travelDurationMinutes = minutes
                            )
                        )
                    }
                },
                singleLine = true,
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Number
                ),
                textStyle = MaterialTheme.typography.bodyMedium.copy(
                    color = if (invalid) {
                        MaterialTheme.colorScheme.error
                    } else {
                        PrimaryText
                    },
                    textDecoration = TextDecoration.Underline
                ),
                cursorBrush = SolidColor(PrimaryText),
                modifier = Modifier.width(24.dp)
            )

            Text(
                "min",
                style = MaterialTheme.typography.bodyMedium
            )
        }

        if (invalid) {
            Text(
                "Enter 0–1440",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.error
            )
        }
    }
}

/**
 * Compact underlined transportation dropdown.
 */
@Composable
private fun TransportationDropdown(
    selected: TransportationMode,
    onSelect: (TransportationMode) -> Unit
) {
    var expanded by remember {
        mutableStateOf(false)
    }

    Box {
        Text(
            text = selected.name.lowercase()
                .replaceFirstChar { it.uppercase() },
            style = MaterialTheme.typography.bodyMedium,
            textDecoration = TextDecoration.Underline,
            color = PrimaryText,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.clickable {
                expanded = true
            }
        )

        DropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            TransportationMode.entries.forEach { mode ->
                DropdownMenuItem(
                    text = {
                        Text(
                            mode.name.lowercase()
                                .replaceFirstChar { it.uppercase() }
                        )
                    },
                    onClick = {
                        onSelect(mode)
                        expanded = false
                    }
                )
            }
        }
    }
}

/**
 * Two independently scrcaollable task columns,
 * with the buffer control at the bottom-left.
 */
@Composable
private fun TaskSelectionSection(
    alarm: Alarm,
    tasks: List<Task>,
    onChange: (Alarm) -> Unit,
    modifier: Modifier = Modifier
) {
    val selectedTasks = tasks.filter {
        it.id in alarm.selectedTaskIds
    }

    val availableTasks = tasks.filter {
        it.id !in alarm.selectedTaskIds
    }

    Row(
        modifier = modifier.fillMaxWidth()
    ) {
        // LEFT: Selected tasks and buffer
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(10.dp)
        ) {
            Text(
                "Tasks:",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn(
                modifier = Modifier.weight(1f)
            ) {
                items(
                    items = selectedTasks,
                    key = { it.id }
                ) { task ->
                    TaskSelectionItem(
                        task = task,
                        selected = true,
                        onClick = {
                            onChange(
                                alarm.copy(
                                    selectedTaskIds =
                                        alarm.selectedTaskIds - task.id
                                )
                            )
                        }
                    )
                }
            }

            Spacer(Modifier.height(12.dp))


            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = "Buffer: ${alarm.bufferMinutes}min",
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryText,
                    maxLines = 1
                )

                CompactStepButton(
                    increment = false,
                    onClick = {
                        onChange(
                            alarm.copy(
                                bufferMinutes =
                                    (alarm.bufferMinutes - 1)
                                        .coerceAtLeast(0)
                            )
                        )
                    }
                )

                CompactStepButton(
                    increment = true,
                    onClick = {
                        onChange(
                            alarm.copy(
                                bufferMinutes =
                                    (alarm.bufferMinutes + 1)
                                        .coerceAtMost(60)
                            )
                        )
                    }
                )
            }

        }

        VerticalDivider(
            color = BorderColor,
            thickness = 1.dp
        )

        // RIGHT: Available tasks
        Column(
            modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(10.dp)
        ) {
            Text(
                "Available Tasks",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            LazyColumn {
                items(
                    items = availableTasks,
                    key = { it.id }
                ) { task ->
                    TaskSelectionItem(
                        task = task,
                        selected = false,
                        onClick = {
                            onChange(
                                alarm.copy(
                                    selectedTaskIds =
                                        alarm.selectedTaskIds + task.id
                                )
                            )
                        }
                    )
                }
            }
        }
    }
}

/**
 * Compact task selection item.
 */
@Composable
private fun TaskSelectionItem(
    task: Task,
    selected: Boolean,
    onClick: () -> Unit
) {
    Text(
        text = if (selected) {
            "• ${task.name} (${task.durationMinutes}min)"
        } else {
            "+ ${task.name} (${task.durationMinutes}min)"
        },
        style = MaterialTheme.typography.bodySmall,
        color = PrimaryText,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(vertical = 7.dp)
    )
}

/**
 * Bottom alarm section:
 * Pre-alarm slider | Wake-up time | Post-alarm slider
 */
@Composable
private fun AlarmControlsSection(
    alarm: Alarm,
    calculation: CalculateWakeUpTimeUseCase.Result,
    onChange: (Alarm) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(10.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Pre-Alarms",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f)
            )

            Text(
                "Get-Up",
                style = MaterialTheme.typography.titleMedium
            )

            Text(
                "Post-Alarms",
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.weight(1f),
                textAlign = TextAlign.End
            )
        }

        Spacer(Modifier.height(8.dp))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            AlarmCountSlider(
                config = alarm.preAlarms,
                modifier = Modifier.weight(1f),
                onChange = {
                    onChange(
                        alarm.copy(preAlarms = it)
                    )
                }
            )

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                modifier = Modifier.weight(1.1f)
            ) {
                Text(
                    "≈ ${calculation.wakeUpTime.format(timeFormat)}",
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryText,
                    maxLines = 1
                )

                Text(
                    "${calculation.totalRequiredMinutes} min needed",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText
                )
            }

            AlarmCountSlider(
                config = alarm.postAlarms,
                modifier = Modifier.weight(1f),
                onChange = {
                    onChange(
                        alarm.copy(postAlarms = it)
                    )
                }
            )
        }
    }
}

/**
 * Reusable compact increment/decrement button.
 */
@Composable
private fun CompactStepButton(
    increment: Boolean,
    onClick: () -> Unit
) {
    IconButton(
        onClick = onClick,
        modifier = Modifier.size(28.dp)
    ) {
        Icon(
            imageVector = if (increment) {
                Icons.Outlined.Add
            } else {
                Icons.Outlined.Remove
            },
            contentDescription = if (increment) {
                "Increase"
            } else {
                "Decrease"
            },
            tint = PrimaryText,
            modifier = Modifier.size(18.dp)
        )
    }
}

/**
 * Slider for selecting 0–10 additional alarms.
 */
@Composable
private fun AlarmCountSlider(
    config: AdditionalAlarmConfig,
    onChange: (AdditionalAlarmConfig) -> Unit,
    modifier: Modifier = Modifier
) {
    var sliderValue by remember {
        mutableFloatStateOf(config.count.toFloat())
    }

    LaunchedEffect(config.count) {
        sliderValue = config.count.toFloat()
    }

    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "${sliderValue.roundToInt()} alarms",
            style = MaterialTheme.typography.bodySmall,
            color = PrimaryText,
            maxLines = 1
        )

        Slider(
            value = sliderValue,
            onValueChange = {
                sliderValue = it
            },
            onValueChangeFinished = {
                val count = sliderValue.roundToInt()

                onChange(
                    config.copy(
                        enabled = count > 0,
                        count = count
                    )
                )
            },
            valueRange = 0f..10f,
            steps = 9,
            modifier = Modifier.height(32.dp)
        )

        Text(
            "${config.intervalMinutes} min apart",
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText
        )

        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            CompactStepButton(
                increment = false,
                onClick = {
                    onChange(
                        config.copy(
                            intervalMinutes =
                                (config.intervalMinutes - 1)
                                    .coerceAtLeast(1)
                        )
                    )
                }
            )

            CompactStepButton(
                increment = true,
                onClick = {
                    onChange(
                        config.copy(
                            intervalMinutes =
                                (config.intervalMinutes + 1)
                                    .coerceAtMost(60)
                        )
                    )
                }
            )
        }
    }
}
