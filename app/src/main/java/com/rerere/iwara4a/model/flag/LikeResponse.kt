package com.rerere.iwara4a.model.flag


import com.google.gson.annotations.SerializedName

data class LikeResponse(
    @SerializedName("contentId")
    val contentId: String,
    @SerializedName("entityType")
    val entityType: String,
    @SerializedName("flagName")
    val flagName: String,
    @SerializedName("flagStatus")
    val flagStatus: String,
    @SerializedName("flagSuccess")
    val flagSuccess: Boolean,
    @SerializedName("newLink")
    val newLink: String,
    @SerializedName("status")
    val status: Boolean
)