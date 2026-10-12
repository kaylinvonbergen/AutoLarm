
package com.kaylinvonbergen.autolarm.alarm

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager

import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Alarm
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

import com.kaylinvonbergen.autolarm.ui.theme.*

import java.time.LocalTime
import java.time.format.DateTimeFormatter

class RingingActivity : ComponentActivity() {

    private var alarmId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        alarmId = intent.getStringExtra(
            AlarmRingingService.EXTRA_ALARM_ID
        )

        if (Build.VERSION.SDK_INT >= 27) {
            setShowWhenLocked(true)
            setTurnScreenOn(true)
        } else {
            @Suppress("DEPRECATION")
            window.addFlags(
                WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED or
                        WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON
            )
        }

        setContent {
            AutoLarmTheme {
                RingingScreen(
                    onDismiss = {
                        dismissAlarm()
                    }
                )
            }
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)

        alarmId = intent.getStringExtra(
            AlarmRingingService.EXTRA_ALARM_ID
        )
    }

    private fun dismissAlarm() {
        val id = alarmId ?: return

        val serviceIntent = Intent(
            this,
            AlarmRingingService::class.java
        ).apply {
            action = AlarmRingingService.ACTION_DISMISS
            putExtra(
                AlarmRingingService.EXTRA_ALARM_ID,
                id
            )
        }

        startService(serviceIntent)
        finish()
    }
}

@Composable
private fun RingingScreen(
    onDismiss: () -> Unit
) {
    val time = LocalTime.now().format(
        DateTimeFormatter.ofPattern("h:mm a")
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Background)
            .safeDrawingPadding()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Icon(
            imageVector = Icons.Outlined.Alarm,
            contentDescription = null,
            tint = Color.White,
            modifier = Modifier.size(72.dp)
        )

        Spacer(Modifier.height(24.dp))

        Text(
            text = "TIME TO GET UP!",
            style = MaterialTheme.typography.headlineLarge,
            color = PrimaryText,
            textAlign = TextAlign.Center
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = time,
            style = MaterialTheme.typography.headlineLarge,
            color = PrimaryText
        )

        Spacer(Modifier.height(48.dp))

        Button(
            onClick = onDismiss,
            modifier = Modifier
                .fillMaxWidth()
                .height(64.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color.White,
                contentColor = NavigationBackground
            ),
            shape = androidx.compose.ui.graphics.RectangleShape
        ) {
            Text(
                "DISMISS",
                style = MaterialTheme.typography.titleLarge
            )
        }
    }
}
