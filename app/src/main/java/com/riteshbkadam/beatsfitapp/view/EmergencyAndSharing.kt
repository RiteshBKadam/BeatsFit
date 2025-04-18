package com.riteshbkadam.beatsfitapp.view

import android.content.Context
import android.content.Intent
import android.location.LocationManager
import android.provider.Settings
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchColors
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.navigation.NavHostController
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.firestore.FirebaseFirestore
import com.riteshbkadam.beatsfitapp.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EmergencyAndSharing(navController: NavHostController,
                        context: Context,
                        account: GoogleSignInAccount,
                        firestore: FirebaseFirestore) {
    val backgroundColor = Color(0xFF0C161C)
    val textColor = Color.White
    val lifecycleOwner = LocalLifecycleOwner.current
    var isLocationEnabled by remember { mutableStateOf(isLocationEnabled(context)) }

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                isLocationEnabled = isLocationEnabled(context)
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    val _savedContacts = remember { mutableStateListOf<Contact>() }


    LaunchedEffect(Unit) {
        val result=fetchSavedContacts(account)
        _savedContacts.addAll(result)

    }
    LaunchedEffect(Unit) {
        snapshotFlow { isLocationEnabled(context) }
            .collect { isLocationEnabled = it }
    }
    when (_savedContacts) {
        null -> LoadingScreen()
        else -> Column(
            modifier = Modifier
                .fillMaxSize()
                .background(backgroundColor)
                .padding(10.dp)
        ) {

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "",
                            modifier = Modifier.size(35.dp),
                            tint = Color.White)
                    }

                    Text(
                        text = "Emergency And Sharing",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                    )

                }
                Spacer(modifier = Modifier.height(15.dp))
                Text(
                    text = "Contacts",
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = textColor,
                    modifier = Modifier.padding(5.dp)
                )
                LazyColumn(modifier = Modifier.wrapContentSize()){
                    items(_savedContacts) { contact ->
                        MemberCard(
                            contact = contact,
                        )
                    }
                }
                Spacer(modifier = Modifier.height(25.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 15.dp)){
                    Text(
                        text = "Location Sharing",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(5.dp)
                    )

                    Switch(
                        enabled = true,
                        checked = isLocationEnabled,
                        onCheckedChange = {
                            context.startActivity(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS))
                        },
                        colors = SwitchColors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF5D9AB6),
                            checkedBorderColor = Color.White,
                            checkedIconColor = Color.White,
                            uncheckedThumbColor = Color.Black,
                            uncheckedTrackColor = Color(0xBC313A3E),
                            uncheckedBorderColor = Color.White,
                            uncheckedIconColor = Color.White,
                            disabledCheckedThumbColor = Color.White,
                            disabledCheckedTrackColor = Color.White,
                            disabledCheckedBorderColor = Color.White,
                            disabledCheckedIconColor = Color.White,
                            disabledUncheckedThumbColor = Color.White,
                            disabledUncheckedTrackColor = Color.White,
                            disabledUncheckedBorderColor = Color.White,
                            disabledUncheckedIconColor = Color.White
                        ),

                    )

                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 15.dp)){
                    Text(
                        text = "SOS Message Settings",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(5.dp)
                    )

                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "",
                        modifier = Modifier.size(35.dp),
                        tint = Color.White)
                }
                Spacer(modifier = Modifier.height(10.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 15.dp)){
                    Text(
                        text = "SOS Call Settings",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = textColor,
                        modifier = Modifier.padding(5.dp)
                    )

                    Icon(imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                        contentDescription = "",
                        modifier = Modifier.size(35.dp),
                        tint = Color.White)
                }

            }
        }
    }


}
fun isLocationEnabled(context: Context): Boolean {
    val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
    return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
            locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
}


@Composable
fun MemberCard(contact: Contact) {
    Card(modifier = Modifier.padding(start = 15.dp, end = 15.dp, bottom = 5.dp, top = 5.dp),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732))){
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .padding(start = 20.dp)){
            Image(painter = painterResource(R.drawable.baseline_person_24), contentDescription = "")
            Spacer(modifier = Modifier.width(5.dp))
            Text(contact.name, fontSize = 18.sp, color = Color.White)
        }
    }
}
