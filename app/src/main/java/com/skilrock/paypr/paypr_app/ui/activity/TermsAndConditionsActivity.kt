package com.skilrock.paypr.paypr_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.text.method.ScrollingMovementMethod
import androidx.appcompat.app.AppCompatActivity
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.activity_terms_and_conditions.*

class TermsAndConditionsActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_terms_and_conditions)

        tvTncText.movementMethod = ScrollingMovementMethod()
        setClickListeners()
    }

    private fun setClickListeners() {
        toolbar.setNavigationOnClickListener {
            vibrate(this)
            onBackPressed()
        }

        tvDisagree.setOnClickListener {
            val intent = Intent(this, PostSplashActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            startActivity(intent)
            overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
        }

        btnAgree.setOnClickListener {
            onBackPressed()
        }
    }

}