package com.tomtruyen.soteria.android

import android.content.pm.PackageManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.tomtruyen.soteria.android.ui.screens.TokenScreen
import com.tomtruyen.soteria.android.ui.screens.add.AddTokenScreen
import com.tomtruyen.soteria.android.ui.screens.scan.ScanTokenScreen
import com.tomtruyen.soteria.android.ui.theme.SoteriaAndroidTheme
import com.tomtruyen.soteria.android.utils.DialogUtils

class MainActivity : ComponentActivity() {
    private val cameraPermission = android.Manifest.permission.CAMERA

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            SoteriaAndroidTheme {
                val context = LocalContext.current
                val navController = rememberNavController()

                val cameraPermissionLauncher = rememberLauncherForActivityResult(
                    ActivityResultContracts.RequestPermission()
                ) { isGranted ->
                    if(isGranted) {
                        navController.navigate(NavGraph.ScanToken)
                    } else {
                        DialogUtils.showErrorDialog(
                            context = context,
                            title = context.getString(R.string.title_camera_permission),
                            message = context.getString(R.string.mesage_camera_permission)
                        )
                    }
                }

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
                               navigateToAddTokenScreen = { navController.navigate(NavGraph.AddToken) },
                               navigateToScanTokenScreen = {
                                   val hasCameraPermissions = ContextCompat.checkSelfPermission(context, cameraPermission) == PackageManager.PERMISSION_GRANTED

                                   if(hasCameraPermissions) {
                                       navController.navigate(NavGraph.ScanToken)
                                   } else {
                                       cameraPermissionLauncher.launch(cameraPermission)
                                   }
                               }
                           )
                       }

                        composable(NavGraph.AddToken) {
                            AddTokenScreen {
                                navController.popBackStack()
                            }
                        }

                        composable(NavGraph.ScanToken) {
                            ScanTokenScreen {
                                navController.popBackStack()
                            }
                        }
                    }
                }
            }
        }
    }
}