package com.android.gamerenter.repository

import android.graphics.Bitmap
import androidx.lifecycle.MutableLiveData
import com.android.gamerenter.fragment.DashboardFragment
import com.android.gamerenter.model.PlatformModel
import com.android.gamerenter.model.VideogameModel
import com.android.gamerenter.viewmodel.DashboardViewModel
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.firestore.CollectionReference
import com.google.firebase.firestore.DocumentSnapshot
import com.google.firebase.firestore.QueryDocumentSnapshot
import com.google.firebase.firestore.QuerySnapshot
import com.google.firebase.storage.FirebaseStorage
import dagger.Binds
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.tasks.await
import java.io.ByteArrayOutputStream
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Named
import javax.inject.Singleton
import javax.security.auth.callback.Callback

@Singleton
class PlatformRepository @Inject constructor(
    private val storageRef: FirebaseStorage,
    @Named("platform_collection") private val platformRef: CollectionReference,
    @Named("upcoming_games_collection") private val upcomingRef: CollectionReference,
    @Named("recent_search_collection") private val recentSearchRef: CollectionReference,
    @Named("all_search_collection") private val allSearchRef: CollectionReference,
    @Named("rented_games_collection") private val rentedRef: CollectionReference,
    @Named("genre_games_collection") private val genreRef: CollectionReference,
    @Named("publisher_games_collection") private val publishersRef: CollectionReference
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
        val rentedVideogame = rentedRef.get().await()
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

    fun updateRentOperation(id: Int, periodRent: Int, timestamp: Long): Boolean {
        var result = false
        var endPeriod = timestamp + TimeUnit.DAYS.toMillis(periodRent.toLong())
        rentedRef.document(id.toString()).update(
            "rented_on", timestamp,
            "rented_end", endPeriod
        ).addOnCompleteListener { result = true }
            .addOnFailureListener { result = false }

        return result
    }

    suspend fun searchVideogame(query: String): MutableList<VideogameModel> {
        var result = mutableListOf<VideogameModel>()
        val searched = allSearchRef.orderBy("name")
            .startAt(query)
            .endAt(query + "\uf8ff")
            .get()
            .await()
        for (item in searched) {
            result.add(item.toObject(VideogameModel::class.java))
        }
        return result
    }

    suspend fun removeRecentSearchItem(
        id: Int,
        onRemoveItem: DashboardFragment.OnRemoveItem
    ) {
        recentSearchRef.document(id.toString())
            .delete()
            .addOnSuccessListener {
                onRemoveItem.onSuccessRemove()
            }
            .addOnFailureListener {
                onRemoveItem.onFailureRemove()
            }.await()
    }

    suspend fun insertVideogame(
        bitmap: Bitmap,
        item: VideogameModel,
        nameFile: String
    ): Boolean {

        val completableDeferred = CompletableDeferred<Boolean>()

        val baos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val data = baos.toByteArray()

        var link = ""
        val uploadTask = storageRef.getReference("GAMES")
            .child(nameFile)
            .child("${nameFile}_image_background")
            .putBytes(data)

        uploadTask.addOnSuccessListener {
            link =
                "gs://gamerenter-6fa4b.appspot.com/GAMES/${nameFile}/${nameFile}_image_background"
            item.background_image = link
            allSearchRef
                .document(nameFile)
                .set(item)
                .addOnCompleteListener { completableDeferred.complete(it.isSuccessful) }
        }
        return completableDeferred.await()
    }

    suspend fun insertItemInRecentSearch(item: VideogameModel): Boolean {
        val completableDeferred = CompletableDeferred<Boolean>()
        recentSearchRef
            .document(item.id.toString())
            .set(item)
            .addOnCompleteListener {
                completableDeferred.complete(it.isSuccessful)
            }
        return completableDeferred.await()
    }

    suspend fun getGenresList(): MutableList<Map<String, String>> {
        var genreList = mutableListOf<Map<String, String>>()
        val result = genreRef.get().await().documents
        for (data in result) {
            data.data?.forEach { item ->
                val map: Map<String, String> = mapOf(item.key to item.value.toString())
                genreList.add(map)
            }
        }

        return genreList
    }

    suspend fun getPublishersList(): MutableList<Map<String, String>> {
        var publisherList = mutableListOf<Map<String, String>>()
        val result = publishersRef.get().await().documents
        for (data in result) {
            data.data?.forEach { item ->
                val map: Map<String, String> = mapOf(item.key to item.value.toString())
                publisherList.add(map)
            }
        }

        return publisherList
    }
}