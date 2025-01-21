package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.domain.Story
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("story")
data class StoryEntity (
    @Id
    val id: Long? = null,

    @Column
    val authorId: Long,

    @Column
    val title: String,

    @Column
    val description: String?,

    @Column
    val startDate: LocalDateTime?,

    @Column
    val endDate: LocalDateTime?,

    @CreatedDate
    @Column
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column
    val updatedAt: LocalDateTime? = null,
) {
    fun toDomain() = Story (
        id = id!!,
        authorId = authorId,
        title = title,
        description = description,
        startDate = startDate!!,
        endDate = endDate!!,
        createdAt = createdAt!!,
        updatedAt = updatedAt!!
    )
}