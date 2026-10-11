
package com.kaylinvonbergen.autolarm

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.foundation.layout.fillMaxSize

import com.kaylinvonbergen.autolarm.ui.navigation.AutoLarmNavHost
import com.kaylinvonbergen.autolarm.ui.theme.AutoLarmTheme
import com.kaylinvonbergen.autolarm.ui.theme.Background

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            AutoLarmTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Background
                ) {
                    AutoLarmNavHost()
                }
            }
        }
    }
}
