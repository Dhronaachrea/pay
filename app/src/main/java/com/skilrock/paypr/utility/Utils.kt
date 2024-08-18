package com.skilrock.paypr.utility

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.res.Configuration
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.util.DisplayMetrics
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.animation.CycleInterpolator
import android.view.animation.TranslateAnimation
import android.view.inputmethod.InputMethodManager
import android.widget.DatePicker
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.textview.MaterialTextView
import com.skilrock.paypr.R
import com.skilrock.paypr.paypr_app.dialog.PayprCallMapDialog
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.dialog.PayprTransactionSortDialog
import java.security.MessageDigest
import java.text.SimpleDateFormat
import java.util.*


fun TextInputEditText.getTrimText() : String {
    return this.text.toString().trim()
}

fun shakeError(): TranslateAnimation {
    val shake = TranslateAnimation(0f, 10f, 0f, 0f)
    shake.duration = 500
    shake.interpolator = CycleInterpolator(7f)
    return shake
}

fun vibrate(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            vibrator.vibrate(VibrationEffect.createOneShot(40, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        else -> {
            vibrator.vibrate(40)
        }
    }
}

fun vibrateShort(context: Context) {
    val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
    when {
        Build.VERSION.SDK_INT >= Build.VERSION_CODES.O -> {
            vibrator.vibrate(VibrationEffect.createOneShot(20, VibrationEffect.DEFAULT_AMPLITUDE))
        }
        else -> {
            vibrator.vibrate(20)
        }
    }
}

fun View.hideKeyboard() {
    val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
    imm.hideSoftInputFromWindow(windowToken, 0)
}

fun encryption(s: String): String {
    val md5 = "MD5"
    try {
        // Create MD5 Hash
        val digest = MessageDigest
            .getInstance(md5)
        digest.update(s.toByteArray())
        val messageDigest = digest.digest()

        // Create Hex String
        val hexString = StringBuilder()
        for (aMessageDigest in messageDigest) {
            var h = Integer.toHexString(0xFF and aMessageDigest.toInt())
            while (h.length < 2) h = "0$h"
            hexString.append(h)
        }
        return hexString.toString()
    } catch (e: Exception) {
        e.printStackTrace()
    }
    return ""
}

fun Dialog.showDialog() {
    window?.attributes?.windowAnimations = R.style.DialogAnimationRightToCenter
    show()
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT)
}

fun PayprErrorDialog.showDialog() {
    window?.attributes?.windowAnimations = R.style.DialogAnimationUpDown
    show()
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT)
}

fun PayprTransactionSortDialog.showDialog() {
    window?.attributes?.windowAnimations = R.style.DialogAnimationZoomInOut
    show()
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT)
}

fun PayprCallMapDialog.showDialog() {
    window?.attributes?.windowAnimations = R.style.DialogAnimationZoomInOut
    show()
    window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
    window?.setLayout(LinearLayout.LayoutParams.MATCH_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT)
}

fun TextInputEditText.getTextTrimmed() : String {
    return text.toString().trim()
}

fun MaterialTextView.getTextTrimmed() : String {
    return text.toString().trim()
}

fun TextInputLayout.putError(errorMsg: String) {
    error = errorMsg
    requestFocus()
    startAnimation(shakeError())
}

fun getPreviousDate(days: Int): String {
    val cal = Calendar.getInstance()
    cal.time = Date()
    cal.add(Calendar.DATE, -days)
    val date = cal.time
    @SuppressLint("SimpleDateFormat") val df =
        SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    return df.format(date)
}

fun getCurrentDate(): String {
    val date = Calendar.getInstance().time
    @SuppressLint("SimpleDateFormat") val df =
        SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH)
    return df.format(date)
}

fun openStartDateDialog(context: Context, tvStartDate: TextView, tvEndDate: TextView) {
    vibrate(context)
    val calender = Calendar.getInstance()
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mYearEnd: Int
    val mMonthEnd: Int
    val mDayEnd: Int
    val arrDate =
        tvStartDate.text.toString().trim { it <= ' ' }.split("-").toTypedArray()
    mYear = arrDate[2].toInt()
    mMonth = arrDate[1].toInt() - 1
    mDay = arrDate[0].toInt()
    val arrDateEnd =
        tvEndDate.text.toString().trim { it <= ' ' }.split("-").toTypedArray()
    mYearEnd = arrDateEnd[2].toInt()
    mMonthEnd = arrDateEnd[1].toInt() - 1
    mDayEnd = arrDateEnd[0].toInt()
    val dialogStartDate = DatePickerDialog(context,
        R.style.paypr_date_picker_theme, { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val month: String =
                if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
            val day: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            val date = "$day-$month-$year"
            tvStartDate.text = date
        },
        mYear,
        mMonth,
        mDay)
    calender[mYearEnd, mMonthEnd, mDayEnd, 0] = 0
    dialogStartDate.datePicker.maxDate = calender.timeInMillis
    dialogStartDate.window?.attributes?.windowAnimations = R.style.DialogAnimationLeftToCenter
    dialogStartDate.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_corners_date_picker))
    dialogStartDate.show()
    dialogStartDate.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.constructive))
}

