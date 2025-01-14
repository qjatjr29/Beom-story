package com.beomsic.userservice.adapter.out.persistence

import com.beomsic.userservice.domain.User
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
    val password: String,

    @Column
    val username: String,

    @Column
    val profileUrl: String? = null,

    @CreatedDate
    @Column
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column
    val updatedAt: LocalDateTime? = null,
) {
    fun toDomain() = User(
        id = id,
        email = email,
        username = username,
        profileUrl = profileUrl,
        createdAt = createdAt!!,
        updatedAt = updatedAt!!
    )
}