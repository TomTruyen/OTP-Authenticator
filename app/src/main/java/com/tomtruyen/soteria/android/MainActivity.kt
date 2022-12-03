package com.tomtruyen.soteria.android

import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tomtruyen.soteria.android.services.WearSyncService
import com.tomtruyen.soteria.android.ui.screens.TokenScreen
import com.tomtruyen.soteria.android.ui.screens.TokenViewModel
import com.tomtruyen.soteria.android.ui.screens.add.AddTokenScreen
import com.tomtruyen.soteria.android.ui.screens.scan.ScanTokenScreen
import com.tomtruyen.soteria.android.ui.screens.settings.SettingsScreen
import com.tomtruyen.soteria.android.ui.screens.settings.SettingsViewModel
import com.tomtruyen.soteria.android.ui.theme.SoteriaAndroidTheme
import com.tomtruyen.soteria.android.utils.DialogUtils
import com.tomtruyen.soteria.android.utils.PermissionUtils
import org.koin.androidx.viewmodel.ext.android.viewModel

class MainActivity : ComponentActivity() {
    private val mTokenViewModel by viewModel<TokenViewModel>()
    private val mSettingsViewModel by viewModel<SettingsViewModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            SoteriaAndroidTheme {
                val context = LocalContext.current
                val navController = rememberNavController()

                val mCameraPermissionLauncher = PermissionUtils.setupPermissionLauncher(
                    onSuccess = { navController.navigate(NavGraph.ScanToken) },
                    onFailure = {
                        DialogUtils.showDialog(
                            context = context,
                            title = context.getString(R.string.title_permission_denied),
                            message = context.getString(R.string.message_camera_permission)
                        )
                    }
                )

                val mStoragePermissionLauncher = PermissionUtils.setupPermissionLauncher(
                    onSuccess = { navController.navigate(NavGraph.Settings) },
                    onFailure = {
                        DialogUtils.showDialog(
                            context = context,
                            title = context.getString(R.string.title_permission_denied),
                            message = context.getString(R.string.message_storage_permission),
                        )
                    }
                )

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    NavHost(
                        navController = navController,
                        startDestination = NavGraph.Tokens
                    ) {
                       composable(NavGraph.Tokens) {
                           TokenScreen(
                               mViewModel = mTokenViewModel,
                               navigateToAddTokenScreen = { navController.navigate(NavGraph.AddToken) },
                               navigateToScanTokenScreen = {
                                   if(PermissionUtils.hasPermission(context, PermissionUtils.CAMERA_PERMISSION)) {
                                       navController.navigate(NavGraph.ScanToken)
                                   } else {
                                       mCameraPermissionLauncher.launch(PermissionUtils.CAMERA_PERMISSION)
                                   }
                               },
                               navigateToSettingsScreen = {
                                   if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q || PermissionUtils.hasPermission(context, PermissionUtils.STORAGE_PERMISSION)) {
                                       navController.navigate(NavGraph.Settings)
                                   } else {
                                       mStoragePermissionLauncher.launch(PermissionUtils.STORAGE_PERMISSION)
                                   }
                               }
                           )
                       }

                        composable(NavGraph.AddToken) {
                            AddTokenScreen(mTokenViewModel) {
                                navController.popBackStack()
                            }
                        }

                        composable(NavGraph.ScanToken) {
                            ScanTokenScreen(mTokenViewModel) {
                                navController.popBackStack()
                            }
                        }

                        composable(NavGraph.Settings) {
                            SettingsScreen(mViewModel = mSettingsViewModel) {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}