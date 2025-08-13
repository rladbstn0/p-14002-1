package com.back.domain.post.post.dto

import com.back.domain.post.post.entity.Post
import lombok.Getter
import java.time.LocalDateTime

@Getter
data class PostWithContentDto private constructor(
    val id: Int,
    val createDate: LocalDateTime,
    val modifyDate: LocalDateTime,
    val authorId: Int,
    val authorName: String,
    val title: String,
    val content: String
) {
    constructor(post: Post) : this(
        id = post.id,
        createDate = post.createDate,
        modifyDate = post.modifyDate,
        authorId = post.author.id,
        authorName = post.author.name,
        title = post.title,
        content = post.content
    )
}