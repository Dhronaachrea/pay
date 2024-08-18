package com.skilrock.paypr.paypr_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.animation.AnimationUtils
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.textview.MaterialTextView
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.ActivityRegistrationOtpBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.LoginResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.RegistrationResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.dialog.PayprSuccessDialog
import com.skilrock.paypr.paypr_app.viewmodel.RegistrationViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_registration_otp.*

class RegistrationOtpActivity : BaseActivity() {

    private lateinit var userName: String
    private lateinit var mobileNumber: String
    private lateinit var emailId: String
    private lateinit var password: String
    private var boxIndex = 0
    private lateinit var viewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDataBindingAndViewModel()
        toolbarNavigation()
        receiveDataFromPreviousActivity()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityRegistrationOtpBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration_otp)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(RegistrationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLinearLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataLoginStatus.observe(this, observerLoginStatus)
        viewModel.liveDataRegistrationStatus.observe(this, observerRegistrationStatus)
    }

    private fun toolbarNavigation() {
        toolbar.setNavigationOnClickListener {
            vibrate(this)
            onBackPressed()
        }
    }

    private fun receiveDataFromPreviousActivity() {
        userName        = intent.getStringExtra("userName") ?: ""
        mobileNumber    = intent.getStringExtra("mobileNumber") ?: ""
        emailId         = intent.getStringExtra("emailId") ?: ""
        password        = intent.getStringExtra("password") ?: ""

        if (userName.isBlank() or mobileNumber.isBlank() or emailId.isBlank() or password.isBlank()) {
            PayprErrorDialog(this, getString(R.string.sign_up_error), getString(R.string.some_technical_issue)) { finish() }.showDialog()
        } else {
            viewModel.name          = userName
            viewModel.mobileNumber  = mobileNumber
            viewModel.email         = emailId
            viewModel.password      = password
            otpWatcher()
            setKeyPadClicks()
        }
    }

    private fun otpWatcher() {
        tvOtp1.setOnClickListener { shiftBoxIndex(1) }
        tvOtp2.setOnClickListener { shiftBoxIndex(2) }
        tvOtp3.setOnClickListener { shiftBoxIndex(3) }
        tvOtp4.setOnClickListener { shiftBoxIndex(4) }
        tvOtp5.setOnClickListener { shiftBoxIndex(5) }
        tvOtp6.setOnClickListener { shiftBoxIndex(6) }
    }

    private fun shiftBoxIndex(index: Int) {
        showKeypad(true)

        boxIndex = index

        tvOtp1.background = ContextCompat.getDrawable(this, R.drawable.otp_outline)
        tvOtp2.background = ContextCompat.getDrawable(this, R.drawable.otp_outline)
        tvOtp3.background = ContextCompat.getDrawable(this, R.drawable.otp_outline)
        tvOtp4.background = ContextCompat.getDrawable(this, R.drawable.otp_outline)
        tvOtp5.background = ContextCompat.getDrawable(this, R.drawable.otp_outline)
        tvOtp6.background = ContextCompat.getDrawable(this, R.drawable.otp_outline)

        when (index) {
            1 -> tvOtp1.background = ContextCompat.getDrawable(this, R.drawable.otp_outline_focused)
            2 -> tvOtp2.background = ContextCompat.getDrawable(this, R.drawable.otp_outline_focused)
            3 -> tvOtp3.background = ContextCompat.getDrawable(this, R.drawable.otp_outline_focused)
            4 -> tvOtp4.background = ContextCompat.getDrawable(this, R.drawable.otp_outline_focused)
            5 -> tvOtp5.background = ContextCompat.getDrawable(this, R.drawable.otp_outline_focused)
            6 -> tvOtp6.background = ContextCompat.getDrawable(this, R.drawable.otp_outline_focused)
        }
    }

    private fun fillOtpBox(text: String) {
        when (boxIndex) {
            1 -> {
                tvOtp1.text = text
                tvOtp1.setTextColor(ContextCompat.getColor(this, R.color.main_text))
                shiftBoxIndex(2)
            }
            2 -> {
                tvOtp2.text = text
                tvOtp2.setTextColor(ContextCompat.getColor(this, R.color.main_text))
                shiftBoxIndex(3)
            }
            3 -> {
                tvOtp3.text = text
                tvOtp3.setTextColor(ContextCompat.getColor(this, R.color.main_text))
                shiftBoxIndex(4)
            }
            4 -> {
                tvOtp4.text = text
                tvOtp4.setTextColor(ContextCompat.getColor(this, R.color.main_text))
                shiftBoxIndex(5)
            }
            5 -> {
                tvOtp5.text = text
                tvOtp5.setTextColor(ContextCompat.getColor(this, R.color.main_text))
                shiftBoxIndex(6)
            }
            6 -> {
                tvOtp6.text = text
                tvOtp6.setTextColor(ContextCompat.getColor(this, R.color.main_text))
            }
        }
    }

    private fun setKeyPadClicks() {
        tvKey1.setOnClickListener { fillOtpBox("1") }
        tvKey2.setOnClickListener { fillOtpBox("2") }
        tvKey3.setOnClickListener { fillOtpBox("3") }
        tvKey4.setOnClickListener { fillOtpBox("4") }
        tvKey5.setOnClickListener { fillOtpBox("5") }
        tvKey6.setOnClickListener { fillOtpBox("6") }
        tvKey7.setOnClickListener { fillOtpBox("7") }
        tvKey8.setOnClickListener { fillOtpBox("8") }
        tvKey9.setOnClickListener { fillOtpBox("9") }
        tvKey0.setOnClickListener { fillOtpBox("0") }
        tvKeyClose.setOnClickListener { showKeypad(false) }
        tvKeyDone.setOnClickListener { proceedToNextScreen() }
        btnRestore.setOnClickListener { proceedToNextScreen() }
    }

    private fun showKeypad(flag: Boolean) {
        if (flag) {
            if (llKeyPad.visibility != View.VISIBLE) {
                llRestore.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_bottom))
                llKeyPad.visibility     = View.VISIBLE
                llRestore.visibility    = View.GONE
                llKeyPad.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_top))
            }
        } else {
            if (llRestore.visibility != View.VISIBLE) {
                llRestore.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_top))
                llKeyPad.visibility = View.GONE
                llRestore.visibility = View.VISIBLE
                llKeyPad.startAnimation(AnimationUtils.loadAnimation(this, R.anim.flip_in_bottom))
            }
        }
    }

    fun onCancelClick(view: View) = onBackPressed()

    override fun onBackPressed() {
        if (boxIndex != 0) {
            shiftBoxIndex(0)
            showKeypad(false)
        } else
            super.onBackPressed()
    }

    override fun hideKeyboard() {
        btnRestore.hideKeyboard()
    }

    private fun proceedToNextScreen() {
        if (validate()) {
            viewModel.otp = getOtp()
            viewModel.onRegisterButtonClick()
        }
    }

    private fun validate() : Boolean {
        if (isOtpBlank(tvOtp1) and isOtpBlank(tvOtp2) and isOtpBlank(tvOtp3)
            and isOtpBlank(tvOtp4) and isOtpBlank(tvOtp5) and isOtpBlank(tvOtp6)) {
            PayprErrorDialog(this, getString(R.string.sign_up_error), getString(R.string.plz_enter_otp)) {}.showDialog()
            return false
        }
        else if (isOtpBlank(tvOtp1) or isOtpBlank(tvOtp2) or isOtpBlank(tvOtp3)
            or isOtpBlank(tvOtp4) or isOtpBlank(tvOtp5) or isOtpBlank(tvOtp6)) {
            PayprErrorDialog(this, getString(R.string.sign_up_error), getString(R.string.wrong_otp_msg)) {}.showDialog()
            return false
        }
        return true
    }

    private fun isOtpBlank(textView: MaterialTextView) : Boolean {
        if(textView.text.toString().trim().isBlank() or textView.text.toString().trim().equals("X", true)) {
            return true
        }
        return false
    }

    private fun getOtp() : String {
        return tvOtp1.getTextTrimmed() + tvOtp2.getTextTrimmed() + tvOtp3.getTextTrimmed() + tvOtp4.getTextTrimmed() + tvOtp5.getTextTrimmed() + tvOtp6.getTextTrimmed()
    }

    private val observerRegistrationStatus = Observer<ResponseStatus<RegistrationResponseData>> {
        when(it) {
            is ResponseStatus.Success ->
                PayprSuccessDialog(this, getString(R.string.success),
                    getString(R.string.your_account_has_been_created), getString(R.string.continue_)) { viewModel.callLoginApi(SharedPrefUtils.getFcmToken(this@RegistrationOtpActivity)) }.showDialog()

            is ResponseStatus.Error ->
                PayprErrorDialog(this, getString(R.string.sign_up_error), it.errorMessage.getMsg(this)) { finish() }.showDialog()

            is ResponseStatus.TechnicalError ->
                PayprErrorDialog(this, getString(R.string.sign_up_error), getString(it.errorMessageCode)) {}.showDialog()
        }
    }

    private val observerLoginStatus = Observer<ResponseStatus<LoginResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                PlayerInfo.setLoginData(this, it.response)
                SharedPrefUtils.putLoggedInUsers(this, PlayerInfo.getPlayerUserName())
                openHomeActivity()
            }

            is ResponseStatus.Error -> openLoginActivity()

            is ResponseStatus.TechnicalError -> openLoginActivity()
        }
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private fun openHomeActivity() {
        val intent = Intent(this, HomePayprActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private var observerLinearLoader = Observer<Boolean> {
        if (it) {
            progressBar.animate().alpha(1f).setDuration(200).withEndAction {
                window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                    WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                progressBar.visibility = View.VISIBLE
            }
        }
        else {
            progressBar.animate().alpha(0f).setDuration(200).withEndAction {
                progressBar.visibility = View.INVISIBLE
                window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            }
        }
    }

}