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

# Support Library
-dontwarn android.support.design.**
-keep class android.support.design.widget.** { *; }
-keep interface android.support.design.widget.** { *; }

# Google Guava
-dontwarn sun.misc.Unsafe
-dontwarn com.google.common.collect.MinMaxPriorityQueue
-keep class com.google.** # TODO: keep shouldn't really be there
-dontwarn com.google.**

# OkHttp
-keep class com.squareup.okhttp.internal.huc.** { *; }
-dontwarn okio.** # see https://github.com/square/okio/issues/60 and https://github.com/square/okhttp/issues/964

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
