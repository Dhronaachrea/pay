package com.skilrock.paypr.paypr_app.ui.fragment

import android.content.Context
import android.view.View
import android.view.WindowManager
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.skilrock.paypr.paypr_app.ui.activity.HomePayprActivity
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.activity_home_paypr.*

abstract class BaseFragmentPaypr : Fragment() {

    lateinit var master: HomePayprActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)
        if (context is HomePayprActivity)
            master = context
    }

    val observerVibrator = Observer<String> { vibrate(master) }

    val observerLoader = Observer<Boolean> {
        if (it) {
            master.progressBar.animate().alpha(1f).setDuration(200).withEndAction {
                master.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                master.progressBar.visibility = View.VISIBLE
            }
        }
        else {
            master.progressBar.animate().alpha(0f).setDuration(200).withEndAction {
                master.progressBar.visibility = View.INVISIBLE
                master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

    val observerHideKeyboard = Observer<Boolean> {
        if (it) hideKeyboard()
    }

    abstract fun hideKeyboard()

    abstract fun setToolbarElements()

    fun switchToHome() {
        master.navigateToHome()
    }

}