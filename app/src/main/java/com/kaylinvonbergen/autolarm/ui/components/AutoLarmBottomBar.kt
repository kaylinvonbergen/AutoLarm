
package com.kaylinvonbergen.autolarm.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

import com.kaylinvonbergen.autolarm.R
import com.kaylinvonbergen.autolarm.ui.theme.*

enum class MainScreen {
    ALARMS,
    TASKS
}

@Composable
fun AutoLarmBottomBar(
    selectedScreen: MainScreen?,
    onAlarmsClick: () -> Unit,
    onTasksClick: () -> Unit,
    onAddClick: () -> Unit,
    onHomeClick: () -> Unit,
    showHomeButton: Boolean = false,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NavigationBackground)
    ) {

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

            NavigationButton(
                text = "ALARMS",
                selected = selectedScreen == MainScreen.ALARMS,
                onClick = onAlarmsClick,
                modifier = Modifier.weight(1f)
            )

            // Center button changes depending on the screen.
            IconButton(
                onClick = if (showHomeButton) {
                    onHomeClick
                } else {
                    onAddClick
                },
                modifier = Modifier.size(40.dp)
            ) {
                if (showHomeButton) {

                    Icon(
                        painter = painterResource(
                            id = R.drawable.home_icon
                        ),
                        contentDescription = "Go to Alarms",
                        tint = Color.Unspecified,
                        modifier = Modifier.size(28.dp)
                    )

                } else {

                    Icon(
                        imageVector = Icons.Outlined.Add,
                        contentDescription = "Add new item",
                        tint = Color.White,
                        modifier = Modifier.size(32.dp)
                    )
                }
            }

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
        color = if (selected) {
            CardBackground
        } else {
            NavigationBackground
        },
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
