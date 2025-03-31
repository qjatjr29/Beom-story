package com.beomsic.userservice.application.port.`in`.command

import org.springframework.web.multipart.MultipartFile

sealed interface UserUpdateCommand {
    val userId: Long
}

data class UserNicknameUpdateCommand (
    override val userId: Long,
    val nickName: String,
): UserUpdateCommand

data class UserPasswordUpdateCommand (
    override val userId: Long,
    val password: String,
): UserUpdateCommand

data class UserProfileUpdateCommand (
    override val userId: Long,
    val profileImage: MultipartFile
): UserUpdateCommand