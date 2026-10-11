
package com.kaylinvonbergen.autolarm.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.kaylinvonbergen.autolarm.ui.theme.*

enum class MainScreen {
    ALARMS,
    TASKS
}

@Composable
fun AutoLarmBottomBar(
    selectedScreen: MainScreen,
    onAlarmsClick: () -> Unit,
    onTasksClick: () -> Unit,
    onAddClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NavigationBackground)
    ) {

        // White top border
        HorizontalDivider(
            thickness = 1.dp,
            color = BorderColor
        )

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {

            // Alarms navigation button
            NavigationButton(
                text = "ALARMS",
                selected = selectedScreen == MainScreen.ALARMS,
                onClick = onAlarmsClick,
                modifier = Modifier.weight(1f)
            )

            // Add button
            IconButton(
                onClick = onAddClick,
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Add,
                    contentDescription = "Add new item",
                    tint = Color.White,
                    modifier = Modifier.size(32.dp)
                )
            }

            // Tasks navigation button
            NavigationButton(
                text = "TASKS",
                selected = selectedScreen == MainScreen.TASKS,
                onClick = onTasksClick,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun NavigationButton(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        modifier = modifier.height(40.dp),
        color = if (selected) CardBackground else NavigationBackground,
        contentColor = Color.White,
        shape = RectangleShape,
        border = BorderStroke(1.dp, BorderColor)
    ) {
        Box(
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = text,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
        }
    }
}
