# Internal
# Models
-keep class com.wlvpn.slider.whitelabelvpn.models.** { *; }
-keepclassmembers class com.wlvpn.slider.whitelabelvpn.models.** { *; }


# Crashlytics
-keepattributes *Annotation*
-keepattributes SourceFile, LineNumberTable, Signature, InnerClasses
-keep class com.crashlytics.** { *; }
-dontwarn com.crashlytics.**

#android http
-dontnote android.net.http.**

#androidsvg
# SVGImageView
-keep class com.caverock.androidsvg.**
-dontwarn com.caverock.androidsvg.**
-dontwarn com.caverock.androidsvg.SVGImageView

#Apache
-keep class org.apache.harmony.xnet.provider.jsse.**

# RX Java
-dontwarn rx.internal.**
-keep class rx.schedulers.Schedulers {
    public static <methods>;
}
-keep class rx.schedulers.ImmediateScheduler {
    public <methods>;
}
-keep class rx.schedulers.TestScheduler {
    public <methods>;
}
-keep class rx.schedulers.Schedulers {
    public static ** test();
}
-keepclassmembers class rx.internal.util.unsafe.*ArrayQueue*Field* {
    long producerIndex;
    long consumerIndex;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueProducerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode producerNode;
}
-keepclassmembers class rx.internal.util.unsafe.BaseLinkedQueueConsumerNodeRef {
    rx.internal.util.atomic.LinkedQueueNode consumerNode;
}

# Google deps
-keep class android.support.v7.widget.** { *; }
-keep class android.support.design.widget.** { *; }
-dontwarn com.google.common.**

# SLF4J
-dontwarn org.slf4j.**

#Fabric
-dontnote io.fabric.sdk.**

##Jackson
-dontnote com.fasterxml.jackson.**

#Jacoco
-dontwarn org.jacoco.agent.rt.internal_773e439.**

# LoganSquare
-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }
-dontwarn com.bluelinelabs.logansquare.processor.**

# AutoValue
-dontwarn javax.lang.**
-dontwarn javax.tools.**
-dontwarn javax.annotation.**
-dontwarn autovalue.shaded.com.**
-dontwarn com.google.auto.value.**

#okHttp
-keep class okhttp3.internal.platform.** { *; }
-dontwarn okhttp3.internal.platform.*
-dontwarn okio.**

# Kotlin
-dontwarn kotlin.**
-dontnote kotlin.**



# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/hassan.nazari/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.
#

-keep class org.apache.http.** { *; }
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }
-keep class android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v4.app.** { *; }
-keepclassmembers class * extends android.os.Parcelable {
    static ** CREATOR;
}

# Keep specific App's classes
-keep class com.wlvpn.slider.whitelabelvpn.api.serverlist.** { *; }
-keep class com.wlvpn.slider.whitelabelvpn.ping.PingResultCacheValue { *; }
-keep class com.wlvpn.slider.whitelabelvpn.models.** { *; }

# Keep Retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic type information for use by reflection by converters and adapters.
-keepattributes Signature
# Retain declared checked exceptions for use by a Proxy instance.
-keepattributes Exceptions

-keepclasseswithmembers class * {
    @retrofit.http.* <methods>;
}

# Fix the following warning:
#    "Ignoring InnerClasses attribute for an anonymous inner class %CLASSNAME% that doesn't come
#     with an associated EnclosingMethod attribute."
# Unless it's causing you problems you don't really have to do anything with these warnings.
-keepattributes EnclosingMethod

# Ignore warnings
-dontwarn org.apache.http.**
-dontwarn android.support.v4.app.**
-dontwarn com.squareup.okhttp.**
-dontwarn okio.**
-dontwarn retrofit.**


# LoganSquare
-keep class com.bluelinelabs.logansquare.** { *; }
-keep @com.bluelinelabs.logansquare.annotation.JsonObject class *
-keep class **$$JsonObjectMapper { *; }
-dontwarn com.bluelinelabs.logansquare.processor.**

# AutoValue
-dontwarn javax.lang.**
-dontwarn javax.tools.**
-dontwarn javax.annotation.**
-dontwarn autovalue.shaded.com.**
-dontwarn com.google.auto.value.**
-dontwarn okio.**

# Kotlin
-dontwarn kotlin.**
-dontnote kotlin.**