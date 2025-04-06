package com.example.beatsfit.view

import android.content.Context
import android.content.Intent
import android.location.Location
import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonColors
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.beatsfit.R
import com.example.beatsfit.viewmodel.UserViewModel
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.delay

@Composable
fun TrackFamily(
    navController: NavHostController,
    userViewModel: UserViewModel,
    context: Context,
    id: String?
) {
    val addedByIds = remember { mutableStateListOf<String>() }
    val firestore = FirebaseFirestore.getInstance()

    LaunchedEffect(id) {
        firestore.collection("users").document(id ?: "")
            .get()
            .addOnSuccessListener { doc ->
                if (doc.exists()) {
                    val list = doc.get("addedBy") as? List<String>
                    if (list != null) {
                        addedByIds.clear()
                        addedByIds.addAll(list)
                    }
                }
            }
            .addOnFailureListener{
                Log.e("Firebaseeee",it.toString())
            }
    }


    Scaffold(
        topBar = { TopAppBar(navController, userViewModel, context) },
        bottomBar = { BottomAppBarWithIcons(navController) }
    ) { paddingValues ->
        Box(modifier = Modifier.padding(paddingValues)) {
            Column {
                addedByIds.forEach { addedById ->
                    FamilyContactCard(context, addedById, firestore )
                }
            }
        }
    }
}

@Composable
fun FamilyContactCard(
    context: Context,
    addedById: String,
    firestore: FirebaseFirestore
) {
    var firstName by remember { mutableStateOf("") }
    var lastName by remember { mutableStateOf("") }
    var phoneNumber by remember { mutableStateOf("") }
    LaunchedEffect(addedById) {
        firestore.collection("users").document(addedById)
            .get()
            .addOnSuccessListener { doc ->
                val _firstName = doc.get("first_name") ?: ""
                firstName = _firstName.toString()

                val _lastName = doc.get("last_name") ?: ""
                lastName = _lastName.toString()

                val _phoneNumber = doc.get("mobile_number") ?: ""
                phoneNumber = _phoneNumber.toString()

            }
    }
    val firstLetter = firstName.trim().take(1).uppercase()
//    Toast.makeText(context,phoneNumber,Toast.LENGTH_SHORT).show()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .height(200.dp),
        border = BorderStroke(
            width = 1.dp,
            color = Color(0x5EDA9D5F)
        ),
        colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732))
    ) {
        Row(
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxSize()
                .padding(20.dp),
        ) {
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.spacedBy(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            )
            {
                Box(
                    modifier = Modifier
                        .size(100.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF8EC7C2)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstLetter,
                        fontSize = 34.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xC60F191F)
                    )
                }
                Text(
                    modifier = Modifier
                        .height(50.dp),
                    text = "$firstName $lastName",
                    fontSize = 18.sp,
                    color = Color(0xD3FFFFFF),
                    textAlign = TextAlign.Center,
                    maxLines = 2, // Limit to one line
                    overflow = TextOverflow.Ellipsis // Truncate with ellipsis
                )
            }

            Column(modifier = Modifier.fillMaxSize()
                .padding(start = 15.dp, end = 0.dp,),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally){
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                    ,
                    horizontalArrangement = Arrangement.SpaceEvenly,
                ) {
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${phoneNumber}")
                            }
                            context.startActivity(intent)
                        },
                        colors = IconButtonColors(
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color(0xFF2196F3)
                        ),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.call),
                            contentDescription = "Call Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    IconButton(
                        onClick = { },
                        colors = IconButtonColors(
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color(0xFF2196F3)
                        ),

                        ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_location_pin_24),
                            contentDescription = "Location Icon",
                            modifier = Modifier.size(28.dp)
                        )
                    }
                    IconButton(
                        onClick = {
                            val intent = Intent(Intent.ACTION_DIAL).apply {
                                data = Uri.parse("tel:${phoneNumber}")
                            }
                            context.startActivity(intent)
                        },
                        colors = IconButtonColors(
                            containerColor = Color(0xFF2196F3),
                            contentColor = Color.White,
                            disabledContentColor = Color.White,
                            disabledContainerColor = Color(0xFF2196F3)
                        ),
                    ) {
                        Image(
                            painter = painterResource(R.drawable.baseline_message_24),
                            contentDescription = "Call Icon",
                            modifier = Modifier.size(26.dp)
                        )
                    }
                }
                Button(onClick ={}) {
                    Text("Monitor Details")
                }
            }

        }
    }
}
