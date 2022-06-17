package com.wlvpn.consumervpn.data.worker

import android.content.Context
import androidx.work.*
import com.wlvpn.consumervpn.BuildConfig
import com.wlvpn.consumervpn.domain.service.servers.ServersService
import com.wlvpn.consumervpn.presentation.di.Injector
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

private const val FIFTEEN_MINUTES_IN_MILLISECONDS = 900000L
private const val TWO_HOURS_IN_MILLISECONDS = 7200000L

/**
 * Worker implementation for refreshing the servers list
 */
class ServersRefreshWorker (
    context: Context,
    workerParams: WorkerParameters
) : Worker(context, workerParams) {

    @Inject
    lateinit var serversService: ServersService

    companion object {

        internal const val WORKER_ID = "consumer:SeversRefreshWorker"

        fun schedule(workManager: WorkManager) {
            //For debug builds use reduced timers and more accepting connection listType
            val start =
                if (BuildConfig.DEBUG) FIFTEEN_MINUTES_IN_MILLISECONDS
                else TWO_HOURS_IN_MILLISECONDS

            val constraints = Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .build()

            val workRequest =
                PeriodicWorkRequestBuilder<ServersRefreshWorker>(start, TimeUnit.MILLISECONDS)
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
        serversService.refreshServers()
            .blockingGet()
            ?.let { throwable ->
                Timber.e(throwable, "Error while refreshing current servers in servers refresh job")
            }

        return Result.success()
    }

}
