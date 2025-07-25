package com.riteshbkadam.beatsfitapp.util

import android.content.Context
import android.net.Uri
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.riteshbkadam.beatsfitapp.viewmodel.UserViewModel
import com.riteshbkadam.beatsfitapp.R
import com.riteshbkadam.beatsfitapp.room.data.User

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar(navController: NavController,userViewModel: UserViewModel,context: Context) {

    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val user by userViewModel.user.observeAsState()
    var title by remember { mutableStateOf("") }
    var alpha by remember { mutableFloatStateOf(0.0F) }

    LaunchedEffect(currentRoute) {
        when {
            currentRoute?.contains("home_screen") == true -> {
                title = "Good ${greetString()}!"
                alpha = 1.0f
            }
            currentRoute == "friends" -> {
                title = "Members"
                alpha = 0.0f
            }
            currentRoute == "members" -> {
                title = "Members"
                alpha = 0.0f
            }
            currentRoute == "health" -> {
                title = "Health Details"
                alpha = 0.0f
            }
            currentRoute?.contains("graphDetail") == true -> {
                title="Health Details"
                alpha=0.0f
            }
            currentRoute == "trackFamily" -> {
                title = "Family's Health"
                alpha = 0.0f
            }
            else -> {
                title = ""
                alpha = 0.0f
            }
        }
    }


    Column {
        TopAppBar(
            title = { user?.let { AppBarTitle(title, alpha, it,context,navController) } }, // Extracted to avoid recomposition
            modifier = Modifier.height(75.dp),
            colors = TopAppBarDefaults.smallTopAppBarColors(
                containerColor = Color(0xFF0f191f)
            )
        )
    }
}

@Composable
fun AppBarTitle(
    title: String,
    alpha: Float,
    user: User,
    context: Context,
    navController: NavController,) {
    val userProfilePicture=user.imageUri.toString()
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 15.dp, end = 20.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Row {
            Text(
                title,
                color = Color.White,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.width(6.dp))
            user.firstName?.let { AnimatedAlphaText(alpha, it, context) }
        }

        Row(Modifier.padding(end = 3.dp, bottom = 1.dp)) {
            Box(
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(Color(0xFF114011))
                    .clickable(onClick = {navController.navigate("user_profile")}),
                contentAlignment = Alignment.Center

            ){

                if(userProfilePicture!="null") {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(Uri.parse(userProfilePicture))
                            .crossfade(true)
                            .build(),
                        contentDescription = "ProfilePic",
                        placeholder = painterResource(R.drawable.profile_placeholder),
                        modifier = Modifier.fillMaxSize()
                    )
                }
                else{
                    if (!user.firstName.isNullOrEmpty()) {
                        Text(user.firstName.trim().take(1).uppercase(), fontSize = 25.sp)
                    }
                }

            }
        }
    }
}

@Composable
fun AnimatedAlphaText(alpha: Float, userName: String, context: Context) {
    Text(
        text=userName,
        color = Color(0xFFE3A356),
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        modifier = Modifier.alpha(alpha)
    )
}

