package com.android.gamerenter.dialog

import android.app.Dialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.Button
import android.widget.ImageView
import androidx.fragment.app.DialogFragment
import com.android.gamerenter.R
import com.bumptech.glide.Glide
import com.google.android.material.textview.MaterialTextView

class GeneralDialog(
    private val type: DIALOG_TYPE,
    private val title: String,
    private val onDialogButtonClick: OnDialogButtonClick? = null,
    private val positiveButtonText: String = "",
    private val negativeButtonText: String = ""
) :
    DialogFragment() {

    private lateinit var imgStatus: ImageView
    private lateinit var titleStatus: MaterialTextView
    private lateinit var btnNegative: Button
    private lateinit var btnPositive: Button

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_general, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        imgStatus = view.findViewById(R.id.img_status)
        titleStatus = view.findViewById(R.id.title)
        btnNegative = view.findViewById(R.id.negative)
        btnPositive = view.findViewById(R.id.positive)

        btnNegative.apply {
            text = negativeButtonText
            setOnClickListener { onDialogButtonClick?.onNegativeButtonClicked(dialog) }
        }
        btnPositive.apply {
            text = positiveButtonText
            setOnClickListener { onDialogButtonClick?.onPositiveButtonClicked(dialog) }
        }

        var resource: Int = when (type) {
            DIALOG_TYPE.SUCCESS -> R.drawable.status_positive
            DIALOG_TYPE.ERROR -> R.drawable.status_negative
            DIALOG_TYPE.INFO -> R.drawable.status_info
        }
        Glide.with(view.context).load(resource).into(imgStatus)

        titleStatus.text = title

        if (type == DIALOG_TYPE.SUCCESS || type == DIALOG_TYPE.ERROR) {
            btnNegative.visibility = GONE
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    public fun hide() {
        dialog?.dismiss()
    }

}

interface OnDialogButtonClick {
    fun onPositiveButtonClicked(dialog: Dialog?)
    fun onNegativeButtonClicked(dialog: Dialog?)
}

enum class DIALOG_TYPE {
    SUCCESS, ERROR, INFO
}