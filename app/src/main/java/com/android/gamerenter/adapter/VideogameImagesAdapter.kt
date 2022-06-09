package com.android.gamerenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.gamerenter.R
import com.bumptech.glide.Glide
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class VideogameImagesAdapter @Inject constructor(private val storageRef: FirebaseStorage) :
    ListAdapter<String, VideogameImagesAdapter.VideogameImagesViewHolder>(
        DIFF_VIDEOGAME_IMAGE_CALLBACK()
    ) {

    private lateinit var imagesList: List<String>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideogameImagesViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.adapter_videogame_image, parent, false)
        return VideogameImagesViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideogameImagesViewHolder, position: Int) {
        val mostPopularResult = getItem(position)
        holder.bind(mostPopularResult)
    }

    fun loadImage(
        itemView: View,
        urlSource: String,
        img: ImageView
    ) {
        Glide.with(itemView.context)
            .load(urlSource)
            .into(img)
    }

    inner class VideogameImagesViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val img: ImageView = itemView.findViewById(R.id.screenshot)

        fun bind(imageLink: String) {
            val ref =
                storageRef.getReferenceFromUrl(imageLink)
            ref.downloadUrl.addOnCompleteListener {
                loadImage(
                    itemView,
                    it.result.toString(),
                    img
                )
            }
        }
    }

    class DIFF_VIDEOGAME_IMAGE_CALLBACK : DiffUtil.ItemCallback<String>() {
        override fun areItemsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }

        override fun areContentsTheSame(
            oldItem: String,
            newItem: String
        ): Boolean {
            return oldItem == newItem
        }
    }
}