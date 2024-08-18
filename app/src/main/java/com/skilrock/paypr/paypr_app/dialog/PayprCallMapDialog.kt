package com.skilrock.paypr.paypr_app.dialog

import android.app.Dialog
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Window
import androidx.appcompat.app.AppCompatActivity
import androidx.core.text.HtmlCompat
import com.google.android.material.snackbar.Snackbar
import com.skilrock.paypr.R
import com.skilrock.paypr.paypr_app.data_class.response_data.MerchantLocationResponseData
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.activity_home_paypr.*
import kotlinx.android.synthetic.main.layout_call_map_dialog_paypr.*

class PayprCallMapDialog(context: Context, private val activity: AppCompatActivity,
                         private val merchant: MerchantLocationResponseData.ResponseData.Data) : Dialog(context) {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        setContentView(R.layout.layout_call_map_dialog_paypr)
        setCancelable(true)

        tvHeaderDialog.text = merchant.orgName

        llCallMerchant.setOnClickListener {
            vibrate(context)

            val contact = merchant.mobileCode + merchant.mobileNumber

            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:${contact}" )
            context.startActivity(intent)

            dismiss()
        }

        llGetDirection.setOnClickListener {
            vibrate(context)

            val latitude    = merchant.latitudes ?: "0.0"
            val longitude   = merchant.longitudes ?: "0.0"

            val mapIntent = Intent(Intent.ACTION_VIEW, Uri.parse("http://maps.google.com/maps?daddr=${latitude},${longitude}"))
            mapIntent.setPackage("com.google.android.apps.maps")
            try {
                activity.startActivity(mapIntent)
            } catch (ex: ActivityNotFoundException) {
                Snackbar.make(activity.llHomeContainer, HtmlCompat.fromHtml("<font color=\"#FF7878\">${
                        context.getString(R.string.maps_not_installed)
                    }</font>", HtmlCompat.FROM_HTML_MODE_LEGACY), Snackbar.LENGTH_LONG).show()
            }

            dismiss()
        }
    }

}