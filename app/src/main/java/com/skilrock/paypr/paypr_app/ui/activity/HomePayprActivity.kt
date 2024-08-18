package com.skilrock.paypr.paypr_app.ui.activity

import android.os.Bundle
import android.os.SystemClock
import android.util.Log
import android.view.View
import androidx.core.content.ContextCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.NavigationUI
import androidx.navigation.ui.setupWithNavController
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.ActivityHomePayprBinding
import com.skilrock.paypr.paypr_app.viewmodel.HomePayprViewModel
import com.skilrock.paypr.utility.adjustDisplayScale
import com.skilrock.paypr.utility.getScreenResolution
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.activity_home_paypr.*


class HomePayprActivity : BaseActivity() {

    private lateinit var viewModel: HomePayprViewModel
    private lateinit var navController: NavController
    private var _currentFragmentLabel = ""
    private var _currentFragmentId = -1

    private var lastClickTime: Long = 0
    private var clickGap = 600

    var updateItem = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setDataBindingAndViewModel()
        initializeNavigationComponents()
        adjustDisplayScale(this)
        getScreenResolution(this)
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivityHomePayprBinding = DataBindingUtil.setContentView(this, R.layout.activity_home_paypr)

        viewModel = ViewModelProvider(this, ViewModelProvider.NewInstanceFactory()).get(HomePayprViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataLoader.observe(this, observerLoader)
        viewModel.liveDataVibrator.observe(this, observerVibrator)
        viewModel.liveDataHideKeyboard.observe(this, observerHideKeyboard)
    }

    private fun initializeNavigationComponents() {
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        navController       = navHostFragment.navController

        NavigationUI.setupWithNavController(bottomNavigationView, navController)

        navController.addOnDestinationChangedListener { _, destination, _ ->
            Log.e("log", "Destination: ${destination.label}")
            _currentFragmentLabel = destination.label.toString()
        }

        bottomNavigationView.setupWithNavController(navController)

        bottomNavigationView.setOnNavigationItemSelectedListener { item ->
            if (SystemClock.elapsedRealtime() - lastClickTime < clickGap) {
                false
            } else {
                lastClickTime = SystemClock.elapsedRealtime()

                when (item.itemId) {
                    R.id.fundTransferFragment -> {
                        navigateToHome()
                        true
                    }
                    R.id.transactionFragment -> {
                        switchMenu(R.id.transactionFragment)
                        true
                    }
                    R.id.retailersFragment -> {
                        switchMenu(R.id.retailersFragment)
                        true
                    }
                    R.id.profileFragment -> {
                        switchMenu(R.id.profileFragment)
                        true
                    }
                    else -> false
                }
            }
        }
    }

    private fun switchMenu(menuId: Int) {
        if (_currentFragmentId != menuId)
            navController.navigate(menuId)
        _currentFragmentId = menuId
    }

    fun setToolbarItems(title: String, showBackArrow: Boolean, showThreeDots: Boolean) {
        if (toolbar.title != title) {
            toolbar.title = title
            if (showBackArrow)
                toolbar.navigationIcon = ContextCompat.getDrawable(this, R.drawable.ic_svg_back_arrow)
            else
                toolbar.navigationIcon = null

            ivThreeDotsMenu.visibility = if (showThreeDots) View.VISIBLE else View.GONE

            toolbar.setNavigationOnClickListener {
                vibrate(this)
                onBackPressed()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp()
    }

    override fun hideKeyboard() {
        //Code here to hide keyboard
    }

    fun navigateToHome() {
        navController.popBackStack(R.id.fundTransferFragment, true)
        navController.navigate(R.id.fundTransferFragment)
        _currentFragmentId = R.id.fundTransferFragment
    }

    override fun onBackPressed() {
        if ((_currentFragmentLabel == "fragment_transaction") || (_currentFragmentLabel == "fragment_retailers") || (_currentFragmentLabel == "fragment_profile")) {
            navController.popBackStack(R.id.fundTransferFragment, true)
            navController.navigate(R.id.fundTransferFragment)
            _currentFragmentId = R.id.fundTransferFragment
        }
        else
            super.onBackPressed()
    }
}