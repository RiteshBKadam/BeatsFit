package com.example.beatsfit.view

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.produceState
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavController
import com.example.beatsfit.util.BottomAppBarWithIcons
import com.example.beatsfit.util.TopAppBar
import com.example.beatsfit.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import kotlinx.coroutines.launch

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun FriendsScreen(navController: NavController, account: GoogleSignInAccount,userViewModel: UserViewModel) {
    val context = LocalContext.current
    var permissionGranted by remember { mutableStateOf(false) }
    var selectedContacts = remember { mutableStateListOf<Contact>() }
    val coroutineScope = rememberCoroutineScope()
    var pickContact by remember { mutableStateOf(false) }
    var show by remember { mutableStateOf(false) }


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
                permissionGranted = true
            } else {
                permissionLauncher.launch(Manifest.permission.READ_CONTACTS)
            }

        }
    }

    val _savedContacts by produceState<List<Contact>?>( // Make nullable
        initialValue = null, // Initial value is null (loading state)
        key1 = account.id // Use account.id as key
    ) {
        value = fetchSavedContacts(account)
    }

    LaunchedEffect(key1 = _savedContacts) {
        val currentRoute =
            navController.currentBackStackEntry?.destination?.route
        val lastRoute=
            navController.previousBackStackEntry?.destination?.route
        if (_savedContacts != null && _savedContacts!!.isEmpty()) {
            if(currentRoute!="friends") {
                navController.navigate("friends") {
                    popUpTo("home_screen/true")
                }
            }
        }else if(lastRoute=="members"){
            pickContact=true
            Log.d("THISSSS2",pickContact.toString())
        }
        else{
            if(_savedContacts != null && !_savedContacts!!.isEmpty()) {
                navController.navigate("members"){
                    popUpTo("home_screen/true")
                }
            }
            Log.d("THISSSS3",show.toString())

        }
    }

    Scaffold(
        modifier = Modifier.background(Color(0xFF0f191f)),
        topBar = {
            TopAppBar(navController, userViewModel, context)
        },
        bottomBar = { BottomAppBarWithIcons(navController) },
        content = {
            Box{
                if (permissionGranted) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0f191f)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text("Add Family Members", fontSize = 17.sp)
                        Button(
                            onClick = { pickContact = true },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFda9d5f))
                        ) {
                            Text("Pick Contact")
                        }

                        if (pickContact) {
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
                        }
                    }
                } else {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(Color(0xFF0f191f)),
                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Button(
                            onClick = { permissionLauncher.launch(Manifest.permission.READ_CONTACTS) },
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
        },
    )
}


