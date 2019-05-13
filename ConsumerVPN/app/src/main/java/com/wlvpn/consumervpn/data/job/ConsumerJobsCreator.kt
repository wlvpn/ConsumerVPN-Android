package com.wlvpn.consumervpn.data.job

import com.evernote.android.job.Job
import com.evernote.android.job.JobCreator
import com.wlvpn.consumervpn.domain.service.authentication.UserAuthenticationService
import com.wlvpn.consumervpn.domain.service.authorization.UserAuthorizationService
import com.wlvpn.consumervpn.domain.service.servers.ServersService
import com.wlvpn.consumervpn.domain.service.vpn.VpnService
import javax.inject.Inject

const val FIFTEEN_MINUTES_IN_MILLISECONDS = 900000L
const val TWO_HOURS_IN_MILLISECONDS = 7200000L
const val TWENTY_FOUR_HOURS_IN_MILLISECONDS = 86400000L

class ConsumerJobsCreator @Inject constructor(
    private val userAuthenticationService: UserAuthenticationService,
    private val userAuthorizationService: UserAuthorizationService,
    private val serversService: ServersService,
    private val vpnService: VpnService
) : JobCreator {

    override fun create(tag: String): Job? {
        return when (tag) {

            TokenRefreshJob.JOB_ID ->
                TokenRefreshJob(userAuthenticationService, userAuthorizationService, vpnService)

            ServersRefreshJob.JOB_ID ->
                ServersRefreshJob(serversService)

            else ->
                null
        }
    }

}
