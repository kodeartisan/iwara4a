package com.rerere.iwara4a.util.okhttp

import kotlinx.coroutines.suspendCancellableCoroutine
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Response
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

suspend fun Call.await(): Response {
    return suspendCancellableCoroutine {
        enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                if(it.isCancelled) return
                it.resumeWithException(e)
            }

            override fun onResponse(call: Call, response: Response) {
                it.resume(response)
            }
        })

        it.invokeOnCancellation {
            try {
                cancel()
            } catch (e: Exception){
                println("===== CANCEL ======")
                // IGNORE
            }
        }
    }
}