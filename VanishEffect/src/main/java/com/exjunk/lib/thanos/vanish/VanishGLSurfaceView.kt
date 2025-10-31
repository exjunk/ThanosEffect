package com.exjunk.lib.thanos.vanish


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Picture
import android.graphics.PixelFormat
import android.graphics.Rect
import android.opengl.GLSurfaceView
import android.util.AttributeSet

/**
 * Custom GLSurfaceView for rendering vanish effect particles
 * Provides transparent overlay with OpenGL particle rendering
 */
class VanishGLSurfaceView @JvmOverloads constructor(
    context: Context,
    attrs: AttributeSet? = null
) : GLSurfaceView(context, attrs) {

    private val renderer: VanishRenderer

    init {
        setEGLContextClientVersion(2)
        setZOrderOnTop(true)
        setEGLConfigChooser(8, 8, 8, 8, 16, 0)
        holder.setFormat(PixelFormat.RGBA_8888)

        renderer = VanishRenderer(context)
        setRenderer(renderer)
        renderMode = RENDERMODE_CONTINUOUSLY
    }

    /**
     * Starts the vanish animation using a Picture object
     *
     * @param picture The captured content as Picture
     * @param rect The bounds of the content
     */
    fun startAnimation(picture: Picture, rect: Rect, backgroundColor: Int = android.graphics.Color.WHITE) {
        renderer.startAnimation(picture, rect, backgroundColor)
    }

    /**
     * Starts the vanish animation using a Bitmap
     *
     * @param bitmap The captured content as Bitmap
     * @param rect The bounds of the content
     */
    fun startAnimationWithBitmap(bitmap: Bitmap, rect: Rect) {
        renderer.startAnimationWithBitmap(bitmap, rect)
    }

    /**
     * Resets the animation to initial state
     */
    fun resetAnimation() {
        renderer.resetAnimation()
    }

    /**
     * Configures animation parameters
     *
     * @param duration Animation duration in milliseconds (default: 1800)
     * @param particleSize Size of each particle (default: 2.0f)
     */
    fun setAnimationConfig(duration: Float = 1800f, particleSize: Float = 2f) {
        renderer.setAnimationConfig(duration, particleSize)
    }
}