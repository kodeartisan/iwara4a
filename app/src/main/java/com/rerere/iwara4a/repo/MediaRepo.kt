package com.rerere.iwara4a.repo

import androidx.annotation.IntRange
import com.rerere.iwara4a.api.IwaraApi
import com.rerere.iwara4a.api.Response
import com.rerere.iwara4a.model.index.SubscriptionList
import com.rerere.iwara4a.model.session.Session
import javax.inject.Inject

class MediaRepo @Inject constructor(
    private val iwaraApi: IwaraApi
) {
    suspend fun getSubscriptionList(
        session: Session,
        @IntRange(from = 0) page: Int
    ): Response<SubscriptionList> = iwaraApi.getSubscriptionList(session, page)

    suspend fun getImageDetail(session: Session, imageId: String) =
        iwaraApi.getImagePageDetail(session, imageId)

    suspend fun getVideoDetail(session: Session, videoId: String) =
        iwaraApi.getVideoPageDetail(session, videoId)

    suspend fun like(session: Session, like: Boolean, link: String) =
        iwaraApi.like(session, like, link)

    suspend fun follow(session: Session, follow: Boolean, link: String) = iwaraApi.follow(session, follow, link)
}