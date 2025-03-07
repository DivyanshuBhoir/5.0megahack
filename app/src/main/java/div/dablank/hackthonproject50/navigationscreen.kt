package div.dablank.hackthonproject50

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

@Composable
fun AppNavigator() {
    val navController = rememberNavController()
    Scaffold(
        topBar = {   },
        bottomBar = { }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            NavHost(navController, startDestination = "login") {
                composable("login") { LoginScreen(navController) }
                composable("signup") { SignupScreen(navController) }
                composable("home") { ChallengeApp(navController) }
                composable("challenges") { ChallengesScreen(navController) }
                composable("wallet") { WalletScreen(navController) }
                composable("profile") { ProfileScreen(navController) }
            }
        }
    }
}


//
//@OptIn(ExperimentalMaterial3Api::class)
//@Composable
//fun Topbar() {
//    TopAppBar(
//        title = { Text("Challenge & Reward", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold) },
//        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF1E1E1E))
//    )
//}
//
//@Composable
//fun BottomNavigationBar(navController: NavHostController) {
//    BottomAppBar(containerColor = Color.DarkGray) {
//        Row(
//            modifier = Modifier.fillMaxWidth(),
//            horizontalArrangement = Arrangement.SpaceEvenly
//        ) {
//            BottomNavItem("Home", "home", navController)
//            BottomNavItem("Challenges", "challenges", navController)
//            BottomNavItem("Wallet", "wallet", navController)
//            BottomNavItem("Profile", "profile", navController)
//        }
//    }
//}
//@Composable
//fun BottomNavItem(label: String, route: String, navController: NavHostController) {
//    Text(
//        text = label,
//        color = if (navController.currentDestination?.route == route) Color.Cyan else Color.White,
//        modifier = Modifier
//            .clickable { navController.navigate(route) }
//            .padding(8.dp)
//    )
//}
