# VanishEffect Library - Thanos snap effect for Jetpack Compose and XML layouts

A stunning Android library that creates the iconic Thanos snap disintegration effect for your UI elements. Powered by OpenGL ES 2.0 with seamless support for both **Jetpack Compose** and **traditional XML layouts**.
Animation is similar  to Telegram's delete message animation 

[![](https://jitpack.io/v/exjunk/vanish-effect.svg)](https://jitpack.io/#exjunk/ThanosEffect)
[![License](https://img.shields.io/badge/License-MIT-blue.svg)](LICENSE)

## Features

-  Smooth particle-based disintegration animation
-  Full support for Jetpack Compose and XML layouts
-  Hardware-accelerated OpenGL rendering
-  True plug-and-play integration
-  Customizable animation parameters
-  Works with any UI element - text, images, cards, complex layouts
-  Lightweight with minimal dependencies

## Demo

![Vanish Effect Demo](demo.gif)

## Installation

### Step 1: Add JitPack repository

Add JitPack to your root `build.gradle` or `settings.gradle.kts`:

**Gradle (Groovy)**
```gradle
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url 'https://jitpack.io' }
    }
}
```

**Gradle (Kotlin DSL)**
```kotlin
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }
}
```

### Step 2: Add the dependency

Add to your app-level `build.gradle`:

```gradle
dependencies {
    implementation 'com.github.exjunk:ThanosEffect:1.0.1'
}
```

## Quick Start

### Jetpack Compose

The simplest way to add the vanish effect:

```kotlin
@Composable
fun QuickStartExample() {
    var glSurfaceView by remember { mutableStateOf<VanishGLSurfaceView?>(null) }
    val controller = rememberVanishController(glSurfaceView)
    var showContent by remember { mutableStateOf(true) }
    
    val boxColor = Color.Blue

    VanishContainer(
        onGLSurfaceViewCreated = { glSurfaceView = it }
    ) {
        if (showContent) {
            Box(
                modifier = Modifier
                    .size(200.dp)
                    .background(boxColor)
                    .vanishable(controller, backgroundColor = boxColor), //  Must match background!
                contentAlignment = Alignment.Center
            ) {
                Text("Snap me!", color = Color.White)
            }
        }
        
        Button(
            onClick = {
                controller.vanish()
                showContent = false
            }
        ) {
            Text("Snap!")
        }
    }
}
```

> ** IMPORTANT:** Always pass `backgroundColor` to `.vanishable()` matching your composable's actual background color. This is required for accurate particle rendering.

---

## Detailed Usage Guide

### Jetpack Compose

#### Complete Example with Reset

```kotlin
@Composable
fun VanishDemo() {
    var glSurfaceView by remember { mutableStateOf<VanishGLSurfaceView?>(null) }
    val controller = rememberVanishController(glSurfaceView)
    
    var showContent by remember { mutableStateOf(true) }
    var isAnimating by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()
    
    val backgroundColor = Color(0xFFCC3BBA)

    VanishContainer(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
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
                    .vanishable(controller, backgroundColor = backgroundColor)
                    .padding(16.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "THANOS",
                        color = Color.White,
                        fontSize = 48.sp,
                        fontWeight = FontWeight.Bold
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
                        controller.vanish()
                        
                        scope.launch {
                            delay(100) // Small delay before hiding
                            showContent = false
                        }
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFE94560)
                    )
                ) {
                    Text("Snap Fingers")
                }
            }

            if (!showContent) {
                Button(
                    onClick = {
                        showContent = true
                        isAnimating = false
                        controller.reset()
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
```

> **TIMING:** Wait 100-200ms after calling `controller.vanish()` before hiding content. This allows the animation to capture the frame properly.

#### Using with Cards and Complex Layouts

```kotlin
@Composable
fun CardVanishExample() {
    var glSurfaceView by remember { mutableStateOf<VanishGLSurfaceView?>(null) }
    val controller = rememberVanishController(glSurfaceView)
    var showCard by remember { mutableStateOf(true) }
    val scope = rememberCoroutineScope()
    
    val cardColor = Color(0xFF6C63FF)

    VanishContainer(
        modifier = Modifier.fillMaxSize()
    ) {
        if (showCard) {
            Card(
                modifier = Modifier
                    .align(Alignment.Center)
                    .width(350.dp)
                    .vanishable(controller, backgroundColor = cardColor), // Match card color
                colors = CardDefaults.cardColors(
                    containerColor = cardColor
                ),
                elevation = CardDefaults.cardElevation(8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Star,
                        contentDescription = null,
                        modifier = Modifier.size(64.dp),
                        tint = Color.White
                    )
                    Text(
                        "Premium Feature",
                        fontSize = 24.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        "This will vanish in style!",
                        fontSize = 14.sp,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                    Button(
                        onClick = {
                            controller.vanish()
                            scope.launch {
                                delay(100)
                                showCard = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.White
                        )
                    ) {
                        Text("Vanish", color = cardColor)
                    }
                }
            }
        }
    }
}
```

#### Custom Animation Parameters

```kotlin
@Composable
fun CustomAnimationExample() {
    var glSurfaceView by remember { mutableStateOf<VanishGLSurfaceView?>(null) }
    val controller = rememberVanishController(glSurfaceView)

    VanishContainer(
        onGLSurfaceViewCreated = { view ->
            glSurfaceView = view
            
            // Customize animation
            view.setAnimationConfig(
                duration = 2500f,    // Slower animation (2.5 seconds)
                particleSize = 4f    // Larger particles
            )
        }
    ) {
        // Your content here
    }
}
```

### XML Layouts (Traditional Views)

#### Step 1: Add VanishGLSurfaceView to Layout

Create your layout file (e.g., `activity_main.xml`):

```xml
<?xml version="1.0" encoding="utf-8"?>
<FrameLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:background="#FFFFFF">

    <!-- GLSurfaceView MUST be added first for proper overlay -->
    <com.vanisheffect.library.VanishGLSurfaceView
        android:id="@+id/glSurfaceView"
        android:layout_width="match_parent"
        android:layout_height="match_parent" />

    <!-- Your content to vanish -->
    <LinearLayout
        android:id="@+id/contentView"
        android:layout_width="300dp"
        android:layout_height="200dp"
        android:layout_gravity="center"
        android:background="#CC3BBA"
        android:orientation="vertical"
        android:gravity="center"
        android:padding="16dp">

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="THANOS"
            android:textColor="#FFFFFF"
            android:textSize="32sp"
            android:textStyle="bold" />

        <TextView
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:text="Snap your fingers!"
            android:textColor="#FFFFFF"
            android:textSize="14sp"
            android:layout_marginTop="8dp" />

    </LinearLayout>

    <!-- Control buttons -->
    <LinearLayout
        android:layout_width="wrap_content"
        android:layout_height="wrap_content"
        android:layout_gravity="bottom|center_horizontal"
        android:layout_marginBottom="32dp"
        android:orientation="vertical"
        android:gravity="center">

        <Button
            android:id="@+id/snapButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:backgroundTint="#E94560"
            android:text="Snap Fingers" />

        <Button
            android:id="@+id/resetButton"
            android:layout_width="wrap_content"
            android:layout_height="wrap_content"
            android:layout_marginTop="8dp"
            android:backgroundTint="#0F3460"
            android:text="Reset ↺"
            android:visibility="gone" />

    </LinearLayout>

</FrameLayout>
```

> **LAYOUT ORDER:** The `VanishGLSurfaceView` must be added as the first child in the FrameLayout to ensure proper z-ordering for the particle overlay.

#### Step 2: Setup in Activity

```kotlin
class MainActivity : AppCompatActivity() {
    
    private lateinit var glSurfaceView: VanishGLSurfaceView
    private lateinit var contentView: View
    private lateinit var snapButton: Button
    private lateinit var resetButton: Button
    private lateinit var controller: VanishController
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        
        // Initialize views
        glSurfaceView = findViewById(R.id.glSurfaceView)
        contentView = findViewById(R.id.contentView)
        snapButton = findViewById(R.id.snapButton)
        resetButton = findViewById(R.id.resetButton)
        
        // Attach vanish effect
        controller = VanishEffect.attachToView(contentView, glSurfaceView)
        
        // Setup buttons
        setupButtons()
    }
    
    private fun setupButtons() {
        snapButton.setOnClickListener {
            controller.vanish()
            
            //Wait before hiding content
            Handler(Looper.getMainLooper()).postDelayed({
                contentView.visibility = View.GONE
                snapButton.visibility = View.GONE
                resetButton.visibility = View.VISIBLE
            }, 100)
        }
        
        resetButton.setOnClickListener {
            contentView.visibility = View.VISIBLE
            snapButton.visibility = View.VISIBLE
            resetButton.visibility = View.GONE
            controller.reset()
            
            // Re-attach after reset
            contentView.post {
                controller = VanishEffect.attachToView(contentView, glSurfaceView)
            }
        }
    }
    
    // IMPORTANT: Properly handle GLSurfaceView lifecycle
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
}
```

> **LIFECYCLE:** Always call `glSurfaceView.onPause()` and `glSurfaceView.onResume()` in your Activity/Fragment lifecycle methods to prevent memory leaks and rendering issues.

#### Using in Fragments

```kotlin
class MyFragment : Fragment() {
    
    private lateinit var glSurfaceView: VanishGLSurfaceView
    private lateinit var controller: VanishController
    
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        
        glSurfaceView = view.findViewById(R.id.glSurfaceView)
        val contentView = view.findViewById<View>(R.id.contentView)
        
        controller = VanishEffect.attachToView(contentView, glSurfaceView)
    }
    
    //  Handle lifecycle in fragments too
    override fun onPause() {
        super.onPause()
        glSurfaceView.onPause()
    }
    
    override fun onResume() {
        super.onResume()
        glSurfaceView.onResume()
    }
}
```

---

## API Reference

### VanishEffect (Object)

Main entry point for the library.

#### Methods

```kotlin
// Create controller for Compose (internal use by rememberVanishController)
fun createController(glSurfaceView: VanishGLSurfaceView?): VanishController

// Attach to XML View
fun attachToView(view: View, glSurfaceView: VanishGLSurfaceView): VanishController
```

### VanishController (Class)

Controls the animation lifecycle.

#### Methods

```kotlin
// Trigger vanish animation
fun vanish()

// Reset animation state
fun reset()
```

#### Internal Methods (used by modifiers)

```kotlin
internal fun updatePicture(picture: Picture)
internal fun updateBitmap(bitmap: Bitmap)
internal fun updateBounds(bounds: Rect)
internal fun updateBackgroundColor(color: Color)
```

### VanishGLSurfaceView (Class)

Custom GLSurfaceView that renders the particle effect.

#### XML Attributes

```xml
<com.vanisheffect.library.VanishGLSurfaceView
    android:id="@+id/glSurfaceView"
    android:layout_width="match_parent"
    android:layout_height="match_parent" />
```

#### Methods

```kotlin
// Configure animation parameters
fun setAnimationConfig(
    duration: Float = 1800f,    // Animation duration in milliseconds
    particleSize: Float = 2f     // Size of each particle
)

// Manual control (advanced usage)
fun startAnimation(picture: Picture, rect: Rect, backgroundColor: Int)
fun startAnimationWithBitmap(bitmap: Bitmap, rect: Rect)
fun resetAnimation()
```

#### Lifecycle Methods

```kotlin
// Must be called in Activity/Fragment lifecycle
override fun onPause() {
    super.onPause()
    glSurfaceView.onPause()
}

override fun onResume() {
    super.onResume()
    glSurfaceView.onResume()
}
```

### Compose Helpers

#### VanishContainer

```kotlin
@Composable
fun VanishContainer(
    modifier: Modifier = Modifier,
    onGLSurfaceViewCreated: (VanishGLSurfaceView) -> Unit = {},
    content: @Composable BoxScope.() -> Unit
)
```

Wraps your content with a transparent GLSurfaceView overlay for rendering particles.

#### rememberVanishController

```kotlin
@Composable
fun rememberVanishController(
    glSurfaceView: VanishGLSurfaceView?
): VanishController
```

Creates and remembers a VanishController instance. Returns a non-null controller immediately (uses placeholder until GLSurfaceView is ready).

#### Modifier.vanishable

```kotlin
@Composable
fun Modifier.vanishable(
    vanishController: VanishController,
    backgroundColor: Color = Color.White
): Modifier
```

Marks a composable as vanishable and captures its content for the animation.

** REQUIRED:** The `backgroundColor` parameter must match your composable's actual background color.

---

## Customization

### Animation Duration and Particle Size

Control how the animation looks and behaves:

```kotlin
// In Compose
VanishContainer(
    onGLSurfaceViewCreated = { view ->
        view.setAnimationConfig(
            duration = 2500f,      // 2.5 seconds (default: 1800ms)
            particleSize = 4f      // Larger particles (default: 2.0f)
        )
    }
)

// In XML/Activity
glSurfaceView.setAnimationConfig(
    duration = 2500f,
    particleSize = 4f
)
```

**Effect of parameters:**
- **duration:** Lower = faster disintegration, Higher = slower, more dramatic effect
- **particleSize:** Lower = more particles (finer effect), Higher = fewer, larger particles

### Custom Background Colors

For non-solid backgrounds or gradients:

```kotlin
// Solid color background
val bgColor = Color(0xFFFF6B6B)
Box(
    modifier = Modifier
        .background(bgColor)
        .vanishable(controller, backgroundColor = bgColor)
)

// Gradient backgrounds - use the dominant color
val gradientBrush = Brush.verticalGradient(
    colors = listOf(Color(0xFF667EEA), Color(0xFF764BA2))
)
Box(
    modifier = Modifier
        .background(gradientBrush)
        .vanishable(controller, backgroundColor = Color(0xFF667EEA)) // Use dominant color
)
```

> **Note:** For gradient backgrounds, use the dominant or average color. The particle system works best with solid colors.

---

##  Important Guidelines & Best Practices

### 1. Background Color Matching

**Always pass the correct background color** to `.vanishable()`:

```kotlin
//CORRECT
val bgColor = Color.Red
Box(
    modifier = Modifier
        .background(bgColor)
        .vanishable(controller, backgroundColor = bgColor)
)

// INCORRECT - Animation won't render properly
Box(
    modifier = Modifier
        .background(Color.Red)
        .vanishable(controller) // Missing or wrong background color
)
```

### 2. Content Visibility Timing

**Wait 100-200ms** after calling `vanish()` before hiding content:

```kotlin
// CORRECT
controller.vanish()
scope.launch {
    delay(100) // Give time for frame capture
    showContent = false
}

// INCORRECT - May not capture frame properly
controller.vanish()
showContent = false // Too fast!
```

### 3. GLSurfaceView Lifecycle

**Always handle lifecycle** in Activities and Fragments:

```kotlin
// CORRECT
override fun onPause() {
    super.onPause()
    glSurfaceView.onPause()
}

override fun onResume() {
    super.onResume()
    glSurfaceView.onResume()
}

// INCORRECT - Missing lifecycle handling leads to crashes
```

### 4. Layout Z-Order (XML)

**GLSurfaceView must be first child** in FrameLayout:

```xml
<!-- CORRECT -->
<FrameLayout>
    <com.vanisheffect.library.VanishGLSurfaceView ... />
    <YourContent ... />
</FrameLayout>

<!-- INCORRECT - Particles won't appear on top -->
<FrameLayout>
    <YourContent ... />
    <com.vanisheffect.library.VanishGLSurfaceView ... />
</FrameLayout>
```

### 5. Reset After Vanish

**Reset the controller** when showing content again:

```kotlin
//  CORRECT
Button(onClick = {
    showContent = true
    controller.reset() // Clear animation state
})

//  INCORRECT - May cause animation issues
Button(onClick = {
    showContent = true // Missing reset
})
```

### 6. Re-attachment for XML Views

**Re-attach controller** after reset in XML layouts:

```kotlin
//  CORRECT
resetButton.setOnClickListener {
    contentView.visibility = View.VISIBLE
    controller.reset()
    
    contentView.post {
        controller = VanishEffect.attachToView(contentView, glSurfaceView)
    }
}
```

---

## 🎯 Common Use Cases

### 1. Onboarding Screens

```kotlin
@Composable
fun OnboardingScreen() {
    var currentScreen by remember { mutableStateOf(0) }
    val controller = rememberVanishController(glSurfaceView)
    
    // Vanish current screen when moving to next
    Button(onClick = {
        controller.vanish()
        scope.launch {
            delay(100)
            currentScreen++
        }
    })
}
```

### 2. Item Deletion Animation

```kotlin
@Composable
fun ItemList(items: List<Item>) {
    items.forEach { item ->
        val controller = rememberVanishController(glSurfaceView)
        
        ItemCard(
            item = item,
            modifier = Modifier.vanishable(controller, itemColor),
            onDelete = {
                controller.vanish()
                scope.launch {
                    delay(100)
                    viewModel.deleteItem(item)
                }
            }
        )
    }
}
```

### 3. Game Elements

```kotlin
@Composable
fun GameEnemy(enemy: Enemy) {
    val controller = rememberVanishController(glSurfaceView)
    
    if (enemy.isAlive) {
        Image(
            painter = painterResource(enemy.image),
            modifier = Modifier.vanishable(controller, Color.Transparent)
        )
    }
    
    // When hit
    if (enemy.isHit) {
        controller.vanish()
    }
}
```

### 4. Success/Completion Effects

```kotlin
@Composable
fun TaskCompleteEffect() {
    var showTask by remember { mutableStateOf(true) }
    val controller = rememberVanishController(glSurfaceView)
    
    TaskCard(
        modifier = Modifier.vanishable(controller, taskColor),
        onComplete = {
            controller.vanish()
            playSuccessSound()
            scope.launch {
                delay(100)
                showTask = false
            }
        }
    )
}
```

---

## Troubleshooting

### Animation Not Visible

**Problem:** Particles don't appear or animation doesn't work.

**Solutions:**
1.  Ensure `backgroundColor` parameter matches actual background
2.  Check GLSurfaceView is first child in XML layouts
3.  Verify `onGLSurfaceViewCreated` callback is being called
4.  Add 100ms delay before hiding content

### App Crashes on Pause/Resume

**Problem:** App crashes when minimizing or rotating.

**Solutions:**
1.  Add `glSurfaceView.onPause()` in `onPause()`
2.  Add `glSurfaceView.onResume()` in `onResume()`
3.  Implement in both Activities and Fragments

### Black Screen After Animation

**Problem:** Content disappears but shows black instead of transparent.

**Solutions:**
1.  Verify GLSurfaceView has transparent configuration (library handles this)
2.  Check parent container background color
3.  Ensure `setZOrderOnTop(true)` is set (library handles this)

### Animation Too Fast/Slow

**Problem:** Animation duration doesn't feel right.

**Solutions:**
```kotlin
// Slower animation (more dramatic)
glSurfaceView.setAnimationConfig(duration = 2500f)

// Faster animation (snappier)
glSurfaceView.setAnimationConfig(duration = 1200f)
```

### Particles Look Pixelated

**Problem:** Particles appear too large or blocky.

**Solutions:**
```kotlin
// Smaller, finer particles
glSurfaceView.setAnimationConfig(particleSize = 1f)

// Larger particles (may look blocky)
glSurfaceView.setAnimationConfig(particleSize = 5f)
```

---

## Requirements

- **Minimum SDK:** 24 (Android 5.0 Lollipop)
- **Target SDK:** 34+
- **Kotlin:** 1.8.0+
- **Jetpack Compose:** 1.5.0+ (for Compose usage only)
- **OpenGL ES:** 2.0 support (present on all Android 5.0+ devices)

---

## License

```
MIT License

Copyright (c) 2025 Exjunk

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```

---

## Support

- **Issues:** [GitHub Issues](https://github.com/exjunk/ThanosEffect/issues)
- **Email:** hello@androiddevapps.com

If you find this library helpful, please ⭐ **star the repository**!

**Made with ❤️ and for the Android community**
