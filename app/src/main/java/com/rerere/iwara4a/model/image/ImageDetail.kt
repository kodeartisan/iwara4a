package com.rerere.iwara4a.model.image

data class ImageDetail(
    val id: String,
    val title: String,
    val imageLinks: List<String>,

    val authorId: String,
    val authorProfilePic: String,

    val watchs: String
) {
    companion object {
        val LOADING = ImageDetail(
            "",
            "",
            emptyList(),
            "",
            "",
            ""
        )
    }
}