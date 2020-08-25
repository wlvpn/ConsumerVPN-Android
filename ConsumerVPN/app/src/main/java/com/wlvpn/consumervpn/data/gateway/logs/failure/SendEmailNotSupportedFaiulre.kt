package com.wlvpn.consumervpn.data.gateway.logs.failure

import com.wlvpn.consumervpn.data.failure.DataFailure

class SendEmailNotSupportedFailure(message: String = "Send email is not supported") :
    DataFailure(message)