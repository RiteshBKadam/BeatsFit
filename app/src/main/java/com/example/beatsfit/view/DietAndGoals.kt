package com.example.beatsfit.view

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beatsfit.R
import com.example.beatsfit.room.data.User
import com.example.beatsfit.viewmodel.UserViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DietAndGoals(navController: NavController, userViewModel: UserViewModel, context: Context) {
    val user by userViewModel.user.observeAsState()
    val backgroundColor = Color(0xFF0C161C)
    val textColor = Color.White
    var weightValue by remember { mutableStateOf("${user?.weight}" ?: "0.0") }
    var stepGoal by remember { mutableIntStateOf(user?.stepGoal ?: 0) }
    var selectedGender by remember { mutableStateOf("${user?.gender}") }
    var heightValue by remember { mutableFloatStateOf(user?.height?.toFloat() ?: 0.0f) }
    var cyclingGoal by remember { mutableStateOf(user?.cyclingGoal?:0) }
    var showAgeDialog by remember { mutableStateOf(false) }

    val newUser = user?.let {
        heightValue?.let { it1 ->
            User(
                it.id, it.firstName, it.lastName, it.imageUri, it.email,
                it1.toInt(), weightValue, selectedGender,stepGoal,cyclingGoal
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(backgroundColor)
            .padding(10.dp)
    ) {

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ){
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                    contentDescription = "",
                    modifier = Modifier.size(35.dp),
                    tint = Color.White
                )
            }

            Text(
                text = "Diet And Goals",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = textColor,
            )
        }
        Column(modifier = Modifier.padding(13.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)) {
            Row(modifier = Modifier
                .width(120.dp)
                .padding(top=15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)){
                Text(
                    "Daily Goals",
                    color = Color.White,
                    fontSize = 17.sp
                )
                Image(painter = painterResource(id = R.drawable.goals), contentDescription = "Goals", modifier = Modifier.size(25.dp))
            }
            Card(modifier = Modifier
                .background(Color.Transparent)
                .fillMaxWidth()
                .height(100.dp)) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xBF13232F))
                        .padding(15.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Steps For A Day :",
                            modifier = Modifier.padding(start = 15.dp),
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(verticalAlignment =Alignment.CenterVertically,
                            modifier = Modifier
                                .width(115.dp)
                                .padding(end = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween){
                            Text(
                                text = stepGoal.toString(),
                                color = Color.White,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Column {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowUp,
                                    tint = Color.White,
                                    contentDescription = "addSteps",
                                    modifier = Modifier.clickable(onClick = { stepGoal += 500 })
                                )
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    tint = Color.White,
                                    contentDescription = "subtractSteps",
                                    modifier = Modifier.clickable(onClick = {
                                        if(stepGoal>0) {
                                        stepGoal -= 500
                                    } })

                                )
                            }
                        }
                    }
                }
            }
            Card(modifier = Modifier
                .background(Color.Transparent)
                .fillMaxWidth()
                .height(100.dp)) {
                Column(
                    Modifier
                        .fillMaxSize()
                        .background(Color(0xBF13232F))
                        .padding(15.dp),
                    verticalArrangement = Arrangement.Center
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Cycling :",
                            modifier = Modifier.padding(start = 15.dp),
                            color = Color.White,
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                        )
                        Row(verticalAlignment =Alignment.CenterVertically,
                            modifier = Modifier
                                .width(115.dp)
                                .padding(end = 10.dp),
                            horizontalArrangement = Arrangement.SpaceBetween){
                            Text(
                                text = cyclingGoal.toString()+" km",
                                color = Color.White,
                                fontSize = 23.sp,
                                fontWeight = FontWeight.Bold,
                                textAlign = TextAlign.Center
                            )
                            Column {
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowUp,
                                    tint = Color.White,
                                    contentDescription = "addSteps",
                                    modifier = Modifier.clickable(onClick = { cyclingGoal += 5 })
                                )
                                Icon(
                                    imageVector = Icons.Filled.KeyboardArrowDown,
                                    tint = Color.White,
                                    contentDescription = "subtractSteps",
                                    modifier = Modifier.clickable(onClick = {
                                        if(cyclingGoal>0) {
                                            cyclingGoal -= 5
                                        }
                                    })
                                )
                            }
                        }
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
            Row(modifier = Modifier
                .width(120.dp)
                .padding(top=15.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(5.dp)){
                Text(
                    "Diet",
                    color = Color.White,
                    fontSize = 17.sp
                )
                Image(painter = painterResource(id = R.drawable.diet), contentDescription = "Goals", modifier = Modifier.size(25.dp))
            }

        }
    }
}