package com.rerere.iwara4a.api.service

import android.util.Log
import com.rerere.iwara4a.api.Response
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
                    .add("op" ,"ログイン")
                    .build()
                val loginRequest = Request.Builder()
                    .url("https://ecchi.iwara.tv/user/login?destination=front")
                    .post(formBody)
                    .build()
                val loginResponse = okHttpClient.newCall(loginRequest).await()

                if(loginResponse.isSuccessful){
                    val cookies = okHttpClient.getCookie().filter { it.domain == "iwara.tv" }
                    if(cookies.isNotEmpty()) {
                        val cookie = cookies.first()
                        Response.success(Session(cookie.name, cookie.value))
                    } else {
                        Response.failed("no cookie returned")
                    }
                }else{
                    Response.failed("http code: ${loginResponse.code}")
                }
            } catch (exception: Exception) {
                exception.printStackTrace()
                Response.failed(exception.javaClass.name)
            }
        }

    suspend fun getSelf(session: Session): Response<Self> = withContext(Dispatchers.IO){
        try {
            Log.i(TAG, "getSelf: Start...")
            okHttpClient.getCookie().init(session)

            val request = Request.Builder()
                .url("https://ecchi.iwara.tv/user")
                .get()
                .build()
            val response = okHttpClient.newCall(request).await()
            val body = Jsoup.parse(response.body?.string() ?: error("null body")).body()

            val nickname = body.getElementsByClass("views-field views-field-name").first().text()
            val profilePic = "http:" + body.getElementsByClass("views-field views-field-picture")
                .first()
                .child(0)
                .child(0)
                .attr("src")

            Log.i(TAG, "getSelf: (nickname=$nickname, profilePic=$profilePic)")

            Response.success(Self(
                nickname = nickname,
                profilePic = profilePic
            ))
        }catch (exception: Exception){
            exception.printStackTrace()
            Response.failed(exception.javaClass.name)
        }
    }
}