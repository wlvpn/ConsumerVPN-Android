package com.wlvpn.consumervpn.data.gateway.logs

import com.wlvpn.consumervpn.domain.gateway.LogsGateway
import io.reactivex.Single
import java.io.BufferedReader
import java.io.FileInputStream

const val LOG_LOCATION = "/log/local_diagnostics.txt"

class LogbackLogsGateway(
    private val localFilesDir: String
) : LogsGateway {

    override fun getStoredLogs(): Single<String> =
        Single.create {
            try {
                it.onSuccess(
                    FileInputStream(localFilesDir + LOG_LOCATION)
                        .bufferedReader()
                        .use(BufferedReader::readText)
                )
            } catch (e: Exception) {
                it.onError(e)
            }
        }

}