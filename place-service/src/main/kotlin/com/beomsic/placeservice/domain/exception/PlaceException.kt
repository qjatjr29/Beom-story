package com.beomsic.placeservice.domain.exception

sealed class PlaceException(
    val code: Int,
    override val message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)


class ServerException(message: String, cause: Throwable? = null): PlaceException(500, message, cause)
class PlaceNotFoundException(message: String, cause: Throwable? = null) : PlaceException(404, message, cause)