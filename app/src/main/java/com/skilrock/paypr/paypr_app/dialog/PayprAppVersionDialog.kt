package com.skilrock.paypr.paypr_app.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.View
import android.view.Window
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.dialog_app_version_paypr.*

class PayprAppVersionDialog(private val isForcefulDownload: Boolean, private val url: String,
                            private val message: String, context: Context,
                            private val operation:(Boolean, String) -> Unit) : Dialog(context){

    init {
        setCancelable(false)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.dialog_app_version_paypr)

        tvMessage.text = message
        btnLater.visibility = if (isForcefulDownload) View.GONE else View.VISIBLE

        setOnClickListeners()
    }

    private fun setOnClickListeners() {
        btnLater.setOnClickListener {
            vibrate(context)
            operation(false, url)
            dismiss()
        }

        btnUpdate.setOnClickListener {
            vibrate(context)
            operation(true, url)
            dismiss()
        }
    }

}