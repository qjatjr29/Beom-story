package com.beomsic.storyservice.adapter.out.persistence.outbox

import org.springframework.data.annotation.CreatedDate
import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Column
import org.springframework.data.relational.core.mapping.Table
import java.time.LocalDateTime

@Table(name = "story_outbox")
class StoryOutbox(
    @Id
    val id: Long? = null,

    @Column("story_id")
    val storyId: Long,

    @Column
    val payload: String,

    @CreatedDate
    @Column("created_at")
    var createdAt: LocalDateTime? = null
)