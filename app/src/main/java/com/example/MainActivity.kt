package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import com.example.ui.SystemMonitorScreen
import com.example.ui.SystemMonitorViewModel
import com.example.ui.theme.SystemMonitorTheme

class MainActivity : ComponentActivity() {

    private val viewModel: SystemMonitorViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            SystemMonitorTheme {
                SystemMonitorScreen(viewModel = viewModel)
            }
        }
    }
}
