# ==============================
# BASIC PROGUARD CONFIGURATION
# ==============================

# Keep the Application class
-keep class com.riteshbkadam.beatsfitapp.** { *; }

# Keep Activities, Fragments, ViewModels
-keep public class * extends android.app.Activity
-keep public class * extends androidx.fragment.app.Fragment
-keep public class * extends androidx.lifecycle.ViewModel

# Keep all Parcelable implementations
-keep class * implements android.os.Parcelable {
    public static final android.os.Parcelable$Creator *;
}

# Keep classes annotated with @Keep
-keep @androidx.annotation.Keep class * { *; }

# Keep custom views if used in XML
-keep public class * extends android.view.View {
    public <init>(android.content.Context);
    public <init>(android.content.Context, android.util.AttributeSet);
    public <init>(android.content.Context, android.util.AttributeSet, int);
}

# ==============================
# RETROFIT / GSON SUPPORT
# ==============================

# Retrofit (keep interfaces)
-keep interface retrofit2.** { *; }
-keep class retrofit2.** { *; }

# Gson (keep models)
-keep class com.riteshbkadam.beatsfitapp.model.** { *; }
-keep class com.google.gson.** { *; }

# If using Moshi (optional)
# -keep class com.squareup.moshi.** { *; }

# ==============================
# FIREBASE (if used)
# ==============================

# Firebase core
-keep class com.google.firebase.** { *; }

# Firebase Messaging / Analytics / Auth
-keep class com.google.firebase.messaging.** { *; }
-keep class com.google.firebase.analytics.** { *; }
-keep class com.google.firebase.auth.** { *; }

# ==============================
# JETPACK / COMPOSE SUPPORT
# ==============================

# Jetpack Compose (safe minimal rule)
-keep class androidx.compose.** { *; }
-dontwarn androidx.compose.**

# ViewModel SavedState
-keep class androidx.lifecycle.SavedStateHandle { *; }

# ==============================
# MISC
# ==============================

# Prevent obfuscation of resource identifiers
-keepclassmembers class **.R$* {
    public static <fields>;
}

# Allow reflection on all classes (if needed)
# -keepclassmembers class * {
#     *;
# }

# Don't strip native method declarations
-keepclasseswithmembernames class * {
    native <methods>;
}
# Please add these rules to your existing keep rules in order to suppress warnings.
# This is generated automatically by the Android Gradle plugin.
-dontwarn com.google.android.gms.auth.api.credentials.Credential$Builder
-dontwarn com.google.android.gms.auth.api.credentials.Credential
-dontwarn com.google.android.gms.auth.api.credentials.CredentialRequest$Builder
-dontwarn com.google.android.gms.auth.api.credentials.CredentialRequest
-dontwarn com.google.android.gms.auth.api.credentials.CredentialRequestResponse
-dontwarn com.google.android.gms.auth.api.credentials.Credentials
-dontwarn com.google.android.gms.auth.api.credentials.CredentialsClient
-dontwarn com.google.android.gms.auth.api.credentials.CredentialsOptions$Builder
-dontwarn com.google.android.gms.auth.api.credentials.CredentialsOptions
-dontwarn com.google.android.gms.auth.api.credentials.HintRequest$Builder
-dontwarn com.google.android.gms.auth.api.credentials.HintRequest