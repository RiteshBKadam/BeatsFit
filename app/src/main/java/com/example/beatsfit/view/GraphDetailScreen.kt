package com.example.beatsfit.view

import android.content.Context
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.beatsfit.util.BottomAppBarWithIcons
import com.example.beatsfit.util.TopAppBar
import com.example.beatsfit.viewmodel.UserViewModel
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.example.beatsfit.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GraphDetailScreen(title: String,
                      navController: NavController,
                      context: Context,
                      userViewModel: UserViewModel,
                      account: GoogleSignInAccount) {
    Scaffold(
        topBar = {
            TopAppBar(navController, userViewModel, context )
        },
        bottomBar = { BottomAppBarWithIcons(navController) },
        content = { padding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(padding)
                    .background(Color(0xFF0f191f))
            ) {
                var resource by remember { mutableIntStateOf(R.drawable.goals) }
                var borderColor by remember { mutableStateOf(Color.White) }
                when(title){
                    "Steps" -> {resource = R.drawable.steps
                        borderColor=Color(0xFF3997FD)
                    }
                    "Heart Rate" -> {resource = R.drawable.heartbeat
                        borderColor=Color(0xFFE66A6F)
                    }
                    "Calories Burned" -> {resource = R.drawable.calories
                        borderColor=Color(0xFFDD8043)
                    }
                    "Distance" -> {resource = R.drawable.km
                        borderColor=Color(0xFF79D153)
                    }
                    "Sleep" -> {resource = R.drawable.sleep_purple
                        borderColor=Color(0xFF663AB6)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))

                BarChart(resource,borderColor,title)
            }
        }
    )
}

@Composable
fun BarChart(resource: Int, borderColor: Color, title: String) {
    Card(
        modifier = Modifier.padding(15.dp,15.dp, 15.dp, 25.dp)
            .fillMaxSize()
            .background(Color.Transparent),
        shape = RoundedCornerShape(50.dp),
        border = BorderStroke(2.dp, Color(0xFF2B474F))
        ){
        Column(modifier = Modifier.fillMaxSize()
            .background(Color(0xFF0f191f))) {
            Row(horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
                    .padding(20.dp)){
                Text(title, color = Color.White, fontSize = 20.sp,)

                Image(painter = painterResource(resource),
                    contentDescription = "icon",
                    modifier = Modifier.border(1.5f.dp,borderColor,CircleShape)
                        .padding(8.dp)
                        .size(30.dp),alignment = Alignment.Center)

            }

        }
    }
}
