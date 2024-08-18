package com.skilrock.paypr.paypr_app.dialog

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.getTextTrimmed
import com.skilrock.paypr.utility.getTrimText
import com.skilrock.paypr.utility.putError
import kotlinx.android.synthetic.main.bottom_sheet_qr_amount.*

class QrCodeAddAmountBottomSheetDialog(val onAddAmountToQr: (String) -> Unit) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "QrCodeAddAmountBottomSheetDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogSheetStyle)
        isCancelable = false
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.bottom_sheet_qr_amount, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        btnCancel.setOnClickListener {
            dismiss()
        }

        btnAdd.setOnClickListener {
            if (validate()) {
                dismiss()
                onAddAmountToQr(etAmountQR.getTrimText())
            }
        }
    }

    private fun validate() : Boolean {
        if (etAmountQR.getTextTrimmed().isBlank()) {
            tilAmountQR.putError(getString(R.string.enter_amount))
            return false
        }

        if (etAmountQR.getTextTrimmed().contains(".")) {
            Log.d("log", "Double Value")
            try {
                val amt = etAmountQR.getTextTrimmed().toDouble()
                if (amt == 0.0) {
                    tilAmountQR.putError(getString(R.string.enter_valid_amount))
                    return false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                tilAmountQR.putError(getString(R.string.enter_valid_amount))
                return false
            }
        }
        else {
            Log.d("log", "Integer Value")
            try {
                val amt = etAmountQR.getTextTrimmed().toInt()
                if (amt == 0) {
                    tilAmountQR.putError(getString(R.string.enter_valid_amount))
                    return false
                }
            } catch (e: Exception) {
                e.printStackTrace()
                tilAmountQR.putError(getString(R.string.enter_valid_amount))
                return false
            }
        }

        return true
    }

}