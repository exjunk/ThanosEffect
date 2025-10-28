package com.exjunk.thanos.effect


import android.R.attr.duration
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exjunk.lib.thanos.vanish.ThanosEffect


// ============================================================================
// EXAMPLE 1: Simple Jetpack Compose Usage
// ============================================================================

@Composable
fun SimpleComposeExample(modifier: Modifier) {
    ThanosEffect(
        modifier = Modifier.fillMaxSize(),
        duration = 2000f,
        onComplete = {
            println("Snap animation completed!")
        }
    ) { snapEffect ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            // Your content that will be "snapped"
            Box(
                modifier = Modifier
                    .size(300.dp, 200.dp)
                    .background(Color(0xFF234111)),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "THANOS",
                    color = Color(0xFFe94560),
                    fontSize = 48.sp
                )
            }

            Spacer(modifier = Modifier.height(32.dp))

            // Trigger the snap effect
            Button(
                onClick = { snapEffect.snap() },
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFe94560)
                )
            ) {
                Text("Snap Fingers")
            }

            if (snapEffect.isAnimating) {
                Spacer(modifier = Modifier.height(16.dp))
                CircularProgressIndicator()
            }
        }
    }
}

// ============================================================================
// EXAMPLE 2: Snap an Image
// ============================================================================

@Composable
fun ImageSnapExample() {
    ThanosEffect(
        modifier = Modifier.fillMaxSize(),
        particleSize = 3f,
        onComplete = {
            println("Image vanished!")
        }
    ) { snapEffect ->
        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.sample_image),
                contentDescription = "Image to snap",
                modifier = Modifier.size(300.dp)
            )

            Spacer(modifier = Modifier.height(32.dp))

            Button(onClick = { snapEffect.snap() }) {
                Text("Vanish Image")
            }
        }
    }
}

// ============================================================================
// EXAMPLE 3: Custom Card with Snap Effect
// ============================================================================

@Composable
fun CardSnapExample() {
    ThanosEffect { snapEffect ->
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = Modifier
                    .fillMaxWidth(0.8f)
                    .height(400.dp),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    verticalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = "User Profile",
                            style = MaterialTheme.typography.headlineMedium
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Name: Tony Stark")
                        Text("Status: Avenger")
                        Text("Power: Genius Billionaire")
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Button(onClick = { snapEffect.snap() }) {
                            Text("Delete")
                        }
                        Button(onClick = { snapEffect.reset() }) {
                            Text("Restore")
                        }
                    }
                }
            }
        }
    }
}