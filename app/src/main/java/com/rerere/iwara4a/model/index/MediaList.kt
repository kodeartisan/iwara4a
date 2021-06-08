package com.rerere.iwara4a.model.index

data class MediaList(
    val currentPage: Int,
    val hasNext: Boolean,
    val mediaList: List<MediaPreview>
)

data class MediaQueryParam(
    var sortType: SortType,
    var filters: List<String>
)

enum class SortType(val value: String) {
    DATE("date"),
    VIEWS("views"),
    LIKES("likes")
}