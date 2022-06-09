package com.android.gamerenter.dialog

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.EditText
import androidx.fragment.app.DialogFragment
import com.android.gamerenter.R
import com.google.android.material.button.MaterialButton
import com.google.android.material.textview.MaterialTextView

class AdminCheckDialog(
    private val title: String,
    private val subtitle: String,
    private val onCheckAdmin: OnCheckAdmin
) : DialogFragment() {

    private lateinit var titleDialog: MaterialTextView
    private lateinit var subtitleDialog: MaterialTextView
    private lateinit var pinDigits: EditText
    private lateinit var checkAdmin: MaterialButton

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.dialog_admin_check, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        titleDialog = view.findViewById(R.id.title)
        subtitleDialog = view.findViewById(R.id.subtitle)
        pinDigits = view.findViewById(R.id.pin_digits)
        checkAdmin = view.findViewById(R.id.check)

        titleDialog.text = title
        subtitleDialog.text = subtitle
        pinDigits.addTextChangedListener(textWatcher)
        checkAdmin.apply {
            isEnabled = false
            text = context.getString(R.string.check_admin)
            setOnClickListener {
                onCheckAdmin.onCheckAdmin(pinDigits.text.toString())
                dialog?.dismiss()
            }
        }
    }

    override fun onStart() {
        super.onStart()
        dialog?.window?.setLayout(
            WindowManager.LayoutParams.MATCH_PARENT,
            WindowManager.LayoutParams.WRAP_CONTENT
        )
    }

    val textWatcher = object : TextWatcher {
        override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

        }

        override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            checkAdmin.isEnabled = !s.toString().isNullOrBlank()
        }

        override fun afterTextChanged(s: Editable?) {
        }

    }

    interface OnCheckAdmin {
        fun onCheckAdmin(value: String)
    }
}