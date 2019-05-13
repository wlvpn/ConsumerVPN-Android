package com.wlvpn.consumervpn.presentation.features.home

import com.wlvpn.consumervpn.presentation.features.BaseContract
import com.wlvpn.consumervpn.presentation.owner.presenter.PresenterOwnerFragment

abstract class HomeViewPagerFragment<P : BaseContract.Presenter<*>> : PresenterOwnerFragment<P>() {

    open fun onPageSelected() {}

}