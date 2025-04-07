package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.domain.model.Category
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
class StoryPaginationRepositoryImpl(
    private val databaseClient: DatabaseClient
): StoryPaginationRepository {
    override suspend fun findAllByAuthorIdWithPaging(authorId: Long, pageable: Pageable): Page<StoryEntity> {
        val offset = pageable.offset
        val size = pageable.pageSize

        return paginate(
            pageable = pageable,
            contentQuery = {
                databaseClient.sql(
                    """
                    SELECT * FROM story 
                    WHERE author_id = :authorId 
                    ORDER BY id DESC 
                    LIMIT :limit OFFSET :offset
                    """.trimIndent()
                )
                    .bind("authorId", authorId)
                    .bind("limit", size)
                    .bind("offset", offset)
                    .map { row, _ ->
                        StoryEntity(
                            id = row.get("id", java.lang.Long::class.java)?.toLong(),
                            authorId = row.get("author_id", java.lang.Long::class.java)?.toLong() ?: throw IllegalStateException("author_id is required"),
                            title = row.get("title", String::class.java) ?: "제목 없음",
                            description = row.get("description", String::class.java),
                            category = row.get("category", String::class.java) ?: throw IllegalStateException("category is required"),
                            status = row.get("status", String::class.java) ?: throw IllegalStateException("status is required"),
                            startDate = row.get("start_date", LocalDateTime::class.java),
                            endDate = row.get("end_date", LocalDateTime::class.java),
                            createdAt = row.get("created_at", LocalDateTime::class.java),
                            updatedAt = row.get("updated_at", LocalDateTime::class.java),
                        )
                    }
                    .all()
                    .collectList()
                    .awaitFirst()
            },
            countQuery = {
                databaseClient.sql("SELECT COUNT(*) FROM story WHERE author_id = :authorId")
                    .bind("authorId", authorId)
                    .map { row, _ -> row.get(0, java.lang.Long::class.java)?.toLong() ?: 0L }
                    .first()
                    .awaitSingle()
            }
        )
    }
}