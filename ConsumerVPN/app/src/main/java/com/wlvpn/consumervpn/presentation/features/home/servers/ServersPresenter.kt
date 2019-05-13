package com.wlvpn.consumervpn.presentation.features.home.servers

import com.wlvpn.consumervpn.domain.model.ServerLocation
import com.wlvpn.consumervpn.domain.model.Settings
import com.wlvpn.consumervpn.domain.service.servers.ServersService
import com.wlvpn.consumervpn.domain.service.settings.SettingsService
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import com.wlvpn.consumervpn.presentation.bus.Event
import com.wlvpn.consumervpn.presentation.bus.SinglePipelineBus
import com.wlvpn.consumervpn.presentation.features.home.servers.adapter.*
import com.wlvpn.consumervpn.presentation.util.SchedulerProvider
import com.wlvpn.consumervpn.presentation.util.defaultSchedulers
import com.wlvpn.consumervpn.presentation.util.isRunning
import com.wlvpn.consumervpn.presentation.util.zipPair
import io.reactivex.Completable
import io.reactivex.Single
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposables
import io.reactivex.schedulers.Schedulers
import timber.log.Timber
import java.util.*
import kotlin.collections.ArrayList

class ServersPresenter(
    private val serversService: ServersService,
    private val settingsService: SettingsService,
    private val vpnService: VpnService,
    private val connectionEventBus: SinglePipelineBus<Event.ConnectionRequestEvent>,
    private val schedulerProvider: SchedulerProvider
) : ServersContract.Presenter {

    private val disposables = CompositeDisposable()

    // Save all Items into this options
    private val allItemList = ArrayList<ServerRowItem>()
    private val currentItemList = ArrayList<ServerRowItem>()
    private val countryItemList = ArrayList<ServerCountryRow>()
    private val itemCityMap = HashMap<String, ArrayList<ServerCityRow>>()
    // Sorting list state
    private val serverListState = ServerRowListState()

    private var serversDisposable = Disposables.disposed()
    private var settingsDisposable = Disposables.disposed()

    override var view: ServersContract.View? = null

    override fun start() {
    }

    override fun cleanUp() {
        disposables.clear()

        super.cleanUp()
    }

    override fun onSaveCurrentListState(itemList: List<ServerRowItem>) {
        currentItemList.clear()
        currentItemList.addAll(itemList)
    }

    override fun onMenuCheckedItems() {
        view?.setMenuCheckedItems(serverListState)
    }

    override fun onListLoad(state: ServerRowListType?) {

        if (state == null
            || state == serverListState.currentSortType
        ) {
            // Same state just load the items
            if (serverListState.currentSortType == ServerRowListType.CountryList) {
                onCountriesLoad()
            } else {
                onCitiesLoad()
            }
        } else {
            // Update state
            serverListState.currentSortType = state

            currentItemList.clear()

            if (serverListState.currentSortType == ServerRowListType.CountryList) {
                onCountriesFilterLoad(null)
            } else {
                onCitiesFiltersLoad(null)
            }
        }
    }

    override fun onLoadFilteredList(nameFilter: String?) {
        if (serverListState.currentSortType == ServerRowListType.CountryList) {
            onCountriesFilterLoad(nameFilter)
        } else {
            onCitiesFiltersLoad(nameFilter)
        }
    }

    override fun onCurrentSelectedItemUpdate(currentItem: ServerRowItem) {
        disposables.add(
            vpnService.isVpnConnected()
                .defaultSchedulers(schedulerProvider)
                .subscribe({ isVpnConnected ->
                    connectToSelectedServer(currentItem, isVpnConnected)
                }, { throwable ->
                    Timber.e(throwable)
                })
        )
    }

    override fun onFastestSelectionSave(itemFastest: ServerFastestRow) {
        disposeSettingsDisposables()

        settingsDisposable = settingsService.updateSelectedFastestAvailable()
            .defaultSchedulers(schedulerProvider)
            .subscribe({
                // Since fastest connection could change through time always show the dialog
                view?.showNewConnectionDialog(itemFastest)
            }, { throwable ->
                Timber.e(throwable)
            }).also { disposable ->
                disposables.add(disposable)
            }
    }

    override fun onFastestCountrySelectionSave(itemFastestCountry: ServerCountryRow) {
        disposeSettingsDisposables()

        settingsDisposable = settingsService.updateSelectedLocation(
            ServerLocation(
                null,
                itemFastestCountry.location.country,
                itemFastestCountry.location.countryCode
            )
        )
            .andThen(vpnService.isVpnConnected())
            .defaultSchedulers(schedulerProvider)
            .subscribe({ isVpnConnected ->
                connectToSelectedServer(itemFastestCountry, isVpnConnected)
            }, { throwable ->
                Timber.e(throwable)
            }).also { disposable ->
                disposables.add(disposable)
            }
    }

    override fun onCitySelectionSave(itemCity: ServerCityRow) {
        disposeSettingsDisposables()

        settingsDisposable = settingsService.updateSelectedLocation(
            ServerLocation(
                itemCity.location.city,
                itemCity.location.country,
                itemCity.location.countryCode
            )
        )
            .andThen(vpnService.isVpnConnected())
            .defaultSchedulers(schedulerProvider)
            .subscribe({ isVpnConnected ->
                connectToSelectedServer(itemCity, isVpnConnected)
            }, { throwable ->
                Timber.e(throwable)
            }).also { disposable ->
                disposables.add(disposable)
            }
    }

    override fun onNewConnectionRequest() {
        disposables.add(
            vpnService.disconnect()
                .onErrorComplete()
                .defaultSchedulers(schedulerProvider)
                .subscribe({
                    connectionEventBus.post(Event.ConnectionRequestEvent)
                }, { throwable ->
                    Timber.e(throwable)
                })
        )
    }

    override fun onDisconnectRequest() {
        disposables.add(
            vpnService.disconnect()
                .onErrorComplete()
                .subscribe({}, { throwable ->
                    Timber.e(throwable)
                })
        )
    }


    private fun onCountriesLoad() {
        disposeServerDisposables()

        if (allItemList.size == 0) {

            serversDisposable = settingsService.getConnectionRequestSettings()
                .flatMap { connectionSettings ->
                    Single.just(setupSelectedRow(connectionSettings))
                }.zipPair(serversService.getAllServersLocations().toSingle())
                .flatMap { pair ->
                    setupInitialItemsAndSelected(pair.first, pair.second)
                    // Set current listType to pass
                    setupCountriesList(null)
                    Single.just(pair.first)
                }
                .defaultSchedulers(schedulerProvider)
                .subscribe({ selectedItem ->
                    view?.setServersData(
                        currentItemList,
                        allItemList,
                        itemCityMap,
                        serverListState,
                        selectedItem,
                        true
                    )
                }, { throwable ->
                    Timber.e(throwable)
                }).also { disposable ->
                    disposables.add(disposable)
                }

        } else {
            serversDisposable = settingsService.getConnectionRequestSettings()
                .defaultSchedulers(schedulerProvider)
                .subscribe({ connectionSettings ->
                    val selectedItem = setupSelectedRow(connectionSettings)
                    view?.setServersData(
                        currentItemList,
                        allItemList,
                        itemCityMap,
                        serverListState,
                        selectedItem,
                        true
                    )
                }, { throwable ->
                    Timber.e(throwable)
                }).also { disposable ->
                    disposables.add(disposable)
                }
        }
    }

    private fun onCitiesLoad() {
        serversDisposable = settingsService.getConnectionRequestSettings()
            .defaultSchedulers(schedulerProvider)
            .subscribe({ connectionSettings ->
                val selectedItem = setupSelectedRow(connectionSettings)
                view?.setServersData(
                    currentItemList,
                    allItemList,
                    itemCityMap,
                    serverListState,
                    selectedItem,
                    false
                )
            }, { throwable ->
                Timber.e(throwable)
            }).also { disposable ->
                disposables.add(disposable)
            }
    }

    private fun onCountriesFilterLoad(countryNameFilter: String?) {
        val filter = countryNameFilter?.trim()

        disposeServerDisposables()

        serversDisposable = Completable.defer {
            setupCountriesList(filter)
            Completable.complete()
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                view?.setServersData(
                    currentItemList,
                    allItemList,
                    itemCityMap,
                    serverListState,
                    null,
                    false
                )
            }, { throwable ->
                Timber.e(throwable)
            }).also { disposable ->
                disposables.add(disposable)
            }
    }

    private fun onCitiesFiltersLoad(cityFilterName: String?) {
        val filter = cityFilterName?.trim()

        disposeServerDisposables()

        serversDisposable = Completable.defer {
            setupCitiesList(filter)
            Completable.complete()
        }
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({
                view?.setServersData(currentItemList, allItemList, itemCityMap, serverListState, null, false)
            }, { throwable ->
                Timber.e(throwable)
            }).also { disposable ->
                disposables.add(disposable)
            }
    }

    private fun setupSelectedRow(connectionSettings: Settings.ConnectionRequest): ServerRowItem {
        var selectedItem: ServerRowItem =
            ServerFastestRow(
                isSelected = true,
                isExpanded = false
            )

        when (connectionSettings.connectionOption) {

            Settings.ConnectionRequest.ConnectOption.FASTEST_SERVER -> { /* Do nothing is already been setup */
            }

            Settings.ConnectionRequest.ConnectOption.FASTEST_IN_LOCATION -> {
                connectionSettings.location?.let { location ->

                    selectedItem = location.city?.let {
                        ServerCityRow(
                            true, false,
                            connectionSettings.location as ServerLocation
                        )
                    } ?: run {
                        ServerCountryRow(
                            true, false,
                            0, connectionSettings.location as ServerLocation
                        )
                    }
                }
            }

            else -> throw NotImplementedError()
        }
        return selectedItem
    }

    private fun setupInitialItemsAndSelected(
        itemSelected: ServerRowItem,
        serverList: List<ServerLocation>
    ) {
        var isSelectedPicked = false

        if (itemSelected is ServerFastestRow) {
            isSelectedPicked = true
            allItemList.add(itemSelected)
        } else {
            val fastest = ServerFastestRow(
                isSelected = false,
                isExpanded = false
            )
            allItemList.add(fastest)
        }

        var currentCountryCode = ""

        for (server in serverList) {

            if (!itemCityMap.containsKey(server.countryCode)) {

                // Save number of cities on previous country
                if (countryItemList.size > 0) {
                    countryItemList[countryItemList.size - 1].cityCount =
                        itemCityMap[currentCountryCode]?.size!!
                }

                itemCityMap[server.countryCode] = ArrayList()

                val countryItem = ServerCountryRow(
                    false,
                    false,
                    0,
                    ServerLocation(
                        null,
                        server.country,
                        server.countryCode
                    )
                )

                currentCountryCode = countryItem.location.countryCode

                if (!isSelectedPicked
                    && itemSelected is ServerCountryRow
                    && server.countryCode == itemSelected.location.countryCode
                ) {
                    isSelectedPicked = true
                    countryItem.isSelected = true
                }

                // Store country item
                countryItemList.add(countryItem)
                allItemList.add(countryItem)
            }

            // Save city in city map and all item listType
            val cityItem = ServerCityRow(
                false,
                false,
                ServerLocation(
                    server.city,
                    server.country,
                    server.countryCode
                )
            )

            if (!isSelectedPicked
                && itemSelected is ServerCityRow
                && server.countryCode == itemSelected.location.countryCode
                && server.city == itemSelected.location.city
            ) {
                isSelectedPicked = true
                cityItem.isSelected = true
            }

            // Store city item
            allItemList.add(cityItem)
            itemCityMap[server.countryCode]?.add(
                cityItem
            )
        }

        // Add city count to last item in country listType
        countryItemList[countryItemList.size - 1].cityCount =
            itemCityMap[currentCountryCode]?.size!!
    }

    private fun setupCountriesList(countryNameFilter: String?) {
        currentItemList.clear()
        if (countryNameFilter.isNullOrBlank()) {
            // important to clear status
            for (item in countryItemList) {
                item.isExpanded = false
            }

            currentItemList.add(allItemList[0]) // Fastest Available
            currentItemList.addAll(countryItemList)
        } else {

            val newList = ArrayList<ServerCountryRow>()

            for (item in countryItemList) {
                // important to clear status
                item.isExpanded = false

                if (item.location.country.contains(countryNameFilter, true)) {
                    newList.add(item)
                }
            }

            // Will show first results that starts with the search term
            val comparator = Comparator<ServerCountryRow> { first, second ->
                val firstStarts = first.location.country.startsWith(countryNameFilter, true)
                val secondStarts = second.location.country.startsWith(countryNameFilter, true)
                if (firstStarts && !secondStarts) {
                    -1
                } else if (secondStarts && !firstStarts) {
                    1
                } else {
                    0
                }
            }
            Collections.sort(newList, comparator)
            currentItemList.addAll(newList)
        }
    }

    private fun setupCitiesList(cityFilterName: String?) {
        currentItemList.clear()

        if (cityFilterName.isNullOrBlank()) {
            currentItemList.add(allItemList[0]) // Fastest Available

            for (item in countryItemList) {
                currentItemList.addAll(itemCityMap.getValue(item.location.countryCode))
            }

        } else {
            val newList = ArrayList<ServerCityRow>()

            for (item in allItemList) {
                if (item is ServerCityRow
                    && item.location.city!!.contains(cityFilterName, true)
                ) {
                    newList.add(item)
                }
            }

            // Will show first results that starts with the search term
            val comparator = Comparator<ServerCityRow> { first, second ->
                val firstStarts = first.location.city!!.startsWith(cityFilterName, true)
                val secondStarts = second.location.city!!.startsWith(cityFilterName, true)
                if (firstStarts && !secondStarts) {
                    -1
                } else if (secondStarts && !firstStarts) {
                    1
                } else {
                    0
                }
            }
            Collections.sort(newList, comparator)
            currentItemList.addAll(newList)
        }
    }

    private fun connectToSelectedServer(
        selectedServer: ServerRowItem,
        isVpnConnected: Boolean
    ) {
        if (!isVpnConnected
        ) {
            // If is not connected always show connection dialog
            view?.showNewConnectionDialog(selectedServer)
        } else {
            disposables.add(
                vpnService.getConnectedServer()
                    .flatMap { connectedServer ->
                        // Determine if we must show the connect dialog or the disconnection one
                        // By default we show the disconnect dialog
                        var shouldConnectToVpn = false
                        when (selectedServer) {
                            // Since fastest connection could change through time always show the dialog
                            is ServerFastestRow -> {
                                shouldConnectToVpn = true
                            }

                            is ServerCountryRow -> {
                                // when the connection location country is different show the connect dialog
                                if (selectedServer.location.countryCode != connectedServer.location.countryCode) {
                                    shouldConnectToVpn = true
                                }
                            }

                            is ServerCityRow -> {
                                // when the connection location country and the city is different
                                // show the connect dialog
                                if (selectedServer.location.countryCode != connectedServer.location.countryCode
                                    || selectedServer.location.city != connectedServer.location.city
                                ) {
                                    shouldConnectToVpn = true
                                }
                            }
                        }
                        Single.just(shouldConnectToVpn)
                    }
                    .defaultSchedulers(schedulerProvider)
                    .subscribe({ shouldConnectToVpn ->
                        if (shouldConnectToVpn) {
                            view?.showNewConnectionDialog(selectedServer)
                        } else {
                            val location: ServerLocation = when (selectedServer) {
                                is ServerCountryRow -> selectedServer.location

                                is ServerCityRow -> selectedServer.location

                                else -> throw NotImplementedError()
                            }
                            view?.showDisconnectDialog(location)
                        }
                    }, { throwable ->
                        Timber.e(throwable)
                    })
            )
        }
    }


    private fun disposeSettingsDisposables() {
        if (settingsDisposable.isRunning()) {
            settingsDisposable.dispose()
        }
    }

    private fun disposeServerDisposables() {
        if (serversDisposable.isRunning()) {
            serversDisposable.dispose()
        }
    }

}