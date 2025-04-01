package com.beomsic.userservice.application.service

import com.beomsic.userservice.application.port.`in`.usecase.UserFindUseCase
import com.beomsic.userservice.application.port.out.UserFindPort
import com.beomsic.userservice.application.service.dto.UserDto
import org.springframework.stereotype.Service

@Service
class UserFindService (
    private val userFindPort: UserFindPort,
) : UserFindUseCase {

    override suspend fun findById(id: Long): UserDto {
        val user = userFindPort.findById(id)
        return UserDto(user)
    }
}