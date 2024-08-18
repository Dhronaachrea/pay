package com.skilrock.paypr.paypr_app.ui.activity

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.skilrock.paypr.R
import com.skilrock.paypr.paypr_app.data_class.response_data.MerchantLocationResponseData
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.activity_merchant_map.*

class MerchantMapActivity : AppCompatActivity() {

    private var _selectedMerchant: Int = -1
    private var merchantList: ArrayList<MerchantLocationResponseData.ResponseData.Data?>? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_merchant_map)
        toolbarNavigation()
        receiveDataFromPreviousActivity()
    }

    private fun receiveDataFromPreviousActivity() {
        toolbar.title       = intent.getStringExtra("title") ?: getString(R.string.retailers_location)
        _selectedMerchant   = intent.getIntExtra("selectedMerchant", -1)
        merchantList        = intent.getParcelableArrayListExtra("merchantList")

        for (merchant in merchantList!!) {
            Log.w("log", "receiveDataFromPreviousActivity: ${merchant?.orgName}")
        }
    }

    private fun toolbarNavigation() {
        toolbar.setNavigationOnClickListener {
            vibrate(this)
            onBackPressed()
        }
    }

}