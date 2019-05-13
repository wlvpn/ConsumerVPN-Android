package com.wlvpn.consumervpn.presentation.features.home.servers.adapter

import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.util.autoNotify
import kotlin.properties.Delegates

private enum class ViewType(val value: Int) {
    FastestViewType(0),
    CountryViewType(1),
    CityViewType(2),
    CityCountryViewType(3)
}

/**
 * Adapts any country listType that must expand and show cities
 * Important!!!! Any listType, map or item listType must contain same elements when constructing those listType this
 * to store the same element and use references to update values
 * @param itemList The items to be displayed
 * @param cityItemsMap HashMap helper to get a listType of cities from current country
 * @param onRowChangeListener Use this as a callback to apply operations outside the adapter
 * @param itemSelected Current selection
 * @param expandCurrentSelection If the current selected country should be expanded,
 * @param listType The current type of list to show
 */
class ServerListAdapter(
    // Contains all items whether it appears or not in the current screen
    //  {Any selection update that do not appears in itemList must be applied on this listType}
    itemList: ArrayList<ServerRowItem>,

    private val allItemList: List<ServerRowItem>,

    private val cityItemsMap: Map<String, List<ServerCityRow>>,

    private val onRowChangeListener: OnAdapterRowChanges?,

    itemSelected: ServerRowItem?,

    expandCurrentSelection: Boolean,

    var listType: ServerRowListType

) : RecyclerView.Adapter<ServerViewHolder<*>>(),
    ServerViewHolder.RowCallback {

    //Delegate that auto-notifies when this value changes
    var itemList: List<ServerRowItem>
            by Delegates.observable(emptyList()) { _, oldList, newList ->
                autoNotify(oldList, newList) { old, new ->
                    old.isSelected == new.isSelected
                            || old.isExpanded == new.isExpanded
                }
            }

    init {
        setHasStableIds(true)

        // We need to expand a country when a city was previously selected and expandSelected value is true
        if (expandCurrentSelection
            && itemSelected is ServerCityRow
        ) {
            val countryPosition = getCountryItemPosition(
                itemSelected.location.countryCode,
                itemList
            )

            if (countryPosition > -1) {
                itemList.addAll(
                    countryPosition + 1,
                    cityItemsMap.getValue(itemSelected.location.countryCode)
                )

                itemList[countryPosition].isExpanded = true
            }
        }

        this.itemList = itemList
    }

    override fun getItemViewType(position: Int): Int {

        return when (itemList[position]) {
            is ServerFastestRow -> ViewType.FastestViewType.value

            is ServerCountryRow -> ViewType.CountryViewType.value

            is ServerCityRow -> {
                if (listType == ServerRowListType.CityList) {
                    ViewType.CityCountryViewType.value
                } else {
                    ViewType.CityViewType.value
                }

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ServerViewHolder<*> {
        return when (viewType) {

            ViewType.FastestViewType.value -> ServerFastestViewHolder(parent)

            ViewType.CountryViewType.value -> ServerCountryViewHolder(parent)

            ViewType.CityViewType.value -> ServerCityTextViewHolder(parent)

            ViewType.CityCountryViewType.value -> ServerCityCountryViewHolder(parent)

            else -> throw NotImplementedError()

        }
    }

    override fun onBindViewHolder(holder: ServerViewHolder<*>, position: Int) {
        when (holder) {
            is ServerFastestViewHolder -> {
                holder.bind(
                    item = itemList[position] as ServerFastestRow,
                    callback = this
                )
            }

            is ServerCountryViewHolder -> {
                holder.bind(
                    item = itemList[position] as ServerCountryRow,
                    callback = this
                )
            }

            is ServerCityTextViewHolder -> {
                holder.bind(
                    item = itemList[position] as ServerCityRow,
                    callback = this
                )
            }

            is ServerCityCountryViewHolder -> {
                holder.bind(
                    item = itemList[position] as ServerCityRow,
                    callback = this
                )
            }
        }
    }

    override fun getItemCount(): Int {
        return itemList.size
    }

    override fun onViewRecycled(holder: ServerViewHolder<*>) {
        // Always Clean any internal references from holder
        holder.clean()
        super.onViewRecycled(holder)
    }

    //We need this in order to notifyX to show the right animations
    override fun getItemId(position: Int): Long =
        itemList[position].id.toLong()

    override fun rowClick(view: View, position: Int, item: ServerRowItem) {
        // Any is Selected change will be reflected by delegate

        when (item) {
            is ServerFastestRow -> updateListSelections(item, view, position)

            is ServerCountryRow -> {
                // Select between row rowClick and expand button rowClick
                when (view.id) {

                    R.id.countryRow -> {
                        updateListSelections(item, view, position)
                    }

                    // Do not send data on callback for country expand
                    R.id.citiesButton -> {
                        onCountryExpandClick(item, view, position)
                    }
                }
            }

            is ServerCityRow -> updateListSelections(item, view, position)
        }
    }

    private fun onCountryExpandClick(rowItem: ServerCountryRow, view: View, countryPosition: Int) {
        rowItem.isExpanded = !rowItem.isExpanded

        val newList = ArrayList(itemList)

        val isLastItem: Boolean

        if (rowItem.isExpanded) {

            isLastItem = (countryPosition == itemList.size - 1)

            newList.addAll(
                countryPosition + 1,
                cityItemsMap.getValue(rowItem.location.countryCode)
            )

        } else {
            for (location in cityItemsMap.getValue(rowItem.location.countryCode)) {
                newList.removeAt(countryPosition + 1)
            }

            // Always false on removing elements
            isLastItem = false
        }

        this.itemList = newList

        notifyDataSetChanged()

        if (isLastItem) {
            onRowChangeListener?.onLastItemExpanded(countryPosition)
        }

        onRowChangeListener?.onRowChange(rowItem, view)
    }

    private fun getCountryItemPosition(countryCode: String, list: List<ServerRowItem>): Int {
        for ((position, item) in list.withIndex()) {
            if (item is ServerCountryRow
                && item.location.countryCode == countryCode
            ) {
                return position
            }
        }

        return -1
    }

    private fun updateAndNotifyCurrentSelected() {
        for ((position, item) in itemList.withIndex()) {
            if (item.isSelected) {
                item.isSelected = false
                notifyItemChanged(position)
                return
            }
        }

        // If the item is not present update the state on the general listType
        for (item in allItemList) {
            if (item.isSelected) {
                item.isSelected = false
                return
            }
        }
    }

    private fun updateListSelections(item: ServerRowItem, view: View, itemPosition: Int) {
        // If the item was previously selected do nothing
        if (!item.isSelected) {

            updateAndNotifyCurrentSelected()

            item.isSelected = true

            notifyItemChanged(itemPosition)

            onRowChangeListener?.onRowChange(item, view)
        } else {
            // Otherwise report that the current selected item was clicked again
            onRowChangeListener?.onCurrentSelectedItemClick(item)
        }
    }


    interface OnAdapterRowChanges {

        fun onRowChange(item: ServerRowItem, view: View)

        fun onLastItemExpanded(itemPosition: Int)

        fun onCurrentSelectedItemClick(currentItem: ServerRowItem)

    }
}