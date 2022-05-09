package com.android.gamerenter.model.upcoming

import com.android.gamerenter.model.upcoming.platform.PlatformEntity
import com.google.gson.annotations.SerializedName

class UpcomingResults(
    @SerializedName("id")
    val id: Int,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("name")
    val name: String,
    @SerializedName("released")
    val released: String?,
    @SerializedName("tba")
    val tba: Boolean,
    @SerializedName("background_image")
    val background_image: String?,
    @SerializedName("rating")
    val rating: Float,
    @SerializedName("rating_top")
    val rating_top: Int,
    @SerializedName("ratings")
    val ratings: Any,
    @SerializedName("ratings_count")
    val ratings_count: Int,
    @SerializedName("reviews_text_count")
    val reviews_text_count: String,
    @SerializedName("added")
    val added: Int,
    @SerializedName("added_by_status")
    val added_by_status: Any,
    @SerializedName("metacritic")
    val metacritic: Int,
    @SerializedName("playtime")
    val playtime: Int,
    @SerializedName("suggestion_count")
    val suggestion_count: Int,
    @SerializedName("updated")
    val updated: String,
    @SerializedName("esrb_rating")
    val esrb_rating: Any,
    @SerializedName("platforms")
    val platforms: List<PlatformEntity>,
    @SerializedName("genres")
    val genres : List<GenreEntity>



    ) {
}