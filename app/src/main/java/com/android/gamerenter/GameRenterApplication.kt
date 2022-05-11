package com.android.gamerenter

import android.app.Application
import androidx.annotation.NonNull
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class GameRenterApplication : Application()