package com.example.gameappdev

import android.content.Context
import android.util.Log
import android.view.PixelCopy
import android.view.animation.OvershootInterpolator
import androidx.compose.animation.core.*
import androidx.compose.foundation.*
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.ripple.rememberRipple
import androidx.compose.runtime.*
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.room.Database
import com.example.gameappdev.call.fetchDatabase
import com.example.gameappdev.call.fetchPlayerStartData
import com.example.gameappdev.database.DataApplication
import com.example.gameappdev.database.PlayerData
import com.example.gameappdev.database.PlayerDatabase
import com.example.gameappdev.ui.theme.Caption
import com.example.gameappdev.ui.theme.ThemeViewModel
import com.example.gameappdev.ui.theme.backgroundColor
import com.example.gameappdev.ui.theme.captionColor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

// contains all the screens
@Composable
fun SplashScreen(navController: NavController) {
    val scale = remember {
        androidx.compose.animation.core.Animatable(0f)
    }
    // AnimationEffect
    LaunchedEffect(key1 = true) {
        scale.animateTo(
            targetValue = 0.7f,
            animationSpec = tween(
                durationMillis = 800,
                easing = {
                    OvershootInterpolator(4f).getInterpolation(it)
                })
        )
        delay(3000L)
        navController.navigate("Home")
    }
    // Image
    Box(contentAlignment = Alignment.Center,
        modifier = Modifier.fillMaxSize()) {
        Image(painter = painterResource(id = R.drawable.logo),
            contentDescription = "Logo",
            modifier = Modifier.scale(scale.value))
    }
}

@Composable
fun HomeScreen(
    navController: NavController,
    openDrawer: () -> Unit,
    context: Context,
    callCounter: MutableState<Int>
)
{
    Scaffold(
        topBar = {
            TopBar(
                title = "Bit Clicker Home",
                buttonIcon = Icons.Filled.Menu,
                onButtonClicked = { openDrawer() }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = { run { navController.navigate("newGame") }
                    //Ensures only one initial creation of db and api call.
                    if(callCounter.value == 0)
                        fetchPlayerStartData(context)
                        callCounter.value++
                        Log.d("test", "findwww ${callCounter.value}")
                },
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = null
                )
            }
        },
        floatingActionButtonPosition = FabPosition.Center,
        isFloatingActionButtonDocked = true,
        bottomBar = {
            BottomAppBar(
                cutoutShape = MaterialTheme.shapes.small.copy(
                    CornerSize(percent = 50)
                )
            ) {
                BottomNavigationBar(navController)
            }
        }
    ) {
        Column(
            verticalArrangement= Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            val infiniteTransition = rememberInfiniteTransition()

            val heartSize by infiniteTransition.animateFloat(
                initialValue = 100.0f,
                targetValue = 250.0f,
                animationSpec = infiniteRepeatable(
                    animation = tween(800, delayMillis = 100,easing = FastOutLinearInEasing),
                    repeatMode = RepeatMode.Reverse
                )
            )
            Image(
                painter = painterResource(id = R.drawable.logo),
                contentDescription = "Logo",
                modifier = Modifier.size(heartSize.dp)
            )
            Text(
                text = "BIT CLICKER",
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(30.dp),
            )
        }
    }
}
@Composable
fun CircleImage(imageSize: Dp) {
    Image(
        painter = painterResource(R.drawable.logo),
        contentDescription = "Circle Image",
        contentScale = ContentScale.Crop,
        modifier = Modifier
            .size(imageSize)
            .clip(CircleShape)
            .border(5.dp, Color.Gray, CircleShape)
    )
}

