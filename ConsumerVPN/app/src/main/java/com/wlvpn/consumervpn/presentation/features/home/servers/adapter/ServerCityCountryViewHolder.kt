package com.wlvpn.consumervpn.presentation.features.home.servers.adapter

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.jakewharton.rxbinding3.view.clicks
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.databinding.ViewServersItemRowCityCountryBinding
import com.wlvpn.consumervpn.presentation.util.setCountryFlag
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit

class ServerCityCountryViewHolder(
    parent: ViewGroup
) : ServerViewHolder<ServerCityRow>(
    parent,
    R.layout.view_servers_item_row_city_country
) {

    private var rowClickDisposable = Disposables.disposed()

    override lateinit var item: ServerCityRow

    override fun clean() {
        rowClickDisposable.dispose()
    }

    override fun bind(item: ServerCityRow, callback: RowCallback) {
        this.item = item

        val textColorCity: Int
        val textColorCountry: Int
        val typeFaceCity: Typeface?
        val typeFaceCountry: Typeface?
        val itemBackground: Drawable?
        val countryText = itemView.context.getString(
            R.string.row_server_city_country_placeholder,
            item.location.country
        )

        if (item.isSelected) {
            itemBackground = ContextCompat.getDrawable(itemView.context, R.drawable.bg_row_servers_selected_ripple)
            textColorCity = ContextCompat.getColor(itemView.context, R.color.server_location_selected_text_color)
            textColorCountry = ContextCompat.getColor(itemView.context, R.color.server_country_selected_text_color)
            typeFaceCity = ResourcesCompat.getFont(itemView.context, R.font.roboto_bold)
            typeFaceCountry = ResourcesCompat.getFont(itemView.context, R.font.roboto_regular)
            ViewCompat.setElevation(
                itemView,
                DEFAULT_ELEVATION
            )
        } else {
            itemBackground = ContextCompat.getDrawable(itemView.context, R.drawable.bg_row_servers_ripple)
            textColorCity = ContextCompat.getColor(itemView.context, R.color.server_location_text_color)
            textColorCountry = ContextCompat.getColor(itemView.context, R.color.white_60)
            typeFaceCity = ResourcesCompat.getFont(itemView.context, R.font.roboto_regular)
            typeFaceCountry = ResourcesCompat.getFont(itemView.context, R.font.roboto_light)
            ViewCompat.setElevation(itemView, 0f)
        }

        ViewServersItemRowCityCountryBinding.bind(itemView).apply {
            setCountryFlag(countryFlagImageView, item.location.countryCode)

            cityTextView.text = item.location.city
            cityTextView.setTextColor(textColorCity)
            cityTextView.typeface = typeFaceCity

            countryTextView.text = countryText
            countryTextView.setTextColor(textColorCountry)
            countryTextView.typeface = typeFaceCountry
            itemView.background = itemBackground
        }

        rowClickDisposable = itemView.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback.rowClick(itemView, adapterPosition, this.item)
            }
    }

}