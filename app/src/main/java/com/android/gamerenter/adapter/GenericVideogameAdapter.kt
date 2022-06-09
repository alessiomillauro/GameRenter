package com.android.gamerenter.adapter

import android.graphics.drawable.Drawable
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
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
    RecyclerView.Adapter<GenericVideogameAdapter.VideogameViewHolder>() {

    private var videogames: List<VideogameModel> = ArrayList()

    private var onItemListClickListener: ((VideogameModel) -> Unit)? = null
    fun setOnItemListClickListener(listener: (VideogameModel) -> Unit) {
        onItemListClickListener = listener
    }

    private var onItemRemoveClickListener: ((VideogameModel) -> Unit)? = null
    fun setOnItemRemoveClickListener(listener: (VideogameModel) -> Unit) {
        onItemRemoveClickListener = listener
    }

    val DIFF_VIDEOGAME_CALLBACK = object : DiffUtil.ItemCallback<VideogameModel>() {
        override fun areItemsTheSame(oldItem: VideogameModel, newItem: VideogameModel): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: VideogameModel, newItem: VideogameModel): Boolean {
            return oldItem.equals(newItem)
        }
    }
    private val differ = AsyncListDiffer(this, DIFF_VIDEOGAME_CALLBACK)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideogameViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.adapter_search_recent, parent, false)
        return VideogameViewHolder(view)
    }

    override fun getItemCount(): Int {
        return differ.currentList.size
    }

    override fun onBindViewHolder(holder: VideogameViewHolder, position: Int) {
        val item = differ.currentList[position]
        holder.bind(item, position)
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

    fun submitList(videogames: List<VideogameModel>) {
        this.videogames = videogames
        differ.submitList(videogames)
    }

    inner class VideogameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val img = itemView.findViewById<ImageView>(R.id.videogame_image_background)
        val progress = itemView.findViewById<ProgressBar>(R.id.progress)
        val title = itemView.findViewById<MaterialTextView>(R.id.videogame_title)
        val remove = itemView.findViewById<ImageView>(R.id.remove_action)

        fun bind(videogameModel: VideogameModel, position: Int) {

            itemView.setOnClickListener {
                onItemListClickListener?.let {
                    it(videogameModel)
                }
            }

            remove.setOnClickListener {
                onItemRemoveClickListener?.let {
                    it(videogameModel)
                    videogames.toMutableList().removeAt(position)
                    notifyItemRemoved(position)
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