package com.example.beatsfit.view

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
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
import com.example.beatsfit.R
import com.example.beatsfit.viewmodel.UserViewModel
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
fun Members(
    account: GoogleSignInAccount,
    navController: NavController,
    context: Context,
    userViewModel: UserViewModel
) {

    val _savedContacts by produceState<List<Contact>?>( // Make nullable
        initialValue = null, // Initial value is null (loading state)
        key1 = account.id // Use account.id as key
    ) {
        value = fetchSavedContacts(account)
    }

    LaunchedEffect(_savedContacts) {
        if (_savedContacts != null && _savedContacts!!.isEmpty()) { // Check for loaded *and* empty
            delay(1200)
            navController.navigate("friends") {
                popUpTo("members") { inclusive = true }
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(navController,userViewModel,context)

        },
        floatingActionButton = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 18.dp, start = 30.dp), // Adjust bottom padding if needed,
                horizontalArrangement = Arrangement.Center
            ) {
                FloatingActionButton(
                    containerColor = Color(0xFF2196F3),
                    shape = CircleShape,
                    onClick = {
                        navController.navigate("friends") },
                ) {
                    if (_savedContacts.isNullOrEmpty()) {
                        Icon(imageVector = Icons.Default.Add, contentDescription = "Add Contact")
                    } else {
                        Icon(imageVector = Icons.Default.Edit, contentDescription = "Edit Contacts")
                    }
                }
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
            when (_savedContacts) {
                null -> LoadingScreen()  // Display loading while _savedContacts is null
                else -> if (_savedContacts!!.isEmpty()) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0f191f)),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            "No contacts found.  Redirecting...",
                            color = Color.White
                        )
                    }
                } else {
                    ContactListScreen(_savedContacts!!, context)
                }
            }
        }
    }
}

@Composable
fun ContactListScreen(_savedContacts: List<Contact>, context: Context) {
    Column(
        modifier = Modifier
            .background(Color(0xFF0f191f))
            .fillMaxSize()
            .padding(top = 15.dp)
    ) {
        Column( modifier = Modifier
            .weight(1f)){
            val contactList = _savedContacts.take(4) // Handle up to 4 contacts dynamically
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

        Row(Modifier.padding(10.dp)
            .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween){
            SOSButton(
                text = "SOS Message",
                onClick = { /* Handle message action */ },
            )
            SOSButton(
                text = "SOS Call",
                onClick = { /* Handle call action */ },
            )
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
            .width(150.dp)
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
    val firstLetter = contact.name.firstOrNull()?.uppercase().toString()

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
                    .background(Color(0xFF8EC7C2))
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
        val _savedContacts = mutableListOf<Contact>()
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
                    _savedContacts.add(Contact(name, phoneNumber))

                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
        _savedContacts
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
