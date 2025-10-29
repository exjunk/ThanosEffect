# Keep all public API classes
-keep public class com.exjunk.lib.thanos.vanish.ThanosEffect** { *; }
-keep public interface com.exjunk.lib.thanos.vanish.SnapEffect { *; }
-keep public class com.exjunk.lib.thanos.vanish.DustGLSurfaceView { *; }

# Keep all public members
-keepclassmembers class com.exjunk.lib.thanos.vanish.** {
    public *;
}

# Don't obfuscate
-dontobfuscate

# Keep OpenGL classes
-keep class android.opengl.** { *; }
-keep class javax.microedition.khronos.** { *; }

# Keep Compose runtime
-keep class androidx.compose.runtime.** { *; }