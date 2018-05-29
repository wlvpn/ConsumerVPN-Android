package com.wlvpn.flags

import android.content.Context
import android.support.annotation.DrawableRes
import java.util.*

private val DEFAULT_RENAMES = mapOf(
        "uk" to "gb"
)

/**
 * Utility for retrieving a country flag.
 *
 * @param context application context
 */
class FlagResource(context: Context) {

    constructor(
            context: Context,
            renames: Map<String, String>
    ) : this(context) {
        this.renames.clear()
        this.renames.putAll(renames)
    }

    private val resources = context.resources
    private val packageName = context.packageName
    private val renames: MutableMap<String, String> = DEFAULT_RENAMES.toMutableMap()

    /**
     * Retrieve the mobile dimen flag for the respective country code.
     *
     * @param country country identifier eg. us
     * @return the country flag resource or 0
     */
    @DrawableRes
    fun forCountry(country: String): Int {
        val countryCodeMapped = cleanCountry(country)
        val resName = "_flag_$countryCodeMapped"
        return getResId(resName)
    }

    /**
     * Retrieve the TV dimen flag for the respective country code.
     *
     * @param country country identifier eg. us
     * @return the country flag resource or 0
     */
    @DrawableRes
    fun forCountryTv(country: String): Int {
        val countryCodeMapped = cleanCountry(country)
        val resName = "_flag_tv_$countryCodeMapped"
        return getResId(resName)
    }

    /**
     * Clean a country name and apply renaming.
     */
    internal fun cleanCountry(country: String): String {
        val countryCodeLC = country.toLowerCase(Locale.ENGLISH)
        return renames.getOrElse(countryCodeLC, { countryCodeLC })
    }

    @DrawableRes
    internal fun getResId(resName: String): Int {
        return resources.getIdentifier(resName, "drawable", packageName)
    }

}
