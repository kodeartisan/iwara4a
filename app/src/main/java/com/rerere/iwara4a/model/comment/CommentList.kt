package com.rerere.iwara4a.model.comment

data class CommentList(
    val total: Int,
    val page: Int,
    val hasNext: Boolean,
    val comments: List<Comment>
)

data class Comment(
    val authorId: String,
    val authorName: String,
    val authorPic: String,
    val posterType: CommentPosterType,

    val content: String,
    val date: String,

    var reply: List<Comment>
)

enum class CommentPosterType {
    NORMAL,
    SELF,
    OWNER
}