package com.skilrock.paypr.paypr_app.viewmodel

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel


abstract class BaseViewModel : ViewModel() {

    val liveDataLoader          = MutableLiveData<Boolean>()
    val liveDataVibrator        = MutableLiveData<String>()
    val liveDataHideKeyboard    = MutableLiveData<Boolean>()

}
