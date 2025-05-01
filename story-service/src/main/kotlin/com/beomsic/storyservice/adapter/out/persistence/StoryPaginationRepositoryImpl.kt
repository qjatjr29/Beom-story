package com.beomsic.storyservice.adapter.out.persistence

import com.beomsic.storyservice.domain.model.StoryStatus
import io.r2dbc.spi.Row
import kotlinx.coroutines.reactive.awaitFirst
import kotlinx.coroutines.reactive.awaitSingle
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import org.springframework.data.domain.Sort
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate
import org.springframework.data.relational.core.query.Criteria
import org.springframework.data.relational.core.query.Query
import org.springframework.r2dbc.core.DatabaseClient
import org.springframework.stereotype.Repository
import java.time.LocalDate
import java.time.LocalDateTime

@Repository
class StoryPaginationRepositoryImpl(
    private val template: R2dbcEntityTemplate,
    private val databaseClient: DatabaseClient
): StoryPaginationRepository {

    override suspend fun findAllWithPaging(pageable: Pageable): Page<StoryEntity> {
        val query = Query.empty()
            .with(pageable)
            .sort(Sort.by(Sort.Direction.DESC, "createdAt"))

        val content = template.select(StoryEntity::class.java)
            .matching(query)
            .all()
            .collectList()
            .awaitFirst()

        val total = template.count(Query.empty(), StoryEntity::class.java)
            .awaitFirst()

        return PageImpl(content, pageable, total)
    }

    override suspend fun findArchivedStoriesWithPaging(pageable: Pageable): Page<StoryEntity> {
        val query = Query.query(
            Criteria.where("status").`is`(StoryStatus.ARCHIVED.name)
        )
            .with(pageable)
            .sort(Sort.by(Sort.Direction.DESC, "createdAt"))

        val content = template.select(StoryEntity::class.java)
            .matching(query)
            .all()
            .collectList()
            .awaitFirst()

        val total = template.count(
            Query.query(Criteria.where("status").`is`(StoryStatus.ARCHIVED.name)),
            StoryEntity::class.java
        ).awaitFirst()

        return PageImpl(content, pageable, total)
    }

    override suspend fun findAllByStatusWithPaging(status: String, pageable: Pageable): Page<StoryEntity> {
        val storyStatus = StoryStatus.fromValue(status).name

        val query = Query.query(
            Criteria.where("status").`is`(storyStatus)
        )
            .with(pageable)
            .sort(Sort.by(Sort.Direction.DESC, "createdAt"))

        val content = template.select(StoryEntity::class.java)
            .matching(query)
            .all()
            .collectList()
            .awaitFirst()

        val total = template.count(
            Query.query(Criteria.where("status").`is`(storyStatus)),
            StoryEntity::class.java
        ).awaitFirst()

        return PageImpl(content, pageable, total)
    }

    override suspend fun findAllByKeywordWithPaging(keyword: String, pageable: Pageable): Page<StoryEntity> {
        val offset = pageable.offset
        val size = pageable.pageSize

        val binds = mapOf(
            "keyword" to keyword,
            "limit" to size,
            "offset" to offset
        )

        // Full Text 기반 검색
        val contentSql = """
            SELECT * FROM story 
            WHERE MATCH(title, description) AGAINST (:keyword IN BOOLEAN MODE)
            ORDER BY created_at DESC
            LIMIT :limit OFFSET :offset
        """.trimIndent()

        val countSql = """
            SELECT COUNT(*) FROM story 
            WHERE MATCH(title, description) AGAINST (:keyword IN BOOLEAN MODE)
        """.trimIndent()

        return paginate(
            pageable = pageable,
            contentQuery = { executeStoryQuery(contentSql, binds) },
            countQuery = { executeCountQuery(countSql, mapOf("keyword" to keyword)) }
        )
    }

    override suspend fun findAllByUserIdWithPaging(userId: Long, pageable: Pageable): Page<StoryEntity> {
        val query = Query.query(
            Criteria.where("authorId").`is`(userId)
        )
            .with(pageable)
            .sort(Sort.by(Sort.Direction.DESC, "createdAt"))

        val content = template.select(StoryEntity::class.java)
            .matching(query)
            .all()
            .collectList()
            .awaitFirst()

        val total = template.count(
            Query.query(Criteria.where("authorId").`is`(userId)),
            StoryEntity::class.java
        ).awaitFirst()

        return PageImpl(content, pageable, total)
    }

    override suspend fun findAllByUserIdAndStatusWithPaging(
        userId: Long,
        status: String,
        pageable: Pageable
    ): Page<StoryEntity> {
        val storyStatus = StoryStatus.fromValue(status)

        val query = Query.query(
            Criteria.where("authorId").`is`(userId)
                .and("status").`is`(storyStatus)
        )
            .with(pageable)
            .sort(Sort.by(Sort.Direction.DESC, "createdAt"))

        val content = template.select(StoryEntity::class.java)
            .matching(query)
            .all()
            .collectList()
            .awaitFirst()

        val total = template.count(
            Query.query(Criteria.where("authorId").`is`(userId)),
            StoryEntity::class.java
        ).awaitFirst()

        return PageImpl(content, pageable, total)
    }

    private fun mapRowToStoryEntity(row: Row): StoryEntity {
        return StoryEntity(
            id = row.get("id", java.lang.Long::class.java)?.toLong(),
            authorId = row.get("author_id", java.lang.Long::class.java)?.toLong() ?: throw IllegalStateException("author_id is required"),
            title = row.get("title", String::class.java) ?: throw IllegalStateException("title is required"),
            description = row.get("description", String::class.java),
            category = row.get("category", String::class.java) ?: throw IllegalStateException("category is required"),
            status = row.get("status", String::class.java) ?: throw IllegalStateException("status is required"),
            startDate = row.get("start_date", LocalDate::class.java),
            endDate = row.get("end_date", LocalDate::class.java),
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