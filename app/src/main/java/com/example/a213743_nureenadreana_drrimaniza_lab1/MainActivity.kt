package com.example.a213743_nureenadreana_drrimaniza_lab1

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.AddItemForm
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.BookingScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.CommunityScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.EditProfileScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.HomeScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.LoginScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.MyBookingsScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.MyListingsScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.NotificationScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.NutritionScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.ProfileScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.RegisterScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.screens.SavedItemsScreen
import com.example.a213743_nureenadreana_drrimaniza_lab1.ui.theme.A213743_NureenAdreana_DrRimaniza_Lab1Theme
import com.example.a213743_nureenadreana_drrimaniza_lab1.viewmodel.AppViewModel
import com.google.firebase.auth.FirebaseAuth

// --- NAVIGATION ROUTES ---
sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Home : Screen("home")
    object Add : Screen("add")
    object Profile : Screen("profile")
    object EditProfile : Screen("edit_profile")
    object Notifications : Screen("notifications")
    object Community : Screen("community")
    object MyBookings : Screen("my_bookings")
    object MyListings : Screen("my_listings")
    object SavedItems : Screen("saved_items")
    object Booking : Screen("booking/{itemName}") {
        fun createRoute(itemName: String) = "booking/$itemName"
    }
    object EditItem : Screen("edit_item")
    object Nutrition : Screen("nutrition/{itemName}") {
        fun createRoute(itemName: String) = "nutrition/$itemName"
    }
}

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            A213743_NureenAdreana_DrRimaniza_Lab1Theme {
                val navController = rememberNavController()
                val viewModel: AppViewModel = viewModel()

                // Check if user is already logged in
                val currentUser = FirebaseAuth.getInstance().currentUser
                val startDestination = if (currentUser != null) Screen.Home.route else Screen.Login.route

                // Sync profile data from Firestore on launch
                LaunchedEffect(currentUser) {
                    if (currentUser != null) {
                        viewModel.syncProfile()
                    }
                }

                val navBackStackEntry by navController.currentBackStackEntryAsState()
                val currentRoute = navBackStackEntry?.destination?.route
                val showBottomBar = currentRoute !in listOf(Screen.Login.route, Screen.Register.route)

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    bottomBar = {
                        if (showBottomBar) BottomNavBar(navController, viewModel)
                    }
                ) { innerPadding ->
                    NavHost(
                        navController = navController,
                        startDestination = startDestination,
                        modifier = Modifier.padding(innerPadding)
                    ) {
                        // Auth screens (no bottom bar)
                        composable(Screen.Login.route) {
                            LoginScreen(navController, viewModel)
                        }
                        composable(Screen.Register.route) {
                            RegisterScreen(navController, viewModel)
                        }

                        // App screens
                        composable(
                            route = "${Screen.Home.route}?focusSearch={focusSearch}",
                            arguments = listOf(navArgument("focusSearch") { defaultValue = false; type = NavType.BoolType })
                        ) { backStackEntry ->
                            val focusSearch = backStackEntry.arguments?.getBoolean("focusSearch") ?: false
                            HomeScreen(navController, viewModel, shouldFocusSearch = focusSearch)
                        }
                        composable(Screen.Add.route) { AddItemForm(navController, viewModel) }
                        composable(Screen.Profile.route) { ProfileScreen(navController, viewModel) }
                        composable(Screen.EditProfile.route) { EditProfileScreen(navController, viewModel) }
                        composable(Screen.Notifications.route) { NotificationScreen(navController) }
                        composable(Screen.Community.route) { CommunityScreen(navController) }
                        composable(Screen.MyBookings.route) { MyBookingsScreen(navController, viewModel) }
                        composable(Screen.MyListings.route) { MyListingsScreen(navController, viewModel) }
                        composable(Screen.SavedItems.route) { SavedItemsScreen(navController, viewModel) }
                        composable(
                            route = Screen.Booking.route,
                            arguments = listOf(navArgument("itemName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
                            BookingScreen(navController, viewModel, itemName)
                        }
                        composable(Screen.EditItem.route) {
                            AddItemForm(navController, viewModel)
                        }
                        composable(
                            route = Screen.Nutrition.route,
                            arguments = listOf(navArgument("itemName") { type = NavType.StringType })
                        ) { backStackEntry ->
                            val itemName = backStackEntry.arguments?.getString("itemName") ?: ""
                            NutritionScreen(navController, itemName)
                        }
                    }
                }
            }
        }
    }
}


@Composable
fun BottomNavBar(navController: NavHostController, viewModel: AppViewModel) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    Surface(modifier = Modifier.fillMaxWidth(), shadowElevation = 16.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .navigationBarsPadding()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavBarItem(Screen.Home.route, Icons.Default.Home, "Home", currentRoute, navController)
            NavBarItem("${Screen.Home.route}?focusSearch=true", Icons.Default.Search, "Search", currentRoute, navController)
            FloatingActionButton(
                onClick = { navController.navigate(Screen.Add.route) },
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(Icons.Default.Add, null, tint = Color.White)
            }
            NavBarItem(Screen.Community.route, Icons.Default.AccountBox, "Community", currentRoute, navController)
            NavBarItem(Screen.Profile.route, Icons.Default.Person, "Profile", currentRoute, navController)
        }
    }
}

@Composable
fun NavBarItem(route: String, icon: ImageVector, label: String, currentRoute: String?, navController: NavController) {
    val selected = currentRoute?.startsWith(route.split("?")[0]) == true
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.clickable { navController.navigate(route) }
    ) {
        Icon(icon, null, tint = if (selected) MaterialTheme.colorScheme.primary else Color.Gray)
        Text(label, fontSize = 10.sp, color = if (selected) MaterialTheme.colorScheme.primary else Color.Gray)
    }
}
