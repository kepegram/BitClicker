package com.example.gameappdev

import android.content.Context
import androidx.navigation.compose.rememberNavController
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import kotlinx.coroutines.launch

@Composable
fun AppMainScreen(
    context: Context,
    callCounter: MutableState<Int>,
    displayCounter: MutableState<Int>,
    currentLevel: MutableState<Int>
) {
    val navController = rememberNavController()

    Surface(color = MaterialTheme.colors.background) {
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()
        val openDrawer = {
            scope.launch {
                drawerState.open()
            }
        }
        ModalDrawer(
            drawerState = drawerState,
            gesturesEnabled = drawerState.isOpen,
            drawerContent = {
                Drawer(
                    onDestinationClicked = { route ->
                        scope.launch {
                            drawerState.close()
                        }
                        navController.navigate(route) {
                            popUpTo = navController.graph.startDestinationId
                            launchSingleTop = true
                        }
                    }
                )
            }
        ) {
            NavHost(
                navController = navController,
                startDestination = "splash_screen"
            ) {
                composable("splash_screen") {
                    SplashScreen(navController = navController)
                }
                composable(NavigationItem.Home.route) {
                    HomeScreen(navController = navController, openDrawer = { openDrawer() }, context = context, callCounter)
                }
                composable(NavigationItem.Settings.route) {
                    SettingsScreen(navController = navController)
                }
                composable(NavigationItem.NewGame.route) {
                    NewGameScreen(navController = navController, context = context, displayCounter, currentLevel)
                }
                composable(DrawerScreens.Credits.route) {
                    CreditsScreen(navController = navController)
                }
            }
        }
    }
}