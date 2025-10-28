package com.exjunk.lib.thanos.vanish


import android.graphics.Bitmap
import android.graphics.Rect
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.geometry.Rect as GeometryRect
import androidx.compose.ui.graphics.Canvas
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.layout.boundsInWindow
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.graphics.createBitmap

/**
 * Internal state management for Snap Effect
 */
internal class SnapEffectState(
    val duration: Float,
    val particleSize: Float,
    val onComplete: (() -> Unit)?
) {
    var isAnimating by mutableStateOf(false)
    var shouldHideContent by mutableStateOf(false)
    var capturedBitmap by mutableStateOf<Bitmap?>(null)
    var contentBounds by mutableStateOf<android.graphics.Rect?>(null)

    val snapEffect = object : SnapEffect {
        override fun snap(bitmap: Bitmap?) {
            capturedBitmap = bitmap
            this@SnapEffectState.isAnimating = true
            this@SnapEffectState.shouldHideContent = true
        }

        override fun reset() {
            this@SnapEffectState.isAnimating = false
            this@SnapEffectState.shouldHideContent = false
            capturedBitmap = null
        }

        override val isAnimating: Boolean
            get() = this@SnapEffectState.isAnimating
    }
}

/**
 * Container that manages the GL surface and content rendering
 */
@Composable
internal fun ThanosEffectContainer(
    modifier: Modifier = Modifier,
    snapState: SnapEffectState,
    content: @Composable BoxScope.() -> Unit
) {
    val context = LocalContext.current
    val glSurfaceViewRef = remember { mutableStateOf<DustGLSurfaceView?>(null) }

    Box(modifier = modifier) {
        // GL Surface overlay (always on top for particle rendering)
        if (snapState.isAnimating) {
            AndroidView(
                factory = { ctx ->
                    DustGLSurfaceView(ctx, snapState.duration, snapState.particleSize).also { view ->
                        glSurfaceViewRef.value = view

                        // Start animation with captured or rendered bitmap
                        val bitmap = snapState.capturedBitmap ?: captureBitmap(view, snapState)
                        bitmap?.let {
                            view.startAnimation(it) {
                                // Animation complete callback
                                snapState.isAnimating = false
                                snapState.onComplete?.invoke()
                            }
                        }
                    }
                },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Original content (hidden during animation)
        if (!snapState.shouldHideContent) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .onGloballyPositioned { coordinates ->
                        val bounds = coordinates.boundsInWindow()
                        snapState.contentBounds = android.graphics.Rect(
                            bounds.left.toInt(),
                            bounds.top.toInt(),
                            bounds.right.toInt(),
                            bounds.bottom.toInt()
                        )
                    },
                content = content
            )
        }
    }
}

/**
 * Capture bitmap from composable content
 */
private fun captureBitmap(view: android.view.View, snapState: SnapEffectState): Bitmap? {
    val bounds = snapState.contentBounds ?: return null

    val bitmap = createBitmap(bounds.width(), bounds.height())

    val canvas = android.graphics.Canvas(bitmap)
    view.rootView.draw(canvas)

    return bitmap
}

/**
 * Modifier extension for capturing composable as bitmap
 */
@Composable
fun Modifier.captureComposable(onCapture: (Bitmap) -> Unit): Modifier {
    return this.drawWithCache {
        val bitmap = createBitmap(size.width.toInt(), size.height.toInt())

        onDrawWithContent {
            val canvas = Canvas(bitmap.asImageBitmap())
            drawIntoCanvas { originalCanvas ->
                // Draw content to capture
                drawContent()
            }

            // Notify capture
            onCapture(bitmap)

            // Continue normal rendering
            drawContent()
        }
    }
}