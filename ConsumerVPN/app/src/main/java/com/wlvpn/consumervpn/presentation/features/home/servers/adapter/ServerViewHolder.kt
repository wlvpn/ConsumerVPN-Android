package com.wlvpn.consumervpn.presentation.features.home.servers.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.LayoutRes
import androidx.recyclerview.widget.RecyclerView

internal const val CLICK_DELAY_MILLISECONDS = 500L
internal const val DEFAULT_ELEVATION = 8f

abstract class ServerViewHolder<T>(

    parent: ViewGroup,

    @LayoutRes layoutId: Int

) : RecyclerView.ViewHolder(

    LayoutInflater.from(parent.context)
        .inflate(layoutId, parent, false)

) {

    abstract var item: T

    /**
     * Use this to clean any object from memory. Is recommended to use this on onViewRecycled
     */
    abstract fun clean()

    abstract fun bind(item: T, callback: RowCallback)

    interface RowCallback {

        fun rowClick(view: View, position: Int, item: ServerRowItem)

    }

}