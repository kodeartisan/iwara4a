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
    suspend fun getSubscriptionList(session: Session, @IntRange(from = 1) page: Int): Response<SubscriptionList> = iwaraApi.getSubscriptionList(session, page)

    suspend fun getImageDetail(session: Session, imageId: String) = iwaraApi.getImagePageDetail(session, imageId)
}