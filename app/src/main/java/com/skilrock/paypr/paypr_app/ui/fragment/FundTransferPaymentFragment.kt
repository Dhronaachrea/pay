package com.skilrock.paypr.paypr_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.FragmentFundTransferPaymentBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.BalanceResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.FundTransferFromWalletResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.dialog.PayprSuccessDialog
import com.skilrock.paypr.paypr_app.ui.activity.LoginActivity
import com.skilrock.paypr.paypr_app.viewmodel.FundTransferViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.fragment_fund_transfer_payment.*

class FundTransferPaymentFragment : BaseFragmentPaypr() {

    private var _name = ""
    private var _destinationAccount = ""
    private var _merchantId = ""
    private var _title = ""
    private var _buttonTitle = ""

    private lateinit var viewModel: FundTransferViewModel
    private lateinit var binding: FragmentFundTransferPaymentBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_fund_transfer_payment, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        receiveParameters()
        setToolbarElements()
        keyboardEnterFunctionality()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(FundTransferViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataValidateAmount.observe(viewLifecycleOwner, observerValidation)
        viewModel.liveDataFundTransferStatus.observe(viewLifecycleOwner, observerResponseStatus)
        viewModel.liveDataBalanceStatus.observe(viewLifecycleOwner, observerBalanceStatus)
    }

    private fun receiveParameters() {
        tilAmountFTP.hint = (getString(R.string.enter_amount) + " (" + BuildConfig.CURRENCY_SYMBOL + ")")
        arguments?.let {
            _name               = FundTransferPaymentFragmentArgs.fromBundle(it).name
            _destinationAccount = FundTransferPaymentFragmentArgs.fromBundle(it).destinationAccount
            _merchantId         = FundTransferPaymentFragmentArgs.fromBundle(it).merchantId
            _buttonTitle        = FundTransferPaymentFragmentArgs.fromBundle(it).buttonTitle
            _title              = FundTransferPaymentFragmentArgs.fromBundle(it).title

            viewModel.destinationAccount    = _destinationAccount
            viewModel.merchantId            = _merchantId
            Log.w("log", "Name: $_name\nDestinationAccount: $_destinationAccount\nMerchantId: $_merchantId")
        }
    }

    override fun onResume() {
        super.onResume()
        arguments?.let {
            etAmountFTP.setText(FundTransferPaymentFragmentArgs.fromBundle(it).amount)
        }
    }

    private fun keyboardEnterFunctionality() {
        etAmountFTP.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    viewModel.onPayClick()
                    true
                }
                else -> false
            }
        }
    }

    override fun hideKeyboard() {
        etAmountFTP.hideKeyboard()
    }

    override fun setToolbarElements() {
        if (_name.isBlank()) {
            tvTitle.text = _destinationAccount
            tvSubTitle.visibility = View.GONE
        } else {
            tvTitle.text = _name
            tvSubTitle.text = _destinationAccount
        }
        master.setToolbarItems(_title, true, showThreeDots = false)
        btnSendFTP.text = _buttonTitle
    }

    private val observerValidation = Observer<Int> {
        tilAmountFTP.putError(getString(it))
    }

    private val observerResponseStatus = Observer<ResponseStatus<FundTransferFromWalletResponseData>> {
        when (it) {
            is ResponseStatus.Success -> { context?.let { cxt ->
                val msg = "<b>${BuildConfig.CURRENCY_SYMBOL} ${etAmountFTP.text.toString().trim()}</b> transferred successfully, with Transaction Id <b>${it.response.txnId}</b>"
                PayprSuccessDialog(cxt, _title, msg, getString(R.string.continue_), true) {
                    viewModel.callBalanceApi()
                }.showDialog()
            }
            }

            is ResponseStatus.Error -> activity?.let { act -> PayprErrorDialog(
                act, "$_title ${getString(R.string.error)}", it.errorMessage.getMsg(act), "", it.errorCode, Intent(master, LoginActivity::class.java)
            ) {}.showDialog() }

            is ResponseStatus.TechnicalError -> activity?.let { act -> PayprErrorDialog(
                act, "$_title ${getString(R.string.error)}", getString(it.errorMessageCode)
            ) {}.showDialog() }

        }
    }

    private val observerBalanceStatus = Observer<ResponseStatus<BalanceResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                it.response.wallet?.let { wallet ->
                    PlayerInfo.setBalance(master, wallet)
                    switchToHome()
                }
            }

            is ResponseStatus.Error -> { switchToHome() }

            is ResponseStatus.TechnicalError -> { switchToHome() }
        }
    }

}