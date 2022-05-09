package com.android.gamerenter.model.upcoming

import com.google.gson.annotations.SerializedName

class GenreEntity(
    @SerializedName("id")
    val id: Int,
    @SerializedName("slug")
    val slug: String,
    @SerializedName("name")
    val name: String
)
