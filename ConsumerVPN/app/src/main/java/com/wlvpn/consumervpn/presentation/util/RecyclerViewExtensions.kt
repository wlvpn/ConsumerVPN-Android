package com.wlvpn.consumervpn.presentation.util

import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView

/**
 * Extension method that auto notifies the adapter when a list changes. Will select any of
 * notifyItem<removed,changed,moved> depending of the diff in both lists.
 *
 * @param oldList The old list with no changes.
 * @param newList The new list with the latest changes.
 * @param compare A comparator used to check changes in the item.
 */
fun <T> RecyclerView.Adapter<*>.autoNotify(oldList: List<T>, newList: List<T>, compare: (T, T) -> Boolean) {

    DiffUtil.calculateDiff(object : DiffUtil.Callback() {

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            oldList[oldItemPosition] == newList[newItemPosition]

        override fun getOldListSize(): Int = oldList.size

        override fun getNewListSize(): Int = newList.size

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean =
            compare(oldList[oldItemPosition], newList[newItemPosition])

    }).dispatchUpdatesTo(this)

}