package com.skilrock.paypr.paypr_app.ui.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.FragmentProfileUpdateBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.UpdateProfileResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprErrorDialog
import com.skilrock.paypr.paypr_app.dialog.PayprProfileUpdateConfirmationDialog
import com.skilrock.paypr.paypr_app.dialog.PayprSuccessDialog
import com.skilrock.paypr.paypr_app.ui.activity.LoginActivity
import com.skilrock.paypr.paypr_app.viewmodel.ProfileUpdateViewModel
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.fragment_profile_update.*


class ProfileUpdateFragment : BaseFragmentPaypr() {


    private lateinit var viewModel: ProfileUpdateViewModel
    private lateinit var binding: FragmentProfileUpdateBinding
    private lateinit var _title: String
    private lateinit var _pageType: String

    private val _changeName     = "CHANGE_NAME"
    private val _changeEmail    = "CHANGE_EMAIL"

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_update, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        receiveParameters()
        setNameOrEmailChecks()
        setToolbarElements()
        setClickListeners()
        keyboardEnterFunctionality()
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(ProfileUpdateViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataUpdateProfile.observe(viewLifecycleOwner, observerResponseStatus)
    }

    private fun receiveParameters() {
        arguments?.let {
            _title              = ProfileUpdateFragmentArgs.fromBundle(it).pageTitle
            _pageType           = ProfileUpdateFragmentArgs.fromBundle(it).pageType
        }
    }

    private fun setNameOrEmailChecks() {
        if (_pageType == _changeName) {
            tilNameInput.visibility     = View.VISIBLE
            tilEmailInput.visibility    = View.GONE
        }
        else if (_pageType == _changeEmail) {
            tilEmailInput.visibility    = View.VISIBLE
            tilNameInput.visibility     = View.GONE
        }
    }

    private fun setClickListeners() {
        btnSaveChanges.setOnClickListener {
            if (validate()) {
                btnSaveChanges.hideKeyboard()
                val updateValue = when (_pageType) {
                    _changeName     -> etNameInput.getTextTrimmed()
                    _changeEmail    -> etEmailInput.getTextTrimmed()
                    else            -> ""
                }

                var subject         = ""
                val currentName     = PlayerInfo.getPlayerFirstName() + if (PlayerInfo.getPlayerLastName().isNotBlank()) " " + PlayerInfo.getPlayerLastName() else ""
                val currentEmail    = PlayerInfo.getPlayerEmailId()
                if (_pageType == _changeName) {
                    subject = "Are you sure you want to change name from <b>$currentName</b> to <b>${updateValue}</b>?"
                } else if (_pageType == _changeEmail) {
                    subject = "Hey $currentName, are you sure you want to change email id from <b>$currentEmail</b> to <b>${updateValue}</b>?"
                }

                PayprProfileUpdateConfirmationDialog(master, _title, subject, ::callUpdateProfileApi).showDialog()
            }
        }
    }

    private fun callUpdateProfileApi() {
        if (_pageType == _changeName) {
            val fullNameList: List<String> = etNameInput.getTextTrimmed().split(" ", limit = 2)
            val firstName: String
            val lastName: String

            when {
                fullNameList.isNullOrEmpty() -> {
                    firstName   = etNameInput.getTextTrimmed()
                    lastName    = ""
                }
                fullNameList.size == 1 -> {
                    firstName   = fullNameList[0]
                    lastName    = ""
                }
                else -> {
                    firstName   = fullNameList[0]
                    lastName    = fullNameList[1]
                }
            }
            viewModel.callProfileUpdateApi(firstName, lastName, PlayerInfo.getPlayerEmailId())
        } else if (_pageType == _changeEmail) {
            viewModel.callProfileUpdateApi(PlayerInfo.getPlayerFirstName(), PlayerInfo.getPlayerLastName(), etEmailInput.getTextTrimmed())
        }
    }

    private fun keyboardEnterFunctionality() {
        etNameInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnSaveChanges.performClick()
                    true
                }
                else -> false
            }
        }

        etEmailInput.setOnEditorActionListener { _, actionId, _ ->
            when (actionId) {
                EditorInfo.IME_ACTION_DONE -> {
                    btnSaveChanges.performClick()
                    true
                }
                else -> false
            }
        }
    }

    private fun validate() : Boolean {
        if (_pageType == _changeName) {
            if (etNameInput.getTextTrimmed().isBlank()) {
                tilNameInput.putError(getString(R.string.name_cannot_be_empty))
                return false
            }
        } else if (_pageType == _changeEmail && !android.util.Patterns.EMAIL_ADDRESS.matcher(etEmailInput.getTextTrimmed()).matches()) {
            tilEmailInput.putError(getString(R.string.enter_valid_email))
            return false
        }
        return true
    }

    private val observerResponseStatus = Observer<ResponseStatus<UpdateProfileResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                it.response.playerInfoBean?.let { plrInfoBean ->
                    PlayerInfo.setFirstNameLastNameEmail(master,
                        plrInfoBean.firstName ?: PlayerInfo.getPlayerFirstName(),
                        plrInfoBean.lastName ?: PlayerInfo.getPlayerLastName(),
                        plrInfoBean.emailId ?: PlayerInfo.getPlayerEmailId())
                }
                PayprSuccessDialog(master,
                    _title,
                    it.response.respMsg ?: getString(R.string.profile_updated_successfully), getString(R.string.continue_)) {
                    master.updateItem = _pageType
                    master.onBackPressed()
                }.showDialog()
            }

            is ResponseStatus.Error -> PayprErrorDialog(master, _title, it.errorMessage.getMsg(master), "", it.errorCode,
                Intent(master, LoginActivity::class.java)) {}.showDialog()

            is ResponseStatus.TechnicalError -> PayprErrorDialog(master, _title,
                getString(it.errorMessageCode)) {}.showDialog()
        }
    }

    override fun hideKeyboard() {
        btnSaveChanges.hideKeyboard()
    }

    override fun setToolbarElements() {
        master.setToolbarItems(_title, showBackArrow = true, showThreeDots = false)
    }

}