package com.android.gamerenter.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.android.gamerenter.R
import com.android.gamerenter.model.PlatformModel
import com.android.gamerenter.model.VideogameModel
import com.google.android.material.textview.MaterialTextView

class VideogameAdapter : ListAdapter<VideogameModel, VideogameAdapter.VideogameViewHolder>(
    DIFF_VIDEOGAME_CALLBACK()
) {

    private lateinit var platformList: List<PlatformModel>

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VideogameViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val view = layoutInflater.inflate(R.layout.adapter_videogame, parent, false)
        return VideogameViewHolder(view)
    }

    override fun onBindViewHolder(holder: VideogameViewHolder, position: Int) {
        val mostPopularResult = getItem(position)
        holder.bind(mostPopularResult)
    }

    fun updatePlatformList(res: List<PlatformModel>) {
        platformList = res
    }

    inner class VideogameViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        val title = itemView.findViewById<MaterialTextView>(R.id.videogame_title)

        fun bind(videogameModel: VideogameModel) {
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