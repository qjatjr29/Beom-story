package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.domain.Category
import com.beomsic.placeservice.domain.Place
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "place")
data class PlaceEntity(

    @Id
    val id: Long? = null,

    @Column
    val storyId: Long,

    @Column
    val authorId: Long,

    @Column
    var name: String,

    @Column
    var description: String?,

    @Column
    var imageUrl: String?,

    @Column
    var category: Category? = Category.기타,

    @Column
    var latitude: Double?,

    @Column
    var longitude: Double?,

    @CreatedDate
    @Column
    val createdAt: LocalDateTime? = null,

    @LastModifiedDate
    @Column
    val updatedAt: LocalDateTime? = null,
) {
    fun toDomain(): Place {
        return Place(
            id = id,
            storyId = storyId,
            authorId = authorId,
            name = name,
            imageUrl = imageUrl,
            description = description,
            category = category,
            latitude = latitude,
            longitude = longitude,
            createdAt = createdAt!!,
            updatedAt = updatedAt!!
        )
    }
}