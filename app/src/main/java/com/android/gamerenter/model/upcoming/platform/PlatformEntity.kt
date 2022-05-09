package com.android.gamerenter.model.upcoming.platform

import com.google.gson.annotations.SerializedName

class PlatformEntity(

    @SerializedName("platform")
    val platform: PlatformResult,
    @SerializedName("released_at")
    val released_at: String,
    @SerializedName("requirements")
    val requirements: Any
) {
}