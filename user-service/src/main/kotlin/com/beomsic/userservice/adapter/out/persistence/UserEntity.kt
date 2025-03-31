package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.domain.model.User
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("users")
data class UserEntity (

    @Id
    val id: Long? = null,

    @Column
    val email: String,

    @Column
    val password: String? = null,

    @Column
    val nickname: String,

    @Column
    val profileUrl: String? = null,

    @Column
    val provider: String? = null,

    @Column
    val providerId: String? = null,

    @CreatedDate
    @Column
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column
    val updatedAt: LocalDateTime? = null,
) {
    fun toDomain() = User(
        id = id ?: throw RuntimeException("Failed to convert entity to domain"),
        email = email,
        nickname = nickname,
        password = password,
        profileUrl = profileUrl,
        createdAt = createdAt!!,
        updatedAt = updatedAt!!
    )
}