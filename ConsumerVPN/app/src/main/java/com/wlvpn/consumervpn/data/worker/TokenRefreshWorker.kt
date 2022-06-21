package com.wlvpn.consumervpn.data.worker

import android.content.Context
import androidx.work.*
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.data.failure.NetworkNotAvailableFailure
import com.wlvpn.consumervpn.data.gateway.authorization.failure.RefreshTokenReLoginFailure
import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.domain.service.authorization.UserAuthorizationService
import com.wlvpn.consumervpn.domain.service.authorization.failure.UserNotAuthorizedFailure
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import com.wlvpn.consumervpn.presentation.di.Injector
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val FIFTEEN_MINUTES_IN_MILLISECONDS = 900000L
private const val TWENTY_FOUR_HOURS_IN_MILLISECONDS = 86400000L

/**
 * Worker implementation for refreshing the session token
 */
class TokenRefreshWorker (
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    @Inject
    lateinit var userAuthenticationService: UserAuthenticationService

    @Inject
    lateinit var userAuthorizationService: UserAuthorizationService

    @Inject
    lateinit var vpnService: VpnService

    companion object {

        internal const val WORKER_ID = "consumer:TokenRefreshWorker"

        fun schedule(workManager: WorkManager) {
            //For debug builds use reduced timers and more accepting connection listType
            val start =
                if (BuildConfig.DEBUG) FIFTEEN_MINUTES_IN_MILLISECONDS
                else TWENTY_FOUR_HOURS_IN_MILLISECONDS

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest =
                PeriodicWorkRequestBuilder<TokenRefreshWorker>(start, TimeUnit.MILLISECONDS)
                    .setConstraints(constraints)
                    .addTag(WORKER_ID)
                    .build()

            workManager.enqueueUniquePeriodicWork(
                WORKER_ID,
                ExistingPeriodicWorkPolicy.KEEP,
                workRequest
            )
        }

    }

    override fun doWork(): Result {
        Injector.INSTANCE.applicationComponent?.inject(this)
        userAuthorizationService.refreshToken()
            .blockingGet()
            ?.let { throwable ->
                when (throwable) {
                    is UserNotAuthorizedFailure ->
                        Timber.e(throwable, "Impossible to refresh token. The user is not authorized")

                    is RefreshTokenReLoginFailure -> {
                        Timber.e(throwable, "Re-login failed while refreshing token. Login out user")

                        // Logout clears the background jobs
                        vpnService.disconnect()
                            .onErrorComplete()
                            .andThen(userAuthenticationService.logout())
                            .blockingGet()
                            .let { logoutThrowable ->
                                Timber.e(logoutThrowable, "Error while login out user on token refresh fail")
                            }
                    }

                    is NetworkNotAvailableFailure ->
                        Timber.e(throwable, "Network was not available while refreshing token")

                    else -> Timber.e(throwable, "An error occurred while refreshing token")
                }
            }

        return Result.success()
    }
}