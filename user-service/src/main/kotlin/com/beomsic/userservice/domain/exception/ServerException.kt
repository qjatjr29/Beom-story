package com.beomsic.userservice.domain.exception

sealed class ServerException (
    val code: Int,
    override val message: String,
) : RuntimeException(message)

data class UserExistsException(
    override val message: String = "이미 존재하는 유저입니다."
) : ServerException(409, message)

data class UserEmailAlreadyException(
    override val message: String = "같은 이메일로 회원가입한 유저가 존재합니다."
) : ServerException(409, message)

data class UserNotFoundException(
    override val message: String = "유저가 존재하지 않습니다."
) : ServerException(404, message)

data class PasswordNotMatchedException(
    override val message: String = "패스워드가 잘못되었습니다."
) : ServerException(409, message)

data class InvalidException(
    override val message: String = "잘못된 형식의 값이 들어왔습니다."
) : ServerException(400, message)

data class InvalidJwtTokenException(
    override val message: String = "잘못된 토큰입니다."
) : ServerException(400, message)

data class InvalidEmailException(
    override val message: String = "잘못된 이메일 형식입니다."
) : ServerException(400, message)

data class InvalidPasswordException(
    override val message: String = "잘못된 password 형식입니다."
) : ServerException(400, message)

data class AuthenticationException(
    override val message: String = "권한이 없습니다."
) : ServerException(403, message)