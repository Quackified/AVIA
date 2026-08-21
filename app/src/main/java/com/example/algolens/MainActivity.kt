package com.example.algolens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.algolens.ui.AlgoLensApp
import com.example.algolens.ui.theme.AlgoLensTheme

/**
 * Main Activity hosting the AlgoLens Jetpack Compose application.
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            AlgoLensTheme {
                AlgoLensApp()
            }
        }
    }
}