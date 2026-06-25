package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.data.database.AppDatabase
import com.example.data.repository.AppRepository
import com.example.ui.MainViewModel
import com.example.ui.MainViewModelFactory
import com.example.ui.screens.*
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
  override fun onCreate(savedInstanceState: Bundle?) {
    super.onCreate(savedInstanceState)
    
    // Initialize Database & Repository
    val database = AppDatabase.getDatabase(this)
    val repository = AppRepository(
      userDao = database.userDao(),
      taxpayerDao = database.taxpayerDao(),
      paymentDao = database.paymentDao(),
      reportDao = database.reportDao()
    )
    
    // Create ViewModel using custom factory
    val viewModel = ViewModelProvider(
      this,
      MainViewModelFactory(application, repository)
    )[MainViewModel::class.java]

    enableEdgeToEdge()
    setContent {
      MyApplicationTheme {
        val navController = rememberNavController()
        
        NavHost(navController = navController, startDestination = "splash") {
          composable("splash") { SplashScreen(navController) }
          composable("login") { LoginScreen(navController, viewModel) }
          composable("register") { RegisterScreen(navController, viewModel) }
          composable("dashboard") { DashboardScreen(navController, viewModel) }
          composable("taxpayers") { TaxpayersScreen(navController, viewModel) }
          composable("collect_form") { CollectFormScreen(navController, viewModel) }
          composable("payments") { PaymentsScreen(navController, viewModel) }
          composable("reports") { ReportsScreen(navController, viewModel) }
          composable("notifications") { NotificationsScreen(navController, viewModel) }
          composable("help") { HelpScreen(navController, viewModel) }
        }
      }
    }
  }
}
