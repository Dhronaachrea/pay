package com.skilrock.paypr.paypr_app.ui.activity

import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import com.skilrock.paypr.utility.Loader
import com.skilrock.paypr.utility.vibrate

abstract class BaseActivity : AppCompatActivity() {

    val observerVibrator = Observer<String> { vibrate(this) }

    val observerLoader = Observer<Boolean> {
        if (it) Loader.showLoader(this) else Loader.dismiss()
    }

    val observerHideKeyboard = Observer<Boolean> {
        if (it) hideKeyboard()
    }

    abstract fun hideKeyboard()

}