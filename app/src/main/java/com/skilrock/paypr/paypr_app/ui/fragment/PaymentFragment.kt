package com.skilrock.paypr.paypr_app.ui.fragment

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.skilrock.paypr.R

class PaymentFragment : BaseFragmentPaypr() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_payment, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setToolbarElements()
    }

    override fun hideKeyboard() {
        //Code here to close the keyboard
    }

    override fun setToolbarElements() {
        master.setToolbarItems(getString(R.string.payments), false, showThreeDots = false)
    }

}