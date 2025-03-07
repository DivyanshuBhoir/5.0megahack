package div.dablank.hackthonproject50

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChallengeApp(navController: NavHostController) {
    Scaffold(
        topBar = { Topbar()},
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Black)
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            StoriesSection()
            SocialFeed()
        }
    }
}

@Composable
fun StoriesSection() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        StoryCircle("Your Story")
        StoryCircle("Alice")
        StoryCircle("Bob")
        StoryCircle("Charlie")
    }
}

@Composable
fun StoryCircle(name: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(Color.Gray),
            contentAlignment = Alignment.Center
        ) {
            Text(name.take(1), color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold)
        }
        Text(name, color = Color.White, fontSize = 14.sp)
    }
}

@Composable
fun SocialFeed() {
    val posts = listOf(
        SocialPostData("Alice", "Fitness Challenge", "3h ago"),
        SocialPostData("Bob", "Gaming Showdown", "1d ago"),
        SocialPostData("Charlie", "Coding Marathon", "5h ago"),
        SocialPostData("Diana", "Photography Contest", "2d ago"),
        SocialPostData("Eve", "Speed Chess Battle", "10h ago")
    )

    LazyColumn(modifier = Modifier.fillMaxWidth()) {
        items(posts) { post ->
            SocialPost(post.user, post.challenge, post.timeAgo)
        }
    }
}

data class SocialPostData(val user: String, val challenge: String, val timeAgo: String)

@Composable
fun SocialPost(user: String, challenge: String, timeAgo: String) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(Color(0xFF2A2A2A))
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text("@$user", color = Color.Cyan, fontSize = 16.sp, fontWeight = FontWeight.Bold)
            Text(challenge, color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text(timeAgo, color = Color.Gray, fontSize = 14.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Button(onClick = {}) { Text("Like") }
                Button(onClick = {}) { Text("Comment") }
                Button(onClick = {}) { Text("Share") }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Topbar() {
    TopAppBar(
        title = { Text("Challenge & Reward", color = Color.White, fontSize = 22.sp, fontWeight = FontWeight.Bold) },
        colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF1E1E1E))
    )
}

@Composable
fun BottomNavigationBar(navController: NavHostController) {
    BottomAppBar(containerColor = Color.DarkGray) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            BottomNavItem("Home", "home", navController)
            BottomNavItem("Challenges", "challenges", navController)
            BottomNavItem("Wallet", "wallet", navController)
            BottomNavItem("Profile", "profile", navController)
        }
    }
}
@Composable
fun BottomNavItem(label: String, route: String, navController: NavHostController) {
    Text(
        text = label,
        color = if (navController.currentDestination?.route == route) Color.Cyan else Color.White,
        modifier = Modifier
            .clickable { navController.navigate(route) }
            .padding(8.dp)
    )
}


