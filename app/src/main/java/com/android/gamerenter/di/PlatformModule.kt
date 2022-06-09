package com.android.gamerenter.di

import com.android.gamerenter.*
import com.android.gamerenter.fragment.DashboardFragment
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Named
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object PlatformModule {

    @Provides
    @Singleton
    fun provideFirebaseStorage() =
        FirebaseStorage.getInstance()

    @Provides
    @Singleton
    fun provideFirebaseFirestore() = FirebaseFirestore.getInstance()

    @Provides
    @Singleton
    @Named("platform_collection")
    fun providePlatformCollectionReference(rootRef: FirebaseFirestore) = rootRef.collection(
        FIRESTORE_COLLECTION_PLATFORM
    )

    @Provides
    @Singleton
    @Named("upcoming_games_collection")
    fun provideUpcomingCollectionReference(rootRef: FirebaseFirestore) = rootRef.collection(
        FIRESTORE_COLLECTION_UPCOMING
    )

    @Provides
    @Singleton
    @Named("recent_search_collection")
    fun provideRecentSearchCollectionReference(rootRef: FirebaseFirestore) = rootRef.collection(
        FIRESTORE_COLLECTION_RECENT_SEARCH
    )

    @Provides
    @Singleton
    @Named("all_search_collection")
    fun provideAllSearchCollectionReference(rootRef: FirebaseFirestore) = rootRef.collection(
        FIRESTORE_COLLECTION_ALL
    )

    @Provides
    @Singleton
    @Named("rented_games_collection")
    fun provideRentedCollectionReference(rootRef: FirebaseFirestore) = rootRef.collection(
        FIRESTORE_COLLECTION_RENTED
    )

    @Provides
    @Singleton
    @Named("genre_games_collection")
    fun provideGenreCollectionReference(rootRef: FirebaseFirestore) = rootRef.collection(
        FIRESTORE_COLLECTION_GENRE
    )

    @Provides
    @Singleton
    @Named("publisher_games_collection")
    fun providePublisherCollectionReference(rootRef: FirebaseFirestore) = rootRef.collection(
        FIRESTORE_COLLECTION_PUBLISHER
    )
}