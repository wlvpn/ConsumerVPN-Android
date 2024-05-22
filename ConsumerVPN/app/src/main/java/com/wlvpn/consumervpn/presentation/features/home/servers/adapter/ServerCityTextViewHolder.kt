package com.wlvpn.consumervpn.presentation.features.home.servers.adapter


import android.graphics.Typeface
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.jakewharton.rxbinding3.view.clicks
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.databinding.ViewServersItemRowCityBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit

class ServerCityTextViewHolder(
    parent: ViewGroup
) : ServerViewHolder<ServerCityRow>(
    parent, R.layout.view_servers_item_row_city
) {

    private var rowClickDisposable = Disposables.disposed()

    override lateinit var item: ServerCityRow

    override fun clean() {
        rowClickDisposable.dispose()
    }

    override fun bind(item: ServerCityRow, callback: RowCallback) {

        this.item = item

        val textColor: Int
        val typeFace: Typeface?

        if (item.isSelected) {
            textColor = ContextCompat.getColor(itemView.context, R.color.server_location_selected_text_color)
            typeFace = ResourcesCompat.getFont(itemView.context, R.font.roboto_bold)
            ViewCompat.setElevation(
                itemView,
                DEFAULT_ELEVATION
            )
        } else {
            textColor = ContextCompat.getColor(itemView.context, R.color.server_location_text_color)
            typeFace = ResourcesCompat.getFont(itemView.context, R.font.roboto_regular)
            ViewCompat.setElevation(itemView, 0f)
        }

        ViewServersItemRowCityBinding.bind(itemView).apply {
            cityName.text = item.location.city
            cityName.setTextColor(textColor)
            cityName.typeface = typeFace
        }

        rowClickDisposable = itemView.clicks()
            .throttleFirst(CLICK_DELAY_MILLISECONDS, TimeUnit.MILLISECONDS)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe {
                callback.rowClick(itemView, adapterPosition, this.item)
            }
    }

}