package com.android.gamerenter.model.platform

import com.google.gson.annotations.SerializedName

class PlatformEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("name")
    val name: String,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("games_count")
    val games_count: Int,
    @SerializedName("image_background")
    val image_background: String,
    @SerializedName("image")
    val image: String,
    @SerializedName("year_start")
    val year_start: Int,
    @SerializedName("year_end")
    val year_end: Int
) {
}