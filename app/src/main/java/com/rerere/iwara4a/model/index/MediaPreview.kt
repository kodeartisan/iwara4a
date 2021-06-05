package com.rerere.iwara4a.model.index

/**
 * 代表了一个封面预览
 */
data class MediaPreview(
    // 标题
    val title: String,
    // 作者
    val author: String,
    // 封面
    val previewPic: String,
    // 喜欢数
    val likes: String,
    // 播放量
    val watchs: String,
    // 类型
    val type: MediaType,
    // 图片ID
    val mediaId: String
)

enum class MediaType {
    VIDEO,
    IMAGE
}