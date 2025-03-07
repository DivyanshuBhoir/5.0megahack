package div.dablank.hackthonproject50

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

@Composable
fun ProfileScreen(navController: NavHostController) {
    var email by remember { mutableStateOf("Loading...") }
    var nametag by remember { mutableStateOf("@unknown") }
    var isDarkTheme by remember { mutableStateOf(true) }

    val auth = FirebaseAuth.getInstance()
    val firestore = FirebaseFirestore.getInstance()
    val userId = auth.currentUser?.uid
    val clipboardManager = LocalClipboardManager.current
    val context = LocalContext.current

    LaunchedEffect(userId) {
        if (userId != null) {
            firestore.collection("users").document(userId).get()
                .addOnSuccessListener { document ->
                    if (document.exists()) {
                        email = document.getString("email") ?: "No Email"
                        nametag = document.getString("nametag") ?: "@unknown"
                    }
                }
                .addOnFailureListener { exception ->
                    Log.e("ProfileScreen", "Error fetching user data", exception)
                }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(if (isDarkTheme) Color.Black else Color.White)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(16.dp))

        // Profile Picture
        ProfilePicture()

        Spacer(modifier = Modifier.height(12.dp))

        Text(email, color = if (isDarkTheme) Color.White else Color.Black, fontSize = 20.sp, fontWeight = FontWeight.Bold)
        Text(nametag, color = if (isDarkTheme) Color.Gray else Color.DarkGray, fontSize = 18.sp)

        Spacer(modifier = Modifier.height(16.dp))

        // Copy Email Button
        ActionButton(text = "Copy Email", color = Color.Cyan) {
            clipboardManager.setText(androidx.compose.ui.text.AnnotatedString(email))
            Toast.makeText(context, "Email copied!", Toast.LENGTH_SHORT).show()
        }

        // Edit Profile Button
        ActionButton(text = "Edit Profile", color = Color.Blue) {
            // Navigate to edit profile screen
            navController.navigate("edit_profile")
        }

        // Change Password
        ActionButton(text = "Change Password", color = Color.Green) {
            // Navigate to change password screen
            navController.navigate("change_password")
        }

        // Toggle Theme Button
        ActionButton(text = "Toggle Theme", color = Color.Magenta) {
            isDarkTheme = !isDarkTheme
        }

        // Logout Button
        ActionButton(text = "Logout", color = Color.Red) {
            auth.signOut()
            navController.navigate("login") // Navigate back to login screen
        }

        // Delete Account Button
        ActionButton(text = "Delete Account", color = Color.Yellow) {
            deleteAccount(auth, firestore, userId, context, navController)
        }
    }
}

// Profile Picture Component
@Composable
fun ProfilePicture() {
    Box(
        modifier = Modifier
            .size(100.dp)
            .clip(CircleShape)
            .background(Color.Gray),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = android.R.drawable.ic_menu_gallery),
            contentDescription = "Profile Picture",
            modifier = Modifier.size(90.dp),
            contentScale = ContentScale.Crop
        )
    }
}

// Reusable Button
@Composable
fun ActionButton(text: String, color: Color, onClick: () -> Unit) {
    TextButton(
        onClick = onClick,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(text, color = color, fontSize = 16.sp)
    }
}

// Delete Account Function
fun deleteAccount(auth: FirebaseAuth, firestore: FirebaseFirestore, userId: String?, context: android.content.Context, navController: NavHostController) {
    userId?.let {
        firestore.collection("users").document(it).delete()
            .addOnSuccessListener {
                auth.currentUser?.delete()?.addOnSuccessListener {
                    Toast.makeText(context, "Account deleted successfully", Toast.LENGTH_SHORT).show()
                    navController.navigate("login")
                }?.addOnFailureListener {
                    Toast.makeText(context, "Failed to delete account", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(context, "Error deleting user data", Toast.LENGTH_SHORT).show()
            }
    }
}
