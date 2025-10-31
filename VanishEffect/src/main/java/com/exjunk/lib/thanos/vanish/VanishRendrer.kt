package com.exjunk.lib.thanos.vanish


import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Picture
import android.graphics.Rect
import android.opengl.ETC1Util.loadTexture
import android.opengl.GLES20
import android.opengl.GLSurfaceView
import android.opengl.GLUtils
import java.nio.ByteBuffer
import java.nio.ByteOrder
import java.nio.FloatBuffer
import javax.microedition.khronos.egl.EGLConfig
import javax.microedition.khronos.opengles.GL10
import androidx.core.graphics.createBitmap

/**
 * OpenGL ES 2.0 Renderer for vanish particle effect
 * Handles particle system rendering and animation
 */
class VanishRenderer(private val context: Context) : GLSurfaceView.Renderer {

    private var programId = 0
    private var aParticleIndex = 0

    private var animationStartTime = -1L
    private var isAnimating = false

    private var duration = 1800f
    private var particleSize = 2f

    private var textureId = 0
    private var particlesIndicesBuffer: FloatBuffer? = null
    private var particlesCount = 0

    private var textureWidth = 0
    private var textureHeight = 0
    private var textureLeft = 0f
    private var textureTop = 0f

    private var viewportWidth = 0
    private var viewportHeight = 0

    private var pendingBitmap: Bitmap? = null
    private var pendingRect: Rect? = null
    private var shouldStartAnimation = false
    private var pendingBackgroundColor: Int = android.graphics.Color.WHITE

    /**
     * Called when the surface is created or recreated
     */
    override fun onSurfaceCreated(gl: GL10?, config: EGLConfig?) {
        GLES20.glClearColor(0.0f, 0.0f, 0.0f, 0.0f)
        GLES20.glEnable(GLES20.GL_BLEND)
        GLES20.glBlendFunc(GLES20.GL_SRC_ALPHA, GLES20.GL_ONE_MINUS_SRC_ALPHA)

        val vertexShader = loadShader(GLES20.GL_VERTEX_SHADER, VERTEX_SHADER_CODE)
        val fragmentShader = loadShader(GLES20.GL_FRAGMENT_SHADER, FRAGMENT_SHADER_CODE)

        programId = GLES20.glCreateProgram()
        GLES20.glAttachShader(programId, vertexShader)
        GLES20.glAttachShader(programId, fragmentShader)
        GLES20.glLinkProgram(programId)

        aParticleIndex = GLES20.glGetAttribLocation(programId, "a_ParticleIndex")
    }

    /**
     * Called when the surface dimensions change
     */
    override fun onSurfaceChanged(gl: GL10?, width: Int, height: Int) {
        viewportWidth = width
        viewportHeight = height

        GLES20.glViewport(0, 0, width, height)
        GLES20.glUseProgram(programId)

        setUniform1f("u_ViewportWidth", width.toFloat())
        setUniform1f("u_ViewportHeight", height.toFloat())
    }

    /**
     * Called to draw the current frame
     */
    override fun onDrawFrame(gl: GL10?) {
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT)

        if (shouldStartAnimation && pendingBitmap != null && pendingRect != null) {
            val bitmap = pendingBitmap!!
            val rect = pendingRect!!

            loadTexture(bitmap)
            prepareParticles(bitmap.width, bitmap.height)

            textureLeft = rect.left.toFloat()
            textureTop = rect.top.toFloat()

            isAnimating = true
            animationStartTime = -1

            pendingBitmap = null
            pendingRect = null
            shouldStartAnimation = false
        }

        if (!isAnimating) return

        val currentTime = System.currentTimeMillis()
        if (animationStartTime < 0) {
            animationStartTime = currentTime
        }

        val elapsedTime = (currentTime - animationStartTime).toFloat()

        if (elapsedTime > duration) {
            isAnimating = false
            animationStartTime = -1
            return
        }

        GLES20.glUseProgram(programId)

        setUniform1f("u_ElapsedTime", elapsedTime)
        setUniform1f("u_AnimationDuration", duration)
        setUniform1f("u_ParticleSize", particleSize)
        setUniform1f("u_TextureWidth", textureWidth.toFloat())
        setUniform1f("u_TextureHeight", textureHeight.toFloat())
        setUniform1f("u_TextureLeft", textureLeft)
        setUniform1f("u_TextureTop", textureTop)

