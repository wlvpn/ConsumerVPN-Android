package com.wlvpn.consumervpn.data.job

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.data.exception.NetworkNotAvailableException
import com.wlvpn.consumervpn.data.gateway.authorization.exception.RefreshTokenReLoginException
import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.domain.service.authorization.UserAuthorizationService
import com.wlvpn.consumervpn.domain.service.authorization.exception.UserNotAuthorizedException
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import timber.log.Timber

/**
 * Job implementation for refreshing the session token
 */
class TokenRefreshJob internal constructor(
    private val userAuthenticationService: UserAuthenticationService,
    private val userAuthorizationService: UserAuthorizationService,
    private val vpnService: VpnService
) : Job() {

    companion object {

        internal const val JOB_ID = "consumer:TokenRefreshJob"

        fun schedule() {
            //For debug builds use reduced timers and more accepting connection listType
            val start =
                if (BuildConfig.DEBUG) FIFTEEN_MINUTES_IN_MILLISECONDS
                else TWENTY_FOUR_HOURS_IN_MILLISECONDS

            JobRequest.Builder(JOB_ID)
                .setRequiredNetworkType(JobRequest.NetworkType.CONNECTED)
                .setRequirementsEnforced(true)
                .setPeriodic(start)
                .setUpdateCurrent(true)
                .build()
                .schedule()
        }
    }

    override fun onRunJob(params: Job.Params): Job.Result {
        userAuthorizationService.refreshToken()
            .blockingGet()
            ?.let { throwable ->
                when (throwable) {
                    is UserNotAuthorizedException ->
                        Timber.e(throwable, "Impossible to refresh token. The user is not authorized")

                    is RefreshTokenReLoginException -> {
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

                    is NetworkNotAvailableException ->
                        Timber.e(throwable, "Network was not available while refreshing token")

                    else -> Timber.e(throwable, "An error occurred while refreshing token")
                }
            }

        return Job.Result.SUCCESS
    }
}
