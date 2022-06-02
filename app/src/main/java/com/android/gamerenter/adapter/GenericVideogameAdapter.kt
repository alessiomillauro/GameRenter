package com.android.gamerenter.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.INVISIBLE
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
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
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GenericVideogameAdapter @Inject constructor(
    private val storageRef: FirebaseStorage
) :
    ListAdapter<VideogameModel, GenericVideogameAdapter.VideogameViewHolder>(
        DIFF_VIDEOGAME_CALLBACK()
    ) {

    private var onItemListClickListener: ((VideogameModel) -> Unit)? = null
    fun setOnItemListClickListener(listener: (VideogameModel) -> Unit) {
        onItemListClickListener = listener
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideogameViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.adapter_videogame_generic, parent, false)
        return VideogameViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideogameViewHolder, position: Int) {
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

    inner class VideogameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val img = itemView.findViewById<ImageView>(R.id.videogame_image_background)
        val progress = itemView.findViewById<ProgressBar>(R.id.progress)
        val title = itemView.findViewById<MaterialTextView>(R.id.videogame_title)

        fun bind(videogameModel: VideogameModel) {

            itemView.setOnClickListener {
                onItemListClickListener?.let {
                    it(videogameModel)
                }
            }

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

class DIFF_VIDEOGAME_CALLBACK : DiffUtil.ItemCallback<VideogameModel>() {
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