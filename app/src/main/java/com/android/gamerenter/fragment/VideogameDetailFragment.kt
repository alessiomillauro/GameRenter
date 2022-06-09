package com.android.gamerenter.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.Menu.NONE
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.constraintlayout.helper.widget.Flow
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.navArgs
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager.HORIZONTAL
import androidx.recyclerview.widget.RecyclerView
import com.android.gamerenter.R
import com.android.gamerenter.adapter.VideogameImagesAdapter
import com.android.gamerenter.dialog.DIALOG_TYPE
import com.android.gamerenter.dialog.GeneralDialog
import com.android.gamerenter.model.PlatformModel
import com.android.gamerenter.viewmodel.VideogameDetailViewModel
import com.bumptech.glide.Glide
import com.google.android.material.card.MaterialCardView
import com.google.android.material.textview.MaterialTextView
import com.google.firebase.storage.FirebaseStorage
import dagger.hilt.android.AndroidEntryPoint
import java.lang.StringBuilder
import java.text.SimpleDateFormat
import java.util.*
import javax.inject.Inject

@AndroidEntryPoint
class VideogameDetailFragment : Fragment() {

    val args: VideogameDetailFragmentArgs by navArgs()

    private val videogameDetailViewModel: VideogameDetailViewModel by viewModels()

    @Inject
    lateinit var firebaseStorage: FirebaseStorage

    private lateinit var parent: ConstraintLayout
    private lateinit var image: ImageView
    private lateinit var title: TextView
    private lateinit var overview: MaterialTextView
    private lateinit var genre: MaterialTextView
    private lateinit var release: MaterialTextView
    private lateinit var playtime: MaterialTextView
    private lateinit var metascoreValue: MaterialTextView
    private lateinit var containerPlatforms: Flow
    private lateinit var rvImages: RecyclerView
    private lateinit var btnRent: Button
    private lateinit var btnPeriodRent: Button

    private var periodRent = -1
    private var platformList = listOf<PlatformModel>()

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
        parent = view.findViewById(R.id.container)
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

        videogameDetailViewModel.platformListLiveData.observe(viewLifecycleOwner) {
            platformList = it

            for (platform in item.platforms) {
                for (plat in platformList) {
                    if (plat.id == platform) {
                        val view = layoutInflater.inflate(R.layout.item_videogame_platform, null)
                        view.id = plat.id
                        val imgPlatform = view.findViewById<ImageView>(R.id.image_platform)
                        val namePlatform = view.findViewById<TextView>(R.id.name_platform)

                        namePlatform.text = plat.name
                        if (!plat.image.isNullOrBlank()) {
                            val ref = firebaseStorage.getReferenceFromUrl(plat.image.toString())
                            ref.downloadUrl.addOnCompleteListener {
                                context?.let { it1 ->
                                    Glide.with(it1).load(it.result).into(imgPlatform)
                                }
                            }
                        } else {
                            context?.let { it1 ->
                                Glide.with(it1)
                                    .load(resources.getDrawable(R.drawable.ic_placeholder_console))
                                    .into(imgPlatform)
                            }
                        }
                        parent.addView(view)
                        containerPlatforms.addView(view)
                    }
                }
            }
        }

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
        metascoreValue.text = item.metascore.toString()

        imagesAdapter.submitList(item.images)
        rvImages.apply {
            layoutManager = LinearLayoutManager(context, HORIZONTAL, false)
            adapter = imagesAdapter
        }

        btnPeriodRent.apply {
            text = "1 GIORNO"
            isEnabled = item.rent_max_period != 1
            setOnClickListener {

                val popupMenu = PopupMenu(context, it)
                for (i in 1..item.rent_max_period) {
                    popupMenu.menu.add(
                        NONE,
                        i,
                        NONE,
                        if (i > 1) "$i GIORNI" else "$i GIORNO"
                    )
                }
                popupMenu.show()

                popupMenu.setOnMenuItemClickListener { item ->
                    periodRent = item?.itemId!!
                    text = if (periodRent > 1) "$periodRent GIORNI" else "$periodRent GIORNO"
                    true
                }
            }
        }

        btnRent.apply {
            text = context.getString(R.string.rent_action_title)
            isEnabled = item.rent_copy_available < item.rent_copy_total
            setOnClickListener {
                videogameDetailViewModel.rent(item.id, periodRent, System.currentTimeMillis())
            }
        }

        videogameDetailViewModel.updateRentResutl.observe(
            viewLifecycleOwner
        ) { t ->
            GeneralDialog(
                if (t) DIALOG_TYPE.SUCCESS else DIALOG_TYPE.ERROR,
                if (t) "Dati aggiornati correttamente" else "Si è verificato un errore. Riprovarez"
            ).show(parentFragmentManager, "dialog")
        }
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