package com.skilrock.paypr.paypr_app.ui.fragment

import android.Manifest
import android.animation.ArgbEvaluator
import android.animation.ValueAnimator
import android.app.Activity
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.app.ActivityOptionsCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.core.text.HtmlCompat
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavDirections
import androidx.navigation.Navigation
import androidx.transition.ChangeBounds
import androidx.transition.TransitionInflater
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.material.snackbar.Snackbar
import com.skilrock.paypr.BuildConfig
import com.skilrock.paypr.R
import com.skilrock.paypr.databinding.FragmentPersonalDataBinding
import com.skilrock.paypr.paypr_app.data_class.response_data.BalanceResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.LogoutResponseData
import com.skilrock.paypr.paypr_app.data_class.response_data.UploadAvatarResponseData
import com.skilrock.paypr.paypr_app.dialog.PayprLogoutDialog
import com.skilrock.paypr.paypr_app.dialog.ProfilePicBottomSheetDialog
import com.skilrock.paypr.paypr_app.ui.activity.DisplayQrCodeActivity
import com.skilrock.paypr.paypr_app.ui.activity.PostSplashActivity
import com.skilrock.paypr.paypr_app.viewmodel.ProfileViewModel
import com.skilrock.paypr.permissions.AppPermissions
import com.skilrock.paypr.utility.*
import kotlinx.android.synthetic.main.activity_home_paypr.*
import kotlinx.android.synthetic.main.fragment_personal_data.*
import java.io.File
import java.io.IOException


class PersonalDataFragment : BaseFragmentPaypr() {

    private lateinit var viewModel: ProfileViewModel
    private lateinit var binding: FragmentPersonalDataBinding

