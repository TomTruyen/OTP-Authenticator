package com.tomtruyen.soteria.wear

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.tomtruyen.soteria.android.ui.theme.SoteriaWearTheme
import com.tomtruyen.soteria.wear.ui.screens.TokenDetailScreen
import com.tomtruyen.soteria.wear.ui.screens.TokenScreen
import com.tomtruyen.soteria.wear.ui.screens.TokenViewModel
import com.tomtruyen.soteria.wear.ui.screens.detail.TokenDetailViewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val mTokenViewModel by viewModels<TokenViewModel>()

        setContent {
            SoteriaWearTheme {
                val navController = rememberSwipeDismissableNavController()

                SwipeDismissableNavHost(
                    navController = navController,
                    startDestination = NavGraph.Tokens
                ) {
                    composable(NavGraph.Tokens) {
                        TokenScreen(mTokenViewModel) { id ->
                            navController.navigate(NavGraph.toTokenDetail(id))
                        }
                    }

                    composable(NavGraph.TokenDetail) {
                        TokenDetailScreen(
                            TokenDetailViewModel(it.arguments?.getString("id")!!)
                        )
                    }
                }
            }
        }
    }
}