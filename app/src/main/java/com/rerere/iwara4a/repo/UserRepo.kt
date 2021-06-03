package com.rerere.iwara4a.repo

import com.rerere.iwara4a.api.IwaraApi
import javax.inject.Inject

class UserRepo @Inject constructor(
    private val iwaraApi: IwaraApi
) {

}