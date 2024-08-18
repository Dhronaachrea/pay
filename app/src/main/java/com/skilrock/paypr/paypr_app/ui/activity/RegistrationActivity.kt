package com.skilrock.paypr.paypr_app.ui.activity

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.text.InputFilter
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.app.ActivityOptionsCompat
import androidx.core.app.NotificationCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.ActivityRegistrationBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.RegistrationOtpResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.RegistrationValidationResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.viewmodel.RegistrationViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_registration.*


class RegistrationActivity : BaseActivity() {

    private lateinit var viewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDataBindingAndViewModel()
        setMobileNumberValidations()
        keyboardEnterFunctionality()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityRegistrationBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(RegistrationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLinearLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataValidation.observe(this, observerValidation)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataRegistrationOtp.observe(this, observerOtp)
        viewModel.liveDataRegistrationValid.observe(this, observerValidationApi)
    }

    private fun setMobileNumberValidations() {
        etMobileRegistration.filters = arrayOf(InputFilter.LengthFilter(BuildConfig.MOBILE_NO_LENGTH))
    }

    private fun keyboardEnterFunctionality() {
        etRePasswordRegistration.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btNextRegistration.hideKeyboard()
                    viewModel.callRegistrationOtpApi()
                    true
                }
                else -> false
            }
        }
    }

    private val observerValidation = Observer<RegistrationViewModel.Validation> {
        when(it) {
            is RegistrationViewModel.Validation.UserName -> tilUsernameRegistration.putError(getString(it.errorMessageCode))
            is RegistrationViewModel.Validation.MobileNumber -> tilMobileRegistration.putError(getString(it.errorMessageCode))
            is RegistrationViewModel.Validation.Email -> tilEmailRegistration.putError(getString(it.errorMessageCode))
            is RegistrationViewModel.Validation.Password -> tilPasswordRegistration.putError(getString(it.errorMessageCode))
            is RegistrationViewModel.Validation.ConfirmPassword -> tilRePasswordRegistration.putError(getString(it.errorMessageCode))
            else -> { Log.w("log", "New Validation") }
        }
    }

    private val observerOtp = Observer<ResponseStatus<RegistrationOtpResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                val pairTitle = androidx.core.util.Pair<View, String>(tvSignUpTitle, "sign_up_title")
                val pairButton = androidx.core.util.Pair<View, String>(btNextRegistration, "transition_button")
                val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairTitle, pairButton)

                if (BuildConfig.BUILD_TYPE == BUILD_TYPE_UAT)
                    showNotification(this, it.response.respMsg ?: "")

                val intent = Intent(this, RegistrationOtpActivity::class.java)
                intent.putExtra("userName", etUserNameRegistration.getTextTrimmed())
                intent.putExtra("mobileNumber", etMobileRegistration.getTextTrimmed())
                intent.putExtra("emailId", etEmailRegistration.getTextTrimmed())
                intent.putExtra("password", etPasswordRegistration.getTextTrimmed())
                startActivity(intent, options.toBundle())
            }

            is ResponseStatus.Error ->
                PayprErrorDialog(this, getString(R.string.sign_up_error), it.errorMessage.getMsg(this)) {}.showDialog()

            is ResponseStatus.TechnicalError ->
                PayprErrorDialog(this, getString(R.string.sign_up_error), getString(it.errorMessageCode)) {}.showDialog()
        }
    }

    private val observerValidationApi = Observer<ResponseStatus<RegistrationValidationResponseData>> {
        when(it) {
            is ResponseStatus.Success -> viewModel.callRegistrationOtpApi()

            is ResponseStatus.Error ->
                PayprErrorDialog(this, getString(R.string.sign_up_error), it.errorMessage.getMsg(this)) {}.showDialog()

            is ResponseStatus.TechnicalError ->
                PayprErrorDialog(this, getString(R.string.sign_up_error), getString(it.errorMessageCode)) {}.showDialog()
        }
    }

    fun onSignUpIn(view: View) {
        val pairLogo    = androidx.core.util.Pair<View, String>(ivLogo, "splash_to_login")
        val options     = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairLogo)
        startActivity(Intent(this, LoginActivity::class.java), options.toBundle())
    }

    override fun hideKeyboard() {
        btNextRegistration.hideKeyboard()
    }

    private fun showNotification(context: Context, body: String) {
        val notificationManager =
            context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val notificationId = 1
        val channelId = "channel-01"
        val channelName = "Channel Name"
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_HIGH
            val mChannel = NotificationChannel(channelId, channelName, importance)
            notificationManager.createNotificationChannel(mChannel)
        }
        val mBuilder: NotificationCompat.Builder = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(R.drawable.ic_svg_paypr_logo)
            .setContentTitle("Paypr")
            .setContentText(body)
            .setStyle(NotificationCompat.BigTextStyle().bigText(body))

        notificationManager.notify(notificationId, mBuilder.build())
    }

    fun onClickTnc(view: View) {
        val pairButton  = androidx.core.util.Pair<View, String>(btNextRegistration, "transition_button")
        val options     = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairButton)
        startActivity(Intent(this, TermsAndConditionsActivity::class.java), options.toBundle())
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