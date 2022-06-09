package com.android.gamerenter.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.ProgressBar
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.gamerenter.R
import com.android.gamerenter.model.VideogameModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.RequestOptions
import com.bumptech.glide.request.target.Target
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.storage.FirebaseStorage
import java.text.SimpleDateFormat
import java.time.Instant
import java.time.ZoneId
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RentedVideogamesAdapter @Inject constructor(
    private val storageRef: FirebaseStorage
) :
    ListAdapter<VideogameModel, RentedVideogamesAdapter.RentedVideogameViewHolder>(
        DIFF_RENTED_VIDEOGAME_CALLBACK()
    ) {

    private var onItemListClickListener: ((VideogameModel) -> Unit)? = null
    fun setOnItemListClickListener(listener: (VideogameModel) -> Unit) {
        onItemListClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RentedVideogameViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.adapter_rented_videogame, parent, false)
        return RentedVideogameViewHolder(view)
    }

    override fun onBindViewHolder(holder: RentedVideogameViewHolder, position: Int) {
        val mostPopularResult = getItem(position)
        holder.bind(mostPopularResult)
    }

    fun loadImage(
        itemView: View,
        urlSource: String,
        img: ImageView,
        progress: ProgressBar,
        placeholder: Int = -1
    ) {

        Glide.with(itemView.context)
            .load(if (urlSource.isNullOrEmpty()) placeholder else urlSource)
            .apply(RequestOptions().override(600, 400))
            .listener(object : RequestListener<Drawable> {
                override fun onLoadFailed(
                    e: GlideException?,
                    model: Any?,
                    target: Target<Drawable>?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress.visibility = INVISIBLE
                    return false
                }

                override fun onResourceReady(
                    resource: Drawable?,
                    model: Any?,
                    target: Target<Drawable>?,
                    dataSource: DataSource?,
                    isFirstResource: Boolean
                ): Boolean {
                    progress.visibility = INVISIBLE
                    return false
                }

            })
            .into(img)
    }

    inner class RentedVideogameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val countdownContainer = itemView.findViewById<LinearLayout>(R.id.countdown)
        val countdownValue = itemView.findViewById<TextView>(R.id.countdown_value)
        val img = itemView.findViewById<ImageView>(R.id.videogame_image_background)
        val progress = itemView.findViewById<ProgressBar>(R.id.progress)
        val title = itemView.findViewById<MaterialTextView>(R.id.videogame_title)

        fun bind(videogameModel: VideogameModel) {

            itemView.setOnClickListener {
                onItemListClickListener?.let {
                    it(videogameModel)
                }
            }

            var diff = videogameModel.rent_end - videogameModel.rent_on

            val days = TimeUnit.MILLISECONDS.toDays(diff);
            diff -= TimeUnit.DAYS.toMillis(days);

            val hours = TimeUnit.MILLISECONDS.toHours(diff);
            diff -= TimeUnit.HOURS.toMillis(hours);

            val minutes = TimeUnit.MILLISECONDS.toMinutes(diff);
            diff -= TimeUnit.MINUTES.toMillis(minutes);

            /*
            val seconds = diff / 1000;
            val minutes = seconds / 60;
            val hours = minutes / 60;
            val days = hours / 24;

             */
            val time = "$days d : ${hours % 24} h : ${minutes % 60} m"

            countdownValue.text = time

            if (videogameModel.background_image.isNotBlank()) {
                val ref =
                    storageRef.getReferenceFromUrl(videogameModel.background_image)
                ref.downloadUrl.addOnCompleteListener {
                    loadImage(
                        itemView,
                        it.result.toString(),
                        img,
                        progress,
                        R.drawable.videogame_placeholder
                    )
                }
            }

            title.text = videogameModel.name
        }
    }
}

class DIFF_RENTED_VIDEOGAME_CALLBACK : DiffUtil.ItemCallback<VideogameModel>() {
    override fun areItemsTheSame(
        oldItem: VideogameModel,
        newItem: VideogameModel
    ): Boolean {
        return oldItem == newItem
    }

    override fun areContentsTheSame(
        oldItem: VideogameModel,
        newItem: VideogameModel
    ): Boolean {
        return oldItem.id == newItem.id
    }
}