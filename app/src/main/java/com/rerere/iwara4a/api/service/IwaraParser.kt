package com.rerere.iwara4a.api.service

import android.util.Log
import com.google.gson.Gson
import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.model.comment.Comment
import com.rerere.iwara4a.model.comment.CommentList
import com.rerere.iwara4a.model.comment.CommentPosterType
import com.rerere.iwara4a.model.detail.image.ImageDetail
import com.rerere.iwara4a.model.detail.video.MoreVideo
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.detail.video.VideoLink
import com.rerere.iwara4a.model.flag.FollowResponse
import com.rerere.iwara4a.model.flag.LikeResponse
import com.rerere.iwara4a.model.index.*
import com.rerere.iwara4a.model.session.Session
import com.rerere.iwara4a.model.user.Self
import com.rerere.iwara4a.util.okhttp.await
import com.rerere.iwara4a.util.okhttp.getCookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup
import org.jsoup.nodes.Element
import java.net.URLDecoder

private const val TAG = "IwaraParser"

/**
 * 使用Jsoup来解析出网页上的资源
 *
 * 某些资源无法通过 restful api 直接获取，因此需要
 * 通过jsoup来解析
 *
 * @author RE
 */
class IwaraParser(
    private val okHttpClient: OkHttpClient
) {
    private val gson = Gson()

    suspend fun login(username: String, password: String): Response<Session> =
        withContext(Dispatchers.IO) {
            okHttpClient.getCookie().clean()

            try {
                // 首先访问login页面解析出 antibot_key
                val keyRequest = Request.Builder()
                    .url("https://ecchi.iwara.tv/user/login?destination=front")
                    .get()
                    .build()
                val keyResponse = okHttpClient.newCall(keyRequest).await()
                val keyResponseData = keyResponse.body?.string() ?: error("no body response")
                val headElement = Jsoup.parse(keyResponseData).head().html()
                val startIndex = headElement.indexOf("key\":\"") + 6
                val endIndex = headElement.indexOf("\"", startIndex)
                val key = headElement.substring(startIndex until endIndex)
                Log.i(TAG, "login: antibot_key = $key")

                // 发送登录POST请求
                val formBody = FormBody.Builder()
                    .add("name", username)
                    .add("pass", password)
                    .add("form_id", "user_login")
                    .add("antibot_key", key)
                    .add("op", "ログイン")
                    .build()
                val loginRequest = Request.Builder()
                    .url("https://ecchi.iwara.tv/user/login?destination=front")
                    .post(formBody)
                    .build()
                val loginResponse = okHttpClient.newCall(loginRequest).await()
                require(loginResponse.isSuccessful)

                if (loginResponse.isSuccessful) {
                    val cookies = okHttpClient.getCookie().filter { it.domain == "iwara.tv" }
                    if (cookies.isNotEmpty()) {
                        val cookie = cookies.first()
                        Log.i(
                            TAG,
                            "login: Successful login (key:${cookie.name}, value:${cookie.value})"
                        )
                        Response.success(Session(cookie.name, cookie.value))
                    } else {
                        Response.failed("no cookie returned")
                    }
                } else {
                    Response.failed("http code: ${loginResponse.code}")
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                Response.failed(exception.javaClass.name)
            }
        }

    suspend fun getSelf(session: Session): Response<Self> = withContext(Dispatchers.IO) {
        try {
            Log.i(TAG, "getSelf: Start...")
            okHttpClient.getCookie().init(session)

            val request = Request.Builder()
                .url("https://ecchi.iwara.tv/user")
                .get()
                .build()
            val response = okHttpClient.newCall(request).await()
            require(response.isSuccessful)
            val body = Jsoup.parse(response.body?.string() ?: error("null body")).body()

            val nickname = body.getElementsByClass("views-field views-field-name").first().text()
            val profilePic = "https:" + body.getElementsByClass("views-field views-field-picture")
                .first()
                .child(0)
                .child(0)
                .attr("src")

            Log.i(TAG, "getSelf: (nickname=$nickname, profilePic=$profilePic)")

            Response.success(
                Self(
                    nickname = nickname,
                    profilePic = profilePic
                )
            )
        } catch (exception: Exception) {
            exception.printStackTrace()
            Response.failed(exception.javaClass.name)
        }
    }

    suspend fun getSubscriptionList(session: Session, page: Int): Response<SubscriptionList> =
        withContext(Dispatchers.IO) {
            try {
                okHttpClient.getCookie().init(session)

                val request = Request.Builder()
                    .url("https://ecchi.iwara.tv/subscriptions?page=$page")
                    .get()
                    .build()
                val response = okHttpClient.newCall(request).await()
                require(response.isSuccessful)
                val body = Jsoup.parse(response.body?.string() ?: error("empty body")).body()
                val elements = body.select("div[id~=^node-[A-Za-z0-9]+\$]")

                val previewList: List<MediaPreview> = elements?.map {
                    val title = it.getElementsByClass("title").text()
                    val author = it.getElementsByClass("username").text()
                    val pic =
                        "https:" + it.getElementsByClass("field-item even")[0].child(0).child(0)
                            .attr("src")
                    val likes = it.getElementsByClass("right-icon").text()
                    val watchs = it.getElementsByClass("left-icon").text()
                    val link = it.getElementsByClass("field-item even")[0].child(0).attr("href")
                    val mediaId = link.substring(link.lastIndexOf("/") + 1)
                    val type = if (link.startsWith("/video")) MediaType.VIDEO else MediaType.IMAGE

                    MediaPreview(
                        title = title,
                        author = author,
                        previewPic = pic,
                        likes = likes,
                        watchs = watchs,
                        mediaId = mediaId,
                        type = type
                    )
                } ?: error("empty elements")

                val totalPage =
                    body.getElementsByClass("pager-current").text().split(" ")[2].toInt()
                val hasNextPage = totalPage != (page + 1)

                Response.success(
                    SubscriptionList(
                        page,
                        hasNextPage,
                        previewList
                    )
                )
            } catch (ex: Exception) {
                ex.printStackTrace()
                Response.failed(ex.javaClass.name)
            }
        }

    suspend fun getImagePageDetail(session: Session, imageId: String): Response<ImageDetail> =
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "getImagePageDetail: start load image detail: $imageId")

                okHttpClient.getCookie().init(session)

                val request = Request.Builder()
                    .url("https://ecchi.iwara.tv/images/$imageId")
                    .get()
                    .build()
                val response = okHttpClient.newCall(request).await()
                require(response.isSuccessful)
                val body = Jsoup.parse(response.body?.string() ?: error("empty body")).body()

                val title = body.getElementsByClass("title").first().text()
                val imageLinks =
                    body.getElementsByClass("field field-name-field-images field-type-file field-label-hidden")
                        .select("img").map {
                            "https:${it.attr("src")}"
                        }
                val authorId = body.getElementsByClass("username").first().text().trim()
                val authorPic =
                    "https:" + body.getElementsByClass("user-picture").first().select("img")
                        .attr("src")
                val watchs = body.getElementsByClass("node-views").first().text().trim()

                Response.success(
                    ImageDetail(
                        id = imageId,
                        title = title,
                        imageLinks = imageLinks,
                        authorId = authorId,
                        authorProfilePic = authorPic,
                        watchs = watchs
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                Response.failed(exception.javaClass.name)
            }
        }

    suspend fun getVideoPageDetail(session: Session, videoId: String): Response<VideoDetail> =
        withContext(Dispatchers.IO) {
            try {
                Log.i(TAG, "getVideoPageDetail: Start load video detail (id:$videoId)")

                okHttpClient.getCookie().init(session)

                val request = Request.Builder()
                    .url("https://ecchi.iwara.tv/videos/$videoId")
                    .get()
                    .build()
                val response = okHttpClient.newCall(request).await()
                require(response.isSuccessful)
                val body = Jsoup.parse(response.body?.string() ?: error("empty body")).body()

                val title = body.getElementsByClass("title").first().text()
                val viewDiv = body.getElementsByClass("node-views").first().text().trim().split(" ")
                val likes = viewDiv[0]
                val watchs = viewDiv[1]

                // 解析出上传日期
                val postDate = body
                    .select("div[class=submitted]")
                    .first()
                    .textNodes().filter { it.text().contains("-") }
                    .first()
                    .text()
                    .split(" ")
                    .filter { it.matches(Regex(".*[0-9]+.*")) }
                    .joinToString(separator = " ")

                // 视频描述
                val description =
                    body.select("div[class=field field-name-body field-type-text-with-summary field-label-hidden]")
                        .first()?.text() ?: ""
                val authorId = body.getElementsByClass("username").first().text().trim()
                val authorPic =
                    "https:" + body.getElementsByClass("user-picture").first().select("img")
                        .attr("src")

                // 更多视频
                val moreVideo = body
                    .select("div[id=block-views-videos-block-1]")
                    .select("div[class=view-content]")
                    .select("div[id~=^node-[A-Za-z0-9]+\$]")
                    .filter {
                        it.select("a").first() != null
                    }
                    .map {
                        val id = it.select("a").first().attr("href").let { str ->
                            str.substring(str.lastIndexOf("/") + 1)
                        }
                        val title = it.select("img").first().attr("title")
                        val pic = "https:" + it.select("img").first().attr("src")
                        val likes = it.select("div[class=right-icon likes-icon]").first().text()
                        val watchs = it.select("div[class=left-icon likes-icon]").first().text()
                        MoreVideo(
                            id = id,
                            title = title,
                            pic = pic,
                            likes = likes,
                            watchs = watchs
                        )
                    }
                // 喜欢
                val likeFlag = body.select("a[href~=^/flag/.+/like/.+\$\$]").first()
                println(likeFlag)
                val isLike = likeFlag.attr("href").startsWith("/flag/unflag/")
                val likeLink = URLDecoder.decode(
                    likeFlag.attr("href").let { it.substring(it.indexOf("/like/") + 6) }, "UTF-8"
                )

                println("Link = $likeLink")

                // 关注UP主
                val followFlag = body.select("a[href~=^/flag/.+/follow/.+\$\$]").first()
                val isFollow = followFlag.attr("href").startsWith("/flag/unflag/")
                val followLink =
                    followFlag.attr("href").let { it.substring(it.indexOf("/follow/") + 8) }

                Log.i(TAG, "getVideoPageDetail: Result(title=$title, author=$authorId)")
                Log.i(TAG, "getVideoPageDetail: Like: $isLike LikeAPI: $likeLink")
                Log.i(TAG, "getVideoPageDetail: Follow: $isFollow FollowAPI: $followLink")

                Response.success(
                    VideoDetail(
                        id = videoId,
                        title = title,
                        likes = likes,
                        watchs = watchs,
                        postDate = postDate,
                        description = description,
                        authorPic = authorPic,
                        authorName = authorId,
                        videoLinks = VideoLink(),// 稍后再用Retrofit获取
                        moreVideo = moreVideo,

                        isLike = isLike,
                        likeLink = likeLink,

                        follow = isFollow,
                        followLink = followLink
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                Log.i(TAG, "getVideoPageDetail: Failed to load video detail")
                Response.failed(exception.javaClass.name)
            }
        }

    suspend fun like(session: Session, like: Boolean, likeLink: String): Response<LikeResponse> =
        withContext(Dispatchers.IO) {
            try {
                okHttpClient.getCookie().init(session)

                val request = Request.Builder()
                    .url("https://ecchi.iwara.tv/flag/${if (like) "flag" else "unflag"}/like/$likeLink")
                    .post(FormBody.Builder().add("js", "true").build())
                    .build()
                val response = okHttpClient.newCall(request).await()
                require(response.isSuccessful)
                val likeResponse = gson.fromJson(
                    response.body?.string() ?: error("empty response"),
                    LikeResponse::class.java
                )
                Response.success(likeResponse)
            } catch (e: Exception) {
                e.printStackTrace()
                Response.failed(e.javaClass.name)
            }
        }

    suspend fun follow(
        session: Session,
        follow: Boolean,
        followLink: String
    ): Response<FollowResponse> = withContext(Dispatchers.IO) {
        try {
            okHttpClient.getCookie().init(session)

            val request = Request.Builder()
                .url("https://ecchi.iwara.tv/flag/${if (follow) "flag" else "unflag"}/follow/$followLink")
                .post(FormBody.Builder().add("js", "true").build())
                .build()
            val response = okHttpClient.newCall(request).await()
            require(response.isSuccessful)
            val followResponse = gson.fromJson(
                response.body?.string() ?: error("empty response"),
                FollowResponse::class.java
            )
            Response.success(followResponse)
        } catch (e: Exception) {
            e.printStackTrace()
            Response.failed(e.javaClass.name)
        }
    }

    suspend fun getCommentList(
        session: Session,
        mediaType: MediaType,
        mediaId: String,
        page: Int
    ): Response<CommentList> = withContext(Dispatchers.IO) {
        try {
            okHttpClient.getCookie().init(session)

            Log.i(TAG, "getCommentList: Loading comments of: $mediaId (${mediaType.value})")
            
            val request = Request.Builder()
                .url("https://ecchi.iwara.tv/${mediaType.value}/$mediaId?page=$page")
                .get()
                .build()
            val response = okHttpClient.newCall(request).await()
            val body = Jsoup.parse(response.body?.string() ?: error("empty body")).body()
            val commentDocu = body.select("div[id=comments]").first()

            // ###########################################################################
            // 内部函数，用于递归解析评论
            fun parseAsComments(document: Element): List<Comment> {
                val commentList = ArrayList<Comment>()
                for (docu in document.children()) {
                    // 此条为评论
                    if (docu.`is`("div[class~=^comment .+\$]")) {
                        val authorId = docu.select("a[class=username]").first().text()
                        val authorPic = "https:" + docu.select("div[class=user-picture]").first().select("img").first().attr("src")
                        val posterTypeValue = docu.attr("class")
                        var posterType = CommentPosterType.NORMAL
                        if(posterTypeValue.contains("by-node-author")){
                            posterType = CommentPosterType.OWNER
                        }
                        if(posterTypeValue.contains("by-viewer")){
                            posterType = CommentPosterType.SELF
                        }
                        val content = docu.select("div[class=content]").first().text()
                        val date = docu.select("div[class=submitted]").first().ownText()

                        val comment = Comment(
                            authorId = authorId,
                            authorPic = authorPic,
                            posterType= posterType,
                            content = content,
                            date = date,
                            reply = emptyList()
                        )

                        // 有回复
                        if (docu.nextElementSibling() != null && docu.nextElementSibling().`is`("div[class=indented]")) {
                            val reply = docu.nextElementSibling()
                            // 递归解析
                            comment.reply = parseAsComments(reply)
                        }

                        commentList.add(comment)
                    }
                }
                return commentList
            }
            // ###########################################################################
            // (用JSOUP解析网页真痛苦)

            val total = (commentDocu.select("h2[class=title]").first()?.text() ?: " 0 评论 ").trim().split(" ")[0].toInt()
            val lastPage = commentDocu.select("li[class~=^pager-.+\$]").last()?.text()?.toInt() ?: 1
            val hasNext = lastPage != (page + 1)
            val comments = parseAsComments(commentDocu)

            Log.i(TAG, "getCommentList: Comment Result(total: $total, last: $lastPage, hasNext: $hasNext)")

            Response.success(
                CommentList(
                    total = total,
                    page = page,
                    hasNext = hasNext,
                    comments = comments
                )
            )
        } catch (ex: Exception) {
            ex.printStackTrace()
            Response.failed(ex.javaClass.name)
        }
    }

    suspend fun getMediaList(session: Session, mediaType: MediaType, page: Int, sort: SortType, filter: List<String>): Response<MediaList> = withContext(Dispatchers.IO){
        try {
            Log.i(TAG, "getMediaList: Start loading media list (type:${mediaType.value}, page: $page, sort: $sort)")
            okHttpClient.getCookie().init(session)

            fun collectFilters(): String {
                var index = 0
                return filter.joinToString(separator = "&") {
                    "f[${index++}]=$it"
                }
            }
            val filters = collectFilters()

            val request = Request.Builder()
                .url("https://ecchi.iwara.tv/${mediaType.value}?page=$page&sort=${sort.value}" + if(filter.isNotEmpty()) "&${filters}" else "")
                .get()
                .build()
            val response = okHttpClient.newCall(request).await()
            require(response.isSuccessful)

            val body = Jsoup.parse(response.body?.string() ?: error("empty body")).body()
            val elements = body.select("div[id~=^node-[A-Za-z0-9]+\$]")

            val previewList: List<MediaPreview> = elements?.map {
                val title = it.getElementsByClass("title").text()
                val author = it.getElementsByClass("username").text()
                val pic =
                    "https:" + it.getElementsByClass("field-item even")[0].child(0).child(0)
                        .attr("src")
                val likes = it.getElementsByClass("right-icon").text()
                val watchs = it.getElementsByClass("left-icon").text()
                val link = it.getElementsByClass("field-item even")[0].child(0).attr("href")
                val mediaId = link.substring(link.lastIndexOf("/") + 1)
                val type = if (link.startsWith("/video")) MediaType.VIDEO else MediaType.IMAGE

                MediaPreview(
                    title = title,
                    author = author,
                    previewPic = pic,
                    likes = likes,
                    watchs = watchs,
                    mediaId = mediaId,
                    type = type
                )
            } ?: error("empty elements")

            val hasNextPage = body.select("ul[class=pager]").first().select("li[class=pager-next]").any()

            Response.success(
                MediaList(
                    currentPage = page,
                    hasNext = hasNextPage,
                    mediaList = previewList
                )
            )
        }catch (e: Exception){
            e.printStackTrace()
            Response.failed(e.javaClass.name)
        }
    }
}