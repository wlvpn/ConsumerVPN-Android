package com.wlvpn.consumervpn.presentation.features.home.servers

import android.content.DialogInterface
import android.os.Bundle
import android.os.Parcelable
import android.view.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import com.jakewharton.rxbinding3.appcompat.queryTextChanges
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.domain.model.ServerLocation
import com.wlvpn.consumervpn.presentation.di.Injector
import com.wlvpn.consumervpn.presentation.features.connection.DisconnectDialogFragment
import com.wlvpn.consumervpn.presentation.features.connection.NewConnectionDialogFragment
import com.wlvpn.consumervpn.presentation.features.connection.OnConnectionDialogResult
import com.wlvpn.consumervpn.presentation.features.home.HomeViewPagerFragment
import com.wlvpn.consumervpn.presentation.features.home.servers.adapter.*
import com.wlvpn.consumervpn.presentation.navigation.FeatureNavigator
import com.wlvpn.consumervpn.presentation.util.isVisible
import io.reactivex.disposables.CompositeDisposable
import kotlinx.android.synthetic.main.fragment_servers_expandable.*
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val TEXT_QUERY_DEBOUNCE_MILLISECONDS = 400L

private const val RECYCLER_STATE_KEY = "RECYCLER_STATE_KEY"

class ServersFragment :
    HomeViewPagerFragment<ServersContract.Presenter>(),
    ServersContract.View,
    ServerListAdapter.OnAdapterRowChanges,
    OnConnectionDialogResult {

    @Inject
    lateinit var featureNavigator: FeatureNavigator

    private lateinit var adapter: ServerListAdapter
    private lateinit var searchView: SearchView
    private lateinit var countrySortItem: MenuItem
    private lateinit var citySortItem: MenuItem
    private var recyclerState: Parcelable? = null
    private val viewDisposables = CompositeDisposable()

    override fun bindPresenter() {
        presenter.bind(this)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
        Injector.INSTANCE.initViewComponent(activity as AppCompatActivity).inject(this)
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_servers_expandable, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        progressBar.visibility = View.VISIBLE

        presenter.onListLoad(null)

        // Thanks to this we can handle on back pressing from fragment
        setupBackButtonHandling()
    }

    override fun onViewStateRestored(savedInstanceState: Bundle?) {
        savedInstanceState?.let { bundle ->
            recyclerState = bundle.getParcelable(RECYCLER_STATE_KEY)
        }

        super.onViewStateRestored(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        // Save current recycler position in state
        recyclerView.layoutManager?.let { state ->
            outState.putParcelable(RECYCLER_STATE_KEY, state.onSaveInstanceState())
        }

        super.onSaveInstanceState(outState)
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.menu_home_servers, menu)

        searchView = menu.findItem(R.id.item_search).actionView as SearchView
        countrySortItem = menu.findItem(R.id.action_sort_by_country)
        citySortItem = menu.findItem(R.id.action_sort_by_city)

        presenter.onMenuCheckedItems()

        viewDisposables.add(
            searchView.queryTextChanges()
                .skipInitialValue()
                .debounce(TEXT_QUERY_DEBOUNCE_MILLISECONDS, TimeUnit.MILLISECONDS)
                .subscribe({ text ->
                    val searchText = text.toString()

                    presenter.onLoadFilteredList(searchText)

                }, { throwable ->
                    Timber.e(throwable)
                })
        )

        searchView.setOnCloseListener {
            presenter.onLoadFilteredList(null)
            false
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {

            R.id.action_sort_by_country -> {
                if (!countrySortItem.isChecked) {
                    citySortItem.isChecked = false
                    countrySortItem.isChecked = true
                    presenter.onListLoad(ServerRowListType.CountryList)
                }
            }

            R.id.action_sort_by_city -> {
                if (!citySortItem.isChecked) {
                    countrySortItem.isChecked = false
                    citySortItem.isChecked = true
                    presenter.onListLoad(ServerRowListType.CityList)
                }
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onPause() {
        super.onPause()

        // On Fragments sometimes the view state remains so always make sure to save recycler state in case
        // The state never enters on restore state
        recyclerView.layoutManager?.let { state ->
            recyclerState = state.onSaveInstanceState()
        }
    }

    override fun showNewConnectionDialog(server: ServerRowItem) {
        activity?.let { fragmentActivity ->
            val dialogFragment: NewConnectionDialogFragment =
                when (server) {
                    is ServerFastestRow -> NewConnectionDialogFragment.newInstance()

                    is ServerCountryRow ->
                        NewConnectionDialogFragment.newInstance(
                            null,
                            server.location.country
                        )

                    is ServerCityRow -> NewConnectionDialogFragment.newInstance(
                        server.location.city,
                        server.location.country
                    )
                }


            dialogFragment.onResultCallback = this
            dialogFragment.show(
                fragmentActivity.supportFragmentManager,
                NewConnectionDialogFragment.TAG
            )
        }
    }

    override fun showDisconnectDialog(location: ServerLocation) {
        activity?.let { fragmentActivity ->

            val dialogFragment = DisconnectDialogFragment
                .newInstance(location.city, location.country)

            dialogFragment.onResultCallback = this
            dialogFragment.show(
                fragmentActivity.supportFragmentManager,
                DisconnectDialogFragment.TAG
            )
        }
    }

    override fun onConnectionDialogResponse(resultCode: Int, dialogTag: String) {
        when (dialogTag) {
            NewConnectionDialogFragment.TAG -> {
                when (resultCode) {
                    DialogInterface.BUTTON_POSITIVE -> {
                        presenter.onNewConnectionRequest()
                        featureNavigator.navigateToConnectView()
                    }

                    DialogInterface.BUTTON_NEGATIVE -> return
                }
            }

            DisconnectDialogFragment.TAG -> {
                when (resultCode) {
                    DialogInterface.BUTTON_POSITIVE -> presenter.onDisconnectRequest()

                    DialogInterface.BUTTON_NEGATIVE -> return
                }
            }
        }
    }

    override fun setMenuCheckedItems(serverListState: ServerRowListState) {
        // This will restore the state with the current one on the presenter
        if (serverListState.currentSortType == ServerRowListType.CountryList) {
            citySortItem.isChecked = false
            countrySortItem.isChecked = true
        } else {
            countrySortItem.isChecked = false
            citySortItem.isChecked = true
        }
    }

    override fun setServersData(
        currentItemList: ArrayList<ServerRowItem>,
        allItemList: ArrayList<ServerRowItem>,
        cityItemsMap: Map<String, List<ServerCityRow>>,
        serverListState: ServerRowListState,
        itemSelected: ServerRowItem?,
        expandCurrentSelection: Boolean
    ) {
        if (!::adapter.isInitialized) {

            adapter = ServerListAdapter(
                currentItemList,
                allItemList,
                cityItemsMap,
                this,
                itemSelected,
                expandCurrentSelection,
                serverListState.currentSortType
            )

            recyclerView.adapter = adapter

            // If there's a previous state  restore it
            recyclerState?.let { state ->
                recyclerView.layoutManager?.onRestoreInstanceState(state)
            }
        } else {
            // always pass the current type list
            adapter.listType = serverListState.currentSortType
            adapter.itemList = currentItemList
            adapter.notifyDataSetChanged()
        }

        progressBar.visibility = View.GONE
    }

    override fun onRowChange(item: ServerRowItem, view: View) {
        when (item) {
            is ServerFastestRow -> presenter.onFastestSelectionSave(item)

            is ServerCountryRow -> {
                if (view.id != R.id.citiesButton) {
                    presenter.onFastestCountrySelectionSave(item)
                }
            }

            is ServerCityRow -> presenter.onCitySelectionSave(item)
        }

        presenter.onSaveCurrentListState(ArrayList(adapter.itemList))
    }

    override fun onLastItemExpanded(itemPosition: Int) {
        recyclerView.scrollToPosition(itemPosition + 1)
    }

    override fun onCurrentSelectedItemClick(currentItem: ServerRowItem) {
        presenter.onCurrentSelectedItemUpdate(currentItem)
    }

    override fun onDestroy() {
        viewDisposables.clear()
        super.onDestroy()
    }

    private fun setupBackButtonHandling() {
        view?.isFocusableInTouchMode = true
        view?.requestFocus()
        view?.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
                if (view?.isVisible() == true) {
                    event?.action?.let {
                        if (keyCode == KeyEvent.KEYCODE_BACK && it == KeyEvent.ACTION_UP) {
                            featureNavigator.navigateToConnectView()
                            return true
                        }
                    }
                }
                return false
            }
        })
    }

}