package com.example.beatsfit

import BottomAppBarWithIcons
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.withContext
import kotlinx.coroutines.tasks.await

data class Contact(
    val name: String,
    val phoneNumber: String
)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Members(account: GoogleSignInAccount, navController: NavController, context: Context) {

    val savedContacts by produceState<List<Contact>>(
        initialValue = emptyList(),
        key1 = account
    ) {
        value = fetchSavedContacts(account)
    }
    LaunchedEffect(savedContacts) {
        if (savedContacts.isEmpty()) {
            delay(1200)
            navController.navigate("friends") {
                popUpTo("members") { inclusive = true }
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Row(modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 15.dp, end = 15.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically)
                {
                    Text("Members", color = Color.White, modifier = Modifier.padding(start = 2.dp))

                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(Color.LightGray)
                    )
                }
                        },
                colors = TopAppBarDefaults.smallTopAppBarColors(containerColor = Color(0xFF0f191f))
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { navController.navigate("friends") }) {
                Icon(imageVector = Icons.Default.Add, contentDescription = "")
            }
        },
        bottomBar = { BottomAppBarWithIcons(navController = navController) }
    ) { padding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .background(Color(0xFF0f191f))
        ) {
            when {
                savedContacts.isEmpty() -> LoadingScreen()
                else -> Column(
                    modifier = Modifier
                        .background(Color(0xFF0f191f))
                        .fillMaxSize()
                        .padding(top = 15.dp)
                ) {
                    Column {
                        val contactList = savedContacts.take(4) // Handle up to 4 contacts dynamically
                        val chunkedContacts = contactList.chunked(2) // Split into rows of 2

                        chunkedContacts.forEach { rowContacts ->
                            Row {
                                rowContacts.forEach { contact ->
                                    SavedContactCard(contact, context)
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(15.dp))

                    Row {
                        SOSButton(
                            text = "SOS Message",
                            onClick = { /* Handle message action */ },
                            modifier = Modifier.weight(1f)
                        )
                        SOSButton(
                            text = "SOS Call",
                            onClick = { /* Handle call action */ },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun SOSButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .height(58.dp)
            .width(197.dp)
            .padding(3.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Color.Transparent,
        ),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0x60FF0000)
        ),
        shape = RoundedCornerShape(100.dp)
    ) {
        Text(
            text = text,
            color = Color.White
        )
    }
}


@SuppressLint("QueryPermissionsNeeded")
@Composable
fun SavedContactCard(
    contact: Contact,
    context: Context,
) {
    val firstLetter = contact.name.firstOrNull()?.toUpperCase().toString()

    Card(
        modifier = Modifier
            .width(196.dp)
            .padding(8.dp)
            .height(270.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0x5EDA9D5F)
        ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732))
    ) {
        Column(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .padding(20.dp),
            verticalArrangement = Arrangement.spacedBy(15.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(100.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFDBB3FF))
                    .align(Alignment.CenterHorizontally),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = firstLetter,
                    fontSize = 34.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xC60F191F)
                )
            }

            Spacer(modifier = Modifier.height(5.dp))

            Text(
                modifier = Modifier
                    .height(50.dp)
                    .fillMaxWidth(),
                text = contact.name,
                fontSize = 18.sp,
                color = Color(0xD3FFFFFF),
                textAlign = TextAlign.Center,
                maxLines = 2, // Limit to one line
                overflow = TextOverflow.Ellipsis // Truncate with ellipsis
            )


            Row(
                horizontalArrangement = Arrangement.spacedBy(30.dp)
            ) {

                Image(
                    painter = painterResource(R.drawable.call),
                    contentDescription = "Call Icon",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${contact.phoneNumber}")
                            }
                            context.startActivity(intent)
                        }
                )
                Image(
                    painter = painterResource(R.drawable.baseline_location_pin_24),
                    contentDescription = "Message Icon",
                    modifier = Modifier
                        .size(28.dp)
                        .clickable {

                        }
                )
            }
        }
    }
}

suspend fun fetchSavedContacts(account: GoogleSignInAccount): List<Contact> {
    return withContext(Dispatchers.IO) {
        val savedContacts = mutableListOf<Contact>()
        val firestore = FirebaseFirestore.getInstance()
        val userId = account.id

        if (userId != null) {
            try {
                val documentSnapshot = firestore.collection("users")
                    .document(userId)
                    .get()
                    .await()

                val contactsData = documentSnapshot.get("addedMembers") as? List<Map<String, String>>
                contactsData?.forEach { contactData ->
                    val name = contactData["name"] ?: "Unknown"
                    val phoneNumber = contactData["phoneNumber"] ?: "Unknown"
                    savedContacts.add(Contact(name, phoneNumber))
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        savedContacts
    }
}

@Composable
fun LoadingScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF0f191f))
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Loading contacts...",
            fontSize = 18.sp,
            color = Color(0xD3FFFFFF)
        )
    }
}