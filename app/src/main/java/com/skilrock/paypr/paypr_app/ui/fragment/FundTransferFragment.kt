package com.skilrock.paypr.paypr_app.ui.fragment

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityOptionsCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.FragmentFundTransferBinding
import com.skilrock.paypr.paypr_app.ui.activity.ContactsActivity
import com.skilrock.paypr.paypr_app.ui.activity.ScannerActivity
import com.skilrock.paypr.paypr_app.viewmodel.FundTransferViewModel
import com.skilrock.paypr.utility.MERCHANT_ID_INFINITI
import com.skilrock.paypr.utility.getTextTrimmed
import com.skilrock.paypr.utility.putError
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.fragment_fund_transfer.*

class FundTransferFragment : BaseFragmentPaypr() {

    companion object {
        const val REQUEST_CODE_CONTACT = 101
        const val REQUEST_CODE_SCANNER = 102
    }

    private lateinit var viewModel: FundTransferViewModel
    private lateinit var binding: FragmentFundTransferBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fund_transfer, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setToolbarElements()
        setClickListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(FundTransferViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
    }

    private fun setClickListeners() {
        etMobileFT.setOnTouchListener(object : OnTouchListener {
            @SuppressLint("ClickableViewAccessibility")
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                if (event?.action == MotionEvent.ACTION_DOWN && event.rawX >= (etMobileFT.right - 90)) {
                    vibrate(master)
                    etMobileFT.setText("")
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(master)
                    startActivityForResult(Intent(master, ContactsActivity::class.java), REQUEST_CODE_CONTACT, options.toBundle())
                    //master.overridePendingTransition(R.anim.fade_in, R.anim.fade_out)
                    return true
                }
                return false
            }
        })

        btnNextFT.setOnClickListener {
            if (validate()) {
                val title       = if (switchRequest.isChecked) getString(R.string.request_money) else getString(R.string.send_money)
                val buttonTitle = if (switchRequest.isChecked) getString(R.string.request_payment) else getString(R.string.send_payment)

                val direction: NavDirections = FundTransferFragmentDirections
                    .actionFundTransferFragmentToFundTransferPaymentFragment("", etMobileFT.getTextTrimmed(), title, buttonTitle, MERCHANT_ID_INFINITI, "")

                Navigation.findNavController(it).navigate(direction)
            }
        }

        tvScanQr.setOnClickListener {
            etMobileFT.setText("")
            startActivityForResult(Intent(master, ScannerActivity::class.java), REQUEST_CODE_SCANNER)
            master.overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        }
    }

    private fun validate() : Boolean {
        if (etMobileFT.getTextTrimmed().isBlank()) {
            tilMobileFT.putError(getString(R.string.enter_mobile_number))
            return false
        }
        if (etMobileFT.getTextTrimmed().length < BuildConfig.MOBILE_NO_LENGTH) {
            tilMobileFT.putError(getString(R.string.enter_valid_mobile_number))
            return false
        }
        return true
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val title       = if (switchRequest.isChecked) getString(R.string.request_money) else getString(R.string.send_money)
        val buttonTitle = if (switchRequest.isChecked) getString(R.string.request_payment) else getString(R.string.send_payment)

        if (resultCode == AppCompatActivity.RESULT_OK) {
            if (requestCode == REQUEST_CODE_CONTACT) {
                val name                = data?.getStringExtra("name") ?: ""
                val merchantId          = data?.getStringExtra("merchantId") ?: ""
                val destinationAccount  = data?.getStringExtra("destinationAccount") ?: ""
                Log.i("log", "~~~ FROM CONTACT ACTIVITY ~~~\nName: $name\nMerchant Id: $merchantId\nDestination Account: $destinationAccount")

                val direction: NavDirections = FundTransferFragmentDirections
                    .actionFundTransferFragmentToFundTransferPaymentFragment(name, destinationAccount, title, buttonTitle, merchantId, "")
                Navigation.findNavController(llFundTransfer).navigate(direction)
            }
            else if (requestCode == REQUEST_CODE_SCANNER) {
                val name                = data?.getStringExtra("name") ?: ""
                val amount              = data?.getStringExtra("amount") ?: ""
                val merchantId          = data?.getStringExtra("merchantId") ?: ""
                val destinationAccount  = data?.getStringExtra("destinationAccount") ?: ""
                Log.i("log", "~~~ FROM SCANNER ACTIVITY ~~~\nName: $name\nMerchant Id: $merchantId\nDestination Account: $destinationAccount\nAmount: $amount")

                val direction: NavDirections = FundTransferFragmentDirections
                    .actionFundTransferFragmentToFundTransferPaymentFragment(name, destinationAccount, title, buttonTitle, merchantId, amount)
                Navigation.findNavController(llFundTransfer).navigate(direction)
            }
        }
    }

    override fun hideKeyboard() {
        //Code here to close keyboard
    }

    override fun setToolbarElements() {
        master.setToolbarItems(getString(R.string.transfers), showBackArrow = false, showThreeDots = false)
    }

}