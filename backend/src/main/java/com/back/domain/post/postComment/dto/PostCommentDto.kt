package com.back.domain.post.postComment.dto

import com.back.domain.post.postComment.entity.PostComment
import java.time.LocalDateTime

class PostCommentDto(postComment: PostComment) {
    val id: Int
    val createDate: LocalDateTime?
    val modifyDate: LocalDateTime?
    val authorId: Int
    val authorName: String?
    val postId: Int
    val content: String?

    init {
        id = postComment.id
        createDate = postComment.createDate
        modifyDate = postComment.modifyDate
        authorId = postComment.author.id
        authorName = postComment.author.name
        postId = postComment.post.id
        content = postComment.content
    }
}