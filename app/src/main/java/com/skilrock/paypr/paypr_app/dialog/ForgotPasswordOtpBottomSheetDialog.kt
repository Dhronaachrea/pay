package com.skilrock.paypr.paypr_app.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.core.text.HtmlCompat
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.getTrimText
import com.skilrock.paypr.utility.putError
import kotlinx.android.synthetic.main.bottom_sheet_otp_forgot_password.*

class ForgotPasswordOtpBottomSheetDialog(private val mobileNumber: String,
                                         val onRegisterClick: (String, String, String) -> Unit,
                                         val onResendOtp: () -> Unit) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ForgotPasswordOtpBottomSheetDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogSheetStyle)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.bottom_sheet_otp_forgot_password, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val message     = getString(R.string.verification_code_successfully_sent_to) + " <b>" + mobileNumber + "</b>."
        tvMessage.text  = HtmlCompat.fromHtml(message, HtmlCompat.FROM_HTML_MODE_LEGACY)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnResetPassword.setOnClickListener {
            if (validate()) {
                dismiss()
                onRegisterClick(etOtp.getTrimText(), etNewPassword.getTrimText(), etConfirmPassword.getTrimText())
            }
        }

        tvResendOtp.setOnClickListener {
            onResendOtp()
        }

        etConfirmPassword.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnResetPassword.performClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun validate() : Boolean {
        if (etOtp.getTrimText().isBlank()) {
            tilOtp.putError(getString(R.string.enter_code))
            return false
        }
        if (etOtp.getTrimText().length < 6) {
            tilOtp.putError(getString(R.string.enter_valid_code))
            return false
        }
        if (etNewPassword.getTrimText().isEmpty() || etNewPassword.getTrimText().length < 8) {
            tilNewPassword.putError(getString(R.string.password_eight_characters))
            return false
        }
        if (etNewPassword.getTrimText() != etConfirmPassword.getTrimText()) {
            tilConfirmPassword.putError(getString(R.string.passwords_do_not_match))
            return false
        }
        return true
    }

}