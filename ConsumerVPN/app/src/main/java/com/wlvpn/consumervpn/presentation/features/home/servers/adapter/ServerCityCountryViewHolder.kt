package com.wlvpn.consumervpn.presentation.features.home.servers.adapter

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.jakewharton.rxbinding3.view.clicks
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.util.setCountryFlag
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import kotlinx.android.synthetic.main.view_servers_item_row_city_country.view.*
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
        val background: Drawable?
        val countryText = itemView.context.getString(
            R.string.row_server_city_country_placeholder,
            item.location.country
        )

        if (item.isSelected) {
            background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_row_servers_selected_ripple)
            textColorCity = ContextCompat.getColor(itemView.context, R.color.text_color_location_selected)
            textColorCountry = ContextCompat.getColor(itemView.context, R.color.text_color_country_selected)
            typeFaceCity = ResourcesCompat.getFont(itemView.context, R.font.roboto_bold)
            typeFaceCountry = ResourcesCompat.getFont(itemView.context, R.font.roboto_regular)
            ViewCompat.setElevation(
                itemView,
                DEFAULT_ELEVATION
            )
        } else {
            background = ContextCompat.getDrawable(itemView.context, R.drawable.bg_row_servers_ripple)
            textColorCity = ContextCompat.getColor(itemView.context, R.color.text_color_location)
            textColorCountry = ContextCompat.getColor(itemView.context, R.color.white_60)
            typeFaceCity = ResourcesCompat.getFont(itemView.context, R.font.roboto_regular)
            typeFaceCountry = ResourcesCompat.getFont(itemView.context, R.font.roboto_light)
            ViewCompat.setElevation(itemView, 0f)
        }

        setCountryFlag(itemView.countryFlagImageView, item.location.countryCode)

        itemView.cityTextView.text = item.location.city
        itemView.cityTextView.setTextColor(textColorCity)
        itemView.cityTextView.typeface = typeFaceCity

        itemView.countryTextView.text = countryText
        itemView.countryTextView.setTextColor(textColorCountry)
        itemView.countryTextView.typeface = typeFaceCountry
        itemView.background = background

        rowClickDisposable = itemView.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback.rowClick(itemView, adapterPosition, this.item)
            }
    }

}