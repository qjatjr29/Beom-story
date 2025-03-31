package com.beomsic.userservice.application.port.out

import com.beomsic.userservice.domain.model.User

interface UserUpdatePort {
    suspend fun updateNickname(id: Long, newNickname: String): User
    suspend fun updatePassword(id: Long, newPassword: String): User
}