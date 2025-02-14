package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.`in`.usecase.CheckDuplicateUseCase
import com.beomsic.userservice.application.port.out.CheckDuplicatePort
import com.beomsic.userservice.domain.exception.InvalidException
import org.springframework.stereotype.Service

@Service
class CheckDuplicateService(
    private val validationService: ValidationService,
    private val checkDuplicatePort: CheckDuplicatePort
): CheckDuplicateUseCase {

    override suspend fun execute(type: String, value: String): Boolean {
        return when (type) {
            "email" -> {
                validationService.validateEmail(value)
                checkDuplicatePort.isDuplicatedEmail(value)
            }
            "nickname" -> checkDuplicatePort.isDuplicatedNickname(value)
            else -> throw InvalidException("중복 검사를 하는 타입이 잘못되었습니다: $type")
        }
    }
}