
package com.kaylinvonbergen.autolarm.ui.screens.info

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

import com.kaylinvonbergen.autolarm.ui.theme.*

@Composable
fun InfoScreen(
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Background)
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {

        // Page heading
        Text(
            text = "How AutoLarm Works",
            style = MaterialTheme.typography.headlineLarge,
            color = PrimaryText
        )

        Text(
            text = "Tell us when you need to arrive. " +
                    "We'll figure out when you need to wake up.",
            style = MaterialTheme.typography.bodyMedium,
            color = SecondaryText
        )

        // Four tutorial steps
        TutorialStep(
            number = "01",
            title = "Create your tasks",
            description = "Add your morning activities and " +
                    "how long each one takes."
        )

        TutorialStep(
            number = "02",
            title = "Set your arrival time",
            description = "Enter when and where you need to " +
                    "arrive, plus your travel time."
        )

        TutorialStep(
            number = "03",
            title = "Build your morning",
            description = "Choose your tasks and add a little " +
                    "extra buffer time."
        )

        TutorialStep(
            number = "04",
            title = "Customize your alarms",
            description = "See your calculated get-up time " +
                    "and configure pre-alarms and post-alarms."
        )

        HorizontalDivider(
            thickness = 1.dp,
            color = BorderColor.copy(alpha = 0.5f)
        )

        // Quick explanations
        Text(
            text = "Good to Know",
            style = MaterialTheme.typography.headlineSmall,
            color = PrimaryText
        )

        InfoExplanation(
            title = "Pre-alarms",
            description = "Earlier alarms to help you " +
                    "start waking up."
        )

        InfoExplanation(
            title = "Post-alarms",
            description = "Backup alarms in case you " +
                    "fall back asleep."
        )

        InfoExplanation(
            title = "Multiple alarms",
            description = "Save up to five morning plans. " +
                    "Swipe left or right to switch between them."
        )

        // Temporary notice until AlarmManager is implemented
        Surface(
            color = CardBackground,
            shape = RectangleShape,
            border = BorderStroke(
                1.dp,
                BorderColor.copy(alpha = 0.5f)
            ),
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = "Note: Alarm ringing is not available " +
                        "yet. Your plans are saved locally.",
                style = MaterialTheme.typography.bodySmall,
                color = SecondaryText,
                modifier = Modifier.padding(12.dp)
            )
        }

        Spacer(Modifier.height(8.dp))
    }
}

@Composable
private fun TutorialStep(
    number: String,
    title: String,
    description: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        Surface(
            color = CardBackground,
            shape = RectangleShape,
            border = BorderStroke(1.dp, BorderColor),
            modifier = Modifier.size(44.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = number,
                    style = MaterialTheme.typography.titleLarge,
                    color = PrimaryText
                )
            }
        }

        Column(
            modifier = Modifier.weight(1f),
            verticalArrangement = Arrangement.spacedBy(3.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = PrimaryText
            )

            Text(
                text = description,
                style = MaterialTheme.typography.bodyMedium,
                color = SecondaryText
            )
        }
    }
}

@Composable
private fun InfoExplanation(
    title: String,
    description: String
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            color = PrimaryText
        )

        Text(
            text = description,
            style = MaterialTheme.typography.bodySmall,
            color = SecondaryText
        )
    }
}
