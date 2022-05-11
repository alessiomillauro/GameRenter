package com.android.gamerenter.model

import java.io.Serializable

data class VideogameModel(
    val id: Int? = -1,
    val name: String? = "",
    val dateOfRelease: String? = "",
    val platforms: List<Int>? = emptyList(),
    val playtime: Int? = -1,
// TODO aggiornare a company model
    val companies: List<Any>? = emptyList(),
    val background_image: String? = "",
    val poster_image: String? = "",
    val rating: Float? =-1F,
    val rating_top: Int? = -1,
    val genre: List<String>? = emptyList()
) : Serializable