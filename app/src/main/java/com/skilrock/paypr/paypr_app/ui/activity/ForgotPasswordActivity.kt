package com.skilrock.paypr.paypr_app.ui.activity

import android.os.Bundle
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.ActivityForgotPasswordBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.ForgotPasswordResponseData
import com.skilrock.paypr.paypr_app.dialog.ForgotPasswordOtpBottomSheetDialog
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.dialog.PayprSuccessDialog
import com.skilrock.paypr.paypr_app.viewmodel.ForgotPasswordViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_forgot_password.*

class ForgotPasswordActivity : BaseActivity() {

    lateinit var viewModel: ForgotPasswordViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDataBindingAndViewModel()
        toolbarNavigation()
        keyboardEnterFunctionality()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityForgotPasswordBinding = DataBindingUtil.setContentView(this, R.layout.activity_forgot_password)
        viewModel = ViewModelProvider(this).get(ForgotPasswordViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataValidateMobileNumber.observe(this, observerValidation)
        viewModel.liveDataOtpStatus.observe(this, observerOtpStatus)
        viewModel.liveDataResetPasswordStatus.observe(this, observerResetPasswordStatus)
    }

    private fun toolbarNavigation() {
        toolbar.setNavigationOnClickListener {
            vibrate(this)
            onBackPressed()
        }
    }

    private fun keyboardEnterFunctionality() {
        etMobile.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnGenerateCode.hideKeyboard()
                    viewModel.sendOtp(true)
                    true
                }
                else -> false
            }
        }
    }

    private val observerValidation = Observer<Int> {
        tilMobile.putError(getString(it))
    }

    private val observerOtpStatus = Observer<ResponseStatus<ForgotPasswordResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                if (it.response.openDialog) {
                    ForgotPasswordOtpBottomSheetDialog(etMobile.getTrimText(), ::onResetPasswordClick, ::onResendOtp).apply {
                        show(supportFragmentManager, ForgotPasswordOtpBottomSheetDialog.TAG)
                    }
                } else {
                    Toast.makeText(this, "Code sent successfully.", Toast.LENGTH_SHORT).show()
                }
            }

            is ResponseStatus.Error ->
                PayprErrorDialog(this, getString(R.string.forgot_password_error), it.errorMessage.getMsg(this)) {}.showDialog()

            is ResponseStatus.TechnicalError ->
                PayprErrorDialog(this, getString(R.string.forgot_password_error), getString(it.errorMessageCode)) {}.showDialog()
        }
    }

    private val observerResetPasswordStatus = Observer<ResponseStatus<ForgotPasswordResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                PayprSuccessDialog(this, getString(R.string.success),
                    getString(R.string.password_changed_successfully), getString(R.string.continue_)) { this.finish() }.showDialog()
            }

            is ResponseStatus.Error ->
                PayprErrorDialog(this, getString(R.string.forgot_password_error), it.errorMessage.getMsg(this)) {}.showDialog()

            is ResponseStatus.TechnicalError ->
                PayprErrorDialog(this, getString(R.string.forgot_password_error), getString(it.errorMessageCode)) {}.showDialog()
        }
    }

    private fun onResetPasswordClick(otp: String, newPassword: String, confirmPassword: String ) {
        viewModel.resetPassword(otp, newPassword, confirmPassword)
    }

    private fun onResendOtp() {
        viewModel.sendOtp(false)
    }

    override fun hideKeyboard() {
        btnGenerateCode.hideKeyboard()
    }

}