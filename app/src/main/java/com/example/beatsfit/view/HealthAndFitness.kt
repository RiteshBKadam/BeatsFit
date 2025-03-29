package com.example.beatsfit.view

import android.widget.Toast
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beatsfit.R
import com.example.beatsfit.room.data.User
import com.example.beatsfit.viewmodel.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HealthAndFitness(navController: NavController,userViewModel: UserViewModel) {
    val user by userViewModel.user.observeAsState()
    val backgroundColor = Color(0xFF13232F) // Dark theme background
    val textColor = Color.White
    var weightValue by remember { mutableStateOf("${user?.weight}"?:"0.0") }
    var ageValue by remember { mutableStateOf("0") }
    var selectedGender by remember { mutableStateOf("${user?.gender}") }
    var heightValue by remember { mutableStateOf(user?.height) }
    var showWeightDialog by remember { mutableStateOf(false) }
    var showAgeDialog by remember { mutableStateOf(false) }

    val newUser= user?.let { heightValue?.let { it1 ->
        User(it.id,it.firstName,it.lastName,it.imageUri,it.email,
            it1.toInt() ,weightValue, selectedGender)
    } }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(16.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.Default.ArrowBack,
                    contentDescription = "Back",
                    tint = textColor
                )
            }
            Text(
                text = "Health and Fitness",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
                modifier = Modifier.padding(start = 8.dp)
            )
        }

        Text(
            "GENDER",
            modifier = Modifier
                .fillMaxWidth()
                .padding(15.dp),
            fontSize = 15.sp,
            color = Color.White,
        )
        Row {
            Box(modifier = Modifier
                .background(Color.Transparent)
                .clip(shape = CircleShape)
                .size(180.dp),
                contentAlignment = Alignment.Center) {

                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    Column(
                        Modifier
                            .clip(CircleShape)
                            .size(130.dp)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable {selectedGender="Male"}
                            .background(if (selectedGender == "Male") Color(0xFF4B6A75) else Color.Transparent)
                        ,
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.male_icon),
                            contentDescription = "",
                            modifier = Modifier
                                .size(75.dp)
                                .padding(start = 5.dp)
                        )
                    }
                    Text("Male", modifier = Modifier.padding(12.dp), color = Color.White, textAlign = TextAlign.Center)

                }
            }
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
                    .clip(shape = CircleShape)
                    .size(180.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally){
                    Column(
                        Modifier
                            .background(Color(0xBF13232F))
                            .clip(CircleShape)
                            .size(130.dp)
                            .border(2.dp, Color.White, CircleShape)
                            .clickable {selectedGender="Female"}
                            .background(if (selectedGender == "Female") Color(0xFF4B6A75) else Color.Transparent),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Image(
                            painter = painterResource(R.drawable.female_icon),
                            contentDescription = "",
                            modifier = Modifier.size(80.dp)
                        )

                    }
                    Text("Female", modifier = Modifier.padding(12.dp), color = Color.White, textAlign = TextAlign.Center)

                }

            }
        }
        Spacer(modifier = Modifier.height(16.dp))
        Card(modifier = Modifier.background(Color.Transparent)) {
            Column(
                Modifier
                    .background(Color(0xBF13232F))
                    .padding(15.dp)
            ) {
                Text(
                    text = "HEIGHT", // Label for the TextField
                    color = Color.White,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 4.dp)
                )

                Text(
                    text = heightValue.toString() + "cm",
                    color = Color.White,
                    fontSize = 25.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .padding(bottom = 4.dp)
                        .fillMaxWidth(),
                    textAlign = TextAlign.Center
                )

                user?.height?.let {
                    heightValue?.toFloat()?.let { it1 ->
                        Slider(
                            value = it1,
                            onValueChange = { newValue ->
                                heightValue = newValue.toInt()
                            },
                            valueRange = 0f..340f,
                            steps = 0,
                            colors = SliderDefaults.colors(
                                thumbColor = Color.White,
                                activeTrackColor = Color.White,
                                inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                            ),
                            modifier = Modifier
                                .padding(10.dp)
                                .background(Color.Transparent)
                        )
                    }
                }

            }
        }
        Spacer(modifier = Modifier.height(20.dp))
        Row(
            horizontalArrangement = Arrangement.Absolute.SpaceBetween,
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(modifier = Modifier
                .background(Color.Transparent)
                .height(115.dp)
                .clickable { showWeightDialog = true }
                .width(110.dp)) {
                Column(
                    Modifier
                        .background(Color(0xBF13232F))
                        .padding(15.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        text = "WEIGHT",
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = weightValue,
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }

            Card(modifier = Modifier
                .background(Color.Transparent)
                .height(115.dp)
                .clickable { showAgeDialog = true }
                .width(110.dp)) {
                Column(
                    Modifier
                        .background(Color(0xBF13232F))
                        .padding(15.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        text = "AGE", // Label for the TextField
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,

                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = "$ageValue",
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
            Card(modifier = Modifier
                .background(Color.Transparent)
                .height(115.dp)
                .width(110.dp)) {
                Column(
                    Modifier
                        .background(Color(0xBF13232F))
                        .padding(15.dp)
                        .fillMaxSize()
                ) {
                    Text(
                        text = "BMI", // Label for the TextField
                        color = Color.White,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold,

                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    Text(
                        text = calculateBMI(weightValue, heightValue?.toInt().toString()),
                        color = Color.White,
                        fontSize = 25.sp,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier
                            .padding(bottom = 4.dp)
                            .fillMaxWidth(),
                        textAlign = TextAlign.Center
                    )
                }
            }
        }

        Button(
            modifier = Modifier
                .align(Alignment.CenterHorizontally)
                .padding(top = 30.dp)
                .size(width = 150.dp, height = 47.dp),
            colors = ButtonColors(
                containerColor = Color(0xFFda9d5f),
                contentColor = Color.White,
                disabledContainerColor =  Color(0xFFda9d5f),
                disabledContentColor = Color.White,
            ),
            onClick = {
                if (newUser != null) {
                    userViewModel.insertUser(newUser)
                }
            },
        ){
            Text("SAVE DETAILS")
        }

        Spacer(modifier = Modifier.width(16.dp))
    }
    // For weight input
    EditDialog(
        title = "Enter Weight in KG",
        initialValue = weightValue,
        showDialog = showWeightDialog,
        onDismiss = { showWeightDialog = false },
        onConfirm = {
            weightValue = it
            showWeightDialog = false
        }
    )

     EditDialog(
        title = "Enter Age (in years)",
        initialValue = ageValue,
        showDialog = showAgeDialog,
        onDismiss = { showAgeDialog = false },
        onConfirm = {
            ageValue = it
            showAgeDialog = false
        }
    )
}
@Composable
fun EditDialog(
    title: String,
    initialValue: String,
    showDialog: Boolean,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var text by remember { mutableStateOf(initialValue) }

    if (showDialog) {
        AlertDialog(
            containerColor = Color(0xFF111D2A),
            onDismissRequest = onDismiss,
            title = { Text(text = title, color = Color.White, fontSize = 20.sp) },
            text = {
                OutlinedTextField(
                    colors = OutlinedTextFieldDefaults.colors(
                        unfocusedBorderColor = Color.White,
                        focusedBorderColor = Color.White,
                        cursorColor = Color.White
                    ),
                    value = text,
                    textStyle = TextStyle(fontSize = 19.sp, color = Color.White),
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    onValueChange = { text = it },
                    label = {
                        if (title.contains("Weight")) {
                            Text("Weight", color = Color.White)
                        }else {
                            Text("Age", color = Color.White)
                        }
                    },
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = { onConfirm(text) }) {
                    Text("OK", fontSize = 15.sp, color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) {
                    Text("Cancel", fontSize = 15.sp, color = Color.White)
                }
            }
        )
    }
}

fun calculateBMI(weight: String, height: String): String {
    val weightKg = weight.toFloatOrNull() ?: return "--"
    val heightM = height.toFloat() / 100
    return String.format("%.1f", weightKg / (heightM * heightM))
}
