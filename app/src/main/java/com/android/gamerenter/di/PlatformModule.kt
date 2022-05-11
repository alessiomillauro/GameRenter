package com.android.gamerenter.di

import com.android.gamerenter.FIRESTORE_COLLECTION_PLATFORM
import com.android.gamerenter.FIRESTORE_COLLECTION_UPCOMING
import com.android.gamerenter.GameRenterApplication
import com.google.firebase.firestore.FirebaseFirestore
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
}