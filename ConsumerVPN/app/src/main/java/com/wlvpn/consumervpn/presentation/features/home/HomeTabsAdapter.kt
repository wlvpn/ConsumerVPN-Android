package com.wlvpn.consumervpn.presentation.features.home

import android.content.Context
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentPagerAdapter
import com.wlvpn.consumervpn.R
import com.wlvpn.consumervpn.presentation.features.home.connection.ConnectionFragment
import com.wlvpn.consumervpn.presentation.features.home.servers.ServersFragment

private const val TAB_COUNT = 2

class HomeTabsAdapter constructor(
    fragmentManager: FragmentManager,
    private val context: Context
) : FragmentPagerAdapter(fragmentManager) {

    private lateinit var connectionFragment: ConnectionFragment
    private lateinit var serversFragment: ServersFragment

    override fun getCount(): Int = TAB_COUNT

    override fun getItem(position: Int): Fragment {
        return when (position) {

            0 -> {
                // There should not be a problem on using this approach,
                // FragmentPagerAdapter will always keep the state
                if (!::connectionFragment.isInitialized) {
                    connectionFragment = ConnectionFragment()
                }
                connectionFragment
            }

            1 -> {
                if (!::serversFragment.isInitialized) {
                    serversFragment = ServersFragment()
                }

                serversFragment
            }

            else -> throw UnsupportedOperationException()

        }
    }

    override fun getPageTitle(position: Int): CharSequence? {
        return when (position) {
            0 -> context.getString(R.string.home_tab_tittle_connection)

            1 -> context.getString(R.string.home_tab_tittle_servers)

            else -> throw UnsupportedOperationException()
        }
    }

}