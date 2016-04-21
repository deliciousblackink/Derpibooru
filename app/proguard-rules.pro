# Add project specific ProGuard rules here.
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# Add any project specific keep options here:

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

-dontskipnonpubliclibraryclasses
-forceprocessing
-optimizationpasses 5

-dontobfuscate
# required for -dontobfuscate (see http://stackoverflow.com/a/7587680/1726690)
-optimizations !field/removal/writeonly,!field/marking/private,!class/merging/*,!code/allocation/variable

-keep class * extends android.app.Activity

# Remove all Log calls
-assumenosideeffects class android.util.Log {
    public static *** d(...);
    public static *** e(...);
    public static *** v(...);
    public static *** wtf(...);
}

# Support Design Library
-dontwarn android.support.design.**
-keep class android.support.design.** { *; }
-keep interface android.support.design.** { *; }
-keep public class android.support.design.R$* { *; }

# Support Library v7
-keep class android.support.v7.widget.RoundRectDrawable { *; }
-keep public class android.support.v7.widget.** { *; }
-keep public class android.support.v7.internal.widget.** { *; }
-keep public class android.support.v7.internal.view.menu.** { *; }
-keep public class * extends android.support.v4.view.ActionProvider {
    public <init>(android.content.Context);
}

# Google Guava
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.collect.MinMaxPriorityQueue
-keep class com.google.** # TODO: this shouldn't really be there
-dontwarn com.google.**

# Glide
-keep public class * implements com.bumptech.glide.module.GlideModule
-keep public enum com.bumptech.glide.load.resource.bitmap.ImageHeaderParser$** {
    **[] $VALUES;
    public *;
}

# Gson (https://github.com/google/gson/blob/master/examples/android-proguard-example/proguard.cfg)
-keepattributes Signature
-keepattributes *Annotation*
-keepattributes EnclosingMethod
-keep class sun.misc.Unsafe { *; }
-keep class com.google.gson.stream.** { *; }
-keep class com.google.gson.examples.android.model.** { *; }

# OkHttp (https://github.com/square/okhttp/issues/2230)
-keep class okhttp3.** { *; }
-keep interface okhttp3.** { *; }
-dontwarn okhttp3.**
-dontwarn okio.** # see https://github.com/square/okio/issues/60 and https://github.com/square/okhttp/issues/964

# Jsoup
-keeppackagenames org.jsoup.nodes

# Butter Knife
-dontwarn butterknife.internal.**
-keep class butterknife.** { *; }
-keep class **$$ViewBinder { *; }
-keepclasseswithmembernames class * {
    @butterknife.* <fields>;
}
-keepclasseswithmembernames class * {
    @butterknife.* <methods>;
}
