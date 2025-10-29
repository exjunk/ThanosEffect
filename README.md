# ThanosEffect Library

[![](https://jitpack.io/v/exjunk/ThanosEffect.svg)](https://jitpack.io/#exjunk/ThanosEffect)
[![License](https://img.shields.io/badge/License-Apache%202.0-blue.svg)](https://opensource.org/licenses/Apache-2.0)


A powerful, easy-to-use Android library that creates the iconic Thanos "snap" dust disintegration effect on any View, Bitmap, or Composable. Built with OpenGL ES for smooth, performant animations.

![db7FfXI2VMwI2GlV](https://github.com/user-attachments/assets/df3ebff1-32f8-439d-8205-9ebc34e649d0)


## Installation

### Step 1: Add JitPack repository

Add this to your root `build.gradle` or `settings.gradle`:

```gradle
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

### Step 2: Add the dependency

```gradle
dependencies {
    implementation 'com.github.yourusername:ThanosEffect:1.0.0'
}
```

## Quick Start

### Jetpack Compose (Recommended)

```kotlin
@Composable
fun MyScreen() {
    ThanosEffect { snapEffect ->
        Column {
            Text("I don't feel so good...")
            Button(onClick = { snapEffect.snap() }) {
                Text("Snap Fingers")
            }
        }
    }
}
```

### Traditional Android Views

```kotlin
// Method 1: Extension Function (Easiest!)
myView.snapWithThanosEffect(
    duration = 2000f,
    onComplete = { Log.d("Thanos", "Snapped!") }
)

// Method 2: Builder Pattern
ThanosEffectBuilder(context)
    .attachTo(myImageView)
    .setDuration(2000f)
    .setParticleSize(3f)
    .setOnComplete { /* callback */ }
    .build()
    .snap()
```

### Snap a Bitmap

```kotlin
val bitmap = BitmapFactory.decodeResource(resources, R.drawable.image)
bitmap.snapWithThanosEffect(
    context = this,
    containerView = rootLayout,
    onComplete = { /* done */ }
)
```

##  API Reference

### Composable API

#### `ThanosEffect`

Main composable wrapper for adding snap effect to content.

```kotlin
@Composable
fun ThanosEffect(
    modifier: Modifier = Modifier,
    duration: Float = 1800f,           // Animation duration in ms
    particleSize: Float = 2f,          // Dust particle size
    onComplete: (() -> Unit)? = null,  // Completion callback
    content: @Composable (SnapEffect) -> Unit
)
```

#### `SnapEffect` Interface

Controller exposed to content for triggering animations.

```kotlin
interface SnapEffect {
    fun snap(bitmap: Bitmap? = null)   // Trigger animation
    fun reset()                         // Reset to initial state
    val isAnimating: Boolean            // Check animation state
}
```

### View API

#### Extension Functions

```kotlin
// Snap any View directly
fun View.snapWithThanosEffect(
    duration: Float = 1800f,
    particleSize: Float = 2f,
    onComplete: (() -> Unit)? = null
)

// Snap a Bitmap in a container
fun Bitmap.snapWithThanosEffect(
    context: Context,
    containerView: ViewGroup,
    duration: Float = 1800f,
    particleSize: Float = 2f,
    onComplete: (() -> Unit)? = null
)
```

#### Builder Pattern

```kotlin
class ThanosEffectBuilder(context: Context) {
    fun attachTo(view: View): ThanosEffectBuilder
    fun setDuration(durationMs: Float): ThanosEffectBuilder
    fun setParticleSize(size: Float): ThanosEffectBuilder
    fun setOnComplete(callback: () -> Unit): ThanosEffectBuilder
    fun useBitmap(bitmap: Bitmap): ThanosEffectBuilder
    fun build(): ThanosEffectController
}
```

#### Controller

```kotlin
class ThanosEffectController {
    fun snap()      // Trigger effect
    fun reset()     // Reset view
    fun cleanup()   // Release resources
}
```

## Advanced Usage

### Custom Duration and Particle Size

```kotlin
ThanosEffect(
    duration = 3000f,      // 3 seconds
    particleSize = 4f      // Larger particles
) { snapEffect ->
    // Your content
}
```

### Sequential Snapping

```kotlin
@Composable
fun SequentialSnap() {
    var index by remember { mutableStateOf(0) }
    val items = listOf("First", "Second", "Third")
    
    if (index < items.size) {
        ThanosEffect(
            onComplete = { index++ }
        ) { snapEffect ->
            Text(items[index])
            Button(onClick = { snapEffect.snap() }) {
                Text("Snap")
            }
        }
    }
}
```

### With ViewModel (Production Pattern)

```kotlin
class MyViewModel : ViewModel() {
    private val _snapState = MutableStateFlow(false)
    val snapState = _snapState.asStateFlow()
    
    fun triggerSnap() {
        _snapState.value = true
    }
}

@Composable
fun MyScreen(viewModel: MyViewModel) {
    val shouldSnap by viewModel.snapState.collectAsState()
    
    ThanosEffect { snapEffect ->
        LaunchedEffect(shouldSnap) {
            if (shouldSnap) {
                snapEffect.snap()
            }
        }
        
        // Your content
    }
}
```

### List Items

```kotlin
LazyColumn {
    items(list) { item ->
        ThanosEffect { snapEffect ->
            Card {
                // Item content
                IconButton(onClick = { 
                    snapEffect.snap()
                    // Remove item after animation
                }) {
                    Icon(Icons.Default.Delete)
                }
            }
        }
    }
}
```

### Custom Bitmap Input

```kotlin
ThanosEffect { snapEffect ->
    Button(onClick = {
        val customBitmap = createMyCustomBitmap()
        snapEffect.snap(customBitmap)
    }) {
        Text("Snap Custom Bitmap")
    }
}
```

## Use Cases

- **Delete Animations** - Smooth item removal in lists
- **Game Effects** - Character/enemy defeat animations
- **UI Transitions** - Creative screen transitions
- **Error States** - Visual feedback for errors
- **Splash Screens** - Engaging app intros
- **Photo Effects** - Creative image filters
- **Tutorial Flows** - Attention-grabbing transitions

##  Configuration

### Performance Tuning

```kotlin
// Faster, less detailed (better for low-end devices)
ThanosEffect(
    duration = 1000f,
    particleSize = 4f
) { ... }

// Slower, more detailed (better for high-end devices)
ThanosEffect(
    duration = 3000f,
    particleSize = 1f
) { ... }
```

### Memory Management

```kotlin
// For Android Views - cleanup when done
val controller = ThanosEffectBuilder(context)
    .attachTo(view)
    .build()

// In onDestroy or when done
controller.cleanup()
```

##  Troubleshooting

### Animation not showing

- Ensure the view/composable has proper dimensions
- Check that OpenGL ES 2.0 is supported on device
- Verify the view is visible before snapping

### Performance issues

- Increase `particleSize` (less particles = better performance)
- Decrease `duration` (shorter animation = less computation)
- Test on actual device, not emulator

### Black screen or crash

- Check bitmap is not null
- Ensure proper threading (use coroutines for delays)
- Verify OpenGL context is initialized

## 📱 Requirements

- **Min SDK**: 24 (Android 5.0 Lollipop)
- **OpenGL ES**: 2.0+
- **Jetpack Compose**: 1.5.0+ (for Compose APIs)

##  Contributing

Contributions are welcome! Please feel free to submit a Pull Request.

1. Fork the repository
2. Create your feature branch (`git checkout -b feature/AmazingFeature`)
3. Commit your changes (`git commit -m 'Add some AmazingFeature'`)
4. Push to the branch (`git push origin feature/AmazingFeature`)
5. Open a Pull Request

## 📄 License

```
Copyright 2024 [Your Name]

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

##  Acknowledgments

- Based on the excellent article: [Building the Thanos Snap Effect](https://habr.com/ru/articles/799163/)
- OpenGL ES rendering techniques
- Medium article: [Building the Thanos Snap Effect Animation in Android with OpenGL & Jetpack compose](https://medium.com/@androiddevapps/building-the-thanos-snap-effect-animation-in-android-with-opengl-jetpack-compose-63c2c361ae25)

## 📞 Support

-  Email: hello@androiddevapps.com
-  Issues: [GitHub Issues](https://github.com/exjunk/ThanosEffect/issues)

---

**Made with ❤️ for the Android community**

If this library helped you, please ⭐ star the repo!
