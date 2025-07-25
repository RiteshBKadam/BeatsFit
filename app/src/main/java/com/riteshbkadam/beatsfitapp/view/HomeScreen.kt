package com.riteshbkadam.beatsfitapp.view

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.with
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.fitness.FitnessOptions
import com.google.android.gms.fitness.data.DataType
import com.riteshbkadam.beatsfitapp.R
import com.riteshbkadam.beatsfitapp.model.HealthData
import com.riteshbkadam.beatsfitapp.util.BottomAppBarWithIcons
import com.riteshbkadam.beatsfitapp.util.LocationUtils
import com.riteshbkadam.beatsfitapp.util.TopAppBar
import com.riteshbkadam.beatsfitapp.util.searchableItems
import com.riteshbkadam.beatsfitapp.viewmodel.BeatsfitViewModel
import com.riteshbkadam.beatsfitapp.viewmodel.LocationViewModel
import com.riteshbkadam.beatsfitapp.viewmodel.UserViewModel
import kotlinx.coroutines.delay

private const val REQUEST_OAUTH_REQUEST_CODE = 1
var fitnessOptions: FitnessOptions? = null

@Composable
fun HomeScreen(
    navController: NavController,
    context: Context,
    account: GoogleSignInAccount,
    beatsfitViewModel: BeatsfitViewModel,
    userViewModel: UserViewModel,
    isPermissionGranted: MutableState<Boolean>
) {
    val healthData by beatsfitViewModel.healthData.collectAsState()
    val isInitialized = remember { mutableStateOf(false) }
    val configuration = LocalConfiguration.current
    val screenWidthDp = configuration.screenWidthDp
    val isTablet = screenWidthDp >= 600

    val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACTIVITY_RECOGNITION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) {
        isPermissionGranted.value = it.values.all { granted -> granted }
    }

    val mistrully = FontFamily(
        Font(R.font.mistrully, FontWeight.Normal),
    )
    val quicksand= FontFamily(
        Font(R.font.quicksand, FontWeight.Normal)
    )
    // Step 1: Prepare all setup in background before showing UI
    LaunchedEffect(Unit) {
        fitnessOptions = buildFitnessOptions()

        fitnessOptions?.let {
            if (!GoogleSignIn.hasPermissions(account, it)) {
                GoogleSignIn.requestPermissions(
                    context as Activity,
                    REQUEST_OAUTH_REQUEST_CODE,
                    account, it
                )
            }
        }

        val allPermissionsGranted = permissions.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }

        if (!allPermissionsGranted) {
            launcher.launch(permissions)
        } else {
            isPermissionGranted.value = true
        }

        while (!isPermissionGranted.value) {
            delay(100)
        }

        delay(300) // optional short delay to stabilize before rendering
        isInitialized.value = true
    }

    // Step 2: Show loading screen or actual UI
    if (isInitialized.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFF0f191f))
                .padding(horizontal = if (isTablet) 28.dp else 0.dp),
            contentAlignment = Alignment.TopCenter
        ) {
            Column(modifier = Modifier.widthIn(max = 600.dp)) {
                homeScreen(
                    context,
                    account,
                    navController,
                    beatsfitViewModel,
                    userViewModel,
                    healthData
                )
            }
        }
    } else {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(Color(0xFF0f191f)),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center

        ) {

            Row {
                Text("Beats", fontFamily = quicksand, fontSize = 50.sp, color = Color.White)
                Text("Fit", fontFamily = mistrully, fontSize = 48.sp, color = Color.White)

            }
        }
    }
}


