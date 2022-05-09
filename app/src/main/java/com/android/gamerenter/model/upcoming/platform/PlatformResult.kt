package com.android.gamerenter.model.upcoming.platform

import com.google.gson.annotations.SerializedName

class PlatformResult(

    @SerializedName("id")
    val id: Int,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("name")
    val name: String
) {
}