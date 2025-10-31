package com.exjunk.lib.thanos.vanish


import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxScope
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.viewinterop.AndroidView

/**
 * Composable container that provides vanish effect overlay
 * Wraps content with transparent GLSurfaceView for particle rendering
 *
 * @param modifier Modifier to be applied to the container
 * @param onGLSurfaceViewCreated Callback when GLSurfaceView is created
 * @param content Content to be displayed
 */
@Composable
fun VanishContainer(
    modifier: Modifier = Modifier,
    onGLSurfaceViewCreated: (VanishGLSurfaceView) -> Unit = {},
    content: @Composable BoxScope.() -> Unit
) {
    Box(modifier = modifier.fillMaxSize()) {
        AndroidView(
            factory = { context ->
                VanishGLSurfaceView(context).also { view ->
                    onGLSurfaceViewCreated(view)
                }
            },
            modifier = Modifier.fillMaxSize()
        )

        Box(modifier = Modifier.fillMaxSize()) {
            content()
        }
    }
}

/**
 * Remember and create a VanishController instance
 * Returns a non-null controller immediately using a placeholder until GLSurfaceView is ready
 *
 * @param glSurfaceView GLSurfaceView for rendering particles
 * @return VanishController instance (never null)
 */
@Composable
fun rememberVanishController(glSurfaceView: VanishGLSurfaceView?): VanishController {
    return remember(glSurfaceView) {
        glSurfaceView?.let { VanishEffect.createController(it) }
            ?: VanishController.placeholder()
    }
}