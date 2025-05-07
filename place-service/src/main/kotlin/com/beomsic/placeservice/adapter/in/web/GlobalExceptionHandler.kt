package com.beomsic.placeservice.adapter.`in`.web

import com.beomsic.common.exception.ErrorResponse
import com.beomsic.placeservice.domain.exception.PlaceException
import mu.KotlinLogging
import org.springframework.core.codec.DecodingException
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.ControllerAdvice
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.support.WebExchangeBindException
import org.springframework.web.server.ServerWebInputException
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

    // JSON 파싱 실패 시 발생
    @ExceptionHandler(DecodingException::class)
    fun handleDecodingException(ex: DecodingException): Mono<ResponseEntity<ErrorResponse>> {
        logger.error(ex.message, ex)
        return Mono.just(ResponseEntity.badRequest().body(ErrorResponse(400, "요청 데이터가 유효하지 않습니다.")))
    }

    // JSON 외에도 파라미터 타입 불일치 등 WebFlux 입력 처리에서 발생
    @ExceptionHandler(ServerWebInputException::class)
    fun handleServerWebInputException(ex: ServerWebInputException): Mono<ResponseEntity<ErrorResponse>> {
        logger.error(ex.message, ex)
        return Mono.just(ResponseEntity.badRequest().body(ErrorResponse(400, "잘못된 입력입니다.")))
    }

    @ExceptionHandler(Exception::class)
    fun handlerException(ex: Exception): Mono<ResponseEntity<ErrorResponse>> {
        logger.error(ex.message, ex)
        return Mono.just(ResponseEntity.internalServerError().body(ErrorResponse(500, ex.message.toString())))
    }

    @ExceptionHandler(PlaceException::class)
    fun handlerException(ex: PlaceException): Mono<ResponseEntity<ErrorResponse>> {
        logger.error(ex.message, ex)
        return Mono.just(ResponseEntity(ErrorResponse(ex.code, ex.message), HttpStatus.valueOf(ex.code)))
    }
}