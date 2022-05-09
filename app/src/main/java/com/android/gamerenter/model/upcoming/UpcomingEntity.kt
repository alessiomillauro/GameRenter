package com.android.gamerenter.model.upcoming

import com.google.gson.annotations.SerializedName

class UpcomingEntity(
    @SerializedName("count")
    val count: Int,
    @SerializedName("next")
    val next: String,
    @SerializedName("previous")
    val previous: String,
    @SerializedName("results")
    val results: List<UpcomingResults>
) {
}