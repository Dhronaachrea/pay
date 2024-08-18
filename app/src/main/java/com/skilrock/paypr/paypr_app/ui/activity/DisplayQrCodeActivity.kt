package com.skilrock.paypr.paypr_app.ui.activity

import android.os.Bundle
import android.util.DisplayMetrics
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import com.skilrock.paypr.R
import com.skilrock.paypr.paypr_app.dialog.QrCodeAddAmountBottomSheetDialog
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_display_qr_code.*
import kotlinx.android.synthetic.main.activity_registration_otp.toolbar


class DisplayQrCodeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_display_qr_code)

        adjustDisplayScale(this)
        toolbarNavigation()
        initializeQrImageView()
        initialSetup()
        renderQr()
    }

    private fun toolbarNavigation() {
        toolbar.setNavigationOnClickListener {
            vibrate(this)
            onBackPressed()
        }
    }

    private fun initializeQrImageView() {
        val displayMetrics = DisplayMetrics()

        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            val display = display
            display?.getRealMetrics(displayMetrics)
            ivQrCode.layoutParams.height = displayMetrics.widthPixels
        } else {
            @Suppress("DEPRECATION")
            windowManager.defaultDisplay.getMetrics(displayMetrics)
            ivQrCode.layoutParams.height = displayMetrics.widthPixels
        }

        ivQrCode.layoutParams.width = LinearLayout.LayoutParams.MATCH_PARENT
    }

    private fun initialSetup() {
        tvNameQr.text   = (PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName())
        tvMobileQr.text = PlayerInfo.getPlayerMobileNumber()
    }

    private fun renderQr(amount: String = "") {
        val name = PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName()
        val mobileNumber = PlayerInfo.getPlayerMobileNumber()
        val qrCodeData = QrCodeData(name = name, destinationAccount = mobileNumber, merchantId = MERCHANT_ID_INFINITI, amount = amount)
        val serializeString = Gson().toJson(qrCodeData)
        val bitmap = QRCodeHelper(this)
            .setContent(serializeString)
            .setErrorCorrectionLevel(ErrorCorrectionLevel.Q)
            .setMargin(2)
            .qrcOde
        ivQrCode.setImageBitmap(bitmap)
    }

    fun onClickAddAmount(view: View) {
        if (tvEnterAmount.text.toString().trim() == getString(R.string.enter_amount_init_caps)) {
            QrCodeAddAmountBottomSheetDialog(::onAddAmountToQr).apply {
                show(supportFragmentManager, QrCodeAddAmountBottomSheetDialog.TAG)
            }
        }
        else {
            tvEnterAmount.text      = getString(R.string.enter_amount_init_caps)
            tvOptional.visibility   = View.VISIBLE
            tvAmount.text           = ""
            tvAmount.visibility     = View.GONE
            renderQr("")
        }
    }

    private fun onAddAmountToQr(amount: String) {
        tvEnterAmount.text          = getString(R.string.remove_amount)
        tvOptional.visibility       = View.INVISIBLE
        tvAmount.text               = ("${PlayerInfo.getCurrencyDisplayCode()} $amount")
        tvAmount.visibility         = View.VISIBLE
        llEnterAmount.visibility    = View.GONE
        renderQr(amount)
    }

}