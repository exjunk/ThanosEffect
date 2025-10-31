package com.exjunk.thanos.effect



import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.FrameLayout
import androidx.appcompat.app.AppCompatActivity
import com.exjunk.lib.thanos.vanish.VanishEffect
import com.exjunk.lib.thanos.vanish.VanishGLSurfaceView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Demo Activity showcasing XML layout usage of VanishEffect library
 * Demonstrates integration with traditional Android Views
 */
class XmlDemoActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: VanishGLSurfaceView
    private lateinit var contentView: View
    private lateinit var snapButton: Button
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setupViews()
        setupVanishEffect()
    }

    /**
     * Creates and configures all views programmatically
     * In real usage, you would inflate from XML layout
     */
    private fun setupViews() {
        val rootLayout = FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
            setBackgroundColor(android.graphics.Color.WHITE)
        }

        glSurfaceView = VanishGLSurfaceView(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT
            )
        }
        rootLayout.addView(glSurfaceView)

        contentView = createContentView()
        rootLayout.addView(contentView)

        snapButton = Button(this).apply {
            text = "Snap Fingers"
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
                bottomMargin = 100
            }
        }
        rootLayout.addView(snapButton)

        resetButton = Button(this).apply {
            text = "Reset ↺"
            visibility = View.GONE
            layoutParams = FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.WRAP_CONTENT,
                FrameLayout.LayoutParams.WRAP_CONTENT
            ).apply {
                gravity = android.view.Gravity.BOTTOM or android.view.Gravity.CENTER_HORIZONTAL
                bottomMargin = 100
            }
        }
        rootLayout.addView(resetButton)

        setContentView(rootLayout)
    }

    /**
     * Creates the content view that will be vanished
     */
    private fun createContentView(): View {
        return FrameLayout(this).apply {
            layoutParams = FrameLayout.LayoutParams(
                dpToPx(300),
                dpToPx(200)
            ).apply {
                gravity = android.view.Gravity.CENTER
            }
            setBackgroundColor(android.graphics.Color.parseColor("#CC3BBA"))

            val textView = android.widget.TextView(context).apply {
                text = "THANOS"
                textSize = 32f
                setTextColor(android.graphics.Color.WHITE)
                layoutParams = FrameLayout.LayoutParams(
                    FrameLayout.LayoutParams.WRAP_CONTENT,
                    FrameLayout.LayoutParams.WRAP_CONTENT
                ).apply {
                    gravity = android.view.Gravity.CENTER
                }
            }
            addView(textView)
        }
    }

    /**
     * Sets up vanish effect with button handlers
     */
    private fun setupVanishEffect() {
        val controller = VanishEffect.attachToView(contentView, glSurfaceView)

        snapButton.setOnClickListener {
            controller.vanish()

            CoroutineScope(Dispatchers.Main).launch {
                delay(100)
                contentView.visibility = View.GONE
                snapButton.visibility = View.GONE
                resetButton.visibility = View.VISIBLE
            }
        }

        resetButton.setOnClickListener {
            contentView.visibility = View.VISIBLE
            snapButton.visibility = View.VISIBLE
            resetButton.visibility = View.GONE
            controller.reset()

            contentView.post {
                VanishEffect.attachToView(contentView, glSurfaceView)
            }
        }
    }

    /**
     * Converts dp to pixels
     */
    private fun dpToPx(dp: Int): Int {
        return (dp * resources.displayMetrics.density).toInt()
    }
}