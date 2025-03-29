package com.example.beatsfit.view

import android.content.Context
import android.net.Uri
import android.widget.Toast
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.*
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.beatsfit.R
import com.example.beatsfit.viewmodel.UserViewModel
@Composable
fun UserProfileScreen(
    navController: NavHostController,
    context: Context,
    userViewModel: UserViewModel
) {
    val user by userViewModel.user.observeAsState()
    val userName= user?.firstName
    val userEmail= user?.email
    val userProfilePicture= user?.imageUri.toString()

    Scaffold(bottomBar = { BottomAppBarWithIcons(navController) }

    ) {
        Box(Modifier.padding(it)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFF0F191F))
                    .padding(10.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Back",
                        tint = Color.White,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "My Profile",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 50.dp, bottom = 50.dp),
                    horizontalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier.padding(start = 30.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(Color.White.copy(alpha = 0.3f))
                                .blur(radius = 10.dp)
                        )

                        Box(
                            modifier = Modifier
                                .size(100.dp)
                                .clip(CircleShape)
                                .background(
//                                    Brush.linearGradient(
//                                        listOf(
//                                            Color(0XFFB1A2CA),
//                                            Color(0xFF1889D5),
//                                            Color(0xFF688CB4)
//                                        ),
//                                        start = Offset.Zero,
//                                        end = Offset.Infinite,
//                                        tileMode = TileMode.Clamp

//                                    )
                                Color(0xFF114011)
                                )// Background color for fallback
                                .border(2.dp, Color.White, CircleShape) ,
                            contentAlignment = Alignment.Center// Optional border
                        ) {
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
                    }else{
                        if (userName != null) {
                            Text(userName.trim().take(1).uppercase(), fontSize = 40.sp)
                        }
                    }
                        }
                    }


                    Spacer(modifier = Modifier.height(10.dp))

                    Column(modifier = Modifier.padding(8.dp)){
                        if (userName != null) {
                            Text(
                                text = userName,
                                fontSize = 22.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        }
                        if (userEmail != null) {
                            Text(
                                text = userEmail,
                                fontSize = 14.sp,
                                color = Color.Gray
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Button(
                            modifier = Modifier.width(120.dp).height(40.dp),
                            onClick = { /* Handle Edit Profile Click */ },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2196F3))
                        ) {
                            Text(text = "Edit Profile", color = Color.White)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))
                Column(modifier = Modifier
                    .fillMaxWidth()) {
                    ProfileOption(icon = Icons.Default.Info, text = "Health and Fitness", onClick = {navController.navigate("healthAndFitness")})
                    ProfileOption(
                        icon = Icons.Default.Share, text = "Emergency & Sharing",
                        onClick = {/*TODO()*/}
                    )
                    ProfileOption(
                        icon = Icons.Default.Favorite, text = "Other Information",
                        onClick = {/*TODO()*/}
                    )
                    ProfileOption(
                        icon = Icons.Default.Settings, text = "App Preferences",
                        onClick = {/*TODO()*/}
                    )
                    ProfileOption(
                        icon = Icons.Default.ExitToApp,
                        text = "Log out",
                        iconColor = Color.Red,
                        textColor = Color.Red,
                        onClick = { logout(navController,context,userViewModel) }
                    )
                }
            }
        }
    }
}

@Composable
fun ProfileOption(
    icon: ImageVector,
    text: String,
    iconColor: Color = Color.White,
    textColor: Color = Color.White,
    onClick: ()->Unit){
//    Card(Modifier.padding(4.dp)){
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = text,
                tint = iconColor,
                modifier = Modifier.size(29.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Medium,
                color = textColor,
                modifier = Modifier.weight(1f)
            )
        }
    }
//}
