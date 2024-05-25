package com.appdev.scanner.ui.navigation

import androidx.annotation.OptIn
import androidx.camera.core.ExperimentalGetImage
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.appdev.scanner.Utils.BarcodeScanner
import com.appdev.scanner.Utils.ScreenRoutes
import com.appdev.scanner.ViewModel.CustomViewModel
import com.appdev.scanner.ui.Screens.MainScreen

@OptIn(ExperimentalGetImage::class)
@Composable
fun navigationCompose(barcodeScanner: BarcodeScanner) {
    val navController = rememberNavController()
    val customViewModel: CustomViewModel = hiltViewModel()
    NavHost(navController = navController,
        startDestination = ScreenRoutes.ViewData.route,
        enterTransition = {
            fadeIn(animationSpec = tween(220, delayMillis = 90)) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(
                    220, delayMillis = 90
                )
            )
        },
        exitTransition = {
            fadeOut(animationSpec = tween(90))
        },
        popEnterTransition = {
            fadeIn(animationSpec = tween(220, delayMillis = 90)) + scaleIn(
                initialScale = 0.92f,
                animationSpec = tween(
                    220, delayMillis = 90
                )
            )
        },
        popExitTransition = {
            fadeOut(animationSpec = tween(90))
        }) {

        composable(
            ScreenRoutes.ViewData.route
        ) {
            MainScreen(controller = navController,customViewModel,barcodeScanner)
        }

    }

}

