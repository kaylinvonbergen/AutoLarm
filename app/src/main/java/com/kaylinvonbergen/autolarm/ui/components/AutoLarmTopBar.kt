
package com.kaylinvonbergen.autolarm.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Info
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.res.painterResource
import com.kaylinvonbergen.autolarm.R

import com.kaylinvonbergen.autolarm.ui.theme.*

@Composable
fun AutoLarmTopBar(
    onInfoClick: () -> Unit,
    onGitHubClick: () -> Unit,
    onSettingsClick: () -> Unit,
    modifier: Modifier = Modifier

) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(NavigationBackground)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(72.dp)
                .padding(horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

        // ICON() VS IMAGE()
            Icon(
                painter = painterResource(id = R.drawable.autolarm_logo),
                contentDescription = "AutoLarm Logo",
                tint = Color.Unspecified,
                modifier = Modifier.size(44.dp)
            )


            Spacer(modifier = Modifier.width(8.dp))

            // Application name
            Text(
                text = "AutoLarm",
                style = MaterialTheme.typography.headlineLarge,
                color = PrimaryText,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )

            // Information button
            IconButton(onClick = onInfoClick) {
                Icon(
                    imageVector = Icons.Outlined.Info,
                    contentDescription = "Information",
                    tint = Color.White
                )
            }

            // GitHub button (temporary icon)
            IconButton(onClick = onGitHubClick) {
                Text(
                    text = "GH",
                    color = Color.White,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            // Settings button
            IconButton(onClick = onSettingsClick) {
                Icon(
                    imageVector = Icons.Outlined.Settings,
                    contentDescription = "Settings",
                    tint = Color.White
                )
            }
        }

        // White bottom border
        HorizontalDivider(
            thickness = 1.dp,
            color = BorderColor
        )
    }
}
