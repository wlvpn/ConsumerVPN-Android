package com.wlvpn.consumervpn.presentation.util

import android.net.Uri
import android.util.DisplayMetrics
import com.facebook.drawee.view.SimpleDraweeView
import com.wlvpn.consumervpn.BuildConfig
import java.util.*

private const val small = "60x60/_flag_"
private const val medium = "80x80/_flag_"
private const val large = "120x120/_flag_"
private const val format = ".png"

fun setCountryFlag(draweeImageView: SimpleDraweeView, isoCountryCode: String) {
    val density = draweeImageView.getDensity()
    val flagResDensity = when {
        density <= DisplayMetrics.DENSITY_HIGH -> FlagResDensity.LOW
        density <= DisplayMetrics.DENSITY_XHIGH -> FlagResDensity.MEDIUM
        else -> FlagResDensity.HIGH
    }
    val flagUri = getUriForCountry(isoCountryCode, flagResDensity)

    // setImageCacheStrategy is a prototype function that always tries to load the image from cache first
    draweeImageView.setImageCacheStrategy(flagUri)
}

private fun getUriForCountry(countryCode: String, flagDensity: FlagResDensity): Uri {
    val flagPrefix = BuildConfig.FLAGS_HOSTNAME + flagDensity.densityText

    var fixedCountryCode = countryCode.toLowerCase(Locale.ENGLISH)
    fixedCountryCode = when (fixedCountryCode) {
        "uk" -> "gb"
        else -> fixedCountryCode
    }

    return Uri.parse(flagPrefix + fixedCountryCode + format)
}

private enum class FlagResDensity(val densityText: String) {
    LOW(small),
    MEDIUM(medium),
    HIGH(large)
}