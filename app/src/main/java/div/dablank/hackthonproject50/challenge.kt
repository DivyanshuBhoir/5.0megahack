package div.dablank.hackthonproject50

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.NavHostController
@Composable
fun ChallengesScreen(navController: NavHostController) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Challenges",
            color = Color.White,
            fontSize = 24.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn {
            items(challengeList) { challenge ->
                ChallengeCard(challenge)
            }
        }
    }
}

data class Challenge(val title: String, val description: String)

val challengeList = listOf(
    Challenge("10K Steps Challenge", "Walk 10,000 steps today and earn rewards!"),
    Challenge("Fitness Challenge", "Complete a 30-minute workout session."),
    Challenge("Gaming Showdown", "Win 5 matches in your favorite game."),
    Challenge("Healthy Eating", "Eat only home-cooked meals for a day."),
    Challenge("Daily Quiz", "Answer 10 trivia questions correctly.")
)

@Composable
fun ChallengeCard(challenge: Challenge) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(Color(0xFF2A2A2A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(challenge.title, color = Color.Cyan, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(challenge.description, color = Color.White, fontSize = 14.sp)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = { /* Handle join challenge */ }) {
                Text("Join Challenge")
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
