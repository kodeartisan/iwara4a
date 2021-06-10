package com.rerere.iwara4a.api

import androidx.annotation.IntRange
import com.rerere.iwara4a.model.comment.CommentList
import com.rerere.iwara4a.model.detail.image.ImageDetail
import com.rerere.iwara4a.model.detail.video.VideoDetail
import com.rerere.iwara4a.model.flag.FollowResponse
import com.rerere.iwara4a.model.flag.LikeResponse
import com.rerere.iwara4a.model.index.MediaList
import com.rerere.iwara4a.model.index.MediaType
import com.rerere.iwara4a.model.index.SortType
import com.rerere.iwara4a.model.index.SubscriptionList
import com.rerere.iwara4a.model.session.Session
import com.rerere.iwara4a.model.user.Self
import com.rerere.iwara4a.model.user.UserData

/**
 * 提供远程资源API, 通过连接IWARA来获取数据
 */
interface IwaraApi {
    /**
     * 尝试登录Iwara
     *
     * @param username 登录用户名
     * @param password 登录密码
     * @return session cookie
     */
    suspend fun login(username: String, password: String): Response<Session>

    /**
     * 获取基础的个人信息
     *
     * @param session 登录凭据
     * @return 简短的个人信息
     */
    suspend fun getSelf(session: Session): Response<Self>

    /**
     * 获取订阅列表
     *
     * @param session 登录凭据
     * @param page 页数
     * @return 订阅列表
     */
    suspend fun getSubscriptionList(session: Session, @IntRange(from = 0) page: Int): Response<SubscriptionList>

    /**
     * 获取图片页面信息
     *
     * @param session 登录凭据
     * @param imageId 图片ID
     * @return 图片页面信息
     */
    suspend fun getImagePageDetail(session: Session, imageId: String): Response<ImageDetail>

    /**
     * 获取视频页面信息
     *
     * @param session 登录凭据
     * @param videoId 视频ID
     * @return 视频页面信息
     */
    suspend fun getVideoPageDetail(session: Session, videoId: String): Response<VideoDetail>

    /**
     * 喜欢某个视频/图片
     *
     */
    suspend fun like(session: Session, like: Boolean, likeLink: String): Response<LikeResponse>

    /**
     * 关注某人
     */
    suspend fun follow(session: Session, follow: Boolean, followLink: String): Response<FollowResponse>

    /**
     * 解析评论列表
     *
     * @param session 登录凭据
     * @param mediaType 媒体类型
     * @param mediaId 媒体ID
     * @param page 评论页数
     *
     * @return 评论列表
     */
    suspend fun getCommentList(session: Session, mediaType: MediaType, mediaId: String, @IntRange(from = 0) page: Int): Response<CommentList>

    /**
     * 获取资源列表
     *
     * @param session 登录凭据
     * @param mediaType 媒体类型
     * @param page 页数
     * @param sort 排序条件
     * @param filter 过滤器
     *
     * @return 资源列表
     */
    suspend fun getMediaList(session: Session, mediaType: MediaType, @IntRange(from = 0) page: Int, sort: SortType, filter: List<String>): Response<MediaList>

    /**
     * 加载用户资料
     *
     * @param session 登录凭据
     * @param userId 用户ID
     * @return 用户数据
     */
    suspend fun getUser(session: Session, userId: String): Response<UserData>

    /**
     * 搜索视频和图片
     *
     * @param session 登录凭据
     * @param query 搜索关键词
     * @param page 页数
     * @param sort 排序条件
     * @param filter 过滤条件
     * @return 资源列表
     */
    suspend fun search(session: Session, query: String, @IntRange(from = 0) page: Int, sort: SortType, filter: List<String>): Response<MediaList>
}