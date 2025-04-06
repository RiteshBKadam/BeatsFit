package com.example.beatsfit.view

import android.Manifest
import android.content.pm.PackageManager
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch

@Composable
fun FriendsScreen(navController: NavController, account: GoogleSignInAccount) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    var selectedContacts = remember { mutableStateListOf<Contact>() }
    val coroutineScope= rememberCoroutineScope()

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        permissionGranted = isGranted
    }


    LaunchedEffect(Unit) {
        coroutineScope.launch {
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.READ_CONTACTS
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                permissionGranted=true
            }
            else {
                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }

        }
    }


    if (permissionGranted) {
        ContactPickerScreen(
            account = account,
            context = context,
            selectedContacts = selectedContacts,
            onContactsSelected = { contact, isSelected ->
                if (isSelected) {
                    selectedContacts.add(contact)
                } else {
                    selectedContacts.remove(contact)
                }
            },
            navController = navController
        )
    } else {
        Column {
            Button(onClick = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) },
                modifier = Modifier.wrapContentSize(),
                colors = ButtonColors(
                    containerColor = Color(0xFF99cccc),
                    contentColor = Color.Black,
                    disabledContainerColor = Color.LightGray,
                    disabledContentColor = Color.White
                )

            ) {
                Text("Permission to read contacts is required.")
            }
        }
    }
}
