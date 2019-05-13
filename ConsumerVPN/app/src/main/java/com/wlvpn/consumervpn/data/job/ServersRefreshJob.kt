package com.wlvpn.consumervpn.data.job

import com.evernote.android.job.Job
import com.evernote.android.job.JobRequest
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.domain.service.servers.ServersService
import timber.log.Timber

class ServersRefreshJob internal constructor(
    private val serversService: ServersService
) : Job() {

    companion object {

        internal const val JOB_ID = "consumer:SeversRefreshJob"

        fun schedule() {
            //For debug builds use reduced timers and more accepting connection listType
            val start =
                if (BuildConfig.DEBUG) FIFTEEN_MINUTES_IN_MILLISECONDS
                else TWO_HOURS_IN_MILLISECONDS

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

        serversService.refreshServers()
            .blockingGet()
            ?.let { throwable ->
                Timber.e(throwable, "Error while refreshing current servers in servers refresh job")
            }

        return Job.Result.SUCCESS
    }
}
