package com.beomsic.storyservice.infrastructure.persistence

import com.beomsic.storyservice.domain.outbox.StoryOutboxPayload
import com.beomsic.storyservice.domain.outbox.StoryOutboxType
import jakarta.persistence.*
import org.springframework.data.annotation.CreatedDate
import org.springframework.data.jpa.domain.support.AuditingEntityListener
import java.time.LocalDateTime

@Entity
@Table(name = "story_outbox") // Story 도메인 전용 테이블
@EntityListeners(AuditingEntityListener::class)
class StoryOutbox(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(nullable = false)
    val storyId: Long,

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    val outboxType: StoryOutboxType,

    @Column(nullable = false)
    val payload: StoryOutboxPayload,

    @CreatedDate
    @Column(updatable = false)
    var createdAt: LocalDateTime? = null
)