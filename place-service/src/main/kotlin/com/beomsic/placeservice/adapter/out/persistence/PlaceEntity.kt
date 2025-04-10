package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.domain.Category
import com.beomsic.placeservice.domain.Place
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.annotation.LastModifiedDate
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDate
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
    val name: String,

    @Column
    val description: String?,

    @Column
    val imageUrl: String?,

    @Column
    val category: String,

    @Column
    val latitude: Double?,

    @Column
    val longitude: Double?,

    @Column
    val address: String?,

    @Column
    val visitedDate: LocalDate,

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
            category = Category.fromValue(category),
            latitude = latitude,
            longitude = longitude,
            address = address,
            visitedDate = visitedDate,
            createdAt = createdAt!!,
            updatedAt = updatedAt!!
        )
    }
}