package com.beomsic.common.exception

data class ErrorResponse (
    var code: Int,
    var message: String = "",
    var details: String = "",
)