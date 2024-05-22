#android http
-dontnote android.net.http.**

-dontwarn dagger.internal.**

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

# Consumer Models for Gson
-keepclassmembers enum com.wlvpn.consumervpn.domain.model.** { *; }

# Add project specific ProGuard rules here.
# By default, the flags in this file are appended to flags specified
# in /Users/hassan.nazari/Library/Android/sdk/tools/proguard/proguard-android.txt
# You can edit the include path and order by changing the proguardFiles
# directive in build.gradle.

-keep class org.apache.http.** { *; }
-keep class android.net.compatibility.** { *; }
-keep class android.net.http.** { *; }
-keep class android.support.v4.app.** { *; }
-keep class android.support.v7.** { *; }
-keep interface android.support.v4.app.** { *; }
-keepclassmembers class * extends android.os.Parcelable {
    static ** CREATOR;
}

# Keep Retrofit
# Platform calls Class.forName on types which do not exist on Android to determine platform.
-dontnote retrofit2.Platform
# Platform used when running on RoboVM on iOS. Will not be used at runtime.
-dontnote retrofit2.Platform$IOS$MainThreadExecutor
# Platform used when running on Java 8 VMs. Will not be used at runtime.
-dontwarn retrofit2.Platform$Java8
# Retain generic listType information for use by reflection by converters and adapters.
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

-keep class dagger.* { *; }
-keep class javax.inject.* { *; }

# IKEv2
-keep class org.strongswan.android.logic.Scheduler { *; }

# Logback
-keep class ch.qos.logback.core.rolling.RollingFileAppender { *; }
-keep class ch.qos.logback.core.rolling.FixedWindowRollingPolicy { *; }
-keep class ch.qos.logback.core.rolling.SizeBasedTriggeringPolicy { *; }

# WireGuard
-keep class com.gentlebreeze.vpn.core.wireguard.api.model.** { *; }
-keep class com.netprotect.vpn.module.wireguard.api.connection.** { *; }
-keep class com.gentlebreeze.vpn.core.configuration.** { *; }

-dontwarn afu.org.checkerframework.dataflow.qual.Pure
-dontwarn afu.org.checkerframework.dataflow.qual.SideEffectFree
-dontwarn afu.org.checkerframework.framework.qual.EnsuresQualifierIf
-dontwarn afu.org.checkerframework.framework.qual.EnsuresQualifiersIf
-dontwarn com.google.firebase.crashlytics.buildtools.reloc.afu.org.checkerframework.checker.formatter.qual.ConversionCategory
-dontwarn com.google.firebase.crashlytics.buildtools.reloc.afu.org.checkerframework.checker.formatter.qual.ReturnsFormat
-dontwarn com.google.firebase.crashlytics.buildtools.reloc.afu.org.checkerframework.checker.nullness.qual.EnsuresNonNull
-dontwarn com.google.firebase.crashlytics.buildtools.reloc.afu.org.checkerframework.checker.regex.qual.Regex
-dontwarn com.google.firebase.crashlytics.buildtools.reloc.org.checkerframework.checker.formatter.qual.ConversionCategory
-dontwarn com.google.firebase.crashlytics.buildtools.reloc.org.checkerframework.checker.formatter.qual.ReturnsFormat
-dontwarn com.google.firebase.crashlytics.buildtools.reloc.org.checkerframework.checker.nullness.qual.EnsuresNonNull
-dontwarn com.google.firebase.crashlytics.buildtools.reloc.org.checkerframework.checker.regex.qual.Regex
-dontwarn javax.mail.Address
-dontwarn javax.mail.Authenticator
-dontwarn javax.mail.BodyPart
-dontwarn javax.mail.Message$RecipientType
-dontwarn javax.mail.Message
-dontwarn javax.mail.Multipart
-dontwarn javax.mail.PasswordAuthentication
-dontwarn javax.mail.Session
-dontwarn javax.mail.Transport
-dontwarn javax.mail.internet.AddressException
-dontwarn javax.mail.internet.InternetAddress
-dontwarn javax.mail.internet.MimeBodyPart
-dontwarn javax.mail.internet.MimeMessage
-dontwarn javax.mail.internet.MimeMultipart
-dontwarn javax.naming.InvalidNameException
-dontwarn javax.naming.NamingException
-dontwarn javax.naming.directory.Attribute
-dontwarn javax.naming.directory.Attributes
-dontwarn javax.naming.ldap.LdapName
-dontwarn javax.naming.ldap.Rdn
-dontwarn javax.servlet.ServletContextEvent
-dontwarn javax.servlet.ServletContextListener
-dontwarn org.apache.avalon.framework.logger.Logger
-dontwarn org.apache.log.Hierarchy
-dontwarn org.apache.log.Logger
-dontwarn org.apache.log4j.Level
-dontwarn org.apache.log4j.Logger
-dontwarn org.apache.log4j.Priority
-dontwarn org.checkerframework.dataflow.qual.Pure
-dontwarn org.checkerframework.dataflow.qual.SideEffectFree
-dontwarn org.checkerframework.framework.qual.EnsuresQualifierIf
-dontwarn org.ietf.jgss.GSSContext
-dontwarn org.ietf.jgss.GSSCredential
-dontwarn org.ietf.jgss.GSSException
-dontwarn org.ietf.jgss.GSSManager
-dontwarn org.ietf.jgss.GSSName
-dontwarn org.ietf.jgss.Oid

