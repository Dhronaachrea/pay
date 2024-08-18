package com.skilrock.paypr.paypr_app.dialog

import android.app.Dialog
import android.content.Context
import android.os.Bundle
import android.view.Window
import com.google.android.material.radiobutton.MaterialRadioButton
import com.skilrock.paypr.R
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.layout_transaction_sort_dialog_paypr.*

class PayprTransactionSortDialog(context: Context, private val alreadySelectedType: String, private val operation:(String)->Unit) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_transaction_sort_dialog_paypr)
        setCancelable(false)

        setSelectedTxnType()

        tvAgree.setOnClickListener {
            vibrate(context)
            dismiss()
            val selectedId = radioGroup.checkedRadioButtonId
            val selectedRadio: MaterialRadioButton = findViewById(selectedId)
            operation(selectedRadio.tag.toString())
        }

        tvDisagree.setOnClickListener {
            vibrate(context)
            dismiss()
        }
    }

    private fun setSelectedTxnType() {
        when (alreadySelectedType) {
            "ALL"               -> radioAll.isChecked = true
            "PLR_WAGER"         -> radioWager.isChecked = true
            "PLR_DEPOSIT"       -> radioDeposit.isChecked = true
            "PRL_WITHDRAWAL"    -> radioWithdrawal.isChecked = true
            "PLR_WINNING"       -> radioWinning.isChecked = true
            "TRANSFER_IN"       -> radioCashIn.isChecked = true
            "TRANSFER_OUT"      -> radioCashOut.isChecked = true
        }
    }

}