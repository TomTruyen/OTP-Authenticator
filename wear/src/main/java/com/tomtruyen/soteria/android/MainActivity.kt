package com.tomtruyen.soteria.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.google.android.gms.wearable.Wearable
import com.tomtruyen.soteria.android.ui.theme.SoteriaWearTheme
import com.tomtruyen.soteria.android.ui.screens.TokenDetailScreen
import com.tomtruyen.soteria.android.ui.screens.TokenScreen
import com.tomtruyen.soteria.android.ui.screens.TokenViewModel
import com.tomtruyen.soteria.android.ui.screens.detail.TokenDetailViewModel

class MainActivity : ComponentActivity() {
    private val mDataClient by lazy { Wearable.getDataClient(this) }

    private val mTokenViewModel by viewModels<TokenViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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