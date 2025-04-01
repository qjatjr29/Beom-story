package com.beomsic.userservice.adapter.out.persistence.adapter

import com.beomsic.userservice.adapter.out.persistence.UserEntity
import com.beomsic.userservice.adapter.out.persistence.UserRepository
import com.beomsic.userservice.adapter.out.persistence.findByIdOrNull
import com.beomsic.userservice.application.port.out.UserUpdatePort
import com.beomsic.userservice.domain.model.AuthType
import com.beomsic.userservice.domain.model.User
import com.beomsic.userservice.infrastructure.util.BCryptUtils
import org.springframework.stereotype.Component

@Component
class UserUpdateAdapter(
    private val userRepository: UserRepository
): UserUpdatePort {

    override suspend fun updateNickname(id: Long, newNickname: String) {
        updateUser(id) { userEntity ->
            userRepository.save(userEntity.copy(nickname = newNickname)).toDomain()
        }
    }

    override suspend fun updatePassword(id: Long, currentPassword: String, newPassword: String) {
        updateUser(id) { userEntity ->
            when {
                userEntity.authType == AuthType.EMAIL_PASSWORD -> checkPassword(currentPassword, userEntity.password)
                userEntity.authType == AuthType.HYBRID -> checkPassword(currentPassword, userEntity.password)
            }
            userRepository.save(userEntity.copy(password = BCryptUtils.hash(newPassword))).toDomain()
        }
    }

    override suspend fun deleteUser(id: Long) {
        userRepository.deleteById(id)
    }

    private suspend fun updateUser(id: Long, updateAction: suspend (UserEntity) -> User): User {
        val userEntity = userRepository.findByIdOrNull(id)
        return updateAction(userEntity)
    }

    private suspend fun checkPassword(password: String, userPassword: String?): Boolean {
        checkNotNull(userPassword)
        return BCryptUtils.verify(password, userPassword)
    }
}