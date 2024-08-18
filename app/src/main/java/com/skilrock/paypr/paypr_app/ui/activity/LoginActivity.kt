package com.skilrock.paypr.paypr_app.ui.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.EditorInfo
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.ActivityLoginBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.LoginResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.viewmodel.LoginViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_login.*

class LoginActivity : BaseActivity() {

    private lateinit var viewModel: LoginViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setDataBindingAndViewModel()
        adjustDisplayScale(this)
        keyboardEnterFunctionality()
        onClickListener()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityLoginBinding = DataBindingUtil.setContentView(this, R.layout.activity_login)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(LoginViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLinearLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
        viewModel.liveDataValidation.observe(this, observerValidation)
        viewModel.liveDataLoginStatus.observe(this, observerLoginStatus)
    }

    private fun keyboardEnterFunctionality() {
        etPassword.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    etPassword.hideKeyboard()
                    viewModel.onLoginButtonClick(SharedPrefUtils.getFcmToken(this@LoginActivity))
                    true
                }
                else -> false
            }
        }
    }

    private fun onClickListener() {
        btnLogin.setOnClickListener {
            viewModel.onLoginButtonClick(SharedPrefUtils.getFcmToken(this@LoginActivity))
        }
    }

    fun onForgotPasswordClick(view: View) {
        val pairInput   = androidx.core.util.Pair<View, String>(llInputs, "splash_to_login_inputs")
        val pairButton  = androidx.core.util.Pair<View, String>(btnLogin, "transition_button")

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairInput, pairButton)

        startActivity(Intent(this, ForgotPasswordActivity::class.java), options.toBundle())
    }

    fun onSignUpClick(view: View) {
        val pairSignIn  = androidx.core.util.Pair<View, String>(tvSignUp, "transition_sign_in_up")
        val pairLogo    = androidx.core.util.Pair<View, String>(ivLogo, "splash_to_login")
        val pairInput   = androidx.core.util.Pair<View, String>(llInputs, "splash_to_login_inputs")
        val pairButton  = androidx.core.util.Pair<View, String>(btnLogin, "transition_button")

        val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this, pairSignIn, pairLogo, pairInput, pairButton)

        startActivity(Intent(this, RegistrationActivity::class.java), options.toBundle())
    }

    private val observerLoginStatus = Observer<ResponseStatus<LoginResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                PlayerInfo.setLoginData(this, it.response)
                openHomeActivity()
            }

            is ResponseStatus.Error -> {
                PayprErrorDialog(this, getString(R.string.login_error), it.errorMessage.getMsg(this)) {}.showDialog()
            }

            is ResponseStatus.TechnicalError ->
                PayprErrorDialog(this, getString(R.string.login_error), getString(it.errorMessageCode)) {}.showDialog()
        }
    }

    private fun openHomeActivity() {
        val intent = Intent(this, GreetingActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(intent)
        overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
    }

    private val observerValidation = Observer<LoginViewModel.Validation> {
        when(it) {
            is LoginViewModel.Validation.UserNameValidation -> textInputLayoutUser.putError(getString(it.errorMessageCode))
            is LoginViewModel.Validation.PasswordValidation -> textInputLayoutPassword.putError(getString(it.errorMessageCode))
        }
    }

    override fun hideKeyboard() {
        btnLogin.hideKeyboard()
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