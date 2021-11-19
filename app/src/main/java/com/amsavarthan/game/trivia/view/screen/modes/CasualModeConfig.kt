package com.amsavarthan.game.trivia.view.screen.modes

import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import com.amsavarthan.game.trivia.ui.theme.AppTheme

@Composable
fun CasualModeConfig(navController: NavController? = null) {

}

@Preview(showBackground = true, uiMode = UI_MODE_NIGHT_YES)
@Composable
fun CasualModeConfigPreview() {
    AppTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            CasualModeConfig()
        }
    }
}