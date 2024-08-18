package com.skilrock.paypr.paypr_app.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.Window
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.layout_permission_dialog_paypr.*

class PayprGpsPermissionDialog(context: Context, private val operationYes:()->Unit, private val operationNo:()->Unit) : Dialog(context) {

    private lateinit var connectingAnimation: Animation

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_permission_dialog_paypr)
        setCancelable(false)

        connectingAnimation = AnimationUtils.loadAnimation(context, R.anim.alpha_scale_animation)
        ivKey.startAnimation(connectingAnimation)

        tvYes.setOnClickListener {
            vibrate(context)
            dismiss()
            operationYes()
        }

        tvNo.setOnClickListener {
            vibrate(context)
            dismiss()
            operationNo()
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