@Composable
fun NewGameScreen(navController: NavController, context: Context, displayCounter: MutableState<Int>) {
    Column(
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            val isNeedExpansion = rememberSaveable{ mutableStateOf(false) }
            val animatedSizeDp: Dp by animateDpAsState(targetValue = if (isNeedExpansion.value) 350.dp else 100.dp)

            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                CircleImage(animatedSizeDp)
                Button(
                    onClick = { isNeedExpansion.value = !isNeedExpansion.value }
                ) {
                    Text(
                        text = "Expand Image!",
                        color = Color.Black
                    )
                }
                Button(
                    onClick = {
                        //var db =DataApplication(applicationContext = context)
                        //Coroutine in order to access database.
                        //This updates the value of the players expCurrency per click.
                        GlobalScope.launch(Dispatchers.IO) {
                            //var db = DataApplication(applicationContext = context).database
                            var db = fetchDatabase(context)
                            var allPlayer = db.playerDataDao().getPlayerData()
                            allPlayer[0].expCurrency ++
                            Log.d("test", "findxxx ${allPlayer[0].expCurrency}")
                            db.playerDataDao().addPlayerData(PlayerData(
                                0,
                                allPlayer[0].level,
                                allPlayer[0].baseClickValue,
                                allPlayer[0].perClickMultiplier,
                                allPlayer[0].expCurrency))

                            //Updating displayCounter to display the correct value.
                            displayCounter.value = allPlayer[0].expCurrency
                        }
                    },
                    modifier = Modifier
                        .padding(top = 50.dp)
                        .width(300.dp)
                ) {
                    Text(
                        text = displayCounter.value.toString(),
                        modifier = Modifier.padding(16.dp),
                        fontWeight = FontWeight.Bold,
                        fontSize = 28.sp
                    )
                }
                Row(modifier = Modifier.padding(25.dp)) {
                    Button(onClick = { navController.navigate("home") }) {
                        Text(text = "Return Home", color = Color.Black)
                    }
                }
            }
        }
    }

/*
@Composable
fun StoreScreen(navController: NavController) {
    Scaffold(
        topBar = {
            TopBar(
                title = "Infinite Clicker",
                buttonIcon = Icons.Filled.ArrowBack,
                onButtonClicked = { navController.navigate("home") }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .wrapContentSize(Alignment.Center)
        ) {
            Text(
                text = "Store View",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colors.primaryVariant,
                modifier = Modifier.align(Alignment.CenterHorizontally),
                textAlign = TextAlign.Center,
                fontSize = 25.sp
            )
        }
    }
}
*/

@Composable
fun SettingsScreen(navController: NavController) {
    val context = LocalContext.current
    val viewModel = remember {
        ThemeViewModel(context.dataStore)
    }
    val value = viewModel.state.observeAsState().value
    val systemInDarkTheme = isSystemInDarkTheme()
    val darkModeChecked by remember(value) {
        val checked = when (value) {
            null -> systemInDarkTheme
            else -> value
        }
        mutableStateOf(checked)
    }
    val useDeviceModeChecked by remember(value) {
        val checked = when (value) {
            null -> true
            else -> false
        }
        mutableStateOf(checked)
    }
    LaunchedEffect(viewModel) {
        viewModel.request()
    }
    Scaffold(
        topBar = {
            TopBar(
                title = "Switch Theme",
                buttonIcon = Icons.Filled.ArrowBack,
                onButtonClicked = { navController.navigate("home") }
            )
        },
        bottomBar = { BottomNavigationBar(navController) }
    ) {
        Box(
            modifier = Modifier
                .background(backgroundColor())
                .fillMaxSize()
        ) {
            Column(
                modifier = Modifier.padding(40.dp),
                verticalArrangement = Arrangement.spacedBy(20.dp),
            ) {
                Row {
                    Caption(
                        text = "\uD83C\uDF19  Dark mode",
                        color = captionColor(),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = darkModeChecked,
                        onCheckedChange = { viewModel.switchToUseDarkMode(it) })
                }
                Row {
                    Caption(
                        text = "\uD83D\uDCF1  Use device settings",
                        color = captionColor(),
                        modifier = Modifier.weight(1f)
                    )
                    Switch(
                        checked = useDeviceModeChecked,
                        onCheckedChange = { viewModel.switchToUseSystemSettings(it) })
                }
            }
        }
    }
}

@Composable
fun CreditsScreen(navController: NavController) {
    TopBar(
        title = "Credits",
        buttonIcon = Icons.Filled.ArrowBack,
        onButtonClicked = { navController.navigate("home") }
    )
    Column(
        modifier = Modifier
            .fillMaxSize()
            .wrapContentSize(Alignment.Center)
    ) {
        Text(
            text = "App by:\n Ryley\n Kadin\n William\n Everett",
            fontWeight = FontWeight.Bold,
            color = MaterialTheme.colors.primaryVariant,
            modifier = Modifier.align(Alignment.CenterHorizontally),
            textAlign = TextAlign.Center,
            fontSize = 25.sp
        )
    }
}




