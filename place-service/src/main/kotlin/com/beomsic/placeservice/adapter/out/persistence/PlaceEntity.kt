package com.beomsic.placeservice.adapter.out.persistence

import com.beomsic.placeservice.domain.Category
import com.beomsic.placeservice.domain.Place
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table

@Table(name = "place")
data class PlaceEntity(

    @Id
    val id: Long? = null,

    @Column
    val storyId: Long,

    @Column
    val name: String,

    @Column
    val description: String?,

    @Column
    val category: Category? = Category.기타,

    @Column
    val latitude: Double?,

    @Column
    val longitude: Double?,
) {
    fun toDomain(): Place {
        return Place(
            id = id,
            storyId = storyId,
            name = name,
            description = description,
            category = category,
            latitude = latitude,
            longitude = longitude
        )
    }
}