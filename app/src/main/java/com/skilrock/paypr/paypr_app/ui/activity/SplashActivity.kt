package com.skilrock.paypr.paypr_app.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.Window
import androidx.core.app.ActivityCompat
import androidx.core.app.ActivityOptionsCompat
import androidx.core.view.ViewCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.dcastalia.localappupdate.DownloadApk
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.ActivitySplashBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.LoginResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.VersionResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprAppVersionDialog
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.viewmodel.SplashViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_splash.*
import java.util.*


class SplashActivity : BaseActivity() {

    private lateinit var _response: VersionResponseData
    private lateinit var viewModel: SplashViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        requestWindowFeature(Window.FEATURE_NO_TITLE)
        super.onCreate(savedInstanceState)
        supportActionBar?.hide()
        adjustDisplayScale(this)
        setDataBindingAndViewModel()
        getFirebaseToken()
    }

    private fun setDataBindingAndViewModel() {
        val binding : ActivitySplashBinding = DataBindingUtil.setContentView(this, R.layout.activity_splash)
        viewModel = ViewModelProvider(this).get(SplashViewModel::class.java)
        binding.viewModel = viewModel

        viewModel.liveDataVersionStatus.observe(this, observerResponse)
    }

    private fun getFirebaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("log", "Fetching FCM registration token failed", task.exception)
                callVersionApi()
                return@OnCompleteListener
            }

            // Get new FCM registration token
            val token = task.result

            // Log and toast
            Log.d("log", "Firebase Token: $token")
            SharedPrefUtils.setFcmToken(this@SplashActivity, token)
            callVersionApi()
        })
    }

    private fun callVersionApi() {
        viewModel.callVersionApi(BuildConfig.VERSION_NAME, SharedPrefUtils.getString(this, SharedPrefUtils.PLAYER_TOKEN))
    }

    private val observerResponse = Observer<ResponseStatus<VersionResponseData>> {
        when(it) {
            is ResponseStatus.Success -> {
                _response = it.response
                if (it.response.appDetails?.isUpdateAvailable == true) {
                    performVersioningOperation(it.response)
                }
                else {
                    proceedToLogin()
                }
            }

            is ResponseStatus.Error -> {
                PayprErrorDialog(this, getString(R.string.version_error), it.errorMessage.getMsg(this)) {finish()}.showDialog()
            }

            is ResponseStatus.TechnicalError -> {
                PayprErrorDialog(this, getString(R.string.version_error), getString(it.errorMessageCode)) {finish()}.showDialog()
            }
        }
    }

    private fun performVersioningOperation(resp: VersionResponseData) {
        resp.appDetails?.mandatory.let { isMandatory ->
            isMandatory?.let {
                showUpdateDialog(it, resp.appDetails?.url, resp.appDetails?.message)
            }
        } ?: PayprErrorDialog(this, getString(R.string.version_error), getString(R.string.some_technical_issue)) {finish()}.showDialog()
    }

    private fun showUpdateDialog(isUpdateForceFull: Boolean, url: String?, message: String?) {
        val updateMsg = message ?: getString(R.string.new_update_available)
        url?.let {
            PayprAppVersionDialog(isUpdateForceFull, it, updateMsg, this, ::onUpdateSelected).showDialog()
        } ?: PayprErrorDialog(this, getString(R.string.version_error), getString(R.string.some_internal_error)) {finish()}.showDialog()
    }

    private fun onUpdateSelected(isNow: Boolean, url: String) {
        if (isNow) {
            if (isWriteStoragePermissionGranted()) downloadApk(url)
        } else {
            proceedToLogin()
        }
    }

    private fun downloadApk(url: String) {
        val downloadApk = DownloadApk(this)
        downloadApk.startDownloadingApk(url)
    }

    private fun proceedToLogin() {
        tvPayPr.postDelayed({
            ViewCompat.getTransitionName(ivLogo)?.let {
                if (SharedPrefUtils.getString(this@SplashActivity, SharedPrefUtils.PLAYER_TOKEN).isEmpty()) {
                    PlayerInfo.destroy()
                    val pairLogo    = androidx.core.util.Pair<View, String>(ivLogo, "splash_to_login")
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashActivity, pairLogo)
                    startActivity(Intent(this@SplashActivity, PostSplashActivity::class.java), options.toBundle())
                    tvPayPr.postDelayed({this@SplashActivity.finish()}, 1500)
                } else {
                    PlayerInfo.setLoginData(Gson().fromJson(SharedPrefUtils.getString(this@SplashActivity, SharedPrefUtils.PLAYER_DATA), LoginResponseData::class.java))
                    val options = ActivityOptionsCompat.makeSceneTransitionAnimation(this@SplashActivity)
                    val intent = Intent(this@SplashActivity, HomePayprActivity::class.java)
                    startActivity(intent, options.toBundle())
                    tvPayPr.postDelayed({this@SplashActivity.finish()}, 1500)
                }
            }
        }, 2000)
    }

    private fun isWriteStoragePermissionGranted(): Boolean {
        return if (Build.VERSION.SDK_INT >= 23) {
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                Log.i("log", "Permission is granted2")
                true
            } else {
                Log.v("log", "Permission is revoked2")
                ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.WRITE_EXTERNAL_STORAGE), 2)
                false
            }
        } else {
            //permission is automatically granted on sdk<23 upon installation
            Log.v("log", "Permission is granted2")
            true
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            2 -> {
                Log.d("log", "External storage2")
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("log", "Permission: " + permissions[0] + " was " + grantResults[0])
                    _response.appDetails?.url.let { it?.let { apkPath -> downloadApk(apkPath) } }
                }
            }
            3 -> {
                Log.d("log", "External storage1")
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.v("log", "Permission: " + permissions[0] + " was " + grantResults[0])
                }
            }
        }
    }

    override fun hideKeyboard() {
        //Code here to hide keyboard
    }
}