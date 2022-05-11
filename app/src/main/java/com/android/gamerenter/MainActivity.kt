package com.android.gamerenter

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.AsyncTask
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.ContactsContract.CommonDataKinds.Website.URL
import android.util.Log
import android.widget.TextView
import com.android.gamerenter.model.PlatformModel
import com.android.gamerenter.model.VideogameModel
import com.android.gamerenter.model.platform.PlatformResult
import com.android.gamerenter.model.upcoming.GenreEntity
import com.android.gamerenter.model.upcoming.UpcomingEntity
import com.android.gamerenter.model.upcoming.UpcomingResults
import com.android.gamerenter.model.upcoming.platform.PlatformEntity
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.Response
import com.android.volley.VolleyError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseApp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import org.json.JSONObject
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.net.URL

class MainActivity : AppCompatActivity() {

    private lateinit var title: TextView

    private lateinit var db: FirebaseFirestore
    private lateinit var storage: FirebaseStorage

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        FirebaseApp.initializeApp(this)
        db = FirebaseFirestore.getInstance()
        storage = Firebase.storage("gs://gamerenter-6fa4b.appspot.com/")

        title = findViewById(R.id.title)

        val requestQueue: RequestQueue = Volley.newRequestQueue(this)

        requestVideogames(requestQueue)
    }

    // page 1740 is the last one
    fun requestVideogames(
        requestQueue: RequestQueue,
        url: String? = "https://api.rawg.io/api/games?key=60c2cac4b2404640bbd566c13ac10774&dates=2022-06-10,2023-12-31&page=1&page_size=40"
    ) {
        val request = JsonObjectRequest(Request.Method.GET, url, null, {
            val gson = Gson()
            val result = gson.fromJson(it.toString(), UpcomingEntity::class.java)
            title.text = it.get("results").toString()

            insertInDb(result.results)
            //insertPlatform(result.results)

            if (!result.next.isNullOrBlank()) {
                requestVideogames(requestQueue, result.next)
            }
        },
            { Log.d("Error", "error") })

        requestQueue.add(request)
    }

    fun insertPlatform(result: List<com.android.gamerenter.model.platform.PlatformEntity>) {
        for (item in result) {
            val platform = PlatformModel(item.id, item.name, image_bckn = item.image_background)

            db.collection("PLATFORM_GAMES")
                .document(platform.id.toString())
                .set(platform)


        }
    }

    fun insertInDb(list: List<UpcomingResults>) {
        for (result in list) {
            InsertImageStorageTask(this).execute(result)
        }
    }

    fun getPlatforms(item: List<PlatformEntity>?): List<Int> {
        var listPratforms: MutableList<Int> = mutableListOf()
        if (item != null) {
            for (platform in item) {
                listPratforms.add(platform.platform.id)
            }
        }
        return listPratforms
    }

    fun getGenres(item: List<GenreEntity>?): List<String> {
        var listGenres: MutableList<String> = mutableListOf()
        if (item != null) {
            for (genre in item) {
                listGenres.add(genre.name)
            }
        }
        return listGenres
    }

    fun getCompanies(item: List<PlatformEntity>?): List<String> {
        var listPratforms: MutableList<String> = mutableListOf()
        if (item != null) {
            for (platform in item) {
                listPratforms.add(platform.platform.name)
            }
        }
        return listPratforms
    }

    fun insertValueImage(result: UpcomingResults, res: ByteArray) {
        var link = ""
        val uploadTask = storage.getReference("GAMES")
            .child("${result.id}")
            .child("${result.id}_image_background")
            .putBytes(res)
        uploadTask.addOnSuccessListener {
            link =
                "gs://gamerenter-6fa4b.appspot.com/GAMES/${result.id}/${result.id}_image_background"

            val upcoming = VideogameModel(
                result.id,
                result.name,
                result.released ?: "",
                getPlatforms(result.platforms),
                result.playtime,
                // TODO update companies info
                listOf(),
                link ?: "",
                "",
                result.rating,
                result.rating_top,
                getGenres(result.genres)
            )

            db.collection("UPCOMING_GAMES")
                .document(upcoming.id.toString())
                .set(upcoming)
                .addOnCompleteListener { Log.d("", result.id.toString() + " correctly added") }



            Log.d("", "Loaded")
        }.addOnFailureListener {
            Log.d("", "Error")
        }

        /*
        storage.getReference("GAMES")
            .child("${platformModel.id}")
            .child("${platformModel.id}_image_background")
            .downloadUrl
            .addOnSuccessListener {
                link = it.toString()
            }
         */
    }

    class InsertImageStorageTask(private val activity: MainActivity) :
        AsyncTask<UpcomingResults, Void, Unit>() {
        override fun doInBackground(vararg params: UpcomingResults?) {
            if (!params[0]?.background_image.isNullOrBlank()) {
                val url = URL(params[0]?.background_image)
                val bitmap = BitmapFactory.decodeStream(url.openConnection().getInputStream())

                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos)
                val data = baos.toByteArray()

                activity.insertValueImage(params[0]!!, data)
            }
        }
    }
}

