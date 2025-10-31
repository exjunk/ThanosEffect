package com.exjunk.lib.thanos.vanish



import android.R.attr.bitmap
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Picture
import android.graphics.Rect
import android.view.View
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithCache
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.draw
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.graphics.nativeCanvas
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.core.graphics.createBitmap

/**
 * Main entry point for VanishEffect library
 * Provides both Compose and XML support for Thanos snap vanish animation
 */
object VanishEffect {

    /**
     * Creates a Composable modifier that captures content and enables vanish animation
     *
     * @param vanishController Controller to trigger and manage the animation
     * @param backgroundColor Background color to be rendered (default: Color.White)
     * @return Modifier that captures content for vanish effect
     */
    @Composable
    fun Modifier.vanishable(
        vanishController: VanishController,
        backgroundColor: Color = Color.White
    ): Modifier {
        val picture = remember { Picture() }

        return this
            .onGloballyPositioned { coordinates ->
                val size = coordinates.size
                val position = coordinates.boundsInRoot()
                vanishController.updateBounds(
                    Rect(
                        position.left.toInt(),
                        position.top.toInt(),
                        position.right.toInt(),
                        position.bottom.toInt()
                    )
                )
                vanishController.updateBackgroundColor(backgroundColor)
            }
            .drawWithCache {
                val width = this.size.width.toInt()
                val height = this.size.height.toInt()

                onDrawWithContent {
                    val pictureCanvas = androidx.compose.ui.graphics.Canvas(
                        picture.beginRecording(width, height)
                    )

                    draw(this, this.layoutDirection, pictureCanvas, this.size) {
                        this@onDrawWithContent.drawContent()
                    }
                    picture.endRecording()

                    vanishController.updatePicture(picture)

                    drawIntoCanvas { canvas ->
                        canvas.nativeCanvas.drawPicture(picture)
                    }
                }
            }
    }

    /**
     * Creates a VanishController instance for managing vanish animations
     *
     * @param glSurfaceView The GLSurfaceView overlay for rendering particles
     * @return VanishController instance
     */
    fun createController(glSurfaceView: VanishGLSurfaceView?): VanishController {
        return VanishController(glSurfaceView)
    }

    /**
     * Attaches vanish effect to a traditional Android View
     *
     * @param view Target view to apply vanish effect
     * @param glSurfaceView The GLSurfaceView overlay for rendering particles
     * @return VanishController instance
     */
    fun attachToView(view: View, glSurfaceView: VanishGLSurfaceView): VanishController {
        val controller = VanishController(glSurfaceView)

        view.post {
            val location = IntArray(2)
            view.getLocationOnScreen(location)

            val bounds = Rect(
                location[0],
                location[1],
                location[0] + view.width,
                location[1] + view.height
            )

            val bitmap = createBitmap(view.width, view.height)
            val canvas = Canvas(bitmap)
            view.draw(canvas)

            controller.updateBounds(bounds)
            controller.updateBitmap(bitmap)
        }

        return controller
    }
}

/**
 * Controller class for managing vanish effect animations
 * Handles picture/bitmap updates and triggers animations
 */
class VanishController internal constructor(
    private val glSurfaceView: VanishGLSurfaceView?
) {
    private var picture: Picture? = null

    private var backgroundColor: Int = android.graphics.Color.WHITE
    private var bitmap: Bitmap? = null
    private var bounds: Rect = Rect(0, 0, 0, 0)

    private val isPlaceholder = glSurfaceView == null

    companion object {
        /**
         * Creates a placeholder controller that does nothing
         * Used until GLSurfaceView is initialized
         */
        internal fun placeholder(): VanishController {
            return VanishController(null)
        }
    }


    /**
     * Updates the captured picture for Compose content
     */
    internal fun updatePicture(picture: Picture) {
        this.picture = picture
    }

    /**
     * Updates the bitmap for XML view content
     */
    internal fun updateBitmap(bitmap: Bitmap) {
        this.bitmap = bitmap
    }

    /**
     * Updates the bounds of the content to be vanished
     */
    internal fun updateBounds(bounds: Rect) {
        this.bounds = bounds
    }

    internal fun updateBackgroundColor(color: Color) {
        this.backgroundColor = android.graphics.Color.argb(
            (color.alpha * 255).toInt(),
            (color.red * 255).toInt(),
            (color.green * 255).toInt(),
            (color.blue * 255).toInt()
        )
    }



    /**
     * Triggers the vanish animation effect
     */
    fun vanish() {
        if (isPlaceholder) return

        picture?.let {
            glSurfaceView?.startAnimation(it, bounds, backgroundColor)
        } ?: bitmap?.let {
            glSurfaceView?.startAnimationWithBitmap(it, bounds)
        }
    }


    /**
     * Resets the animation state
     */
    fun reset() {
        if (isPlaceholder) return
        glSurfaceView?.resetAnimation()
    }
}