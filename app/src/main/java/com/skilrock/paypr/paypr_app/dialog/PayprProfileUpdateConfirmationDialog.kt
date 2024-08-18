package com.skilrock.paypr.paypr_app.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.core.text.HtmlCompat
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.layout_profile_update_dialog_paypr.*

class PayprProfileUpdateConfirmationDialog(context: Context, private val header: String,
                                           private val message: String, private val operationYes:()->Unit) : Dialog(context) {

    private lateinit var connectingAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_profile_update_dialog_paypr)
        setCancelable(false)

        connectingAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_scale_animation)
        ivKey.startAnimation(connectingAnimation)

        tvHeader.text   = header
        tvSubject.text  = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)

        tvYes.setOnClickListener {
            vibrate(context)
            dismiss()
            operationYes()
        }

        tvNo.setOnClickListener {
            vibrate(context)
            dismiss()
        }
    }

    override fun dismiss() {
        Log.i("log", "~~~ Clear Dialog Animation ~~~")
        ivKey.clearAnimation()
        connectingAnimation.cancel()
        connectingAnimation.reset()
        super.dismiss()
    }

}