package com.beomsic.userservice.application.port.out

interface UserUpdatePort {
    suspend fun updateNickname(id: Long, newNickname: String)
    suspend fun updatePassword(id: Long, newPassword: String)
    suspend fun deleteUser(id: Long)
}