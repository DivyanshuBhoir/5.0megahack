package div.dablank.hackthonproject50

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun WalletScreen(navController: NavHostController) {
    val auth = FirebaseAuth.getInstance()
    val userId = auth.currentUser?.uid
    val db = FirebaseFirestore.getInstance()

    var nametag by remember { mutableStateOf("Loading...") }
    var balance by remember { mutableStateOf("0") }
    var transactions by remember { mutableStateOf<List<Transaction>>(emptyList()) }

    // Fetch user data from Firestore
    LaunchedEffect(userId) {
        if (userId != null) {
            fetchUserData(db, userId) { fetchedNametag, fetchedBalance ->
                nametag = fetchedNametag
                balance = fetchedBalance
            }
            fetchTransactions(db, userId) { fetchedTransactions ->
                transactions = fetchedTransactions
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black)
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Welcome, $nametag!",
            color = Color.Cyan,
            fontSize = 22.sp,
            fontWeight = FontWeight.Bold
        )
        Spacer(modifier = Modifier.height(16.dp))

        WalletBalance(balance) {
            if (userId != null) {
                addDummyTransaction(db, userId) {
                    fetchUserData(db, userId) { newNametag, newBalance ->
                        balance = newBalance
                    }
                    fetchTransactions(db, userId) { newTransactions ->
                        transactions = newTransactions
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        Text("Transaction History", color = Color.White, fontSize = 18.sp, fontWeight = FontWeight.Bold)
        Spacer(modifier = Modifier.height(8.dp))

        LazyColumn {
            items(transactions) { transaction ->
                TransactionItem(transaction)
            }
        }
    }
}

@Composable
fun WalletBalance(balance: String, onRedeemClick: () -> Unit) {
    Card(
        shape = RoundedCornerShape(15.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(Color(0xFF2A2A2A))
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Current Balance", color = Color.White, fontSize = 16.sp)
            Text("₹$balance", color = Color.Cyan, fontSize = 24.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(8.dp))
            Button(onClick = onRedeemClick) {
                Text("Add Dummy Transaction")
            }
        }
    }
}

data class Transaction(val title: String, val amount: String, val date: String)

@Composable
fun TransactionItem(transaction: Transaction) {
    Card(
        shape = RoundedCornerShape(10.dp),
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(Color(0xFF1E1E1E))
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(transaction.title, color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                Text(transaction.date, color = Color.Gray, fontSize = 12.sp)
            }
            Text(
                transaction.amount,
                color = if (transaction.amount.startsWith("+")) Color.Green else Color.Red,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

// Fetch user balance and name from Firestore
fun fetchUserData(db: FirebaseFirestore, userId: String, onResult: (String, String) -> Unit) {
    db.collection("users").document(userId).get()
        .addOnSuccessListener { document ->
            val nametag = document.getString("nametag") ?: "Unknown"
            val balance = document.getLong("balance")?.toString() ?: "0"
            onResult(nametag, balance)
        }
        .addOnFailureListener { e ->
            Log.e("WalletScreen", "Error fetching user data", e)
        }
}

// Fetch transaction history from Firestore
fun fetchTransactions(db: FirebaseFirestore, userId: String, onResult: (List<Transaction>) -> Unit) {
    db.collection("users").document(userId).collection("transactions")
        .orderBy("date")
        .get()
        .addOnSuccessListener { snapshot ->
            val transactions = snapshot.documents.map { doc ->
                Transaction(
                    title = doc.getString("title") ?: "Unknown",
                    amount = doc.getString("amount") ?: "₹0",
                    date = doc.getString("date") ?: "Unknown"
                )
            }
            onResult(transactions)
        }
        .addOnFailureListener { e ->
            Log.e("WalletScreen", "Error fetching transactions", e)
        }
}

// Add a dummy transaction and update balance
fun addDummyTransaction(db: FirebaseFirestore, userId: String, onComplete: () -> Unit) {
    val newTransaction = Transaction(
        title = "Bonus Reward",
        amount = "+₹100",
        date = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault()).format(Date())
    )

    val userRef = db.collection("users").document(userId)
    val transactionsRef = userRef.collection("transactions")

    db.runTransaction { transaction ->
        val userDoc = transaction.get(userRef)
        val currentBalance = userDoc.getLong("balance") ?: 0
        val newBalance = currentBalance + 100

        transaction.update(userRef, "balance", newBalance)
        transactionsRef.add(newTransaction)
    }.addOnSuccessListener {
        onComplete()
    }.addOnFailureListener { e ->
        Log.e("WalletScreen", "Error adding transaction", e)
    }
}
