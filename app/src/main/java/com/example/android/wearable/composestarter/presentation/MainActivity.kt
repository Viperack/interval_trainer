/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.wearable.composestarter.presentation

import android.content.Context
import android.os.Bundle
import android.os.VibrationAttributes
import android.os.VibrationEffect
import android.os.Vibrator
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material.Icon
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Stop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.wear.compose.material.ChipDefaults
import androidx.wear.compose.material.CompactButton
import androidx.wear.compose.material.CompactChip
import androidx.wear.compose.material.Text
import androidx.wear.compose.navigation.SwipeDismissableNavHost
import androidx.wear.compose.navigation.composable
import androidx.wear.compose.navigation.rememberSwipeDismissableNavController
import com.example.android.wearable.composestarter.presentation.theme.WearAppTheme
import kotlinx.coroutines.delay


var exerciseDuration: Long = 3000
var restDuration: Long = 3000
var repetitions: Int = 3
val titleFontSize: Int = 10


/**
 * Simple "Hello, World" app meant as a starting point for a new project using Compose for Wear OS.
 *
 * Displays only a centered [Text] composable, and the actual text varies based on the shape of the
 * device (round vs. square/rectangular).
 *
 * If you plan to have multiple screens, use the Wear version of Compose Navigation. You can carry
 * over your knowledge from mobile and it supports the swipe-to-dismiss gesture (Wear OS's
 * back action). For more information, go here:
 * https://developer.android.com/reference/kotlin/androidx/wear/compose/navigation/package-summary
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            WearApp()
        }
    }
}

@Composable
fun WearApp() {
    WearAppTheme {
        val navController = rememberSwipeDismissableNavController()
        SwipeDismissableNavHost(
            navController = navController, startDestination = "startPage"
        ) {
            composable("startPage") {
                StartPageScreen(
                    onNavigateToSelectExerciseTime = { navController.navigate("selectExerciseTime") },
                    onNavigateToSelectRestTime = { navController.navigate("selectRestTime") },
                )
            }
            composable("selectExerciseTime") {
                selectExerciseTimeScreen(onNavigateToStartPage = {
                    navController.navigate("startPage") {
                        popUpTo("startPage")
                    }
                })
            }
            composable("selectRestTime") {
                selectRestTimeScreen(onNavigateToStartPage = {
                    navController.navigate("startPage") {
                        popUpTo("startPage")
                    }
                })
            }
        }
    }
}

@Composable
fun StartPageScreen(
    onNavigateToSelectExerciseTime: () -> Unit,
    onNavigateToSelectRestTime: () -> Unit,
) {
    val context = LocalContext.current
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    val vibrationPattern = longArrayOf(0, 500, 50, 300)
    var currentExerciseTime: Long by remember {
        mutableStateOf(exerciseDuration)
    }
    var isExerciseTimerRunning: Boolean by remember {
        mutableStateOf(false)
    }
    var currentRestTime: Long by remember {
        mutableStateOf(restDuration)
    }
    var isRestTimerRunning: Boolean by remember {
        mutableStateOf(false)
    }
    var currentRepetitions: Int by remember {
        mutableStateOf(repetitions)
    }
    var isExerciseCurrentTimer: Boolean by remember {
        mutableStateOf(true)
    }
    LaunchedEffect(key1 = currentExerciseTime, key2 = isExerciseTimerRunning) {
        if (currentExerciseTime <= 0 && isExerciseTimerRunning) {
            isExerciseTimerRunning = false
            currentExerciseTime = exerciseDuration
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))
            isExerciseCurrentTimer = false
            isRestTimerRunning = true
        } else if (isExerciseTimerRunning) {
            delay(1000L)
            currentExerciseTime -= 1000L
        }
    }
    LaunchedEffect(key1 = currentRestTime, key2 = isRestTimerRunning) {
        if (currentRestTime <= 0 && isRestTimerRunning) {
            isRestTimerRunning = false
            currentRestTime = restDuration
            currentRepetitions -= 1
            vibrator.vibrate(VibrationEffect.createOneShot(100, VibrationEffect.DEFAULT_AMPLITUDE))

            isExerciseCurrentTimer = true
            if (currentRepetitions != 0) {
                isExerciseTimerRunning = true
            } else {
                currentRepetitions = repetitions
            }
        } else if (isRestTimerRunning) {
            delay(1000L)
            currentRestTime -= 1000L
        }


    }
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {

        Row {
            Timer(currentExerciseTime)
            Spacer(modifier = Modifier.size(5.dp))
            Timer(currentRestTime)

        }

        Row {
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                CompactChip(onClick = {/*TODO*/ },
                    colors = ChipDefaults.primaryChipColors(),
                    border = ChipDefaults.chipBorder(),
                    label = {
                        Text(
                            textAlign = TextAlign.Center, text = "$currentRepetitions"
                        )
                    })
                Text(
                    text = "Reps", fontSize = titleFontSize.sp
                )
            }
            CompactButton(

                onClick = {
                    if (isExerciseCurrentTimer) {
                        isExerciseTimerRunning = !isExerciseTimerRunning
                    } else {
                        isRestTimerRunning = !isRestTimerRunning
                    }
                },

                ) {
                Icon(Icons.Filled.PlayArrow, "menu")   // ok
            }
            CompactButton(
                onClick = {
                    isExerciseTimerRunning = false;
                    currentExerciseTime = exerciseDuration;
                    isRestTimerRunning = false;
                    currentRestTime = exerciseDuration;
                    currentRepetitions = repetitions;
                          },
            ) {
                Icon(Icons.Filled.Stop, "print") // ok

            }


        }


    }
}

@Composable
fun selectExerciseTimeScreen(
    onNavigateToStartPage: () -> Unit,

    ) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SELECT EXERCISE TIME SCREEN")
    }
}


@Composable
fun selectRestTimeScreen(
    onNavigateToStartPage: () -> Unit,

    ) {
    Column(
        Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("SELECT REST TIME SCREEN")
    }
}

@Composable
fun Timer(
    currentTime: Long
    // onNavigateToSelectExerciseTime : () -> Unit
    // title: String
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Text(
            text = "Exercise", fontSize = titleFontSize.sp
        )
        CompactChip(
            onClick = {}, //onNavigateToSelectExerciseTime,
            colors = ChipDefaults.primaryChipColors(), border = ChipDefaults.chipBorder(), label = {
                Text(
                    textAlign = TextAlign.Center, text = Util.displayTimeString(currentTime)
                )
            })
    }
}

/*Column(
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = "Exercise",
                    fontSize = titleFontSize.sp
                )
                CompactChip(
                    onClick = onNavigateToSelectExerciseTime,
                    colors = ChipDefaults.primaryChipColors(),
                    border = ChipDefaults.chipBorder(),
                    label = {
                        Text(
                            textAlign = TextAlign.Center,
                            text = Util.displayTimeString(exerciseDuration)
                        )
                    }
                )
            }*/



