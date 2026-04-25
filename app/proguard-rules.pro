# Keep kotlinx.serialization generated metadata for route/data DTOs.
-keepclassmembers class **$$serializer { *; }
-keepclassmembers class * {
    *** Companion;
}
-keep @kotlinx.serialization.Serializable class * { *; }

# Keep Navigation serializable route objects.
-keep class com.aj.giphysearch.core.navigation.** { *; }

# Koin + ViewModel naming safety.
-keep class kotlin.reflect.jvm.internal.** { *; }
-keepnames class * extends androidx.lifecycle.ViewModel

# Ktor + Ktorfit generated implementations.
-keep class io.ktor.** { *; }
-dontwarn io.ktor.**
-keep class com.aj.giphysearch.data.gifs._*Impl { *; }

# Coil 3 + media playback stack.
-dontwarn coil3.**
-dontwarn androidx.media3.decoder.**
-dontwarn androidx.media3.exoplayer.ext.**
-dontwarn androidx.media3.exoplayer.hls.**

# Timber annotation noise.
-dontwarn org.jetbrains.annotations.**
