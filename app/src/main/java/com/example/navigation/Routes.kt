package com.example.navigation

import kotlinx.serialization.Serializable

@Serializable
object SplashRoute

@Serializable
object CalculatorRoute

@Serializable
object SetupRoute

@Serializable
object VaultHomeRoute

@Serializable
data class CategoryDetailRoute(val type: String)

@Serializable
object NotesRoute

@Serializable
object ContactsRoute

@Serializable
object FileManagerRoute

@Serializable
object AppLockRoute

@Serializable
object BrowserRoute

@Serializable
object WallpaperRoute

@Serializable
object TrashRoute

@Serializable
object IntruderCameraRoute

@Serializable
object SettingsRoute

@Serializable
object CameraRoute
