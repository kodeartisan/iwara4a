package com.rerere.iwara4a.model.detail.video


import com.google.gson.annotations.SerializedName

class VideoLink : ArrayList<VideoLinkItem>(){
}

data class VideoLinkItem(
    @SerializedName("mime")
    val mime: String,
    @SerializedName("resolution")
    val resolution: String,
    @SerializedName("uri")
    val uri: String
){
    fun toLink() = "https:" + unescapeJava(uri).replace("\\/","/")
}

fun unescapeJava(escaped: String): String {
    var escaped = escaped
    if (escaped.indexOf("\\u") == -1) return escaped
    var processed = ""
    var position = escaped.indexOf("\\u")
    while (position != -1) {
        if (position != 0) processed += escaped.substring(0, position)
        val token = escaped.substring(position + 2, position + 6)
        escaped = escaped.substring(position + 6)
        processed += token.toInt(16).toChar()
        position = escaped.indexOf("\\u")
    }
    processed += escaped
    return processed
}