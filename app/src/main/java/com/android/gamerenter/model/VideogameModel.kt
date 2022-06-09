package com.android.gamerenter.model

import java.io.Serializable
import java.util.*

class VideogameModel(
    var background_image: String = "",
    // TODO aggiornare a company model
    val companies: List<String> = listOf(),
    val dateOfRelease: String = "",
    val genre: List<String> = listOf(),
    val id: Int = -1,
    val images: List<String> = listOf(),
    val metascore: Int = -1,
    val name: String = "",
    val overview: String = "",
    val platforms: List<Int> = listOf(),
    val playtime: Int = -1,
    val poster_image: String = "",
    val rating: Float = -1F,
    val rating_top: Int = -1,
    val rent_copy_available: Int = -1,
    val rent_copy_total: Int = -1,
    val rent_end: Long = -1,
    val rent_max_period: Int = -1,
    val rent_on: Long = -1,
    val rent_status: Int = -1
) : Serializable {
}