fun openEndDateDialog(context: Context, tvStartDate: TextView, tvEndDate: TextView) {
    vibrate(context)
    val calender = Calendar.getInstance()
    val mYear: Int
    val mMonth: Int
    val mDay: Int
    val mYearStart: Int
    val mMonthStart: Int
    val mDayStart: Int
    val arrDate =
        tvEndDate.text.toString().trim { it <= ' ' }.split("-").toTypedArray()
    mYear = arrDate[2].toInt()
    mMonth = arrDate[1].toInt() - 1
    mDay = arrDate[0].toInt()
    val arrDateStart =
        tvStartDate.text.toString().trim { it <= ' ' }.split("-").toTypedArray()
    mYearStart = arrDateStart[2].toInt()
    mMonthStart = arrDateStart[1].toInt() - 1
    mDayStart = arrDateStart[0].toInt()
    val dialogEndDate = DatePickerDialog(context,
        R.style.paypr_date_picker_theme, { _: DatePicker?, year: Int, monthOfYear: Int, dayOfMonth: Int ->
            val month: String =
                if (monthOfYear + 1 < 10) "0" + (monthOfYear + 1) else (monthOfYear + 1).toString()
            val day: String = if (dayOfMonth < 10) "0$dayOfMonth" else dayOfMonth.toString()
            val date = "$day-$month-$year"
            tvEndDate.text = date
        },
        mYear,
        mMonth,
        mDay)
    dialogEndDate.datePicker.maxDate = Date().time
    calender[mYearStart, mMonthStart, mDayStart, 0] = 0
    dialogEndDate.datePicker.minDate = calender.timeInMillis
    dialogEndDate.window?.attributes?.windowAnimations = R.style.DialogAnimationRightToCenter
    dialogEndDate.window?.setBackgroundDrawable(ContextCompat.getDrawable(context, R.drawable.rounded_corners_date_picker))
    dialogEndDate.show()
    dialogEndDate.getButton(DatePickerDialog.BUTTON_POSITIVE).setTextColor(context.getColor(R.color.constructive))
}

fun getScreenResolution(activity: Activity) {
    val displayMetrics = DisplayMetrics()
    activity.windowManager.defaultDisplay.getMetrics(displayMetrics)

    val width = displayMetrics.widthPixels
    val height = displayMetrics.heightPixels

    Log.w("log", "Screen Width: $width")
    Log.w("log", "Screen Height: $height")

    displayMetrics.let {  }
}

fun String.getMsg(context: Context): String {
    return when {
        this.trim().isBlank() -> context.getString(R.string.some_technical_issue)
        else -> this
    }
}

fun performLogoutCleanUp(activity: Activity, intent: Intent, wasSessionExpired: Boolean = false) {
    SharedPrefUtils.clearAppSharedPref(activity)
    activity.finish()
    if (wasSessionExpired)
        intent.putExtra("wasSessionExpired", wasSessionExpired)
    intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
    activity.startActivity(intent)
}

fun adjustDisplayScale(context: Context) {
    val configuration: Configuration? = context.resources.configuration
    if (configuration != null) {
        Log.w("log", "adjustDisplayScale: " + configuration.densityDpi)
        when {
            configuration.densityDpi >= 485 //for 6 inch device OR for 538 ppi
            -> configuration.densityDpi = 500 //decrease "display size" by ~30
            configuration.densityDpi >= 400
            -> configuration.densityDpi = 400
            configuration.densityDpi >= 300 //for 5.5 inch device OR for 432 ppi
            -> configuration.densityDpi = 300 //decrease "display size" by ~30
            configuration.densityDpi >= 100 //for 4 inch device OR for 233 ppi
            -> configuration.densityDpi = 200
        } //decrease "display size" by ~30
        val metrics: DisplayMetrics = context.resources.displayMetrics
        val wm = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager?
        wm!!.defaultDisplay.getMetrics(metrics)
        metrics.scaledDensity = configuration.densityDpi * metrics.density
        context.resources.updateConfiguration(configuration, metrics)
    }
}

