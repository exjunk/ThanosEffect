package com.exjunk.thanos.effect


import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.exjunk.lib.thanos.vanish.VanishContainer
import com.exjunk.lib.thanos.vanish.VanishEffect.vanishable
import com.exjunk.lib.thanos.vanish.VanishGLSurfaceView
import com.exjunk.lib.thanos.vanish.rememberVanishController
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Demo Activity showcasing Jetpack Compose usage of VanishEffect library
 * Demonstrates both basic and advanced usage patterns
 */
class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            MaterialTheme {
                ComposeDemo()
                //SimpleVanishExample()
            }
        }
    }
}

/**
 * Main composable demonstrating vanish effect usage
 */
@Composable
fun ComposeDemo() {
    var glSurfaceView by remember { mutableStateOf<VanishGLSurfaceView?>(null) }
    val vanishController = rememberVanishController(glSurfaceView)

    var showContent by remember { mutableStateOf(true) }
    var isAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    val backgroundColor = Color(0xFFCC3BBA)

    VanishContainer(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFFFFFF)),
        onGLSurfaceViewCreated = { view ->
            glSurfaceView = view
        }
    ) {
        if (showContent) {
            Box(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(300.dp)
                    .height(200.dp)
                    .background(backgroundColor)
                    .vanishable(vanishController, backgroundColor)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "THANOS",
                        color = Color.Black,
                        fontSize = 48.sp,
                        style = MaterialTheme.typography.displayLarge
                    )
                    Text(
                        text = "Snap your fingers!",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                }
            }
        }

        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            if (showContent && !isAnimating) {
                Button(
                    onClick = {
                        isAnimating = true
                        vanishController.vanish()

                        scope.launch {
                            delay(100)
                            showContent = false
                        }
                    }
                ) {
                    Text("Snap Fingers")
                }
            }

            if (!showContent) {
                Button(
                    onClick = {
                        showContent = true
                        isAnimating = false
                        vanishController?.reset()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF0F3460)
                    )
                ) {
                    Text("Reset ↺")
                }
            }
        }
    }
}

@Composable
fun SimpleVanishExample() {
    var glSurfaceView by remember { mutableStateOf<VanishGLSurfaceView?>(null) }
    val controller = rememberVanishController(glSurfaceView)
    var showBox by remember { mutableStateOf(true) }
    val backgroundColor = Color.Blue

    VanishContainer(
        onGLSurfaceViewCreated = { glSurfaceView = it }
    ) {
        if (showBox) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(backgroundColor)
                    .vanishable(controller, backgroundColor)
            )
        }

        Button(
            onClick = {
                controller.vanish()
                showBox = false
            },
            modifier = Modifier.align(Alignment.BottomCenter)
        ) {
            Text("Vanish")
        }
    }
}