package com.android.gamerenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu.NONE
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.android.gamerenter.FIREBASE_STORAGE_GAMES_ROOT
import com.android.gamerenter.FIREBASE_STORAGE_LOCATION
import com.android.gamerenter.R
import com.android.gamerenter.adapter.VideogameImagesAdapter
import com.android.gamerenter.dialog.DIALOG_TYPE
import com.android.gamerenter.dialog.GeneralDialog
import com.android.gamerenter.viewmodel.VideogameDetailViewModel
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class VideogameDetailFragment : Fragment() {

    val args: VideogameDetailFragmentArgs by navArgs()

    private val videogameDetailViewModel: VideogameDetailViewModel by viewModels()

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private lateinit var image: ImageView
    private lateinit var title: TextView
    private lateinit var overview: MaterialTextView
    private lateinit var genre: MaterialTextView
    private lateinit var release: MaterialTextView
    private lateinit var playtime: MaterialTextView
    private lateinit var metascoreValue: MaterialTextView
    private lateinit var containerPlatforms: LinearLayout
    private lateinit var rvImages: RecyclerView
    private lateinit var btnRent: Button
    private lateinit var btnPeriodRent: Button

    private var periodRent = -1

    @Inject
    lateinit var imagesAdapter: VideogameImagesAdapter

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_videogame_detail, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val item = args.videogameModel

        val container: MaterialCardView = view.findViewById(R.id.container_overview)
        title = view.findViewById(R.id.title)
        image = view.findViewById(R.id.toolbar_image)
        overview = container.findViewById(R.id.overview)
        genre = container.findViewById(R.id.genre)
        release = view.findViewById(R.id.release)
        playtime = view.findViewById(R.id.playtime)
        metascoreValue = view.findViewById(R.id.metascore_value)
        containerPlatforms = view.findViewById(R.id.platforms_sub_container)
        rvImages = view.findViewById(R.id.rv_images)
        btnRent = view.findViewById(R.id.rent)
        btnPeriodRent = view.findViewById(R.id.rent_period)

        firebaseStorage.getReferenceFromUrl(item.background_image)
            .downloadUrl
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    val uri = it.result
                    Glide.with(view)
                        .load(uri.toString())
                        .into(image)
                }
            }

        title.text = item.name
        overview.text =
            if (item.overview.isBlank()) resources.getString(R.string.no_data_available) else item.overview
        release.text = getReleaseDate(item.dateOfRelease)
        genre.text = getGenresList(item.genre)
        playtime.text =
            String.format(resources.getString(R.string.average_playtime), item.playtime.toString())
        //metascoreValue.text = item.

        rvImages.apply {
            layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            adapter = imagesAdapter
        }

        btnPeriodRent.setOnClickListener {
            val popupMenu = PopupMenu(context, it)
            for (i in 1..7) {
                popupMenu.menu.add(
                    NONE,
                    i,
                    NONE,
                    if (i > 1) "$i GIORNI" else "$i GIORNO"
                )
            }
            popupMenu.show()

            popupMenu.setOnMenuItemClickListener(object : PopupMenu.OnMenuItemClickListener {
                override fun onMenuItemClick(item: MenuItem?): Boolean {
                    periodRent = item?.itemId!!
                    return true
                }
            })
        }

        btnRent.apply {
            isEnabled = item.rent_copy_available < item.rent_copy_total
            setOnClickListener {
                videogameDetailViewModel.rent(item.id, periodRent, System.currentTimeMillis())
            }
        }

        videogameDetailViewModel.updateRentResutl.observe(
            viewLifecycleOwner,
            androidx.lifecycle.Observer { t ->  })
    }

    private fun getGenresList(genres: List<String>): String {
        var sb = StringBuilder()
        genres.forEachIndexed { index, item ->
            sb.append(if (index == genres.size - 1) item else "$item ・")
        }
        return sb.toString()
    }

    private fun getReleaseDate(release: String): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd")
        var date: Date = sdf.parse(release)
        val sdf2 = SimpleDateFormat("dd-MMM-yyyy")
        return sdf2.format(date)
    }
}