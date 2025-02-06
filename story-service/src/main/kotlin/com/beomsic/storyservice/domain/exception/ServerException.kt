package com.beomsic.storyservice.domain.exception

sealed class ServerException (
    val code: Int,
    override val message: String,
) : RuntimeException(message)