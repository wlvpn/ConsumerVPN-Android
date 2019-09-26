package com.wlvpn.consumervpn.presentation.owner.presenter

/**
 * A contract that defines functionality of classes holding a presenter.
 */
interface PresenterOwner {

    /**
     * Tells to start the binding between presenter owner and presenter
     */
    fun bindPresenter()

    fun isPresenterInitialized(): Boolean
}