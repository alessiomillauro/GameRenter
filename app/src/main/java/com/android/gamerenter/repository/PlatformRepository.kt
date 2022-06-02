package com.android.gamerenter.repository

import com.android.gamerenter.model.PlatformModel
import com.android.gamerenter.model.VideogameModel
import com.google.firebase.firestore.CollectionReference
import dagger.Binds
import kotlinx.coroutines.tasks.await
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton

@Singleton
class PlatformRepository @Inject constructor(
    @Named("platform_collection") private val platformRef: CollectionReference,
    @Named("upcoming_games_collection") private val upcomingRef: CollectionReference,
    @Named("recent_search_collection") private val recentSearchRef: CollectionReference,
    @Named("all_search_collection") private val allSearchRef: CollectionReference,
    @Named("rented_games_collection") private val rentedRef: CollectionReference,
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

    suspend fun getRentedVideogameList(): MutableList<VideogameModel> {
        val rentedVideogameList = mutableListOf<VideogameModel>()
        val rentedVideogame = recentSearchRef.get().await()
        for (item in rentedVideogame) {
            item.toObject(VideogameModel::class.java).let {
                rentedVideogameList.add(it)
            }
        }
        return rentedVideogameList
    }

    suspend fun getRecentSearchVideogameList(): MutableList<VideogameModel> {
        val recentSearchVideogameList = mutableListOf<VideogameModel>()
        val recentSearchVideogames = recentSearchRef.get().await()
        for (item in recentSearchVideogames) {
            item.toObject(VideogameModel::class.java).let {
                recentSearchVideogameList.add(it)
            }
        }
        return recentSearchVideogameList
    }

    suspend fun getAllVideogames(): MutableList<VideogameModel> {
        val recentSearchVideogameList = mutableListOf<VideogameModel>()
        val recentSearchVideogames = allSearchRef.limitToLast(20)
            .get()
            .await()
        for (item in recentSearchVideogames) {
            item.toObject(VideogameModel::class.java).let {
                recentSearchVideogameList.add(it)
            }
        }
        return recentSearchVideogameList
    }

    suspend fun updateRentOperation(id: Int, periodRent: Int, timestamp: Long): Boolean {
        var result = false
        var endPeriod = timestamp + TimeUnit.DAYS.toMillis(periodRent.toLong())
        rentedRef.document(id.toString()).update(
            "rented_on", timestamp,
            "rented_end", endPeriod
        ).addOnCompleteListener { result = true }
            .addOnFailureListener { result = false }

        return result
    }
}