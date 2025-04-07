package com.beomsic.storyservice.adapter.out.persistence

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable

suspend fun <T> paginate(
    pageable: Pageable,
    contentQuery: suspend () -> List<T>,
    countQuery: suspend () -> Long
): Page<T> {
    val content = contentQuery()
    val total = countQuery()
    return PageImpl(content, pageable, total)
}