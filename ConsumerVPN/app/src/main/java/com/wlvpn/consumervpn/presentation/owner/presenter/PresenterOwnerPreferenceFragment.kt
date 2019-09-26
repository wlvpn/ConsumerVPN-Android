package com.wlvpn.consumervpn.presentation.owner.presenter

import androidx.preference.PreferenceFragmentCompat
import com.wlvpn.consumervpn.presentation.features.BaseContract
import javax.inject.Inject

/**
 * This base class is used to manage presenter lifecycle.
 * Please do NOT put another logic other than presenter management.
 * If need, create a subclass of this one to add specific logic.
 *
 * If you don't want to use this use this super class, please remember to manually call presenter lifecycle methods.
 */

//TODO We need a way to simplify this class to not depend on parents like [Fragment] or [Activity]
abstract class PresenterOwnerPreferenceFragment<P : BaseContract.Presenter<*>> : PreferenceFragmentCompat(),
    PresenterOwner {

    @Inject
    open lateinit var presenter: P

    override fun onResume() {
        super.onResume()
        bindPresenter()
        if (!::presenter.isInitialized) {
            throw PresenterNotInitializedException()
        }
        presenter.start()
    }

    override fun onPause() {
        presenter.unbind()
        super.onPause()
    }

    override fun onDestroy() {
        if (activity?.isFinishing!!) {
            presenter.cleanUp()
        }
        super.onDestroy()
    }

    override fun isPresenterInitialized(): Boolean {
        return ::presenter.isInitialized
    }

    class PresenterNotInitializedException
        (message: String = "Presenter needs to be initialized with bindPresenter()") : RuntimeException(message)
}