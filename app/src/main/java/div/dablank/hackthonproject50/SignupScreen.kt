package div.dablank.hackthonproject50

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import android.widget.Toast
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

@Composable
fun SignupScreen(navController: NavController) {
    var nametag by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var confirmPassword by remember { mutableStateOf("") }
    var errorMessage by remember { mutableStateOf("") }

    val auth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val context = LocalContext.current

    Column(modifier = Modifier.padding(16.dp)) {
        Text(text = "SignUp", style = MaterialTheme.typography.headlineLarge)
        Spacer(modifier = Modifier.height(16.dp))

        OutlinedTextField(
            value = nametag,
            onValueChange = { nametag = it },
            label = { Text("Nametag") }
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Email)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(8.dp))

        OutlinedTextField(
            value = confirmPassword,
            onValueChange = { confirmPassword = it },
            label = { Text("Confirm Password") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
        )
        Spacer(modifier = Modifier.height(8.dp))

        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = MaterialTheme.colorScheme.error)
            Spacer(modifier = Modifier.height(8.dp))
        }

        Button(onClick = {
            if (nametag.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                errorMessage = "Please fill in all fields"
            } else if (password != confirmPassword) {
                errorMessage = "Passwords do not match"
            } else {
                checkNametagUnique(nametag) { isUnique ->
                    if (isUnique) {
                        auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener { task ->
                                if (task.isSuccessful) {
                                    val userId = task.result?.user?.uid
                                    if (userId != null) {
                                        val user = hashMapOf(
                                            "nametag" to nametag,
                                            "email" to email,
                                            "balance" to 1000 // Initial balance
                                        )

                                        db.collection("users").document(userId).set(user)
                                            .addOnSuccessListener {
                                                Toast.makeText(context, "Signup Successful!", Toast.LENGTH_SHORT).show()
                                                navController.navigate("login")
                                            }
                                            .addOnFailureListener { e ->
                                                errorMessage = "Error saving user: ${e.message}"
                                            }
                                    }
                                } else {
                                    errorMessage = "Signup Failed: ${task.exception?.message}"
                                }
                            }
                    } else {
                        errorMessage = "Nametag already taken, choose another one."
                    }
                }
            }
        }) {
            Text("Sign Up")
        }
        Spacer(modifier = Modifier.height(8.dp))

        TextButton(onClick = { navController.navigate("login") }) {
            Text("Already have an account? Login")
        }
    }
}

/**
 * Function to check if the nametag is already taken in Firestore
 */
fun checkNametagUnique(nametag: String, onResult: (Boolean) -> Unit) {
    val db = FirebaseFirestore.getInstance()
    db.collection("users")
        .whereEqualTo("nametag", nametag)
        .get()
        .addOnSuccessListener { querySnapshot ->
            onResult(querySnapshot.isEmpty) // If empty, nametag is unique
        }
        .addOnFailureListener {
            onResult(false) // Assume not unique if error occurs
        }
}
