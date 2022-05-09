package com.android.gamerenter.model.platform

import com.android.gamerenter.model.upcoming.UpcomingResults
import com.google.gson.annotations.SerializedName

class PlatformResult(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: String,
    @SerializedName("previous")
    val previous: String,
    @SerializedName("results")
    val results: List<PlatformEntity>
) {
}