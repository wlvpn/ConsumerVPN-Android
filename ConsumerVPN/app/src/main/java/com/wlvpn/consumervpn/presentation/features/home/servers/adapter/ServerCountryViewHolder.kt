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
import kotlinx.android.synthetic.main.view_servers_item_row_country.view.*
import java.util.concurrent.TimeUnit

class ServerCountryViewHolder(
    parent: ViewGroup
) : ServerViewHolder<ServerCountryRow>(
    parent,
    R.layout.view_servers_item_row_country
) {

    private var rowClickDisposable = Disposables.disposed()

    private var expandButtonClickDisposable = Disposables.disposed()

    override lateinit var item: ServerCountryRow

    override fun clean() {
        rowClickDisposable.dispose()
        expandButtonClickDisposable.dispose()
    }

    override fun bind(item: ServerCountryRow, callback: RowCallback) {
        this.item = item

        val rowTextColor: Int
        val typeFace: Typeface?
        val locationsSize = item.cityCount

        if (item.isSelected) {
            rowTextColor = ContextCompat.getColor(itemView.context, R.color.color_accent)
            typeFace = ResourcesCompat.getFont(itemView.context, R.font.roboto_bold)
        } else {
            rowTextColor = ContextCompat.getColor(itemView.context, R.color.text_color_primary)
            typeFace = ResourcesCompat.getFont(itemView.context, R.font.roboto_regular)
        }

        setCountryFlag(itemView.countryFlagImageView, item.location.countryCode)

        val background: Drawable? = if (item.isExpanded || item.isSelected) {
            ViewCompat.setElevation(
                itemView,
                DEFAULT_ELEVATION
            )
            ContextCompat.getDrawable(itemView.context, R.drawable.bg_row_servers_selected_ripple)
        } else {
            ViewCompat.setElevation(itemView, 0f)
            ContextCompat.getDrawable(itemView.context, R.drawable.bg_row_servers_ripple)
        }

        itemView.nameTextView.text = item.location.country
        itemView.background = background
        itemView.nameTextView.setTextColor(rowTextColor)
        itemView.nameTextView.typeface = typeFace
        itemView.citiesButton.setTextColor(rowTextColor)
        itemView.citiesButton.text = itemView.context.resources.getQuantityString(
            R.plurals.row_server_cities_number,
            locationsSize,
            locationsSize
        )

        rowClickDisposable = itemView.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback.rowClick(itemView, adapterPosition, this.item)
            }

        expandButtonClickDisposable = itemView.citiesButton.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback.rowClick(itemView.citiesButton, adapterPosition, this.item)
            }
    }

}