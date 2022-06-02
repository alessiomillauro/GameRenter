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
import com.android.gamerenter.model.PlatformModel
import com.android.gamerenter.model.VideogameModel
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.storage.FirebaseStorage
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UpcomingVideogameAdapter @Inject constructor(private val storageRef: FirebaseStorage) :
    ListAdapter<VideogameModel, UpcomingVideogameAdapter.UpcomingVideogameViewHolder>(
        DIFF_VIDEOGAME_CALLBACK()
    ) {

    private lateinit var platformList: List<PlatformModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UpcomingVideogameViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.adapter_videogame_upcoming, parent, false)
        return UpcomingVideogameViewHolder(view)
    }

    override fun onBindViewHolder(holder: UpcomingVideogameViewHolder, position: Int) {
        val mostPopularResult = getItem(position)
        holder.bind(mostPopularResult)
    }

    fun updatePlatformList(res: List<PlatformModel>) {
        platformList = res
    }

    fun getPlatformIcon(platformIdRef: Int): String {
        var value = ""
        for (item in platformList) {
            if (platformIdRef == item.id) {
                value = item.image.toString()
            }
        }
        return value
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

    inner class UpcomingVideogameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val img = itemView.findViewById<ImageView>(R.id.videogame_image_background)
        val progress = itemView.findViewById<ProgressBar>(R.id.progress)
        val title = itemView.findViewById<MaterialTextView>(R.id.videogame_title)
        val platform_logo_1 = itemView.findViewById<ImageView>(R.id.platform_logo_1)
        val platform_logo_2 = itemView.findViewById<ImageView>(R.id.platform_logo_2)
        val platform_logo_3 = itemView.findViewById<ImageView>(R.id.platform_logo_3)
        val platform_view_more = itemView.findViewById<ImageView>(R.id.platform_logo_more)
        val metascoreValue = itemView.findViewById<MaterialTextView>(R.id.metascore_value)

        fun bind(videogameModel: VideogameModel) {

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

            if (videogameModel.platforms.isNotEmpty()) {
                if (videogameModel.platforms.size > 3) {
                    platform_view_more.visibility = INVISIBLE
                } else {
                    when (videogameModel.platforms.size) {
                        1 -> {
                            val ref =
                                storageRef.getReferenceFromUrl(getPlatformIcon(videogameModel.platforms[0]))
                            ref.downloadUrl.addOnCompleteListener {
                                loadImage(
                                    itemView,
                                    it.result.toString(),
                                    progress = progress,
                                    img = platform_logo_1
                                )
                            }
                            platform_logo_2.visibility = INVISIBLE
                            platform_logo_3.visibility = INVISIBLE
                        }
                        2 -> {
                            val ref1 =
                                storageRef.getReferenceFromUrl(getPlatformIcon(videogameModel.platforms[0]))
                            ref1.downloadUrl.addOnCompleteListener {
                                loadImage(
                                    itemView,
                                    it.result.toString(),
                                    img = platform_logo_1,
                                    progress = progress
                                )
                            }
                            val platftormIcon1 = getPlatformIcon(videogameModel.platforms[1])
                            if (!platftormIcon1.isNullOrBlank()) {
                                val ref2 =
                                    storageRef.getReferenceFromUrl(platftormIcon1)
                                ref2.downloadUrl.addOnCompleteListener {
                                    loadImage(
                                        itemView,
                                        it.result.toString(),
                                        platform_logo_2,
                                        progress = progress
                                    )
                                }
                            }
                            platform_logo_3.visibility = INVISIBLE

                        }
                        3 -> {
                            val ref1 =
                                storageRef.getReferenceFromUrl(getPlatformIcon(videogameModel.platforms[0]))
                            ref1.downloadUrl.addOnCompleteListener {
                                loadImage(
                                    itemView,
                                    it.result.toString(),
                                    platform_logo_1,
                                    progress = progress
                                )
                            }
                            if (videogameModel.platforms[1] != null) {
                                val ref2 =
                                    storageRef.getReferenceFromUrl(getPlatformIcon(videogameModel.platforms[1]))
                                ref2.downloadUrl.addOnCompleteListener {
                                    loadImage(
                                        itemView,
                                        it.result.toString(),
                                        platform_logo_2,
                                        progress = progress
                                    )
                                }
                            }
                            if (videogameModel.platforms[2] != null) {
                                val ref3 =
                                    storageRef.getReferenceFromUrl(getPlatformIcon(videogameModel.platforms[2]))
                                ref3.downloadUrl.addOnCompleteListener {
                                    loadImage(
                                        itemView,
                                        it.result.toString(),
                                        platform_logo_1,
                                        progress = progress
                                    )
                                }
                            }
                        }
                    }
                }
            }

            //metascoreValue.text = videogameModel.metascore.toString()
            title.text = videogameModel.name
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
}