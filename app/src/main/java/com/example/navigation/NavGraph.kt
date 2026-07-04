package com.example.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.toRoute
import com.example.data.VaultRepository
import com.example.ui.calculator.CalculatorScreen
import com.example.ui.calculator.CalculatorViewModel
import com.example.ui.setup.SetupScreen
import com.example.ui.setup.SetupViewModel
import com.example.ui.vault.CategoryDetailScreen
import com.example.ui.vault.CameraScreen
import com.example.ui.vault.SettingsScreen
import com.example.ui.vault.VaultHomeScreen
import com.example.ui.vault.VaultViewModel
import com.example.ui.vault.SettingsViewModel
import com.example.ui.vault.ContactsScreen
import com.example.ui.vault.FileManagerScreen
import com.example.ui.vault.NotesScreen
import com.example.ui.vault.AppLockScreen
import com.example.ui.vault.BrowserScreen
import com.example.ui.vault.WallpaperScreen
import com.example.ui.vault.TrashScreen
import com.example.ui.vault.IntruderCameraScreen
import com.example.navigation.SplashScreen

@Composable
fun NavGraph(
    vaultRepository: VaultRepository,
    calculatorViewModel: CalculatorViewModel,
    setupViewModel: SetupViewModel,
    vaultViewModel: VaultViewModel,
    settingsViewModel: SettingsViewModel,
    onRouteChanged: (String?) -> Unit = {}
) {
    val navController = rememberNavController()
    val isSetupComplete by vaultRepository.isSetupComplete.collectAsStateWithLifecycle(initialValue = false)

    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    androidx.compose.runtime.LaunchedEffect(currentBackStackEntry) {
        onRouteChanged(currentBackStackEntry?.destination?.route)
    }

    NavHost(
        navController = navController,
        startDestination = SplashRoute
    ) {
        composable<SplashRoute> {
            SplashScreen(onTimeout = {
                val destination = if (isSetupComplete) CalculatorRoute else SetupRoute
                navController.navigate(destination) {
                    popUpTo(SplashRoute) { inclusive = true }
                }
            })
        }
        composable<CalculatorRoute> {
            CalculatorScreen(
                viewModel = calculatorViewModel,
                onUnlock = {
                    navController.navigate(VaultHomeRoute) {
                        popUpTo(CalculatorRoute) { inclusive = true }
                    }
                }
            )
        }
        composable<SetupRoute> {
            SetupScreen(
                viewModel = setupViewModel,
                onComplete = {
                    navController.navigate(CalculatorRoute) {
                        popUpTo(SetupRoute) { inclusive = true }
                    }
                }
            )
        }
        composable<VaultHomeRoute> {
            VaultHomeScreen(
                viewModel = vaultViewModel,
                onNavigate = { route ->
                    navController.navigate(route)
                }
            )
        }
        composable<ContactsRoute> {
            ContactsScreen(onBack = { navController.popBackStack() })
        }
        composable<FileManagerRoute> {
            FileManagerScreen(
                viewModel = vaultViewModel,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable<NotesRoute> {
            NotesScreen(onBack = { navController.popBackStack() })
        }
        composable<AppLockRoute> {
            AppLockScreen(onBack = { navController.popBackStack() })
        }
        composable<BrowserRoute> {
            BrowserScreen(onBack = { navController.popBackStack() })
        }
        composable<WallpaperRoute> {
            WallpaperScreen(onBack = { navController.popBackStack() })
        }
        composable<TrashRoute> {
            TrashScreen(onBack = { navController.popBackStack() })
        }
        composable<IntruderCameraRoute> {
            IntruderCameraScreen(onBack = { navController.popBackStack() })
        }
        composable<CategoryDetailRoute> { backStackEntry ->
            val route: CategoryDetailRoute = backStackEntry.toRoute()
            CategoryDetailScreen(
                type = route.type,
                viewModel = vaultViewModel,
                onBack = { navController.popBackStack() },
                onOpenCamera = { navController.navigate(CameraRoute) }
            )
        }
        composable<SettingsRoute> {
            SettingsScreen(
                viewModel = settingsViewModel,
                setupViewModel = setupViewModel,
                onBack = { navController.popBackStack() },
                onNavigate = { route -> navController.navigate(route) }
            )
        }
        composable<CameraRoute> {
            CameraScreen(
                viewModel = vaultViewModel,
                onBack = { navController.popBackStack() }
            )
        }
    }
}
