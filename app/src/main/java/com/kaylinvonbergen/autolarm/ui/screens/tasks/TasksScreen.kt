
package com.kaylinvonbergen.autolarm.ui.screens.tasks

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.kaylinvonbergen.autolarm.domain.model.Task
import com.kaylinvonbergen.autolarm.ui.theme.*

@Composable
fun TasksScreen(
    tasks: List<Task>,
    showCreateDialog: Boolean,
    onDismissCreateDialog: () -> Unit,
    onCreateTask: (Task) -> Unit,
    onUpdateTask: (Task) -> Unit,
    onDeleteTask: (Task) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingTask by remember { mutableStateOf<Task?>(null) }
    var deletingTask by remember { mutableStateOf<Task?>(null) }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
    ) {
        if (tasks.isEmpty()) {
            EmptyTasksState()
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(
                    items = tasks,
                    key = { it.id }
                ) { task ->
                    TaskCard(
                        task = task,
                        onEditClick = {
                            editingTask = task
                        }
                    )
                }
            }
        }
    }

    // Create a new task
    if (showCreateDialog) {
        TaskEditorDialog(
            task = null,
            onDismiss = onDismissCreateDialog,
            onSave = { name, duration ->
                onCreateTask(
                    Task(
                        name = name,
                        durationMinutes = duration
                    )
                )
                onDismissCreateDialog()
            }
        )
    }

    // Edit an existing task
    editingTask?.let { task ->
        TaskEditorDialog(
            task = task,
            onDismiss = {
                editingTask = null
            },
            onSave = { name, duration ->
                onUpdateTask(
                    task.copy(
                        name = name,
                        durationMinutes = duration
                    )
                )
                editingTask = null
            },
            onDelete = {
                editingTask = null
                deletingTask = task
            }
        )
    }

    // Confirm deletion
    deletingTask?.let { task ->
        AlertDialog(
            onDismissRequest = {
                deletingTask = null
            },
            title = {
                Text(
                    text = "Delete Task?",
                    style = MaterialTheme.typography.headlineSmall
                )
            },
            text = {
                Text(
                    text = "Are you sure you want to delete \"${task.name}\"? " +
                            "This cannot be undone."
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onDeleteTask(task)
                        deletingTask = null
                    }
                ) {
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
            },
            dismissButton = {
                TextButton(
                    onClick = {
                        deletingTask = null
                    }
                ) {
                    Text("Cancel")
                }
            },
            containerColor = NavigationBackground
        )
    }
}

/**
 * Displayed when no tasks have been created.
 */
@Composable
private fun EmptyTasksState() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(CardBackground)
                .then(
                    Modifier
                        .heightIn(min = 64.dp)
                )
                .padding(1.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Surface(
                modifier = Modifier.fillMaxWidth(),
                color = CardBackground,
                shape = RectangleShape,
                border = BorderStroke(1.dp, BorderColor)
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "You don't have any tasks!",
                        style = MaterialTheme.typography.titleMedium,
                        color = PrimaryText
                    )

                    Text(
                        text = "create a new task to continue.",
                        style = MaterialTheme.typography.bodySmall,
                        color = SecondaryText
                    )
                }
            }
        }
    }
}

/**
 * Individual task card.
 */
@Composable
private fun TaskCard(
    task: Task,
    onEditClick: () -> Unit
) {
    Surface(
        color = CardBackground,
        shape = RectangleShape,
        border = BorderStroke(1.dp, BorderColor),
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 68.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 10.dp, top = 8.dp, bottom = 8.dp, end = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = task.name,
                    style = MaterialTheme.typography.titleMedium,
                    color = PrimaryText,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis,
                    lineHeight = 20.sp
                )

                Text(
                    text = "Takes: ${task.durationMinutes}min",
                    style = MaterialTheme.typography.bodySmall,
                    color = SecondaryText,
                    maxLines = 1
                )
            }

            IconButton(
                onClick = onEditClick,
                modifier = Modifier.size(40.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Edit,
                    contentDescription = "Edit ${task.name}",
                    tint = Color.White,
                    modifier = Modifier.size(22.dp)
                )
            }
        }
    }
}

/**
 * Shared dialog for creating and editing tasks.
 */
@Composable
private fun TaskEditorDialog(
    task: Task?,
    onDismiss: () -> Unit,
    onSave: (String, Int) -> Unit,
    onDelete: (() -> Unit)? = null
) {
    var name by remember(task?.id) {
        mutableStateOf(task?.name ?: "")
    }

    var durationText by remember(task?.id) {
        mutableStateOf(task?.durationMinutes?.toString() ?: "")
    }

    val duration = durationText.toIntOrNull()

    val isValid =
        name.isNotBlank() &&
                duration != null &&
                duration > 0

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = NavigationBackground,

        title = {
            Text(
                text = if (task == null) "Create Task" else "Edit Task",
                style = MaterialTheme.typography.headlineSmall,
                color = PrimaryText
            )
        },

        text = {
            Column(
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Task name") },
                    placeholder = { Text("e.g. Shower") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )

                OutlinedTextField(
                    value = durationText,
                    onValueChange = { input ->
                        if (input.all { it.isDigit() }) {
                            durationText = input
                        }
                    },
                    label = { Text("Duration (minutes)") },
                    placeholder = { Text("e.g. 15") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Number
                    ),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },

        confirmButton = {
            TextButton(
                enabled = isValid,
                onClick = {
                    val validDuration = duration
                    if (validDuration != null && isValid) {
                        onSave(name.trim(), validDuration)
                    }
                }
            ) {
                Text(if (task == null) "Create" else "Save")
            }
        },

        dismissButton = {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (onDelete != null) {
                    TextButton(onClick = onDelete) {
                        Text(
                            "Delete",
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                TextButton(onClick = onDismiss) {
                    Text("Cancel")
                }
            }
        }
    )
}
