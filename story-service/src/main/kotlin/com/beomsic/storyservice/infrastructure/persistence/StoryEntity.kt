package com.beomsic.storyservice.infrastructure.persistence

import com.beomsic.storyservice.domain.model.Category
import com.beomsic.storyservice.domain.model.Story
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Table(name = "story")
@Entity
@EntityListeners(AuditingEntityListener::class)
class StoryEntity(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column
    val authorId: Long,

    @Column
    val title: String,

    @Column
    val description: String?,

    @Column
    val category: String,

    @Column
    val startDate: LocalDateTime?,

    @Column
    val endDate: LocalDateTime?,

    @CreatedDate
    @Column(updatable = false)
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
        category = Category.entries.find { it.value == category } ?: Category.DAILY_RECORD,
        startDate = startDate!!,
        endDate = endDate!!,
        createdAt = createdAt!!,
        updatedAt = updatedAt!!
    )
}