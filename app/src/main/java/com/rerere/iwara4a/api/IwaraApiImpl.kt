package com.rerere.iwara4a.api

import com.rerere.iwara4a.api.service.IwaraParser
import com.rerere.iwara4a.api.service.IwaraService
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
import com.rerere.iwara4a.util.autoRetry

/**
 * IwaraAPI接口的具体实现
 *
 * 内部持有 iwaraParser 和 iwaraService 两个模块, 根据资源是否可以
 * 通过restful api直接访问来选择使用哪个模块获取数据
 */
class IwaraApiImpl(
    private val iwaraParser: IwaraParser,
    private val iwaraService: IwaraService
) : IwaraApi {
    override suspend fun login(username: String, password: String): Response<Session> =
        autoRetry { iwaraParser.login(username, password) }

    override suspend fun getSelf(session: Session): Response<Self> =
        autoRetry { iwaraParser.getSelf(session) }

    override suspend fun getSubscriptionList(
        session: Session,
        page: Int
    ): Response<SubscriptionList> = autoRetry { iwaraParser.getSubscriptionList(session, page) }

    override suspend fun getImagePageDetail(
        session: Session,
        imageId: String
    ): Response<ImageDetail> = autoRetry { iwaraParser.getImagePageDetail(session, imageId) }

    override suspend fun getVideoPageDetail(
        session: Session,
        videoId: String
    ): Response<VideoDetail> {
        val response = autoRetry { iwaraParser.getVideoPageDetail(session, videoId) }
        return if (response.isSuccess()) {
            val link = try {
                iwaraService.getVideoInfo(videoId = videoId)
            } catch (ex: Exception) {
                return Response.failed(ex.javaClass.name)
            }
            response.read().videoLinks = link
            response
        } else {
            response
        }
    }

    override suspend fun like(
        session: Session,
        like: Boolean,
        likeLink: String
    ): Response<LikeResponse> = iwaraParser.like(session, like, likeLink)

    override suspend fun follow(
        session: Session,
        follow: Boolean,
        followLink: String
    ): Response<FollowResponse> = iwaraParser.follow(session, follow, followLink)

    override suspend fun getCommentList(
        session: Session,
        mediaType: MediaType,
        mediaId: String,
        page: Int
    ): Response<CommentList> = autoRetry {
        iwaraParser.getCommentList(
            session,
            mediaType,
            mediaId,
            page
        )
    }

    override suspend fun getMediaList(
        session: Session,
        mediaType: MediaType,
        page: Int,
        sort: SortType,
        filter: List<String>
    ): Response<MediaList> = autoRetry {
        iwaraParser.getMediaList(
            session,
            mediaType,
            page,
            sort,
            filter
        )
    }

    override suspend fun getUser(session: Session, userId: String): Response<UserData> = autoRetry {
        iwaraParser.getUser(
            session,
            userId
        )
    }

    override suspend fun search(
        session: Session,
        query: String,
        page: Int,
        sort: SortType,
        filter: List<String>
    ): Response<MediaList> = autoRetry {
        iwaraParser.search(
            session,
            query,
            page,
            sort,
            filter
        )
    }
}