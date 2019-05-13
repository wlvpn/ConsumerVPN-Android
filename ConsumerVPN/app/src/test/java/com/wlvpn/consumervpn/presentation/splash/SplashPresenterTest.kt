package com.wlvpn.consumervpn.presentation.splash

import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.presentation.features.splash.SplashContract
import com.wlvpn.consumervpn.presentation.features.splash.SplashPresenter
import com.wlvpn.consumervpn.presentation.util.StartupStatus
import com.wlvpn.consumervpn.util.TestSchedulerProvider
import io.reactivex.Single
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.Mockito.verify
import org.mockito.junit.MockitoJUnitRunner

@RunWith(MockitoJUnitRunner::class)
class SplashPresenterTest {

    @Mock
    lateinit var userAuthenticationService: UserAuthenticationService

    @Mock
    lateinit var view: SplashContract.View

    @Mock
    lateinit var startupStatus: StartupStatus

    private lateinit var sut: SplashContract.Presenter

    private val testSchedulerProvider = TestSchedulerProvider()

    @Before
    fun before() {
        `when`(userAuthenticationService.isAuthenticated()).thenReturn(Single.just(true))

        sut = SplashPresenter(testSchedulerProvider, userAuthenticationService, startupStatus)
        sut.bind(view)
    }

    @Test
    fun start_shouldShowLogin_withUnauthorized_user() {
        `when`(userAuthenticationService.isAuthenticated()).thenReturn(Single.just(false))

        sut.start()

        verify(view).navigateToLogin()
    }

    @Test
    fun start_shouldShowHome_withAuthorized_user() {
        sut.start()

        verify(view).navigateToHome()
    }
}