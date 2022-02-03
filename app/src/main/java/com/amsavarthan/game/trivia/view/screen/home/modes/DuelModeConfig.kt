package com.amsavarthan.game.trivia.view.screen.home.modes

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.ui.theme.AppTheme

@Composable
fun DuelModeConfig(navController: NavController? = null) {

}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun DuelModeConfigPreview() {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            DuelModeConfig()
        }
    }
}