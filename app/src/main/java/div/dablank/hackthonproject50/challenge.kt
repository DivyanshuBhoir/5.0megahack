package div.dablank.hackthonproject50

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun ChallengesScreen(navController: NavHostController) {
    Scaffold(
        topBar = { Topbar() },
        bottomBar = { BottomNavigationBar(navController) }
    ) { paddingValues ->


            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color.Black, Color(0xFF1A1A1A))
                        )
                    )
                    .padding(paddingValues)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "🔥 Challenges 🔥",
                    color = Color.Cyan,
                    fontSize = 28.sp,
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
    }


data class Challenge(val title: String, val description: String)

val challengeList = listOf(
    Challenge("🏃 10K Steps Challenge", "Walk 10,000 steps today and earn rewards!"),
    Challenge("💪 Fitness Challenge", "Complete a 30-minute workout session."),
    Challenge("🎮 Gaming Showdown", "Win 5 matches in your favorite game."),
    Challenge("🥗 Healthy Eating", "Eat only home-cooked meals for a day."),
    Challenge("🧠 Daily Quiz", "Answer 10 trivia questions correctly.")
)

@Composable
fun ChallengeCard(challenge: Challenge) {
    val db = FirebaseFirestore.getInstance()
    val user = FirebaseAuth.getInstance().currentUser
    var completed by remember { mutableStateOf(false) }
    var failed by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(Color(0xFF121212))
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Image(
                painter = painterResource(R.drawable.ok),
                contentDescription = "Background image",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )

            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    challenge.title,
                    color = Color.Cyan,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(challenge.description, color = Color.White, fontSize = 14.sp)
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Button(
                        onClick = {
                            updateBalance(db, user?.uid, 100)
                            completed = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Green)
                    ) {
                        Text("✅ Complete")
                    }
                    Button(
                        onClick = {
                            updateBalance(db, user?.uid, -50)
                            failed = true
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                    ) {
                        Text("❌ Failed")
                    }
                }
                AnimatedVisibility(visible = completed, enter = fadeIn(), exit = fadeOut()) {
                    Text(
                        "🎉 Challenge Completed! +100💰",
                        color = Color.Green,
                        fontWeight = FontWeight.Bold
                    )
                }
                AnimatedVisibility(visible = failed, enter = fadeIn(), exit = fadeOut()) {
                    Text(
                        "😢 Challenge Failed! -50💰",
                        color = Color.Red,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}
fun updateBalance(db: FirebaseFirestore, userId: String?, amount: Int) {
    if (userId == null) return
    val userRef = db.collection("users").document(userId)

    db.runTransaction { transaction ->
        val snapshot = transaction.get(userRef)
        val currentBalance = snapshot.getLong("balance") ?: 0
        transaction.update(userRef, "balance", currentBalance + amount)
    }
}
