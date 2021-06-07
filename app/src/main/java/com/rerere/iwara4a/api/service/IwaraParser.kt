package com.rerere.iwara4a.api.service

import android.util.Log
import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.model.image.ImageDetail
import com.rerere.iwara4a.model.index.MediaPreview
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.model.index.SubscriptionList
import com.rerere.iwara4a.model.session.Session
import com.rerere.iwara4a.model.user.Self
import com.rerere.iwara4a.model.video.VideoDetail
import com.rerere.iwara4a.model.video.VideoLink
import com.rerere.iwara4a.util.okhttp.await
import com.rerere.iwara4a.util.okhttp.getCookie
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Request
import org.jsoup.Jsoup

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
                val hasNextPage = totalPage != page

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
                        .first().text()
                val authorId = body.getElementsByClass("username").first().text().trim()
                val authorPic =
                    "https:" + body.getElementsByClass("user-picture").first().select("img")
                        .attr("src")

                Log.i(TAG, "getVideoPageDetail: Result(title=$title, author=$authorId)")

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
                        videoLinks = VideoLink()// 稍后再用Retrofit获取
                    )
                )
            } catch (exception: Exception) {
                exception.printStackTrace()
                Log.i(TAG, "getVideoPageDetail: Failed to load video detail")
                Response.failed(exception.javaClass.name)
            }
        }
}