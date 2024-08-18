package com.skilrock.paypr.paypr_app.dialog

import android.app.Activity
import android.app.Dialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.SESSION_EXPIRE_CODE
import com.skilrock.paypr.utility.performLogoutCleanUp
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.layout_error_dialog_paypr.*

class PayprErrorDialog(private val activity: Activity, private val header: String,
                       private val message: String, private val btnText: String = "",
                       private val code: Int = -1, private var intent: Intent? = null,
                       private val operation: () -> Unit) : Dialog(activity) {

    private lateinit var connectingAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_error_dialog_paypr)
        setCancelable(false)

        tvHeaderED.text     = if (header.trim().isEmpty()) context.getString(R.string.service_error) else header
        tvSubjectED.text    = message
        tvActionED.text     = if (btnText.trim().isEmpty()) context.getString(R.string.ok) else btnText

        connectingAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_scale_animation)
        ivLogoEd.startAnimation(connectingAnimation)

        tvActionED.setOnClickListener {
            vibrate(activity)
            dismiss()
            if (code == SESSION_EXPIRE_CODE) {
                intent?.let {
                    performLogoutCleanUp(activity, it, true)
                }
            }
            else
                operation()
        }
    }

    override fun dismiss() {
        Log.i("log", "~~~ Clear Dialog Animation ~~~")
        ivLogoEd.clearAnimation()
        connectingAnimation.cancel()
        connectingAnimation.reset()
        super.dismiss()
    }

}