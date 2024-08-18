package com.skilrock.paypr.paypr_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.adjustDisplayScale
import kotlinx.android.synthetic.main.activity_post_splash.*

class PostSplashActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        adjustDisplayScale(this)
        setContentView(R.layout.activity_post_splash)
        setClickListeners()
    }

    private fun setClickListeners() {
        btnGetStarted.setOnClickListener {
            val pairLogo    = androidx.core.util.Pair<View, String>(ivLogo, "splash_to_login")
            val options     = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairLogo)
            startActivity(Intent(this, RegistrationActivity::class.java), options.toBundle())
        }

        llSignIn.setOnClickListener {
            val pairLogo    = androidx.core.util.Pair<View, String>(ivLogo, "splash_to_login")
            val options     = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairLogo)
            startActivity(Intent(this, LoginActivity::class.java), options.toBundle())
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
    }

}