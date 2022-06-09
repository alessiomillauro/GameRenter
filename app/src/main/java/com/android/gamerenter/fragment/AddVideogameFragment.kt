package com.android.gamerenter.fragment

import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore.ACTION_IMAGE_CAPTURE
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.widget.DatePicker
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.ProgressBar
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.navigation.fragment.findNavController
import com.android.gamerenter.R
import com.android.gamerenter.dialog.DIALOG_TYPE
import com.android.gamerenter.dialog.GeneralDialog
import com.android.gamerenter.dialog.OnDialogButtonClick
import com.android.gamerenter.model.VideogameModel
import com.android.gamerenter.viewmodel.AddVideogameViewModel
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.android.material.chip.ChipGroup
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textview.MaterialTextView
import dagger.hilt.android.AndroidEntryPoint
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar.*

@AndroidEntryPoint
class AddVideogameFragment() : Fragment(), DatePickerDialog.OnDateSetListener {

    private val addVideogameViewModel: AddVideogameViewModel by viewModels()

    private lateinit var name: TextInputEditText
    private lateinit var overview: TextInputEditText
    private lateinit var playtime: TextInputEditText
    private lateinit var genre: ChipGroup
    private lateinit var platform: ChipGroup
    private lateinit var producers: ChipGroup
    private lateinit var releaseValue: MaterialTextView
    private lateinit var releaseAction: ImageButton
    private lateinit var previewValue: ImageView
    private lateinit var previewAction: ImageButton
    private lateinit var insert: MaterialButton

    private lateinit var filePath: Uri

    val startActivityIntent =
        registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) {
            val photo: Bitmap = it.data?.extras?.get("data") as Bitmap
            previewValue.setImageBitmap(photo)
        }


    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            insert.isEnabled = !s.toString().isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {

        }

    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_add_videogame, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        name = view.findViewById(R.id.add_name)
        overview = view.findViewById(R.id.add_overview)
        playtime = view.findViewById(R.id.add_playtime)
        genre = view.findViewById(R.id.cg_genre)
        platform = view.findViewById(R.id.cg_platforms)
        producers = view.findViewById(R.id.cg_developer)
        releaseValue = view.findViewById(R.id.release_value)
        releaseAction = view.findViewById(R.id.calendar_action)
        previewValue = view.findViewById(R.id.poster_preview)
        previewAction = view.findViewById(R.id.camera_action)
        insert = view.findViewById(R.id.insert)

        name.addTextChangedListener(textWatcher)

        addVideogameViewModel.getGenreVideogameList()
        addVideogameViewModel.getPlatformVideogameList()
        addVideogameViewModel.getPublishersVideogameList()

        releaseAction.setOnClickListener {
            context?.let { it1 ->
                val dialog = DatePickerDialog(it1)
                dialog.setOnDateSetListener(this)
                dialog.show()
            }
        }

        previewAction.setOnClickListener {
            val file = File("CUSTOM_${(1..999999).random()}")
            val outputFile = Uri.fromFile(file)

            val cameraIntent = Intent(ACTION_IMAGE_CAPTURE)

            startActivityIntent.launch(cameraIntent)
        }

        insert.apply {
            text = "INSERISCI"
            setOnClickListener {
                val item = VideogameModel(
                    name = name.text.toString(),
                    overview = overview.text.toString(),
                    playtime = if (playtime.text.isNullOrBlank()) -1 else Integer.parseInt(playtime.text.toString()),
                    genre = getSelectedGenres(),
                    platforms = listOf(),
                    companies = listOf(),
                    dateOfRelease = releaseValue.text.toString(),
                    background_image = ""
                )

                GeneralDialog(
                    DIALOG_TYPE.INFO,
                    "Sei sicuro di voler procede?", object : OnDialogButtonClick {
                        override fun onPositiveButtonClicked(dialog: Dialog?) {
                            val nameFile = "CUSTOM_${(1..999999).random()}"
                            val bitmap = convertVectorToDrawable(previewValue.drawable)
                            addVideogameViewModel.addVideogame(
                                bitmap,
                                item,
                                nameFile
                            )
                            dialog?.dismiss()
                        }

                        override fun onNegativeButtonClicked(dialog: Dialog?) {
                            dialog?.dismiss()
                        }
                    }, "PROCEDI", "ANNULLA"
                ).show(childFragmentManager, "")//.showDialog(context)
                //dialog.show(parentFragmentManager, "")
            }
        }

        addVideogameViewModel.feedbackInsert.observe(viewLifecycleOwner, Observer {
            GeneralDialog(
                if (it) DIALOG_TYPE.SUCCESS else DIALOG_TYPE.ERROR,
                if (it) "Operazione completata con successo!" else "OPS! Qualcosa è andato storto",
                object : OnDialogButtonClick {
                    override fun onPositiveButtonClicked(dialog: Dialog?) {
                        dialog?.dismiss()
                        activity?.finish()
                    }

                    override fun onNegativeButtonClicked(dialog: Dialog?) {

                    }
                }
            ).show(childFragmentManager, "")


        })

        addVideogameViewModel.genreVideogamesLiveData.observe(viewLifecycleOwner) {
            for (item in it) {
                item.forEach { (key, value) ->
                    val chip = Chip(context)
                    chip.text = value
                    chip.isCheckable = true

                    genre.addView(chip)
                }
            }
        }

        addVideogameViewModel.platformVideogamesLiveData.observe(viewLifecycleOwner) {
            for (item in it) {
                val chip = Chip(context)
                chip.text = item.name
                chip.isCheckable = true

                platform.addView(chip)
            }
        }

        val platformChips = platform.checkedChipIds

        addVideogameViewModel.publishersVideogamesLiveData.observe(viewLifecycleOwner) {
            for (item in it) {
                item.forEach { (key, value) ->
                    val chip = Chip(context)
                    chip.id = Integer.parseInt(key)
                    chip.text = value
                    chip.isCheckable = true

                    producers.addView(chip)
                }
            }
        }

        val publisherChips = producers.checkedChipIds
    }

    private fun getSelectedGenres(): List<String> {
        var genreList = listOf<String>()
        val chipIds = genre.checkedChipIds
        if (chipIds.isNotEmpty()) {
            chipIds.forEachIndexed { index, i ->
                val currentChip = (genre.getChildAt(index) as Chip).text.toString()
                genreList.toMutableList().add(currentChip)
            }
        }
        return genreList
    }

    override fun onDateSet(view: DatePicker?, year: Int, month: Int, dayOfMonth: Int) {
        val calendar = getInstance()
        calendar.set(YEAR, year)
        calendar.set(MONTH, month)
        calendar.set(DAY_OF_MONTH, dayOfMonth)

        val sdf = SimpleDateFormat("yyyy-MM-dd")
        val release = calendar.time
        val result = sdf.format(release)

        releaseValue.text = result
    }

    private fun convertVectorToDrawable(drawable: Drawable): Bitmap {
        val bitmap = Bitmap.createBitmap(
            drawable.intrinsicWidth,
            drawable.intrinsicHeight,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)

        drawable.setBounds(0, 0, canvas.width, canvas.height);
        drawable.draw(canvas);
        return bitmap;
    }
}