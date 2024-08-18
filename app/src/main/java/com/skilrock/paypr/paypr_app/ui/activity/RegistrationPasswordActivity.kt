package com.skilrock.paypr.paypr_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.inputmethod.EditorInfo
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.ActivityRegistrationPasswordBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.RegistrationResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.dialog.PayprSuccessDialog
import com.skilrock.paypr.paypr_app.viewmodel.RegistrationViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_registration_password.*

class RegistrationPasswordActivity : BaseActivity() {

    private lateinit var userName: String
    private lateinit var mobileNumber: String
    private lateinit var emailId: String
    private lateinit var otp: String
    private lateinit var viewModel: RegistrationViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDataBindingAndViewModel()
        keyboardEnterFunctionality()
        checkAllPreviousDataIsAvailable()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityRegistrationPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_registration_password)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(RegistrationViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataValidation.observe(this, observerValidation)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataRegistrationStatus.observe(this, observerRegistrationStatus)
    }

    private fun checkAllPreviousDataIsAvailable() {
        userName        = intent.getStringExtra("userName") ?: ""
        mobileNumber    = intent.getStringExtra("mobileNumber") ?: ""
        emailId         = intent.getStringExtra("emailId") ?: ""
        otp             = intent.getStringExtra("otp") ?: ""

        if (userName.isBlank() or mobileNumber.isBlank() or emailId.isBlank() or otp.isBlank()) {
            PayprErrorDialog(this, getString(R.string.sign_up_error), getString(R.string.some_technical_issue)) {
                finish()
            }.showDialog()
        } else {
            viewModel.name          = userName
            viewModel.mobileNumber  = mobileNumber
            viewModel.email         = emailId
            viewModel.otp           = otp
        }
    }

    private fun keyboardEnterFunctionality() {
        etRePasswordRegistration.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.onRegisterButtonClick()
                    true
                }
                else -> false
            }
        }
    }

    private val observerRegistrationStatus = Observer<ResponseStatus<RegistrationResponseData>> {
        when(it) {
            is ResponseStatus.Success ->
                PayprSuccessDialog(this, getString(R.string.success),
                    getString(R.string.your_account_has_been_created), getString(R.string.continue_)) { openLoginActivity() }.showDialog()

            is ResponseStatus.Error ->
                PayprErrorDialog(this, getString(R.string.sign_up_error), it.errorMessage.getMsg(this)) {}.showDialog()

            is ResponseStatus.TechnicalError ->
                PayprErrorDialog(this, getString(R.string.sign_up_error), getString(it.errorMessageCode)) {}.showDialog()
        }
    }

    private val observerValidation = Observer<RegistrationViewModel.Validation> {
        when(it) {
            is RegistrationViewModel.Validation.Password -> tilPasswordRegistration.putError(getString(it.errorMessageCode))
            is RegistrationViewModel.Validation.ConfirmPassword -> tilRePasswordRegistration.putError(getString(it.errorMessageCode))
            else -> { Log.w("log", "New Validation") }
        }
    }

    override fun hideKeyboard() {
        btSignUpRegistration.hideKeyboard()
    }

    fun onSignUpIn(view: View) {
        openLoginActivity()
    }

    private fun openLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    fun onClickBack(view: View) { onBackPressed() }

    fun onClickTnc(view: View) {
        val pairButton  = androidx.core.util.Pair<View, String>(btSignUpRegistration, "transition_button")
        val options     = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairButton)
        startActivity(Intent(this, TermsAndConditionsActivity::class.java), options.toBundle())
    }
}