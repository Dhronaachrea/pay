package com.skilrock.paypr.paypr_app.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.skilrock.paypr.R
import kotlinx.android.synthetic.main.bottom_sheet_profile_pic.*

class ProfilePicBottomSheetDialog(private val captureImageFromCamera: () -> Unit,
                                  private val chooseImageFromGallery: () -> Unit) : BottomSheetDialogFragment() {

    companion object {
        const val TAG = "ProfilePicBottomSheetDialog"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setStyle(DialogFragment.STYLE_NORMAL, R.style.DialogSheetStyle)
        isCancelable = true
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        return inflater.inflate(R.layout.bottom_sheet_profile_pic, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tvOpenCamera.setOnClickListener {
            dismiss()
            captureImageFromCamera()
        }

        tvChooseFromGallery.setOnClickListener {
            dismiss()
            chooseImageFromGallery()
        }
    }

}