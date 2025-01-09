package com.beomsic.userservice.application.service

import com.beomsic.userservice.domain.exception.InvalidEmailException
import com.beomsic.userservice.domain.exception.InvalidPasswordException
import org.springframework.stereotype.Component

@Component
class ValidationService {

    // 이메일 유효성 검사
    fun validateEmail(email: String) {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$".toRegex()
        if (!email.matches(emailRegex)) {
            throw InvalidEmailException()
        }
    }

    // 패스워드 유효성 검사 (예: 최소 8자 이상, 숫자 및 특수문자 포함 등)
    fun validatePassword(password: String) {
        val passwordRegex = "^(?=.*[0-9])(?=.*[!@#\$%^&*])(?=\\S+$).{8,}\$".toRegex()
        if (!password.matches(passwordRegex)) {
            throw InvalidPasswordException()
        }
    }
}