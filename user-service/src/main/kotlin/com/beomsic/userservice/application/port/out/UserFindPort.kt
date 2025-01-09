package com.beomsic.userservice.application.port.out

import com.beomsic.userservice.adapter.out.persistence.UserEntity

interface UserFindPort {
    suspend fun findByEmail(email: String) : UserEntity?
}