@Composable
fun homeScreen(context: Context,
               account: GoogleSignInAccount,
               navController:NavController,
               beatsfitViewModel: BeatsfitViewModel,
               userViewModel: UserViewModel,
               healthData: HealthData) {

    var isExpanded by remember { mutableStateOf(false) }

    val alphaVal by animateFloatAsState(
        targetValue = if (isExpanded) 0.6f else 1f,
        animationSpec = tween(durationMillis = 0)
    )
    val locationUtils = LocationUtils(context)
    val locationViewModel: LocationViewModel = viewModel()

    val scrollState = rememberScrollState()


    // Animate position changes of the FAB using animateDpAsState
    val offsetX by animateDpAsState(
        targetValue = if (isExpanded) (-70).dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing), label = ""
    )

    val offsetY1 by animateDpAsState(
        targetValue = if (isExpanded) (-30).dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing), label = ""
    )

    val offsetX2 by animateDpAsState(
        targetValue = if (isExpanded) (0).dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing), label = ""
    )

    val offsetY2 by animateDpAsState(
        targetValue = if (isExpanded) (-73).dp else 0.dp,
        animationSpec = tween(durationMillis = 300, easing = LinearOutSlowInEasing), label = ""
    )

    var contacts=remember {  mutableStateListOf<Contact>()}
    LaunchedEffect(account) {
        beatsfitViewModel.startFetchingHealthData(
            context, account,
            locationUtils,
            locationViewModel
        )
        val result = fetchSavedContacts(account)
        contacts.addAll(result)
    }


        Scaffold(
            modifier = Modifier
                .background(Color(0xFF0f191f))
                .fillMaxWidth()
                .fillMaxHeight(),
            topBar = {
                TopAppBar(navController, userViewModel, context)
            },
            bottomBar = { BottomAppBarWithIcons(navController) },
            floatingActionButton = {
                Box {
                    FloatingActionButton(
                        onClick = { isExpanded = !isExpanded

                            if (contacts.size > 1) {
                                            makeCall(context, contacts[0].phoneNumber.toString())
                                        } else {
                                            Toast.makeText(context, "Not enough contacts to make a call", Toast.LENGTH_SHORT).show()
                                        }
                                  },
                        containerColor = Color(0xFFFF0000),
                        shape = CircleShape,
                        modifier = Modifier
                            .offset(x = offsetX, y = offsetY1)
                            .border(1.dp, Color.Red, CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.call),
                            contentDescription = "Share"
                        )
                    }

                    FloatingActionButton(
                        onClick = { isExpanded = !isExpanded
                            fitnessOptions?.let {
                                GoogleSignIn.requestPermissions(
                                    context as Activity,
                                    REQUEST_OAUTH_REQUEST_CODE,
                                    account, it
                                )
                            }
                        },
                        containerColor = Color(0xFFFF0000),
                        shape = CircleShape,
                        modifier = Modifier
                            .offset(x = offsetX2, y = offsetY2)
                            .border(1.dp, Color.Red, CircleShape)
                    ) {
                        Icon(
                            painter = painterResource(R.drawable.baseline_notifications_active_24),
                            contentDescription = "Edit"
                        )
                    }

                    FloatingActionButton(
                        onClick = { isExpanded = !isExpanded },
                        containerColor = Color(0xFFFF0000),
                        shape = CircleShape,
                        modifier = Modifier.border(1.dp, Color.Red, CircleShape)

                    ) {
                        var text by remember { mutableStateOf("") }
                        if(isExpanded){
                            text="X"
                        }else{
                            text="SOS"
                        }
                        Text(text)
                    }
                }
            },
            content = {
                Box (
                    modifier = Modifier
                        .padding(it)
                        .verticalScroll(scrollState)
                        .fillMaxSize()
                ) {

                    Column(modifier = Modifier
                        .fillMaxSize()
                        .background(Color(0xFF0f191f))
                    ){FitnessContent(healthData, alphaVal, userViewModel, context, navController)
                    }
                }
            }
        )
}

fun makeCall(context: Context, no: String) {


        val callIntent = Intent(Intent.ACTION_CALL)
        callIntent.data = Uri.parse("tel:$no")
        if (ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.CALL_PHONE
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            context.startActivity(callIntent)
        } else {
            ActivityCompat.requestPermissions(
                context as Activity,
                arrayOf(Manifest.permission.CALL_PHONE),
                1
            )
        }
}

