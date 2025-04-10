package com.beomsic.placeservice.adapter.out.persistence.outbox

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table("image_rollback_outbox")
data class ImageRollbackOutbox(
    @Id
    val id: Long? = null,

    @Column("place_id")
    val placeId: Long,

    @Column
    val imageUrl: String,

    @CreatedDate
    @Column("created_at")
    var createdAt: LocalDateTime? = null
)
