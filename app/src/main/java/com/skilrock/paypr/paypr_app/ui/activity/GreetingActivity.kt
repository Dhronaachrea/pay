package com.skilrock.paypr.paypr_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.ActivityGreetingBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.LastLoginResponseData
import com.skilrock.paypr.paypr_app.viewmodel.GreetingViewModel
import com.skilrock.paypr.utility.PlayerInfo
import com.skilrock.paypr.utility.ResponseStatus
import com.skilrock.paypr.utility.adjustDisplayScale
import kotlinx.android.synthetic.main.activity_greeting.*

class GreetingActivity : BaseActivity() {

    private lateinit var viewModel: GreetingViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDataBindingAndViewModel()
        adjustDisplayScale(this)
        setUiDetails()
        viewModel.callLastLoginDetailApi()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityGreetingBinding = DataBindingUtil.setContentView(this, R.layout.activity_greeting)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(GreetingViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataVersionStatus.observe(this, observerLastLogin)
    }

    private fun setUiDetails() {
        val greet = viewModel.getGreetTag() + " <b>" + PlayerInfo.getPlayerFirstName() + "</b>"
        tvGreet.text = HtmlCompat.fromHtml(greet, HtmlCompat.FROM_HTML_MODE_LEGACY)

        val text = "<b>${PlayerInfo.getPlayerFirstName()}</b> <b>${PlayerInfo.getPlayerLastName()}</b> ${getString(R.string.account)}"
        tvAccount.text = HtmlCompat.fromHtml(text, HtmlCompat.FROM_HTML_MODE_LEGACY)
    }

    private val observerLastLogin = Observer<ResponseStatus<LastLoginResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                it.response.loginDetailList?.let { list ->
                    if (list.isNullOrEmpty())
                        tvLastLogin.text = ""
                    else {
                        list[0]?.loginDate?.let { loginDate ->
                            val text = getString(R.string.last_login) + " " + viewModel.getDate(loginDate) + " " + viewModel.getTime(loginDate)
                            tvLastLogin.text = text
                        } ?: run { tvLastLogin.text = "" }
                    }
                } ?: run { tvLastLogin.text = "" }
            }

            is ResponseStatus.Error -> tvLastLogin.text = ""

            is ResponseStatus.TechnicalError -> tvLastLogin.text = ""
        }
    }

    override fun hideKeyboard() {
        //Code here to hide keyboard
    }

    fun onClickContinue(view: View) {
        val intent = Intent(this, HomePayprActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

}