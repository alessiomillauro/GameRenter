package com.android.gamerenter.model

import java.io.Serializable
import java.util.*

class VideogameModel(
    val id: Int,
    val name: String,
    val dateOfRelease: String,
    val platforms: List<Int>,
    val playtime: Int,
// TODO aggiornare a company model
    val companies: List<String>,
    val background_image: String,
    val poster_image: String,
    val rating: Float,
    val rating_top: Int,
    val genre: List<String>
) : Serializable {
}