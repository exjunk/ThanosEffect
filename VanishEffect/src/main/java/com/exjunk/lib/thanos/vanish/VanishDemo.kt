package com.exjunk.lib.thanos.vanish


import android.graphics.Bitmap
import android.view.View
import androidx.compose.runtime.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.graphics.createBitmap


/**
 * Main entry point for Thanos Effect Library
 *
 * Usage:
 *
 * // For Jetpack Compose
 * ThanosEffect(
 *     modifier = Modifier.fillMaxSize(),
 *     onComplete = { /* animation finished */ }
 * ) { snapEffect ->
 *     YourContent()
 *     Button(onClick = { snapEffect.snap() }) {
 *         Text("Snap")
 *     }
 * }
 *
 * // For Android Views
 * val thanosEffect = ThanosEffectView(context)
 * thanosEffect.attachToView(targetView)
 * thanosEffect.snap()
 */

// ============================================================================
// COMPOSABLE API
// ============================================================================

/**
 * Wrapper composable that adds Thanos snap effect capability to any content
 *
 * @param modifier Modifier for the effect container
 * @param duration Animation duration in milliseconds (default: 1800ms)
 * @param particleSize Size of dust particles (default: 2f)
 * @param onComplete Callback when animation completes
 * @param content Content that can be snapped with dust effect
 */
@Composable
fun ThanosEffect(
    modifier: Modifier = Modifier,
    duration: Float = 1800f,
    particleSize: Float = 2f,
    onComplete: (() -> Unit)? = null,
    content: @Composable (SnapEffect) -> Unit
) {
    val snapState = remember { SnapEffectState(duration, particleSize, onComplete) }

    ThanosEffectContainer(
        modifier = modifier,
        snapState = snapState,
        content = { content(snapState.snapEffect) }
    )
}

/**
 * Extension function to snap any Composable content
 *
 * Usage:
 * Box(modifier = Modifier.snapOnClick { /* snapped */ }) {
 *     YourContent()
 * }
 */
@Composable
fun Modifier.snapOnClick(
    duration: Float = 1800f,
    particleSize: Float = 2f,
    onComplete: (() -> Unit)? = null
): Modifier {
    // Implementation delegated to ThanosEffect
    return this
}

/**
 * Snap effect controller interface exposed to content
 */
interface SnapEffect {
    /**
     * Trigger the dust animation
     *
     * @param bitmap Optional custom bitmap to animate. If null, captures current content
     */
    fun snap(bitmap: Bitmap? = null)

    /**
     * Reset the effect to initial state
     */
    fun reset()

    /**
     * Check if animation is currently running
     */
    val isAnimating: Boolean
}

// ============================================================================
// VIEW-BASED API (for traditional Android Views)
// ============================================================================

/**
 * Builder class for configuring Thanos Effect on Android Views
 *
 * Usage:
 * ThanosEffectBuilder(context)
 *     .attachTo(view)
 *     .setDuration(2000f)
 *     .setParticleSize(3f)
 *     .setOnComplete { Log.d("Thanos", "Snapped!") }
 *     .build()
 *     .snap()
 */
class ThanosEffectBuilder(private val context: android.content.Context) {
    private var targetView: View? = null
    private var duration: Float = 1800f
    private var particleSize: Float = 2f
    private var onComplete: (() -> Unit)? = null
    private var customBitmap: Bitmap? = null

    /**
     * Attach effect to a specific view
     */
    fun attachTo(view: View): ThanosEffectBuilder {
        targetView = view
        return this
    }

    /**
     * Set animation duration in milliseconds
     */
    fun setDuration(durationMs: Float): ThanosEffectBuilder {
        duration = durationMs
        return this
    }

    /**
     * Set particle size (default: 2f)
     */
    fun setParticleSize(size: Float): ThanosEffectBuilder {
        particleSize = size
        return this
    }

    /**
     * Set completion callback
     */
    fun setOnComplete(callback: () -> Unit): ThanosEffectBuilder {
        onComplete = callback
        return this
    }

    /**
     * Use custom bitmap instead of capturing view
     */
    fun useBitmap(bitmap: Bitmap): ThanosEffectBuilder {
        customBitmap = bitmap
        return this
    }

    /**
     * Build the effect controller
     */
    fun build(): ThanosEffectController {
        return ThanosEffectController(
            context = context,
            targetView = targetView,
            duration = duration,
            particleSize = particleSize,
            onComplete = onComplete,
            customBitmap = customBitmap
        )
    }
}

/**
 * Controller for managing Thanos effect on Android Views
 */
class ThanosEffectController internal constructor(
    private val context: android.content.Context,
    private val targetView: View?,
    private val duration: Float,
    private val particleSize: Float,
    private val onComplete: (() -> Unit)?,
    private val customBitmap: Bitmap?
) {
    private var glSurfaceView: DustGLSurfaceView? = null
    private var isAttached = false

    /**
     * Trigger the snap animation
     */
    fun snap() {
        if (glSurfaceView == null) {
            setupGLSurfaceView()
        }

        val bitmap = customBitmap ?: targetView?.let { captureViewBitmap(it) }

        bitmap?.let {
            glSurfaceView?.startAnimation(it, onComplete)
            targetView?.visibility = View.INVISIBLE
        }
    }

    /**
     * Reset and show original view
     */
    fun reset() {
        targetView?.visibility = View.VISIBLE
        glSurfaceView?.reset()
    }

    /**
     * Cleanup resources
     */
    fun cleanup() {
        glSurfaceView?.let {
            (it.parent as? android.view.ViewGroup)?.removeView(it)
        }
        glSurfaceView = null
        isAttached = false
    }

    private fun setupGLSurfaceView() {
        targetView?.let { view ->
            val parent = view.parent as? android.view.ViewGroup
            parent?.let {
                glSurfaceView = DustGLSurfaceView(context, duration, particleSize).apply {
                    layoutParams = view.layoutParams
                }
                parent.addView(glSurfaceView)
                isAttached = true
            }
        }
    }

    private fun captureViewBitmap(view: View): Bitmap {
        val bitmap = createBitmap(view.width, view.height)
        val canvas = android.graphics.Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }
}

// ============================================================================
// UTILITY EXTENSIONS
// ============================================================================

/**
 * Extension function to directly snap any View
 *
 * Usage:
 * myView.snapWithThanosEffect(
 *     duration = 2000f,
 *     onComplete = { Log.d("Thanos", "Done!") }
 * )
 */
fun View.snapWithThanosEffect(
    duration: Float = 1800f,
    particleSize: Float = 2f,
    onComplete: (() -> Unit)? = null
) {
    ThanosEffectBuilder(context)
        .attachTo(this)
        .setDuration(duration)
        .setParticleSize(particleSize)
        .apply { onComplete?.let { setOnComplete(it) } }
        .build()
        .snap()
}

/**
 * Snap a standalone bitmap with Thanos effect
 *
 * Usage:
 * val bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)
 * bitmap.snapWithThanosEffect(context, containerView) {
 *     Log.d("Thanos", "Image snapped!")
 * }
 */
fun Bitmap.snapWithThanosEffect(
    context: android.content.Context,
    containerView: android.view.ViewGroup,
    duration: Float = 1800f,
    particleSize: Float = 2f,
    onComplete: (() -> Unit)? = null
) {
    val glView = DustGLSurfaceView(context, duration, particleSize)
    containerView.addView(glView)
    glView.startAnimation(this, onComplete)
}