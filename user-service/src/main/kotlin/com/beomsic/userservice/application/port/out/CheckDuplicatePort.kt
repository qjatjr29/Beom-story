package com.beomsic.userservice.application.port.out

interface CheckDuplicatePort {
    suspend fun isDuplicatedEmail(email: String): Boolean
    suspend fun isDuplicatedNickname(nickname: String): Boolean
}