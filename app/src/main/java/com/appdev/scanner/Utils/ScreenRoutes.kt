package com.appdev.scanner.Utils

sealed class ScreenRoutes(val route: String) {
    object ViewData : ScreenRoutes("viewData")
    object BarCodeParentScreen : ScreenRoutes("parentScreen")
    object BarCodeCameraScreen: ScreenRoutes("CameraScreen")
}
