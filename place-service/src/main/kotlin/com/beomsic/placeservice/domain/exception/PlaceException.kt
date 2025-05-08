package com.beomsic.placeservice.domain.exception

sealed class PlaceException(
    val code: Int,
    override val message: String,
    cause: Throwable? = null
) : RuntimeException(message, cause)

data class PlaceNotFoundException(
    override val message: String = "장소가 존재하지 않습니다.",
) : PlaceException(404, message)

data class ForbiddenException(
    override val message: String = "요청에 대한 권한이 없습니다.",
) : PlaceException(403, message)

data class UnauthorizedPlaceAccessException(
    override val message: String = "요청에 대한 권한이 없습니다.",
) : PlaceException(403, message)

data class InvalidException(
    override val message: String = "잘못된 값이 들어왔습니다.",
) : PlaceException(400, message)

class ServerException(message: String, cause: Throwable? = null): PlaceException(500, message, cause)
