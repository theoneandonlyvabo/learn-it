package com.learnit.app.presentation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.learnit.app.presentation.screen.HomeScreen
import com.learnit.app.presentation.viewmodel.HomeViewModel

@Composable
fun App() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = "home") {
        composable("home") {
            val viewModel = hiltViewModel<HomeViewModel>()
            HomeScreen(viewModel.productDetailsState.collectAsState().value)
        }
    }
}