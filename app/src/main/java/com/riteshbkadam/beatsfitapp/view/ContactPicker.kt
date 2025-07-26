package com.riteshbkadam.beatsfitapp.view
import android.content.Context
import android.database.Cursor
import android.provider.ContactsContract
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.FieldValue
import com.google.firebase.firestore.FirebaseFirestore
import com.riteshbkadam.beatsfitapp.util.formatPhoneNumber
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@Composable
fun ContactPickerScreen(
    account: GoogleSignInAccount,
    context: Context,
    selectedContacts: List<Contact>,
    onContactsSelected: (Contact, Boolean) -> Unit,
    navController: NavController
) {
    val maxSelected = 4
    val allContacts = remember { mutableStateListOf<Contact>() }
    val filteredContacts = remember { mutableStateListOf<Contact>() }
    val coroutineScope = rememberCoroutineScope()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        coroutineScope.launch {
            val fetchedContacts = fetchAllContacts(context)
            allContacts.addAll(fetchedContacts)
            filteredContacts.addAll(fetchedContacts)
        }
    }

    val phoneNumbers=selectedContacts.map{formatPhoneNumber(it.phoneNumber)}
    Column(
        modifier = Modifier
            .background(Color(0xFF0f191f))
            .fillMaxSize()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                "Choose members",
                fontSize = 20.sp,
                color = Color(0xFFda9d5f),
                modifier = Modifier.padding(16.dp)
            )
            Button(
                onClick = {
                    if (selectedContacts.size < 1) {
                    } else {
                        val firestore = FirebaseFirestore.getInstance()
                        val userId = account.id
                        firestore.collection("users")
                            .whereIn("mobile_number", phoneNumbers)
                            .get()
                            .addOnSuccessListener { it ->
                                var selectedContactOneSignalId = ""
                                if (!it.isEmpty) {
                                    for (doc in it.documents) {
                                        selectedContactOneSignalId =
                                            doc.get("oneSignalId").toString()

                                    }
                                }

                                val contactsData = selectedContacts.map { contact ->
                                    mapOf(
                                        "name" to contact.name,
                                        "phoneNumber" to formatPhoneNumber(contact.phoneNumber),
                                        "oneSignalId" to selectedContactOneSignalId
                                    )
                                }

                                if (userId != null) {
                                    firestore.collection("users")
                                        .document(userId)
                                        .update("addedMembers", contactsData)
                                        .addOnSuccessListener {
                                        }
                                        .addOnFailureListener { e ->
                                        }
                                    for (contact in contactsData) {
                                        firestore.collection("users").whereEqualTo(
                                            "mobile_number",
                                            contact.get("phoneNumber")
                                        )
                                            .get()
                                            .addOnSuccessListener {
                                                if (!it.isEmpty) {
                                                    val doc = it.documents[0]
                                                    val thisDocId = doc.id
                                                    firestore.collection("users")
                                                        .document(thisDocId)
                                                        .update(
                                                            "addedBy",
                                                            FieldValue.arrayUnion(userId)
                                                        )
                                                        .addOnSuccessListener {
                                                        }
                                                        .addOnFailureListener { e ->
                                                        }
                                                } else {
                                                }
                                            }
                                            .addOnFailureListener {
                                            }
                                    }

                                }

                            }
                        navController.navigate("members")
                    }
                },
                enabled = selectedContacts.size in 1..maxSelected,
                modifier = Modifier.padding(16.dp),
                colors = ButtonColors(
                    containerColor = Color(0xFF99cccc),
                    contentColor = Color.Black,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.White
                )
            ) {
                Text("Done")
            }
        }

        // Search Bar
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                filteredContacts.clear()
                filteredContacts.addAll(
                    if (query.isEmpty()) allContacts
                    else allContacts.filter {
                        it.name.contains(query, ignoreCase = true) ||
                                it.phoneNumber.contains(query, ignoreCase = true)
                    }
                )
            },
            label = { Text("Search Contacts") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp),
            singleLine = true
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .background(Color(0xFF0f191f))
        ) {
            items(filteredContacts, key = { it.phoneNumber }) { contact ->
                val isDisabled = selectedContacts.size >= maxSelected && !selectedContacts.contains(contact)
                val coroutineScope = rememberCoroutineScope()

                ContactItem(
                    contact = contact,
                    isSelected = selectedContacts.contains(contact),
                    isDisabled = isDisabled,
                    onClick = {
                        if (!isDisabled) {
                            coroutineScope.launch(Dispatchers.IO) {
                                val isSelected = selectedContacts.contains(contact)
                                withContext(Dispatchers.Main) {
                                    onContactsSelected(contact, !isSelected)
                                }
                            }
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun ContactItem(
    contact: Contact,
    isSelected: Boolean,
    isDisabled: Boolean,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable(enabled = !isDisabled) { onClick() },
        colors = CardDefaults.cardColors(containerColor = if (isDisabled) Color.Gray else Color(0x1BFFFFFF))
    ) {
        Row(modifier = Modifier.padding(16.dp)) {
            Checkbox(
                checked = isSelected,
                onCheckedChange = { if (!isDisabled) onClick() },
                enabled = !isDisabled,
                colors = CheckboxDefaults.colors(
                    checkmarkColor = Color.Black,
                    uncheckedColor = Color(0xD3FFFFFF),
                    disabledCheckedColor = Color(0x55FFFFFF),
                    checkedColor = Color(0xFF99cccc)
                )
            )
            Column(modifier = Modifier.padding(start = 8.dp)) {
                Text(
                    text = contact.name.trim().take(20),
                    fontSize = 18.sp,
                    color = if (isDisabled) Color.LightGray else Color(0xD3FFFFFF)
                )
                Text(
                    text = contact.phoneNumber,
                    fontSize = 14.sp,
                    color = if (isDisabled) Color.LightGray else Color(0xD3FFFFFF),
                    modifier = Modifier.padding(top = 4.dp)
                )
            }
        }
    }
}


suspend fun fetchAllContacts(context: Context): List<Contact> {
    return withContext(Dispatchers.IO) {
        val contactList = mutableListOf<Contact>()
        val contentResolver = context.contentResolver

        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER

        )

        val cursor: Cursor? = contentResolver.query(
            uri,
            projection,
            null,
            null,
            "${ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME} ASC",
        )

        cursor?.use {
            val nameIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)
            val numberIndex = it.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER)

            while (it.moveToNext()) {
                val name = it.getString(nameIndex) ?: "Unknown"
                val phoneNumber = it.getString(numberIndex) ?: "Unknown"
                contactList.add(Contact(name, phoneNumber))
            }
        }

        contactList
    }
}
