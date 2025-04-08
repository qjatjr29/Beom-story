package com.beomsic.storyservice.domain.exception

sealed class ServerException (
    val code: Int,
    override val message: String,
): RuntimeException(message)

data class StoryNotFoundException(
    override val message: String = "기록이 존재하지 않습니다."
): ServerException(404, message)

data class UnauthorizedStoryAccessException(
    override val message: String = "스토리에 접근할 권한이 없습니다."
): ServerException(403, message)

data class InvalidStoryStatusException(
    override val message: String = "유효하지 않은 스토리 상태입니다."
): ServerException(400, message)

data class StoryDeletionFailedException(
    override val message: String = "스토리 삭제에 실패했습니다."
): ServerException(500, message)