        val uTexture = GLES20.glGetUniformLocation(programId, "u_Texture")
        GLES20.glUniform1i(uTexture, 0)
        GLES20.glActiveTexture(GLES20.GL_TEXTURE0)
        GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureId)

        particlesIndicesBuffer?.let { buffer ->
            GLES20.glVertexAttribPointer(aParticleIndex, 1, GLES20.GL_FLOAT, false, 0, buffer)
            GLES20.glEnableVertexAttribArray(aParticleIndex)
            GLES20.glDrawArrays(GLES20.GL_POINTS, 0, particlesCount)
            GLES20.glDisableVertexAttribArray(aParticleIndex)
        }
    }

    /**
     * Starts animation with Picture object
     */
    fun startAnimation(picture: Picture, rect: Rect, backgroundColor: Int = android.graphics.Color.WHITE) {
        val bitmap = createBitmapFromPicture(picture, rect, backgroundColor)
        startAnimationWithBitmap(bitmap, rect)
    }

    /**
     * Starts animation with Bitmap object
     */
    fun startAnimationWithBitmap(bitmap: Bitmap, rect: Rect) {
        if (bitmap.width == 0 || bitmap.height == 0) {
            return
        }

        pendingBitmap = bitmap
        pendingRect = rect
        shouldStartAnimation = true
    }

    /**
     * Resets animation state
     */
    fun resetAnimation() {
        isAnimating = false
        animationStartTime = -1
        shouldStartAnimation = false
    }

    /**
     * Configures animation parameters
     */
    fun setAnimationConfig(animDuration: Float, particleSize: Float) {
        this.duration = animDuration
        this.particleSize = particleSize
    }

    /**
     * Creates bitmap from Picture with background
     */
    private fun createBitmapFromPicture(picture: Picture, rect: Rect, backgroundColor: Int): Bitmap {
        val width = if (picture.width > 0) picture.width else rect.width()
        val height = if (picture.height > 0) picture.height else rect.height()

        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(bitmap)

        val paint = android.graphics.Paint().apply {
            color = backgroundColor  // Use the passed color instead of hardcoded white
            style = android.graphics.Paint.Style.FILL
        }
        canvas.drawRect(0f, 0f, width.toFloat(), height.toFloat(), paint)
        canvas.drawPicture(picture)

        return bitmap
    }

    /**
     * Loads bitmap into OpenGL texture
     */
    private fun loadTexture(bitmap: Bitmap) {
        val textureHandle = IntArray(1)
        GLES20.glGenTextures(1, textureHandle, 0)

        if (textureHandle[0] != 0) {
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, textureHandle[0])

            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MIN_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_MAG_FILTER,
                GLES20.GL_LINEAR
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_S,
                GLES20.GL_CLAMP_TO_EDGE
            )
            GLES20.glTexParameteri(
                GLES20.GL_TEXTURE_2D,
                GLES20.GL_TEXTURE_WRAP_T,
                GLES20.GL_CLAMP_TO_EDGE
            )

            GLUtils.texImage2D(GLES20.GL_TEXTURE_2D, 0, bitmap, 0)
            GLES20.glBindTexture(GLES20.GL_TEXTURE_2D, 0)
        }

        textureId = textureHandle[0]
    }

    /**
     * Prepares particle system for animation
     */
    private fun prepareParticles(bitmapWidth: Int, bitmapHeight: Int) {
        textureWidth = (bitmapWidth / particleSize).toInt()
        textureHeight = (bitmapHeight / particleSize).toInt()
        particlesCount = textureWidth * textureHeight

        val particleIndices = FloatArray(particlesCount) { it.toFloat() }

        particlesIndicesBuffer = ByteBuffer.allocateDirect(particleIndices.size * 4)
            .order(ByteOrder.nativeOrder())
            .asFloatBuffer()
            .put(particleIndices)
        particlesIndicesBuffer?.position(0)
    }

    /**
     * Loads and compiles shader
     */
    private fun loadShader(type: Int, shaderCode: String): Int {
        val shader = GLES20.glCreateShader(type)
        GLES20.glShaderSource(shader, shaderCode)
        GLES20.glCompileShader(shader)
        return shader
    }

    /**
     * Sets uniform float value in shader
     */
    private fun setUniform1f(name: String, value: Float) {
        val location = GLES20.glGetUniformLocation(programId, name)
        GLES20.glUniform1f(location, value)
    }

    companion object {
        private const val VERTEX_SHADER_CODE = """
            precision highp float;
            
            uniform float u_AnimationDuration;
            uniform float u_ParticleSize;
            uniform float u_ElapsedTime;
            uniform float u_ViewportWidth;
            uniform float u_ViewportHeight;
            uniform float u_TextureWidth;
            uniform float u_TextureHeight;
            uniform float u_TextureLeft;
            uniform float u_TextureTop;
            
            attribute float a_ParticleIndex;
            
            varying vec2 v_ParticleCoord;
            varying float v_ParticleLifetime;
            
            float random(float v) {
                return fract(sin(v) * 100000.0);
            }
            
            float random(vec2 st) {
                return fract(sin(dot(st.xy, vec2(12.9898, 78.233))) * 43758.5453123);
            }
            
            float interpolateLinear(float start, float end, float factor) {
                return start + (end - start) * factor;
            }
            
            float normalizeX(float x) {
                return 2.0 * x / u_ViewportWidth - 1.0;
            }
            
            float normalizeY(float y) {
                return 1.0 - 2.0 * y / u_ViewportHeight;
            }
            
            vec2 getPositionFromIndex(float particleSize, float index) {
                float y = floor(index / u_TextureWidth);
                float x = index - y * u_TextureWidth;
                return vec2(
                    particleSize * (x + 0.5) + u_TextureLeft,
                    particleSize * (y + 0.5) + u_TextureTop
                );
            }
            
            vec4 calculatePosition(vec2 position, float r, float factor) {
                float normalizedX = normalizeX(position.x);
                float normalizedY = normalizeY(position.y);
                
                float x = interpolateLinear(
                    normalizedX,
                    normalizedX + (fract(10000.0 * random(normalizedY) * random(normalizedY) * r) - 0.5),
                    factor
                );
                
                float y = interpolateLinear(
                    normalizedY,
                    normalizedY + (fract(100000.0 * random(normalizedX) * random(normalizedX) * r) - 0.25),
                    factor
                );
                
                return vec4(x, y, 0.0, 1.0);
            }
            
            void main() {
                vec2 position = getPositionFromIndex(u_ParticleSize, a_ParticleIndex);
                
                float r = random(vec2(position.x, position.y));
                
                float particleAnimationMinTime = u_AnimationDuration / 4.0;
                float particleAnimationTotalTime = particleAnimationMinTime * (1.0 + r);
                float particleAnimationDelay = position.x / u_ViewportWidth * particleAnimationMinTime;
                
                float particleLifetime = min(u_ElapsedTime / (particleAnimationDelay + particleAnimationTotalTime), 1.0);
                
                float acceleration = 1.0 + 3.0 * (position.x / u_ViewportWidth);
                
                gl_Position = calculatePosition(position, r, pow(particleLifetime, acceleration));
                gl_PointSize = u_ParticleSize;
                
                v_ParticleLifetime = particleLifetime;
                
                vec2 textureOffset = vec2(u_TextureLeft, u_TextureTop);
                vec2 originalTextureSize = vec2(u_TextureWidth * u_ParticleSize, u_TextureHeight * u_ParticleSize);
                v_ParticleCoord = (position - textureOffset) / originalTextureSize;
            }
        """

        private const val FRAGMENT_SHADER_CODE = """
            precision mediump float;
            
            uniform sampler2D u_Texture;
            
            varying vec2 v_ParticleCoord;
            varying float v_ParticleLifetime;
            
            float interpolateLinear(float start, float end, float factor) {
                return start + (end - start) * factor;
            }
            
            void main() {
                if (v_ParticleLifetime == 1.0) {
                    discard;
                }
                
                vec4 textureColor = texture2D(u_Texture, v_ParticleCoord);
                
                if (textureColor.a == 0.0) {
                    discard;
                }
                
                vec2 center = vec2(0.5, 0.5);
                float distanceToCenter = distance(center, gl_PointCoord);
                float visibilityFactor = pow(v_ParticleLifetime, 5.0);
                
                if (distanceToCenter > 1.0 - visibilityFactor) {
                    discard;
                }
                
                float alpha = interpolateLinear(textureColor.a, 0.0, visibilityFactor);
                gl_FragColor = vec4(textureColor.xyz, alpha);
            }
        """
    }
}