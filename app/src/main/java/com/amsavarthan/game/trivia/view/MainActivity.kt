package com.amsavarthan.game.trivia.view

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.amsavarthan.game.trivia.ui.theme.AppTheme
import com.amsavarthan.game.trivia.view.screen.HomeScreen
import com.google.accompanist.insets.ProvideWindowInsets

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme {
                ProvideWindowInsets {
                    HomeScreen()
                }
            }
        }
    }
}
