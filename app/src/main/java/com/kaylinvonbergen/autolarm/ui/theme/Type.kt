
package com.kaylinvonbergen.autolarm.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import com.kaylinvonbergen.autolarm.R

// Khand — headings, titles, navigation, and labels
val Khand = FontFamily(
    Font(R.font.khand_light, FontWeight.Light),
    Font(R.font.khand_regular, FontWeight.Normal),
    Font(R.font.khand_medium, FontWeight.Medium),
    Font(R.font.khand_semibold, FontWeight.SemiBold),
    Font(R.font.khand_bold, FontWeight.Bold)
)

// Barlow — body text, descriptions, and inputs
val Barlow = FontFamily(
    Font(R.font.barlow_thin, FontWeight.Thin),
    Font(R.font.barlow_thin_italic, FontWeight.Thin, FontStyle.Italic),

    Font(R.font.barlow_extralight, FontWeight.ExtraLight),
    Font(R.font.barlow_extralight_italic, FontWeight.ExtraLight, FontStyle.Italic),

    Font(R.font.barlow_light, FontWeight.Light),
    Font(R.font.barlow_light_italic, FontWeight.Light, FontStyle.Italic),

    Font(R.font.barlow_regular, FontWeight.Normal),
    Font(R.font.barlow_italic, FontWeight.Normal, FontStyle.Italic),

    Font(R.font.barlow_medium, FontWeight.Medium),
    Font(R.font.barlow_medium_italic, FontWeight.Medium, FontStyle.Italic),

    Font(R.font.barlow_semibold, FontWeight.SemiBold),
    Font(R.font.barlow_semibold_italic, FontWeight.SemiBold, FontStyle.Italic),

    Font(R.font.barlow_bold, FontWeight.Bold),
    Font(R.font.barlow_bold_italic, FontWeight.Bold, FontStyle.Italic),

    Font(R.font.barlow_extrabold, FontWeight.ExtraBold),
    Font(R.font.barlow_extrabold_italic, FontWeight.ExtraBold, FontStyle.Italic),

    Font(R.font.barlow_black, FontWeight.Black),
    Font(R.font.barlow_black_italic, FontWeight.Black, FontStyle.Italic)
)

// AutoLarm typography system
val Typography = Typography(
    // Large display text
    displayLarge = TextStyle(
        fontFamily = Khand,
        fontWeight = FontWeight.Bold,
        fontSize = 40.sp,
        lineHeight = 48.sp
    ),
    displayMedium = TextStyle(
        fontFamily = Khand,
        fontWeight = FontWeight.Bold,
        fontSize = 36.sp,
        lineHeight = 44.sp
    ),
    displaySmall = TextStyle(
        fontFamily = Khand,
        fontWeight = FontWeight.SemiBold,
        fontSize = 32.sp,
        lineHeight = 40.sp
    ),

    // Screen headings
    headlineLarge = TextStyle(
        fontFamily = Khand,
        fontWeight = FontWeight.Bold,
        fontSize = 30.sp,
        lineHeight = 36.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = Khand,
        fontWeight = FontWeight.SemiBold,
        fontSize = 26.sp,
        lineHeight = 32.sp
    ),
    headlineSmall = TextStyle(
        fontFamily = Khand,
        fontWeight = FontWeight.SemiBold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),

    // Card titles and navigation labels
    titleLarge = TextStyle(
        fontFamily = Khand,
        fontWeight = FontWeight.Bold,
        fontSize = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = Khand,
        fontWeight = FontWeight.SemiBold,
        fontSize = 18.sp,
        lineHeight = 24.sp
    ),
    titleSmall = TextStyle(
        fontFamily = Khand,
        fontWeight = FontWeight.Medium,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),

    // Main content
    bodyLarge = TextStyle(
        fontFamily = Barlow,
        fontWeight = FontWeight.Normal,
        fontSize = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = Barlow,
        fontWeight = FontWeight.Normal,
        fontSize = 14.sp,
        lineHeight = 20.sp
    ),
    bodySmall = TextStyle(
        fontFamily = Barlow,
        fontWeight = FontWeight.Normal,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),

    // Buttons, form labels, and captions
    labelLarge = TextStyle(
        fontFamily = Khand,
        fontWeight = FontWeight.SemiBold,
        fontSize = 16.sp,
        lineHeight = 22.sp
    ),
    labelMedium = TextStyle(
        fontFamily = Barlow,
        fontWeight = FontWeight.Medium,
        fontSize = 12.sp,
        lineHeight = 16.sp
    ),
    labelSmall = TextStyle(
        fontFamily = Barlow,
        fontWeight = FontWeight.Medium,
        fontSize = 11.sp,
        lineHeight = 16.sp
    )
)
