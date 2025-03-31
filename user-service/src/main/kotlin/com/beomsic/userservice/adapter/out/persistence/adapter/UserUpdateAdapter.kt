package com.beomsic.userservice.adapter.out.persistence.adapter

import com.beomsic.userservice.adapter.out.persistence.UserEntity
import com.beomsic.userservice.adapter.out.persistence.UserRepository
import com.beomsic.userservice.adapter.out.persistence.findByIdOrNull
import com.beomsic.userservice.application.port.out.UserUpdatePort
import com.beomsic.userservice.domain.model.User
import com.beomsic.userservice.infrastructure.util.BCryptUtils
import org.springframework.stereotype.Component

@Component
class UserUpdateAdapter(
    private val userRepository: UserRepository
): UserUpdatePort {

    override suspend fun updateNickname(id: Long, newNickname: String): User {
        return updateUser(id) { userEntity ->
            userRepository.save(userEntity.copy(nickname = newNickname)).toDomain()
        }
    }

    override suspend fun updatePassword(id: Long, newPassword: String): User {
        return updateUser(id) { userEntity ->
            userRepository.save(userEntity.copy(password = BCryptUtils.hash(newPassword))).toDomain()
        }
    }

    private suspend fun updateUser(id: Long, updateAction: suspend (UserEntity) -> User): User {
        val userEntity = userRepository.findByIdOrNull(id)
        return updateAction(userEntity)
    }
}