@OptIn(ExperimentalAnimationApi::class)
@Composable
fun FitnessContent(healthData: HealthData, alphaVal:Float,userViewModel: UserViewModel,context: Context,navController: NavController) {
    var searchText by remember { mutableStateOf("") }
    var progress by remember { mutableFloatStateOf(healthData.steps) }
    val user by userViewModel.user.observeAsState()
    var showProgressBar by remember { mutableStateOf(false) }
    if(progress!=0.0f){
        showProgressBar=true
    }


        Column(
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .alpha(alphaVal)
                .background(Color(0xFF0f191f))
                .padding(15.dp)
        ) {
            Spacer(modifier = Modifier.height(24.dp))

            TextField(
                value = searchText,
                onValueChange = { searchText = it },
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                colors = TextFieldDefaults.colors(
                    unfocusedContainerColor = Color.White,
                    focusedContainerColor = Color.White,
                    unfocusedIndicatorColor = Color.Transparent,
                    focusedIndicatorColor = Color.Transparent,
                    focusedTextColor = Color.Black,
                    unfocusedTextColor = Color.Black
                ),
                textStyle = TextStyle(fontSize = 18.sp),
                placeholder = { Text("Search...", fontSize = 18.sp) },
                shape = RoundedCornerShape(24.dp),
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Filled.Search,
                        contentDescription = "Search"
                    )
                },
                keyboardOptions = KeyboardOptions.Default.copy(
                    imeAction = ImeAction.Search
                ),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        val match = searchableItems.firstOrNull { item ->
                            item.keywords.any { keyword ->
                                keyword.contains(searchText.trim(), ignoreCase = true)
                            }
                        }

                        if (match != null) {
                            navController.navigate(match.route)
                        } else {
                            Toast.makeText(context, "Feature not found", Toast.LENGTH_SHORT).show()
                        }
                    }
                )
            )




            Spacer(modifier = Modifier.height(24.dp))
            var currentTipIndex by remember { mutableStateOf(0) }
            val tips = mutableListOf<String>()
            val tipsArray = context.resources.getStringArray(R.array.tips)

            if (user?.stepGoal == 0 || user?.stepGoal == null) {
                tips.add(tipsArray[6])
            }
            if (healthData.steps < 0.75f * (user?.stepGoal?.toFloat() ?: 3000f)) {
                tips.add(tipsArray[0])  // Tip for steps

            }
            if (healthData.heartRate == 0.0f) {
                tips.add(tipsArray[4])  // Tip to connect smartwatch
            }

            if (healthData.heartRate > 100 && !tips.contains(tipsArray[4])) {
                tips.add(tipsArray[1])  // Tip for high heart rate
            }
            if (healthData.heartRate < 70 && !tips.contains(tipsArray[4])) {
                tips.add(tipsArray[2])  // Tip for low heart rate
            }
            if (healthData.calories < 300) {
                tips.add(tipsArray[3])  // Tip for calories
            }

            if (tips.isEmpty()) {
                tips.add(tipsArray[5])  // Default tip if no conditions match
            }


            LaunchedEffect(tips) {
                while (true) {
                    delay(4000)
                    currentTipIndex = (currentTipIndex + 1) % tips.size
                }
            }

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .align(Alignment.CenterHorizontally),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732)),
                border = BorderStroke(1.dp, Color(0x50E3A356)),
            ) {

                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 20.dp),
                        text = "Tip of the Day",
                        color = Color(0xFFda9d5f),
                        fontSize = 20.sp,
                        textAlign = TextAlign.Center
                    )
                    AnimatedContent(
                        targetState = tips[currentTipIndex],
                        transitionSpec = {
                            slideInHorizontally { it } + fadeIn() with
                                    slideOutHorizontally { -it } + fadeOut()
                        }
                    ) {

                        Text(
                            modifier = Modifier.padding(15.dp),
                            text = it,
                            fontSize = 17.sp,
                            style = TextStyle(lineHeight = 18.sp),
                            textAlign = TextAlign.Center
                        )

                    }
                }
                Spacer(modifier = Modifier.height(16.dp))

            }
            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.Center
            ) {
                tips.forEachIndexed { index, _ ->
                    Box(
                        modifier = Modifier
                            .height(7.dp)
                            .width(15.dp)
                            .padding(horizontal = 4.dp)
                            .clip(CircleShape)
                            .background(
                                if (index == currentTipIndex) Color(0xFFda9d5f) // active
                                else Color(0x50da9d5f) // inactive
                            )
                    )
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                StatItem(
                    value = healthData.heartRate.toString(),
                    unit = "BPM",
                    color = Color(0xFFFF6B6B),
                    icon = Icons.Default.Favorite
                )
                StatItem(
                    value = healthData.steps.toString(),
                    unit = "Steps",
                    color = Color(0xFF4DABF7),
                    icon = Icons.Default.Person
                )
                StatItem(
                    value = "${String.format("%.2f", healthData.distance / 1000)} km",
                    unit = "km",
                    color = Color(0xFF51CF66),
                    icon = Icons.Default.LocationOn
                )
            }


            Spacer(modifier = Modifier.height(24.dp))

            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { navController.navigate("dietAndGoals") }),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732)),
                border = BorderStroke(1.dp, Color(0x50E3A356))
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = "Achieve Your Goals!",
                            color = Color(0xFFda9d5f),
                            fontSize = 20.sp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "Set and track your daily health goals to stay active and healthy. Keep smashing those milestones!",
                            fontSize = 14.sp,
                            style = TextStyle(lineHeight = 16.5.sp),
                            textAlign = TextAlign.Start,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )
                        if (showProgressBar) {
                            user?.stepGoal?.let {
                                if (it != 0) {
                                    LinearProgressIndicator(
                                        progress = progress.toFloat() / it,
                                        color = Color(0xFFda9d5f),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .height(8.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                    )


                                    Text(
                                        text = "Steps: ${progress.toInt()} / ${it.toInt()} ",
                                        fontSize = 12.sp,
                                        color = Color.Gray,
                                        modifier = Modifier.padding(top = 4.dp)
                                    )
                                }
                            }
                        }
                    }
                    Box(
                        modifier = Modifier.size(110.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.goals), // Replace with your asset
                            contentDescription = "Daily Goals",
                            Modifier.size(80.dp)
                        )
                    }
                }
            }
            Spacer(Modifier.padding(15.dp))


            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable(onClick = { navController.navigate("friends") }),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732)),
                border = BorderStroke(1.dp, Color(0x50E3A356))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceEvenly,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.family),
                        contentDescription = "family",
                        Modifier
                            .size(110.dp)
                            .padding(start = 5.dp)
                    )

                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(5.dp)
                    ) {
                        Box {
                            Text(
                                text = "Let your family care you!",
                                modifier = Modifier
                                    .align(alignment = Alignment.TopStart)
                                    .padding(5.dp),
                                color = Color(0xFFda9d5f),
                                fontSize = 20.sp,
                            )
                        }
                        Text(
                            text = "Stay connected with loved ones as they monitor your health in real time, ensuring care and support when you need it most.",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(5.dp, 0.dp, 10.dp, 10.dp),
                            textAlign = TextAlign.Justify,
                            style = TextStyle(lineHeight = 16.5.sp)
                        )
                    }
                }
            }
            Spacer(Modifier.padding(15.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = Color(0xFF1C2732)),
                border = BorderStroke(1.dp, Color(0x50E3A356))
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Start,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column(
                        modifier = Modifier
                            .weight(1f)
                            .padding(start = 8.dp)
                    ) {
                        Box {
                            Text(
                                text = "Track your Health!",
                                modifier = Modifier
                                    .align(alignment = Alignment.TopStart)
                                    .padding(0.dp, 8.dp, 0.dp, 8.dp),
                                color = Color(0xFFda9d5f),
                                fontSize = 20.sp,
                            )
                        }
                        Text(
                            text = "Easily track your vital health metric all in one place to stay on top of your well-being.",
                            fontSize = 14.sp,
                            modifier = Modifier
                                .padding(0.dp, 0.dp, 8.dp, 20.dp),
                            textAlign = TextAlign.Start, // Ensures text aligns left
                            style = TextStyle(lineHeight = 16.5.sp)
                        )
                    }
                    Image(
                        painter = painterResource(id = R.drawable.vitals),
                        contentDescription = "family",
                        Modifier
                            .size(80.dp)
                            .weight(0.5f) // Use weight to take up the remaining space

                    )
                }
            }

        }
    }

@Composable
fun StatItem(
    value: String,
    unit: String,
    color: Color,
    icon: ImageVector
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .clip(CircleShape)
                .border(2.dp, color, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = color,
                modifier = Modifier.size(24.dp)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            value,
            color = Color.White,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            unit,
            color = Color.White,
            fontSize = 14.sp
        )
    }
}

fun buildFitnessOptions(): FitnessOptions {
    return FitnessOptions.builder()
        .addDataType(DataType.TYPE_STEP_COUNT_DELTA, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_HEART_RATE_BPM, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_CALORIES_EXPENDED, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_BODY_FAT_PERCENTAGE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_SLEEP_SEGMENT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_WORKOUT_EXERCISE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_LOCATION_SAMPLE, FitnessOptions.ACCESS_READ)
        .addDataType(DataType.TYPE_ACTIVITY_SEGMENT, FitnessOptions.ACCESS_WRITE)
        .build()

}

