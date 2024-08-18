package com.skilrock.paypr.paypr_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.FragmentChangePasswordBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.ChangePasswordResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.dialog.PayprSuccessDialog
import com.skilrock.paypr.paypr_app.ui.activity.LoginActivity
import com.skilrock.paypr.paypr_app.viewmodel.ProfileViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.fragment_change_password.*

class ChangePasswordFragment : BaseFragmentPaypr() {


    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentChangePasswordBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_change_password, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setToolbarElements()
        keyboardEnterFunctionality()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataChangePassword.observe(viewLifecycleOwner, observerResponseStatus)
        viewModel.liveDataValidationChangePassword.observe(viewLifecycleOwner, observerValidation)
    }

    private fun keyboardEnterFunctionality() {
        etReNewPassword.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.callChangePasswordApi()
                    true
                }
                else -> false
            }
        }
    }

    private val observerResponseStatus = Observer<ResponseStatus<ChangePasswordResponseData>> {
        when (it) {
            is ResponseStatus.Success -> PayprSuccessDialog(master,
                getString(R.string.change_password),
                getString(R.string.password_changed_successfully), getString(R.string.continue_)) {
                performLogoutCleanUp(master, Intent(master, LoginActivity::class.java))
            }.showDialog()

            is ResponseStatus.Error -> PayprErrorDialog(master,
                getString(R.string.change_password_error),
                it.errorMessage.getMsg(master), "", it.errorCode,
                Intent(master, LoginActivity::class.java)) {}.showDialog()

            is ResponseStatus.TechnicalError -> PayprErrorDialog(
                master, getString(R.string.change_password_error), getString(it.errorMessageCode)
            ) {}.showDialog()

        }
    }

    private val observerValidation = Observer<ProfileViewModel.ChangePasswordValidation> {
        when (it) {
            is ProfileViewModel.ChangePasswordValidation.OldPasswordValidation ->
                tilCurrentPassword.putError(getString(it.errorMessageCode))

            is ProfileViewModel.ChangePasswordValidation.NewPasswordValidation ->
                tilNewPassword.putError(getString(it.errorMessageCode))

            is ProfileViewModel.ChangePasswordValidation.ConfirmPasswordValidation ->
                tilReNewPassword.putError(getString(it.errorMessageCode))
        }
    }

    override fun hideKeyboard() {
        btnSaveChanges.hideKeyboard()
    }

    override fun setToolbarElements() {
        master.setToolbarItems(getString(R.string.change_password), showBackArrow = true, showThreeDots = false)
    }

}