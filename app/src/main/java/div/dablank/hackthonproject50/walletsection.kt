import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.speech.RecognizerIntent
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import div.dablank.hackthonproject50.BottomNavigationBar
import div.dablank.hackthonproject50.R
import div.dablank.hackthonproject50.Topbar
import java.text.SimpleDateFormat
import java.util.*
@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun WalletScreen(navController: NavHostController) {
    Box(modifier = Modifier.fillMaxSize()) {
        Image(
            painter = painterResource(R.drawable.ok),
            contentDescription = "background image",
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        Scaffold(
            topBar = { Topbar() },
            bottomBar = { BottomNavigationBar(navController) },
            containerColor = Color.Transparent  // Ensure scaffold does not block background
        ) { paddingValues ->
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                WalletContent(navController)
            }
        }
    }
}

@Composable
fun WalletContent(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid ?: return

    val db = FirebaseFirestore.getInstance()

    var nametag by remember { mutableStateOf("Loading...") }
    var balance by remember { mutableStateOf("₹0") }
    var transactions by remember { mutableStateOf<List<UserTransaction>>(emptyList()) }
    var transactionMessage by remember { mutableStateOf("") }
    var inputCommand by remember { mutableStateOf("") }

    val voiceRecognitionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            val data = result.data
            val spokenText =
                data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.firstOrNull()
            if (!spokenText.isNullOrEmpty()) {
                inputCommand = spokenText
            }
        }
    }

    LaunchedEffect(userId) {
        fetchUserData(db, userId) { fetchedNametag, fetchedBalance ->
            nametag = fetchedNametag
            balance = "₹$fetchedBalance"
        }
        fetchTransactions(db, userId) { fetchedTransactions ->
            transactions = fetchedTransactions
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.7f)) // Add overlay for readability
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            "Welcome, $nametag!",
            color = Color.Cyan,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            "Balance: $balance",
            color = Color.Green,
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(16.dp))
        OutlinedTextField(
            value = inputCommand,
            onValueChange = { inputCommand = it },
            label = { Text("Enter Command (e.g. Send ₹500 to John)") },
            modifier = Modifier.fillMaxWidth()
        )
        Spacer(modifier = Modifier.height(8.dp))

        Button(onClick = {
            processTextCommand(db, userId, inputCommand) { resultMessage ->
                transactionMessage = resultMessage
                fetchUserData(db, userId) { _, newBalance -> balance = "₹$newBalance" }
                fetchTransactions(db, userId) { newTransactions ->
                    transactions = newTransactions
                }
            }
        }) {
            Text("Process Command")
        }

        Button(onClick = {
            val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                putExtra(
                    RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM
                )
                putExtra(RecognizerIntent.EXTRA_PROMPT, "Speak your command")
            }
            voiceRecognitionLauncher.launch(intent)
        }) {
            Text("🎤 Voice Command")
        }

        Spacer(modifier = Modifier.height(8.dp))
        Text(transactionMessage, color = Color.White, fontSize = 16.sp)

        LazyColumn {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

fun fetchUserData(db: FirebaseFirestore, userId: String, onResult: (String, String) -> Unit) {
    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            if (document.exists()) {
                val nametag = document.getString("name") ?: "Unknown"
                val balance = document.getLong("balance")?.toString() ?: "0"
                onResult(nametag, balance)
            } else {
                onResult("Unknown", "0")
            }
        }
        .addOnFailureListener {
            onResult("Error", "0")
        }
}

fun fetchTransactions(db: FirebaseFirestore, userId: String, onResult: (List<UserTransaction>) -> Unit) {
    db.collection("users").document(userId).collection("transactions")
        .orderBy("date")
        .get()
        .addOnSuccessListener { documents ->
            val transactions = documents.mapNotNull { doc ->
                val title = doc.getString("title") ?: "Unknown"
                val amount = doc.getString("amount") ?: "₹0"
                val date = doc.getString("date") ?: "N/A"
                UserTransaction(title, amount, date)
            }
            onResult(transactions)
        }
        .addOnFailureListener {
            onResult(emptyList())
        }
}

data class UserTransaction(
    val title: String,
    val amount: String,
    val date: String
)

@Composable
fun TransactionItem(transaction: UserTransaction) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.DarkGray)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = transaction.title,
                color = Color.White,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = transaction.amount,
                color = if (transaction.amount.startsWith("-")) Color.Red else Color.Green,
                fontSize = 16.sp
            )
            Text(
                text = transaction.date,
                color = Color.Gray,
                fontSize = 14.sp
            )
        }
    }
}

fun sendMoney(
    db: FirebaseFirestore,
    senderId: String,
    recipientName: String,
    amount: Double,
    onResult: (String) -> Unit
) {
    val senderRef = db.collection("users").document(senderId)

    db.runTransaction { transaction ->
        val senderDoc = transaction.get(senderRef)
        val senderBalance = senderDoc.getLong("balance") ?: 0

        if (amount > senderBalance) throw Exception("Insufficient balance!")

        val newSenderBalance = senderBalance - amount
        transaction.update(senderRef, "balance", newSenderBalance)

        val newTransaction = hashMapOf(
            "title" to "Sent to $recipientName",
            "amount" to "-₹$amount",
            "date" to SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
        )

        senderRef.collection("transactions").add(newTransaction)
    }.addOnSuccessListener {
        onResult("Sent ₹$amount to $recipientName successfully!")
    }.addOnFailureListener {
        onResult("Transaction failed!")
    }
}

fun processTextCommand(db: FirebaseFirestore, userId: String, command: String, onResult: (String) -> Unit) {
    val regex = Regex("Send ₹?(\\d+) to (.+)", RegexOption.IGNORE_CASE)
    val match = regex.find(command)

    if (match != null) {
        val amount = match.groupValues[1].toDoubleOrNull()
        val recipientName = match.groupValues[2]

        if (amount != null) {
            sendMoney(db, userId, recipientName, amount, onResult)
        } else {
            onResult("Invalid amount!")
        }
    } else {
        onResult("Invalid command! Use 'Send ₹500 to John'")
    }
}
