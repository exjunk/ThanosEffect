package com.exjunk.thanos.effect

import com.exjunk.lib.thanos.vanish.VanishEffect
import com.exjunk.lib.thanos.vanish.VanishGLSurfaceView

import android.os.Bundle
import android.view.View
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Demo Activity using XML layout file for VanishEffect
 * Shows how to integrate with inflated layouts
 *
 * Requires: activity_xml_demo.xml layout file
 */
class XmlDemoWithLayoutActivity : AppCompatActivity() {

    private lateinit var glSurfaceView: VanishGLSurfaceView
    private lateinit var contentView: View
    private lateinit var snapButton: Button
    private lateinit var resetButton: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

         setContentView(R.layout.activity_xml_demo)

        setupViewsFromLayout()
        setupVanishEffect()
    }

    /**
     * Binds views from inflated layout
     * Replace with actual view binding or findViewById
     */
    private fun setupViewsFromLayout() {
         glSurfaceView = findViewById(R.id.glSurfaceView)
         contentView = findViewById(R.id.contentView)
         snapButton = findViewById(R.id.snapButton)
         resetButton = findViewById(R.id.resetButton)
    }

    /**
     * Configures vanish effect on the content view
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

    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }

    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
}