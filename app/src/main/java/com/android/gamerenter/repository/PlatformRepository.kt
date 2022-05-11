package com.android.gamerenter.repository

import com.android.gamerenter.model.PlatformModel
import com.android.gamerenter.model.VideogameModel
import com.google.firebase.firestore.CollectionReference
import dagger.Binds
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PlatformRepository @Inject constructor(
    @Named("platform_collection") private val platformRef: CollectionReference,
    @Named("upcoming_games_collection") private val upcomingRef: CollectionReference
) {

    suspend fun getPlatformsList(): MutableList<PlatformModel> {
        val platformList = mutableListOf<PlatformModel>()
        val platforms = platformRef.get().await()
        for (item in platforms) {
            item.toObject(PlatformModel::class.java).let {
                platformList.add(it)
            }
        }
        return platformList
    }

    suspend fun getUpcomingVideogameList(): MutableList<VideogameModel> {
        val upcomingVideogameList = mutableListOf<VideogameModel>()
        val upcomingVideogames = upcomingRef.get().await()
        for (item in upcomingVideogames) {
            item.toObject(VideogameModel::class.java).let {
                upcomingVideogameList.add(it)
            }
        }
        return upcomingVideogameList
    }
}