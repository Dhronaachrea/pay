package com.skilrock.paypr.paypr_app.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.TransactionRowReportBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.MyTransactionResponseData


class MyTransactionPayprAdapter : RecyclerView.Adapter<MyTransactionPayprAdapter.MyTransactionViewHolder>() {

    private var txnList: ArrayList<MyTransactionResponseData.Txn?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyTransactionViewHolder {
        val binding: TransactionRowReportBinding = DataBindingUtil.inflate(LayoutInflater.from(
            parent.context), R.layout.transaction_row_report, parent, false)
        return MyTransactionViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MyTransactionViewHolder, position: Int) {
        holder.rowTransactionReportBinding.txn = txnList?.get(position)
    }

    override fun getItemCount(): Int {
        return txnList?.size ?: 0
    }

    fun setTxnList(list: ArrayList<MyTransactionResponseData.Txn?>?) {
        txnList = list
        notifyDataSetChanged()
    }

    fun clearData() {
        txnList?.clear()
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return txnList?.get(position)?.transactionId?.toLong() ?: -1
    }

    class MyTransactionViewHolder(val rowTransactionReportBinding: TransactionRowReportBinding) :
        RecyclerView.ViewHolder(rowTransactionReportBinding.root)
}