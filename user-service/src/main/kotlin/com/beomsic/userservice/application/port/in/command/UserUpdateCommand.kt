package com.beomsic.userservice.application.port.`in`.command

import org.springframework.web.multipart.MultipartFile

sealed interface UserUpdateCommand {
    val userId: Long
    val authUserId: Long
}

data class UserNicknameUpdateCommand (
    override val userId: Long,
    override val authUserId: Long,
    val nickName: String,
): UserUpdateCommand

data class UserPasswordUpdateCommand (
    override val userId: Long,
    override val authUserId: Long,
    val currentPassword: String,
    val newPassword: String,
): UserUpdateCommand

data class UserProfileUpdateCommand (
    override val userId: Long,
    override val authUserId: Long,
    val profileImage: MultipartFile
): UserUpdateCommand