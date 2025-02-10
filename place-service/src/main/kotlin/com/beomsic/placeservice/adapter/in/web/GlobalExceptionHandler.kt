package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.common.exception.ErrorResponse
import mu.KotlinLogging
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import reactor.core.publisher.Mono

@ControllerAdvice
class GlobalExceptionHandler {

    private val logger = KotlinLogging.logger {}

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