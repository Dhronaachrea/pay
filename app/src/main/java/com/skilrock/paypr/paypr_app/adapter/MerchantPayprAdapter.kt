package com.skilrock.paypr.paypr_app.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.RecyclerView
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.RowMerchantBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.MerchantLocationResponseData.ResponseData.Data
import com.skilrock.paypr.paypr_app.dialog.PayprCallMapDialog
import com.skilrock.paypr.paypr_app.ui.fragment.MerchantsFragment
import com.skilrock.paypr.utility.showDialog


class MerchantPayprAdapter(private val activity: AppCompatActivity, private val context: Context,
                           private val fragment: MerchantsFragment)
    : RecyclerView.Adapter<MerchantPayprAdapter.MerchantViewHolder>() {

    private var merchantList: java.util.ArrayList<Data?>? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MerchantViewHolder {
        val binding: RowMerchantBinding = DataBindingUtil.inflate(LayoutInflater.from(
            parent.context), R.layout.row_merchant, parent, false)
        return MerchantViewHolder(binding)
    }

    override fun onBindViewHolder(holder: MerchantViewHolder, position: Int) {
        holder.rowMerchantBinding.merchant = merchantList?.get(position)
        holder.rowMerchantBinding.llMerchantRow.setOnClickListener {

            holder.rowMerchantBinding.merchant?.let { merchant ->
                PayprCallMapDialog(context, activity, merchant).showDialog()
            }
        }
    }

    override fun getItemCount(): Int {
        return merchantList?.size ?: 0
    }

    fun setMerchantList(list: ArrayList<Data?>?) {
        merchantList = list
        notifyDataSetChanged()
        fragment.scrollToTop()
    }

    class MerchantViewHolder(val rowMerchantBinding: RowMerchantBinding) :
        RecyclerView.ViewHolder(rowMerchantBinding.root)
}