package com.wlvpn.consumervpn.domain.model

data class SystemInformation(
    var appVersionName: String = "",
    var model: String = "",
    var manufacturer: String = "",
    var osVersion: String = "",
    var sdkLevel: String = "",
    var board: String = "",
    var brand: String = ""
)