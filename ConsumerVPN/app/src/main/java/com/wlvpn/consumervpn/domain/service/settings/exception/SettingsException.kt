package com.wlvpn.consumervpn.domain.service.settings.exception

import com.wlvpn.consumervpn.domain.exception.DomainException

/**
 * An specialization of [DomainException], if we want more specific naming, extend this class
 */
open class SettingsException(message: String) : DomainException(message)