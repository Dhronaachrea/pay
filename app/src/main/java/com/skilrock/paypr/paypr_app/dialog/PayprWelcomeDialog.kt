package com.skilrock.paypr.paypr_app.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.layout_welcome_dialog_paypr.*

class PayprWelcomeDialog(context: Context, private val header: String, private val message: String,
                         private val btnText: String = "", private val operation:()->Unit) : Dialog(context) {

    private lateinit var connectingAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_welcome_dialog_paypr)
        setCancelable(false)

        tvHeaderSD.text     = if (TextUtils.isEmpty(header)) context.getString(R.string.success) else header
        tvSubjectSD.text    = message
        tvActionSD.text     = if (btnText.trim().isEmpty()) context.getString(R.string.ok) else btnText

        connectingAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_scale_animation)
        ivLogoSd.startAnimation(connectingAnimation)

        tvActionSD.setOnClickListener {
            vibrate(context)
            dismiss()
            operation()
        }
    }

    override fun dismiss() {
        Log.i("log", "~~~ Clear Dialog Animation ~~~")
        ivLogoSd.clearAnimation()
        connectingAnimation.cancel()
        connectingAnimation.reset()
        super.dismiss()
    }

}