package com.wlvpn.consumervpn.presentation.features.home.servers.adapter

import android.graphics.Typeface
import android.graphics.drawable.Drawable
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.content.res.ResourcesCompat
import androidx.core.view.ViewCompat
import com.jakewharton.rxbinding3.view.clicks
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.databinding.ViewServersItemRowCountryBinding
import com.wlvpn.consumervpn.databinding.ViewServersItemRowFastestBinding
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposables
import java.util.concurrent.TimeUnit

class ServerFastestViewHolder(
    parent: ViewGroup
) : ServerViewHolder<ServerFastestRow>(
    parent,
    R.layout.view_servers_item_row_fastest
) {

    private var rowClickDisposable = Disposables.disposed()

    override lateinit var item: ServerFastestRow

    override fun clean() {
        rowClickDisposable.dispose()
    }

    override fun bind(item: ServerFastestRow, callback: RowCallback) {
        this.item = item

        val textColor: Int
        val typeFace: Typeface?
        val itemBackground: Drawable?

        if (item.isSelected) {
            itemBackground = ContextCompat.getDrawable(itemView.context, R.drawable.bg_row_servers_selected_ripple)
            textColor = ContextCompat.getColor(itemView.context, R.color.server_location_selected_text_color)
            typeFace = ResourcesCompat.getFont(itemView.context, R.font.roboto_bold)
            ViewCompat.setElevation(
                itemView,
                DEFAULT_ELEVATION
            )
        } else {
            itemBackground = ContextCompat.getDrawable(itemView.context, R.drawable.bg_row_servers_ripple)
            textColor = ContextCompat.getColor(itemView.context, R.color.server_location_text_color)
            typeFace = ResourcesCompat.getFont(itemView.context, R.font.roboto_regular)
            ViewCompat.setElevation(itemView, 0f)
        }

        ViewServersItemRowFastestBinding.bind(itemView).apply {
            fastestTextView.setTextColor(textColor)
            fastestTextView.typeface = typeFace
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