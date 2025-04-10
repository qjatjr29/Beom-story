package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.domain.model.StoryStatus
import io.r2dbc.spi.Row
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
    override suspend fun findAllWithPaging(pageable: Pageable): Page<StoryEntity> {
        val offset = pageable.offset
        val size = pageable.pageSize

        val binds = mapOf(
            "limit" to size,
            "offset" to offset
        )

        val contentSql = """
            SELECT * FROM story
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
        """.trimIndent()

        val countSql = """
            SELECT COUNT(*) FROM story
        """.trimIndent()

        return paginate(
            pageable = pageable,
            contentQuery = { executeStoryQuery(contentSql, binds) },
            countQuery = { executeCountQuery(countSql, emptyMap()) }
        )
    }

    override suspend fun findAllByUserIdWithPaging(userId: Long, pageable: Pageable): Page<StoryEntity> {
        val offset = pageable.offset
        val size = pageable.pageSize

        val binds = mapOf(
            "userId" to userId,
            "limit" to size,
            "offset" to offset
        )

        val contentSql = """
            SELECT * FROM story 
            WHERE author_id = :userId 
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
        """.trimIndent()

        val countSql = """
            SELECT COUNT(*) FROM story WHERE author_id = :userId
        """.trimIndent()

        return paginate(
            pageable = pageable,
            contentQuery = { executeStoryQuery(contentSql, binds) },
            countQuery = { executeCountQuery(countSql, mapOf("userId" to userId)) }
        )
    }

    override suspend fun findAllByUserIdAndStatusWithPaging(
        userId: Long,
        status: String,
        pageable: Pageable
    ): Page<StoryEntity> {

        val offset = pageable.offset
        val size = pageable.pageSize
        val storyStatus = StoryStatus.fromValue(status)

        val binds = mapOf(
            "userId" to userId,
            "status" to storyStatus.name,
            "limit" to size,
            "offset" to offset
        )

        val contentSql = """
            SELECT * FROM story 
            WHERE author_id = :userId AND status = :status
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
        """.trimIndent()

        val countSql = """
            SELECT COUNT(*) FROM story 
            WHERE author_id = :userId AND status = :status
        """.trimIndent()

        return paginate(
            pageable = pageable,
            contentQuery = { executeStoryQuery(contentSql, binds) },
            countQuery = { executeCountQuery(countSql, mapOf("userId" to userId, "status" to storyStatus.name)) }
        )
    }

    private fun mapRowToStoryEntity(row: Row): StoryEntity {
        return StoryEntity(
            id = row.get("id", java.lang.Long::class.java)?.toLong(),
            authorId = row.get("author_id", java.lang.Long::class.java)?.toLong() ?: throw IllegalStateException("author_id is required"),
            title = row.get("title", String::class.java) ?: throw IllegalStateException("title is required"),
            description = row.get("description", String::class.java),
            category = row.get("category", String::class.java) ?: throw IllegalStateException("category is required"),
            status = row.get("status", String::class.java) ?: throw IllegalStateException("status is required"),
            startDate = row.get("start_date", LocalDateTime::class.java),
            endDate = row.get("end_date", LocalDateTime::class.java),
            createdAt = row.get("created_at", LocalDateTime::class.java),
            updatedAt = row.get("updated_at", LocalDateTime::class.java),
        )
    }

    private suspend fun executeStoryQuery(sql: String, binds: Map<String, Any>): List<StoryEntity> {
        val spec = databaseClient.sql(sql).let { statement ->
            binds.entries.fold(statement) { acc, (key, value) ->
                acc.bind(key, value)
            }
        }

        return spec.map { row, _ -> mapRowToStoryEntity(row) }
            .all()
            .collectList()
            .awaitFirst()
    }

    private suspend fun executeCountQuery(sql: String, binds: Map<String, Any>): Long {
        val spec = databaseClient.sql(sql).let { statement ->
            binds.entries.fold(statement) { acc, (key, value) ->
                acc.bind(key, value)
            }
        }

        return spec.map { row, _ -> row.get(0, java.lang.Long::class.java)?.toLong() ?: 0L }
            .first()
            .awaitSingle()
    }
}