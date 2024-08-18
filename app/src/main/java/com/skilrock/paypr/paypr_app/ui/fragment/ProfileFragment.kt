package com.skilrock.paypr.paypr_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.navigation.fragment.FragmentNavigatorExtras
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.FragmentProfileBinding
import com.skilrock.paypr.paypr_app.adapter.MyTransactionPayprAdapter
import com.skilrock.paypr.paypr_app.data_class.response_data.BalanceResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.MyTransactionResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.ui.activity.LoginActivity
import com.skilrock.paypr.paypr_app.viewmodel.ProfileViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.fragment_profile.*

class ProfileFragment : BaseFragmentPaypr() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentProfileBinding
    private lateinit var adapter: MyTransactionPayprAdapter
    private var isListLoadable = true

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setToolbarElements()
        clickListener()
        setUpRecyclerView()
        loadListData()
    }

    override fun onResume() {
        super.onResume()
        reflectProfileImage()
        viewModel.playerName.postValue(PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName())
        viewModel.resetBalance()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataResponse.observe(viewLifecycleOwner, observerMyTransactions)
        viewModel.liveDataBalanceStatus.observe(viewLifecycleOwner, observerBalanceStatus)
    }

    private fun reflectProfileImage() {
        Log.i("log", "Profile Pic Url: ${PlayerInfo.getProfilePicPath()}")
        Glide.with(master.applicationContext)
            .load(PlayerInfo.getProfilePicPath())
            .apply(
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
            .into(ivProfileImage)
    }

    private fun setUpRecyclerView() {
        rvTransactionsProfile.layoutManager = LinearLayoutManager(master)
        rvTransactionsProfile.setHasFixedSize(true)
        adapter = MyTransactionPayprAdapter()
        rvTransactionsProfile.adapter = adapter
    }

    private fun loadListData() {
        if (isListLoadable) {
            viewModel.callTransactionApi()
            isListLoadable = false
        }
    }

    private val observerMyTransactions = Observer<ResponseStatus<MyTransactionResponseData>> {
        adapter.clearData()
        viewModel.callBalanceApi(false)
        when(it) {
            is ResponseStatus.Success -> {
                val txnList: ArrayList<MyTransactionResponseData.Txn?>? = it.response.txnList
                val isListEmpty: Boolean = it.response.txnList?.isEmpty() ?: true
                if (isListEmpty) {
                    tvNoTransactionsProfile.visibility  = View.VISIBLE
                    llTransactionsProfile.visibility    = View.GONE
                    tvNoTransactionsProfile.text        = getString(R.string.you_have_not_done_any_transactions_lately)
                }
                else {
                    tvNoTransactionsProfile.visibility  = View.GONE
                    llTransactionsProfile.visibility    = View.VISIBLE
                    adapter.setTxnList(txnList)
                }
            }

            is ResponseStatus.Error -> {
                if (it.errorCode == SESSION_EXPIRE_CODE) {
                    PayprErrorDialog(master, getString(R.string.profile), it.errorMessage.getMsg(master), "", it.errorCode,
                        Intent(master, LoginActivity::class.java)
                    ) {}.showDialog()
                } else {
                    tvNoTransactionsProfile.visibility  = View.VISIBLE
                    llTransactionsProfile.visibility    = View.GONE
                    tvNoTransactionsProfile.text =
                        getString(R.string.problem_fetching_recent_transactions)
                }
            }

            is ResponseStatus.TechnicalError -> {
                tvNoTransactionsProfile.visibility  = View.VISIBLE
                llTransactionsProfile.visibility    = View.GONE
                tvNoTransactionsProfile.text        = getString(R.string.problem_fetching_recent_transactions)
            }
        }
    }

    private val observerBalanceStatus = Observer<ResponseStatus<BalanceResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                it.response.wallet?.let { wallet ->
                    PlayerInfo.setBalance(master, wallet)
                }
            }

            is ResponseStatus.Error -> { Log.e("log", "Error in fetching balance.") }

            is ResponseStatus.TechnicalError -> { Log.e("log", "Technical error in fetching balance.") }
        }
    }

    private fun clickListener() {
        cardProfile.setOnClickListener {
            val extras = FragmentNavigatorExtras(cardProfile to "profile_to_personal_data")

            val direction: NavDirections = ProfileFragmentDirections.actionProfileFragmentToPersonalDataFragment("")
            Navigation.findNavController(it).navigate(direction, extras)
        }
    }

    override fun hideKeyboard() {
        //Code here to close the keyboard
    }

    override fun setToolbarElements() {
        master.setToolbarItems(getString(R.string.profile), false, showThreeDots = false)
    }

}