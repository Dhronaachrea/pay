package com.skilrock.paypr.paypr_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.FragmentTransactionBinding
import com.skilrock.paypr.paypr_app.adapter.MyTransactionPayprAdapter
import com.skilrock.paypr.paypr_app.data_class.response_data.MyTransactionResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.dialog.PayprTransactionSortDialog
import com.skilrock.paypr.paypr_app.ui.activity.LoginActivity
import com.skilrock.paypr.paypr_app.viewmodel.TransactionViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_home_paypr.*
import kotlinx.android.synthetic.main.fragment_transaction.*

class TransactionFragment : BaseFragmentPaypr() {

    private lateinit var viewModel: TransactionViewModel
    private lateinit var binding: FragmentTransactionBinding
    private lateinit var adapter: MyTransactionPayprAdapter
    private var isFirstTimeResponse = true
    private var txnList: ArrayList<MyTransactionResponseData.Txn?>? = null
    private var selectedTxnType = "ALL"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_transaction, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setToolbarElements()
        setUpRecyclerView()
        loadListData()
        setOnClickListeners()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(TransactionViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataResponse.observe(viewLifecycleOwner, observerMyTransactions)
    }

    private fun setUpRecyclerView() {
        rvReportTransactions.layoutManager = LinearLayoutManager(master)
        rvReportTransactions.setHasFixedSize(true)
        adapter = MyTransactionPayprAdapter()
        rvReportTransactions.adapter = adapter
    }

    private fun loadListData() {
        viewModel.callTransactionApi()
    }

    private fun setOnClickListeners() {

        containerFromDate.setOnClickListener { openStartDateDialog(master, tvStartDate, tvEndDate) }

        containerEndDate.setOnClickListener { openEndDateDialog(master, tvStartDate, tvEndDate) }
    }

    private val observerMyTransactions = Observer<ResponseStatus<MyTransactionResponseData>> {
        adapter.clearData()
        txnList?.clear()
        when(it) {
            is ResponseStatus.Success -> {
                txnList = it.response.txnList
                val isListEmpty: Boolean = it.response.txnList?.isEmpty() ?: true
                if (isListEmpty) {
                    if (!isFirstTimeResponse) {
                        PayprErrorDialog(master, getString(R.string.my_transaction), getString(R.string.no_transactions_found)) {}.showDialog()
                    }
                }
                else {
                    val list: ArrayList<MyTransactionResponseData.Txn?> = ArrayList()
                    txnList?.let { listTxn ->
                        for (txn in listTxn)
                            list.add(txn)

                        adapter.setTxnList(list)
                    }
                }

                isFirstTimeResponse = false
            }

            is ResponseStatus.Error -> {
                if (!isFirstTimeResponse) {
                    PayprErrorDialog(master, getString(R.string.my_transaction), it.errorMessage.getMsg(master), "", it.errorCode,
                        Intent(master, LoginActivity::class.java)) {}.showDialog()
                }

                isFirstTimeResponse = false
            }

            is ResponseStatus.TechnicalError -> {
                if (!isFirstTimeResponse) {
                    PayprErrorDialog(master, getString(R.string.my_transaction), getString(it.errorMessageCode)) {}.showDialog()
                }

                isFirstTimeResponse = false
            }
        }
    }

    private fun onSortItemSelected(id: String) {
        selectedTxnType = id
        if (id == "ALL") {
            adapter.clearData()
            val list: ArrayList<MyTransactionResponseData.Txn?> = ArrayList()
            txnList?.let { listTxn ->
                for (txn in listTxn)
                    list.add(txn)

                adapter.setTxnList(list)
            }
        } else {
            val list: ArrayList<MyTransactionResponseData.Txn?> = ArrayList()
            for (index in 0 until (txnList?.size ?: 0)) {
                if (txnList?.get(index)?.txnType == id) list.add(txnList?.get(index))
            }
            adapter.clearData()
            adapter.setTxnList(list)
        }
    }

    override fun hideKeyboard() {
        //Code here to close keyboard
    }

    override fun setToolbarElements() {
        master.setToolbarItems(getString(R.string.transaction_history), false, showThreeDots = false)

        master.ivThreeDotsMenu.setOnClickListener {
            PayprTransactionSortDialog(master, selectedTxnType, ::onSortItemSelected).showDialog()
        }
    }

}