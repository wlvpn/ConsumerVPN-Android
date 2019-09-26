package com.wlvpn.consumervpn.presentation.features.home.servers

import com.wlvpn.consumervpn.domain.model.ServerLocation
import com.wlvpn.consumervpn.presentation.features.BaseContract
import com.wlvpn.consumervpn.presentation.features.home.servers.adapter.*

class ServersContract {

    interface Presenter : BaseContract.Presenter<View> {

        fun onSaveCurrentListState(itemList: List<ServerRowItem>)

        fun onMenuCheckedItems()

        fun onListLoad(state: ServerRowListType?)

        fun onLoadFilteredList(nameFilter: String?)

        fun onCurrentSelectedItemUpdate(currentItem: ServerRowItem)

        fun onFastestSelectionSave(itemFastest: ServerFastestRow)

        fun onFastestCountrySelectionSave(itemFastestCountry: ServerCountryRow)

        fun onCitySelectionSave(itemCity: ServerCityRow)

        fun onNewConnectionRequest()

        fun onDisconnectRequest()

    }

    interface View : BaseContract.View {

        fun showNewConnectionDialog(server: ServerRowItem)

        fun showDisconnectDialog(location: ServerLocation)

        fun setMenuCheckedItems(serverListState: ServerRowListState)

        fun setServersData(
            currentItemList: ArrayList<ServerRowItem>,
            allItemList: ArrayList<ServerRowItem>,
            cityItemsMap: Map<String, List<ServerCityRow>>,
            serverListState: ServerRowListState,
            itemSelected: ServerRowItem?,
            expandCurrentSelection: Boolean
        )

        fun toolbarVisibility(isVisible: Boolean)

    }
}