    private var mImageFileLocation = ""
    private var photoFile: File? = null
    private var postPath: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        sharedElementEnterTransition = TransitionInflater.from(context).inflateTransition(android.R.transition.move)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sharedElementEnterTransition    = ChangeBounds().apply { duration = 750 }
        sharedElementReturnTransition   = ChangeBounds().apply { duration = 750 }

        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_personal_data, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        setViewModel()
        setToolbarElements()
        clickListener()
        setVersion()
    }

    override fun onResume() {
        super.onResume()
        reflectProfileImage()

        viewModel.playerName.postValue(PlayerInfo.getPlayerFirstName() + " " + PlayerInfo.getPlayerLastName())
        viewModel.emailId.postValue(PlayerInfo.getPlayerEmailId())

        if (master.updateItem == "CHANGE_NAME")
            updateAnimation(llName)
        else if (master.updateItem == "CHANGE_EMAIL")
            updateAnimation(llEmail)

    }

    private fun reflectProfileImage() {
        Glide.with(master.applicationContext)
            .load(PlayerInfo.getProfilePicPath())
            .apply(
                RequestOptions()
                    .skipMemoryCache(true)
                    .diskCacheStrategy(DiskCacheStrategy.NONE)
            )
            .into(ivProfileImage)
    }

    private fun setViewModel() {
        viewModel = ViewModelProvider(this).get(ProfileViewModel::class.java)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        viewModel.liveDataVibrator.observe(viewLifecycleOwner, observerVibrator)
        viewModel.liveDataLoader.observe(viewLifecycleOwner, observerLoader)
        viewModel.liveDataHideKeyboard.observe(viewLifecycleOwner, observerHideKeyboard)
        viewModel.liveDataLogoutStatus.observe(viewLifecycleOwner, observerLogoutStatus)
        viewModel.liveDataBalanceStatus.observe(viewLifecycleOwner, observerBalanceStatus)
        viewModel.liveDataProfileUpdateStatus.observe(viewLifecycleOwner, observerUpdateProfileStatus)
    }

    private fun setVersion() {
        val version = getString(R.string.version) + ": " + BuildConfig.VERSION_NAME +
                if (BuildConfig.INTERNAL_VERSION.isNotBlank()) " (${BuildConfig.INTERNAL_VERSION})" else ""
        tvVersion.text = version
    }

    private val observerLogoutStatus = Observer<ResponseStatus<LogoutResponseData>> {
        performLogoutCleanUp(master, Intent(master, PostSplashActivity::class.java))
    }

    private val observerBalanceStatus = Observer<ResponseStatus<BalanceResponseData>> {
        when (it) {
            is ResponseStatus.Success -> {
                if (it.response.showToast) {
                    greenToast(getString(R.string.balance_refreshed))
                    it.response.wallet?.let { wallet -> PlayerInfo.setBalance(master, wallet) }
                    it.response.showToast = false
                }
            }

            is ResponseStatus.Error -> redToast(it.errorMessage.getMsg(master))

            is ResponseStatus.TechnicalError -> redToast(getString(it.errorMessageCode))
        }
    }

    private fun redToast(message: String) {
        Snackbar.make(master.llHomeContainer, HtmlCompat.fromHtml("<font color=\"#FF7878\">${message}</font>", HtmlCompat.FROM_HTML_MODE_LEGACY), Snackbar.LENGTH_LONG).show()
    }

    private fun greenToast(message: String) {
        Snackbar.make(master.llHomeContainer, HtmlCompat.fromHtml("<font color=\"#5BDA8C\">${message}</font>", HtmlCompat.FROM_HTML_MODE_LEGACY), Snackbar.LENGTH_LONG).show()
    }

    private fun clickListener() {
        tvSignOut.setOnClickListener {
            PayprLogoutDialog(master) { viewModel.callLogoutApi() }.showDialog()
        }

        cardChangePassword.setOnClickListener {
            Navigation.findNavController(it).navigate(R.id.action_personalDataFragment_to_changePasswordFragment)
        }

        cardName.setOnClickListener {
            val direction: NavDirections = PersonalDataFragmentDirections
                .actionPersonalDataFragmentToProfileUpdateFragment(getString(R.string.change_name), getString(R.string.enter_name),
                    InputType.TYPE_TEXT_FLAG_CAP_WORDS, "CHANGE_NAME")

            Navigation.findNavController(it).navigate(direction)
        }

        cardEmail.setOnClickListener {
            val direction: NavDirections = PersonalDataFragmentDirections
                .actionPersonalDataFragmentToProfileUpdateFragment(getString(R.string.change_email), getString(R.string.enter_email),
                    InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS, "CHANGE_EMAIL")

            Navigation.findNavController(it).navigate(direction)
        }

        cardQR.setOnClickListener {
            val pairButton  = androidx.core.util.Pair<View, String>(ivQr, "qr_image")
            val options = ActivityOptionsCompat.makeSceneTransitionAnimation(master, pairButton)
            val intent = Intent(master, DisplayQrCodeActivity::class.java)
            startActivity(intent, options.toBundle())
        }

        ivProfileImage.setOnClickListener { checkPermissions() }
    }

    private fun updateAnimation(view: View) {
        val colorFrom           = ContextCompat.getColor(master, R.color.additional_text_2)
        val colorTo             = ContextCompat.getColor(master, R.color.white)
        val colorAnimation      = ValueAnimator.ofObject(ArgbEvaluator(), colorFrom, colorTo)
        colorAnimation.duration = 1000

        colorAnimation.addUpdateListener { animator -> view.setBackgroundColor(animator.animatedValue as Int) }
        colorAnimation.repeatCount = 4
        colorAnimation.start()
        master.updateItem = ""
    }

    override fun hideKeyboard() {
        //Code here to close the keyboard
    }

    override fun setToolbarElements() {
        master.setToolbarItems(getString(R.string.personal_data), true, showThreeDots = false)
    }

    private fun checkPermissions() {
        if (AppPermissions.checkPermissionFoStorage(master, arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)))
            AppPermissions.requestPermissionForStorageAndCamera(master)
        else
            showOptionsBottomDialog()
    }

    private fun showOptionsBottomDialog() {
        ProfilePicBottomSheetDialog(::captureImageFromCamera, ::chooseImageFromGallery).apply {
            show(master.supportFragmentManager, ProfilePicBottomSheetDialog.TAG)
        }
    }

    private fun captureImageFromCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (intent.resolveActivity(master.packageManager) != null) {
            try {
                photoFile = createImageFile()
            } catch (ex: IOException) {
                redToast(getString(R.string.something_went_wrong))
            }
            val photoURI = photoFile?.let {
                FileProvider.getUriForFile(master, BuildConfig.APPLICATION_ID + ".provider", it)
            }
            intent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI)
            startActivityForResult(intent, REQUEST_TAKE_PHOTO)
        }
    }

    private fun chooseImageFromGallery() {
        val intent = Intent(
            Intent.ACTION_PICK,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI
        )
        startActivityForResult(intent, REQUEST_PICK_PHOTO)
    }

    private fun createImageFile(): File {

        val imageFileName =
            "Infiniti_" + PlayerInfo.getPlayerMobileNumber()
        val storageDir =
            master.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val image = File.createTempFile(
            imageFileName,  /* prefix */
            ".jpg",  /* suffix */
            storageDir /* directory */
        )
        mImageFileLocation = image.absolutePath
        return image
    }

    private fun deleteTempFiles(file: File): Boolean {
        if (file.isDirectory) {
            val files = file.listFiles()
            if (files != null) {
                for (f in files) {
                    if (f.isDirectory) {
                        deleteTempFiles(f)
                    } else {
                        f.delete()
                    }
                }
            }
        }
        return file.delete()
    }

    companion object {
        private const val REQUEST_PICK_PHOTO = 100
        private const val REQUEST_TAKE_PHOTO = 200
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK) {
            if (requestCode == REQUEST_PICK_PHOTO) {
                if (data != null) {
                    // Get the Image from data
                    val selectedImage = data.data
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)

                    selectedImage?.let { selectedImageNonNull ->
                        val cursor = master.contentResolver?.query(selectedImageNonNull, filePathColumn, null, null, null)

                        cursor?.let {
                            it.moveToFirst()

                            val columnIndex = it.getColumnIndex(filePathColumn[0])
                            val mediaPath = it.getString(columnIndex)
                            ivProfileImage.setImageBitmap(BitmapFactory.decodeFile(mediaPath))
                            it.close()
                            postPath = mediaPath
                            postPath?.let { path -> viewModel.onProfileUpload(path) }
                        }
                    }
                }

            } else if (requestCode == REQUEST_TAKE_PHOTO) {
                postPath = mImageFileLocation
                postPath?.let { viewModel.onProfileUpload(postPath!!) }
            }

        } else if (resultCode != Activity.RESULT_CANCELED)
            redToast(getString(R.string.something_went_wrong))
    }

    private val observerUpdateProfileStatus = Observer<ResponseStatus<UploadAvatarResponseData>> {
        photoFile?.let { file -> deleteTempFiles(file) }
        when (it) {
            is ResponseStatus.Success -> {
                if (it.response.showToast) {
                    PlayerInfo.setProfileImage(it.response.avatarPath?.removePrefix(PlayerInfo.getPlayerInfo()?.playerLoginInfo?.commonContentPath.toString()) ?: "")
                    greenToast(getString(R.string.image_uploaded_successfully))
                    it.response.showToast = false
                }
            }

            is ResponseStatus.Error -> { redToast(it.errorMessage.getMsg(master)) }

            is ResponseStatus.TechnicalError -> { redToast(getString(it.errorMessageCode)) }
        }
    }
}