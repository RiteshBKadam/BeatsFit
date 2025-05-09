package com.example.beatsfit.view

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import androidx.navigation.NavHostController
import com.example.beatsfit.util.BottomAppBarWithIcons


@Composable
fun AppPreferences(navController: NavHostController,
                   context: Context
){
    val backgroundColor = Color(0xFF0C161C)
    val textColor = Color.White
    val packageName = context.packageName
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            // Permission granted
        } else {
            // Permission denied
        }
    }
    Scaffold(bottomBar = { BottomAppBarWithIcons(navController) }

    ) {
        Box(Modifier.padding(it)) {
            Column(
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
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                                contentDescription = "",
                                modifier = Modifier.size(35.dp),
                                tint = Color.White
                            )
                        }

                        Text(
                            text = "App Preferences",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = textColor,
                        )

                    }
                }
                Spacer(modifier = Modifier.height(35.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            if(ContextCompat.checkSelfPermission(context,Manifest.permission.POST_NOTIFICATIONS)!= PackageManager.PERMISSION_GRANTED) {
                                permissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }else{
                                val intent =
                                    Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                        data = Uri.fromParts("package", packageName, null)
                                    }
                                context.startActivity(intent)
                            }
                        }
                        .padding(end = 15.dp)){
                    Text(
                        text = "Notification Settings",
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
                Spacer(modifier = Modifier.height(15.dp))
                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {

                        }
                        .fillMaxWidth()
                        .padding(end = 15.dp)){
                    Text(
                        text = "Privacy & Security",
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
                Spacer(modifier = Modifier.height(15.dp))

                Row(horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(end = 15.dp)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            val intent =
                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                    data = Uri.fromParts("package", packageName, null)
                                }
                            context.startActivity(intent)
                        }
                ){
                    Text(
                        text = "Manage Permissions",
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