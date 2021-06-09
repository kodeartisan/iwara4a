package com.rerere.iwara4a.util

import android.content.Context
import android.content.Intent
import com.rerere.iwara4a.model.index.MediaType

fun shareMedia(context: Context, mediaType: MediaType, mediaId: String){
    val sendIntent: Intent = Intent().apply {
        action = Intent.ACTION_SEND
        putExtra(Intent.EXTRA_TEXT, "https://ecchi.iwara.tv/${mediaType.value}/$mediaId")
        type = "text/plain"
    }

    val shareIntent = Intent.createChooser(sendIntent, null)
    context.startActivity(shareIntent)
}