package com.beomsic.userservice.application.port.`in`.usecase

interface CheckDuplicateUseCase {
    suspend fun execute(type: String, value: String): Boolean
}