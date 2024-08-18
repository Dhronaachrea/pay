package com.skilrock.paypr.paypr_app.ui.fragment

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.SeekBar
import androidx.core.content.ContextCompat
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.FragmentMerchantsBinding
import com.skilrock.paypr.paypr_app.adapter.MerchantPayprAdapter
import com.skilrock.paypr.paypr_app.data_class.response_data.MerchantLocationResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.dialog.PayprGpsPermissionDialog
import com.skilrock.paypr.paypr_app.ui.activity.LoginActivity
import com.skilrock.paypr.paypr_app.viewmodel.MerchantViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_home_paypr.*
import kotlinx.android.synthetic.main.fragment_merchants.*


class MerchantsFragment : BaseFragmentPaypr() {

    private lateinit var viewModel: MerchantViewModel
    private lateinit var binding: FragmentMerchantsBinding
    private lateinit var adapter: MerchantPayprAdapter

    private val _permissionId = 9001
    private val _settingActivityRequestCode = 9002

    private var gpsTracker: GpsTracker? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_merchants, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setToolbarElements()
        setUpRecyclerView()
        clickListeners()
        checkRunTimePermissionForLocation()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(MerchantViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataMerchant.observe(viewLifecycleOwner, observerMerchants)
    }

    private fun setUpRecyclerView() {
        rvMerchant.layoutManager = LinearLayoutManager(master)
        rvMerchant.setHasFixedSize(true)
        adapter = MerchantPayprAdapter(master, master, this)
        rvMerchant.adapter = adapter
    }

    private fun clickListeners() {
        seekBar.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, progress: Int, fromUser: Boolean) {
                vibrateShort(master)
            }

            override fun onStartTrackingTouch(seekBar: SeekBar) {
                //Method is called to track the touch
            }

            override fun onStopTrackingTouch(seekBar: SeekBar) {
                gpsTracker?.let { gps ->
                    if (gps.canGetLocation()) {
                        fetchLocation(gps)
                    } else {
                        setError(getString(R.string.unable_to_get_your_location))
                    }
                }
            }
        })

        tvError.setOnClickListener {
            checkRunTimePermissionForLocation()
        }
    }

    private fun checkRunTimePermissionForLocation() {
        try {
            if (ContextCompat.checkSelfPermission(master.applicationContext,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(arrayOf(Manifest.permission.ACCESS_FINE_LOCATION), _permissionId)
            } else {
                getLocation()
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun getLocation() {
        gpsTracker = GpsTracker(master)
        gpsTracker?.let { gps: GpsTracker ->
            if (gps.canGetLocation()) {
                fetchLocation(gps)
            } else {
                PayprGpsPermissionDialog(master, {
                    startActivityForResult(Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS), _settingActivityRequestCode)
                }, {
                    setError(getString(R.string.gps_is_disabled))
                    showSnackBar(getString(R.string.gps_is_disabled))
                }).showDialog()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == _settingActivityRequestCode) {
            master.window.setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE, WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
            master.progressBar.visibility = View.VISIBLE
            llMerchantLayout.postDelayed({
                master.progressBar.visibility = View.INVISIBLE
                master.window.clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE)
                gpsTracker?.let { gps ->
                    if (gps.canGetLocation()) {
                        fetchLocation(gps)
                    } else {
                        setError(getString(R.string.unable_to_get_your_location))
                    }
                }
            }, 3000)
        }
    }

    private fun fetchLocation(gps: GpsTracker) {
        val latitude: Double = gps.latitude
        val longitude: Double = gps.longitude
        Log.w("log", "Latitude: $latitude")
        Log.w("log", "Longitude: $longitude")
        if (latitude == 0.0 && longitude == 0.0) {
            setError(getString(R.string.unable_to_get_your_location))
        } else
            viewModel.callMerchantApi(latitude, longitude, getDistanceInMiles())
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == _permissionId) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                if (ContextCompat.checkSelfPermission(master, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    getLocation()
                }
                else {
                    setError(getString(R.string.need_to_allow_location_permission))
                    showSnackBar(getString(R.string.location_permission_denied))
                }

            } else {
                setError(getString(R.string.need_to_allow_location_permission))
                showSnackBar(getString(R.string.location_permission_denied))
            }
        }

    }

    private val observerMerchants = Observer<ResponseStatus<MerchantLocationResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                if (it.response.responseData?.data.isNullOrEmpty()) {
                    setError(getString(R.string.no_merchant_found_nearby))
                } else {
                    tvError.visibility = View.GONE
                    rvMerchant.visibility = View.VISIBLE
                    adapter.setMerchantList(it.response.responseData?.data)
                }
            }

            is ResponseStatus.Error -> activity?.let { act ->
                setError(it.errorMessage.getMsg(act))
                if (it.errorCode == SESSION_EXPIRE_CODE) {
                    PayprErrorDialog(act, "${getString(R.string.retailers_location)} ${getString(R.string.error)}", it.errorMessage.getMsg(act), "", it.errorCode,
                        Intent(master, LoginActivity::class.java)) {}.showDialog()
                }
            }

            is ResponseStatus.TechnicalError -> setError(getString(it.errorMessageCode))

        }
    }

    private fun setError(errorMsg: String) {
        tvError.text            = errorMsg
        tvError.visibility      = View.VISIBLE
        rvMerchant.visibility   = View.GONE
    }

    override fun onStop() {
        super.onStop()
        gpsTracker?.stopUsingGPS()
    }

    override fun hideKeyboard() {
        //Code here to close the keyboard
    }

    fun scrollToTop() {
        rvMerchant.smoothScrollToPosition(0)
    }

    override fun setToolbarElements() {
        master.setToolbarItems(getString(R.string.merchants), false, showThreeDots = false)
    }

    private fun showSnackBar(text: String) {
        Snackbar.make(master.llHomeContainer, HtmlCompat.fromHtml("<font color=\"#FF7878\">${text
        }</font>", HtmlCompat.FROM_HTML_MODE_LEGACY), Snackbar.LENGTH_LONG).show()
    }

    private fun getDistanceInMiles() : Double {
        var distance = 0
        when(seekBar.progress) {
            0 -> distance = 10
            1 -> distance = 15
            2 -> distance = 20
            3 -> distance = 25
            4 -> distance = 30
            5 -> distance = 35
            6 -> distance = 70
        }
        return 0.621371 * distance
    }

}