package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.Story
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "story")
data class StoryEntity(
    @Id
    val id: Long? = null,

    @Column
    val authorId: Long,

    @Column
    val title: String,

    @Column
    val description: String?,

    @Column
    val category: Category,

    @Column
    val startDate: LocalDateTime?,

    @Column
    val endDate: LocalDateTime?,

    @Column
    val status: StoryStatus,

    @CreatedDate
    @Column
    var createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column
    var updatedAt: LocalDateTime? = null
) {

    fun toDomain() = Story(
        id = id!!,
        authorId = authorId,
        title = title,
        description = description,
        category = category,
        startDate = startDate!!,
        endDate = endDate!!,
        createdAt = createdAt!!,
        updatedAt = updatedAt!!
    )
}