package com.beomsic.userservice.infrastructure.util

import at.favre.lib.crypto.bcrypt.BCrypt

object BCryptUtils {

    /**
     * 해시 함수
     */
    fun hash(password: String) =
        BCrypt.withDefaults().hashToString(12, password.toCharArray())

    /**
     * 요청으로 들어온 평문문자열과 데이터베이스에 있는 해시 함수를 비교
     * 검증
     */
    fun verify(password: String, hashedPassword: String) =
        BCrypt.verifyer().verify(password.toCharArray(), hashedPassword).verified
}