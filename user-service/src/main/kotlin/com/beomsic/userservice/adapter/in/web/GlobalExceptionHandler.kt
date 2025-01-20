package com.beomsic.userservice.adapter.`in`.web

import com.beomsic.userservice.domain.exception.ServerException
import com.beomsic.common.exception.ErrorResponse
import mu.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

    @ExceptionHandler(ServerException::class)
    fun handlerCustomServerException(ex: ServerException): Mono<ResponseEntity<ErrorResponse>> {
        logger.error(ex.message, ex)
        return Mono.just(
            ResponseEntity(
                ErrorResponse(ex.code, ex.message),
                HttpStatus.valueOf(ex.code)
            )
        )
    }

    // Binding exception
    @ExceptionHandler(WebExchangeBindException::class)
    fun handlerWebClientException(ex: WebExchangeBindException): Mono<ResponseEntity<ErrorResponse>> {
        logger.error(ex.message, ex)
        return Mono.just(ResponseEntity.badRequest().body(ErrorResponse(400, ex.message)))
    }

    @ExceptionHandler(Exception::class)
    fun handlerException(ex: Exception): Mono<ResponseEntity<ErrorResponse>> {
        logger.error(ex.message, ex)
        return Mono.just(ResponseEntity.internalServerError().body(ErrorResponse(500, ex.message.toString())))
    }
}