-keep class com.gentlebreeze.vpn.core.wireguard.api.WireGuardEndpoint { *; }
-keep class com.gentlebreeze.vpn.sdk.tier.data.datasource.UserLimitsDataSource { *; }
-keep class com.gentlebreeze.vpn.sdk.features.create.data.datasource.AccountCreationDataSource { *; }

# Consumer Models for Gson
-keep class com.wlvpn.consumervpn.domain.model.** { <fields>; }

# IKEv2
-keep class org.strongswan.android.logic** { *; }
-keep class com.gentlebreeze.vpn.module.strongswan.api.configuration.StrongSwanConfiguration** { *; }
-keep class com.gentlebreeze.vpn.module.strongswan.api.model.StrongSwanVpnProfile* { *; }
-keep class com.gentlebreeze.vpn.module.strongswan.api.model.StrongSwanVpnType* { *; }
-keep class org.strongswan.android.logic.Scheduler { *; }
-keep class org.strongswan.android.logic.CharonVpnService** {
    private static java.lang.String getAndroidVersion();
    private static java.lang.String getDeviceString();
    native <methods>;
}

-keep class org.strongswan.android.security** {
    public <methods>;
}
-keep class org.strongswan.android.utils** {
    public <methods>;
}

#Diagnostics
-keepclassmembers class ch.qos.logback.classic.pattern.* { <init>(); }
-keep public class org.slf4j.** { *; }
-keep public class ch.qos.logback.** { *; }

# Native methods
-keepclasseswithmembernames,includedescriptorclasses class * {
    native <methods>;
}

# Retrofit
-keepattributes RuntimeVisibleAnnotations
-keep class * extends androidx.navigation.Navigator

# Retrofit does reflection on generic parameters. InnerClasses is required to use Signature and
# EnclosingMethod is required to use InnerClasses.
-keepattributes Signature, InnerClasses, EnclosingMethod

# Retrofit does reflection on method and parameter annotations.
-keepattributes RuntimeVisibleAnnotations, RuntimeVisibleParameterAnnotations

# Keep annotation default values (e.g., retrofit2.http.Field.encoded).
-keepattributes AnnotationDefault

# Retain service method parameters when optimizing.
-keepclassmembers,allowshrinking,allowobfuscation interface * {
    @retrofit2.http.* <methods>;
}

# Ignore annotation used for build tooling.
-dontwarn org.codehaus.mojo.animal_sniffer.IgnoreJRERequirement

# Ignore JSR 305 annotations for embedding nullability information.
-dontwarn javax.annotation.**

# Guarded by a NoClassDefFoundError try/catch and only used when on the classpath.
-dontwarn kotlin.Unit

# Top-level functions that can only be used by Kotlin.
-dontwarn retrofit2.KotlinExtensions
-dontwarn retrofit2.KotlinExtensions$*

# With R8 full mode, it sees no subtypes of Retrofit interfaces since they are created with a Proxy
# and replaces all potential values with null. Explicitly keeping the interfaces prevents this.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface <1>

# Keep inherited services.
-if interface * { @retrofit2.http.* <methods>; }
-keep,allowobfuscation interface * extends <1>

# With R8 full mode generic signatures are stripped for classes that are not
# kept. Suspend functions are wrapped in continuations where the type argument
# is used.
-keep,allowobfuscation,allowshrinking class kotlin.coroutines.Continuation

# R8 full mode strips generic signatures from return types if not kept.
-if interface * { @retrofit2.http.* public *** *(...); }
-keep,allowoptimization,allowshrinking,allowobfuscation class <3>

# With R8 full mode generic signatures are stripped for classes that are not kept.
-keep,allowobfuscation,allowshrinking class retrofit2.Response