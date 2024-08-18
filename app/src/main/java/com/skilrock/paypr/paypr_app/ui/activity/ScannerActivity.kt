package com.skilrock.paypr.paypr_app.ui.activity

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.budiyev.android.codescanner.AutoFocusMode
import com.budiyev.android.codescanner.CodeScanner
import com.budiyev.android.codescanner.DecodeCallback
import com.budiyev.android.codescanner.ScanMode
import com.google.gson.Gson
import com.google.zxing.Result
import com.skilrock.paypr.R
import com.skilrock.paypr.permissions.AppPermissions
import com.skilrock.paypr.utility.QrCodeData
import com.skilrock.paypr.utility.adjustDisplayScale
import com.skilrock.paypr.utility.vibrate
import kotlinx.android.synthetic.main.activity_scanner.*

class ScannerActivity : AppCompatActivity() {

    private var mCodeScanner: CodeScanner? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_scanner)

        adjustDisplayScale(this)
        checkScannerPermission()
        scannerView.setOnClickListener { mCodeScanner?.startPreview() }
    }

    private fun checkScannerPermission() {
        if (AppPermissions.checkPermission(this))
            startScanning()
        else
            AppPermissions.requestPermission(this)
    }

    private fun startScanning() {
        mCodeScanner = CodeScanner(this, scannerView)

        mCodeScanner?.camera = CodeScanner.CAMERA_BACK
        mCodeScanner?.formats = CodeScanner.ALL_FORMATS
        mCodeScanner?.autoFocusMode = AutoFocusMode.SAFE
        mCodeScanner?.scanMode = ScanMode.SINGLE
        mCodeScanner?.isAutoFocusEnabled = true
        mCodeScanner?.isFlashEnabled = false

        mCodeScanner?.let { codeScanner ->
            codeScanner.decodeCallback = DecodeCallback { result: Result ->
                runOnUiThread { onScanned(result) }
            }
        }
    }

    private fun onScanned(result: Result) {
        vibrate(this)
        val scannedText = result.text

        try {
            val qrCodeData = Gson().fromJson(scannedText, QrCodeData::class.java)
            val returnIntent = Intent()
            returnIntent.putExtra("destinationAccount", qrCodeData.destinationAccount)
            returnIntent.putExtra("name", qrCodeData.name)
            returnIntent.putExtra("merchantId", qrCodeData.merchantId)
            returnIntent.putExtra("amount", qrCodeData.amount)
            setResult(RESULT_OK, returnIntent)
            finish()
            overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(this, getString(R.string.invalid_qr_scanned), Toast.LENGTH_SHORT).show()
        }
    }

    override fun onResume() {
        super.onResume()
        mCodeScanner?.startPreview()
    }

    override fun onPause() {
        mCodeScanner?.releaseResources()
        super.onPause()
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == 200) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // PERMISSION GRANTED
                startScanning()
            } else {
                // PERMISSION DENIED
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                    && ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    onBackPressed()
                }
            }
        }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        overridePendingTransition(R.anim.zoom_in, R.anim.zoom_out)
    }

}