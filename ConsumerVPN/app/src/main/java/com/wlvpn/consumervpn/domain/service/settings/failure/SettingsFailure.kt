package com.wlvpn.consumervpn.domain.service.settings.failure

import com.wlvpn.consumervpn.domain.failure.DomainFailure

/**
 * An specialization of [DomainFailure], if we want more specific naming, extend this class
 */
open class SettingsFailure(message: String) : DomainFailure(message)