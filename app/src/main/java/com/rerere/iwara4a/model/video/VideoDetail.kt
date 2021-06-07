package com.rerere.iwara4a.model.video

data class VideoDetail(
    // 视频信息
    val id: String,
    val title: String,
    var videoLinks: VideoLink,
    val likes: String,
    val watchs: String,
    val postDate: String,
    val description: String,

    // 视频作者信息
    val authorPic: String,
    val authorName: String,

    // 作者的更多视频
    val moreVideo: List<MoreVideo>
){
    companion object {
        val LOADING = VideoDetail(
            "","",VideoLink(), "","","","","","", emptyList()
        )
    }
}

data class MoreVideo(
    val id: String,
    val title: String,
    val pic: String,
    val watchs: String,
    val likes: String
)