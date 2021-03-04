package com.devstree.mediafilepicker.bottomsheet

import android.Manifest
import android.app.Activity
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.media.MediaPlayer
import android.net.Uri
import android.os.Build.VERSION
import android.os.Build.VERSION_CODES
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.View.*
import android.view.ViewGroup
import androidx.annotation.ColorRes
import androidx.annotation.DrawableRes
import androidx.core.content.ContextCompat
import com.devstree.mediafilepicker.R
import com.devstree.mediafilepicker.databinding.BottomSheetCameraDialogBinding
import com.devstree.mediafilepicker.enumeration.MediaType
import com.devstree.mediafilepicker.enumeration.SelectionType
import com.devstree.mediafilepicker.enumeration.SelectionType.*
import com.devstree.mediafilepicker.listener.MediaPickerCallback
import com.devstree.mediafilepicker.model.Media
import com.devstree.mediafilepicker.model.Thumb
import com.devstree.mediafilepicker.utils.FileUtil
import com.devstree.mediafilepicker.utils.MediaLog
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import java.io.File

/**
 * Created by Dhaval Baldha on 22/12/2020.
 */

// refer : https://developer.android.com/training/data-storage/shared/media#request-permissions
open class BottomSheetFilePicker(val applicationId: String) : BaseBottomSheet(), OnClickListener {
    private var file: File? = null
    private var selectionType = ALL
    private var action = TAKE_IMAGE
    private var directAction = false
    private var mediaPickerCallback: MediaPickerCallback? = null
    private lateinit var binding: BottomSheetCameraDialogBinding

    @DrawableRes
    var actionButtonBg: Int? = null

    @ColorRes
    var actionButtonTextColor: Int? = null

    @DrawableRes
    var cancelButtonBg: Int? = null

    @ColorRes
    var cancelButtonTextColor: Int? = null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        binding = BottomSheetCameraDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mapping()
        updateUi()
        if (directAction) {
            if (action == PICK_CONTACT) selectContact()
            else selectFile()
        }
    }

    private fun mapping() {
        with(binding) {
            btnTakePhoto.setText(R.string.take_a_photo)
            btnChooseImage.setText(R.string.choose_image_from_gallery)
            btnTakeVideo.setText(R.string.take_a_video)
            btnChooseVideo.setText(R.string.choose_video_from_gallery)
            btnCancel.setText(R.string.cancel)
        }

        binding.btnTakePhoto.setOnClickListener(this)
        binding.btnChooseImage.setOnClickListener(this)
        binding.btnTakeVideo.setOnClickListener(this)
        binding.btnChooseVideo.setOnClickListener(this)
        binding.btnCancel.setOnClickListener(this)

        binding.btnTakePhoto.visibility = GONE
        binding.btnChooseImage.visibility = GONE
        binding.btnTakeVideo.visibility = GONE
        binding.btnChooseVideo.visibility = GONE

        when (selectionType) {
            ALL -> {
                binding.btnTakePhoto.visibility = VISIBLE
                binding.btnChooseImage.visibility = VISIBLE
                binding.btnTakeVideo.visibility = VISIBLE
                binding.btnChooseVideo.visibility = VISIBLE
            }

            IMAGE -> {
                binding.btnTakePhoto.visibility = VISIBLE
                binding.btnChooseImage.visibility = VISIBLE
            }

            VIDEO -> {
                binding.btnTakeVideo.visibility = VISIBLE
                binding.btnChooseVideo.visibility = VISIBLE
            }

            TAKE_IMAGE -> {
                binding.btnTakePhoto.visibility = VISIBLE
                directAction = true
                action = TAKE_IMAGE
            }

            TAKE_VIDEO -> {
                binding.btnTakeVideo.visibility = VISIBLE
                directAction = true
                action = TAKE_VIDEO
            }

            TAKE_IMAGE_VIDEO -> {
                binding.btnTakePhoto.visibility = VISIBLE
                binding.btnTakeVideo.visibility = VISIBLE
            }

            PICK_IMAGE -> {
                binding.btnChooseImage.visibility = VISIBLE
                directAction = true
                action = PICK_IMAGE
            }

            PICK_VIDEO -> {
                binding.btnChooseVideo.visibility = VISIBLE
                directAction = true
                action = PICK_IMAGE
            }

            PICK_IMAGE_VIDEO -> {
                binding.btnChooseImage.visibility = VISIBLE
                binding.btnChooseVideo.visibility = VISIBLE
            }
            PICK_CONTACT -> {
                directAction = true
                action = PICK_CONTACT
            }
        }
    }

    fun updateUi() {
        if (actionButtonBg != null) {
            with(binding) {
                btnTakePhoto.setBackgroundResource(actionButtonBg!!)
                btnChooseImage.setBackgroundResource(actionButtonBg!!)
                btnTakeVideo.setBackgroundResource(actionButtonBg!!)
                btnChooseVideo.setBackgroundResource(actionButtonBg!!)
            }
        }
        if (actionButtonTextColor != null) {
            with(binding) {
                btnTakePhoto.setTextColor(ContextCompat.getColor(requireContext(), actionButtonTextColor!!))
                btnChooseImage.setTextColor(ContextCompat.getColor(requireContext(), actionButtonTextColor!!))
                btnTakeVideo.setTextColor(ContextCompat.getColor(requireContext(), actionButtonTextColor!!))
                btnChooseVideo.setTextColor(ContextCompat.getColor(requireContext(), actionButtonTextColor!!))
            }
        }
        if (cancelButtonBg != null) {
            binding.btnCancel.setBackgroundResource(cancelButtonBg!!)
        }
        if (cancelButtonTextColor != null) {
            binding.btnCancel.setTextColor(ContextCompat.getColor(requireContext(), cancelButtonTextColor!!))
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnCancel -> hideBottomSheet()
            binding.btnTakePhoto -> {
                action = TAKE_IMAGE
                selectFile()
            }
            binding.btnTakeVideo -> {
                action = TAKE_VIDEO
                selectFile()
            }
            binding.btnChooseImage -> {
                action = PICK_IMAGE
                selectFile()
            }
            binding.btnChooseVideo -> {
                action = PICK_VIDEO
                selectFile()
            }
        }
    }

    private fun requestPermission(): Boolean {
        if (EasyPermissions.hasPermissions(mContext, *permissions)) return true
        requestPermissions(this, getString(R.string.permission_camera_rationale), REQUEST_PERMISSION, permissions)
        return false
    }

    private fun requestContactPermission(): Boolean {
        if (EasyPermissions.hasPermissions(mContext, *contact_permission)) return true
        requestPermissions(this, getString(R.string.permission_contact_rationale), REQUEST_CONTACT_PERMISSION, contact_permission)
        return false
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, permissionCallbacks)
    }

    val permissionCallbacks = object : PermissionCallbacks {
        override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        }

        override fun onPermissionsGranted(requestCode: Int, perms: MutableList<String>) {
            if (requestCode == REQUEST_CONTACT_PERMISSION) {
                selectContact()
            } else {
                selectFile()
            }
        }

        override fun onPermissionsDenied(requestCode: Int, perms: MutableList<String>) {
            hideBottomSheet()
        }
    }

    @AfterPermissionGranted(REQUEST_PERMISSION)
    private fun selectFile() {
        if (!requestPermission()) return
        if (action == TAKE_IMAGE || action == TAKE_VIDEO) {
            val intent: Intent
            if (action == TAKE_IMAGE) {
                intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                file = FileUtil.createNewFile(context, MediaType.IMAGE)
            } else {
                intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                file = FileUtil.createNewFile(context, MediaType.VIDEO)
            }

            val uri: Uri = FileUtil.getURI(context, applicationId, file)
            intent.putExtra(MediaStore.EXTRA_OUTPUT, uri)

            if (VERSION.SDK_INT >= VERSION_CODES.LOLLIPOP) {
                intent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION)
            } else {
                val resInfoList = mContext.packageManager.queryIntentActivities(
                    intent,
                    PackageManager.MATCH_DEFAULT_ONLY
                )
                for (resolveInfo in resInfoList) {
                    val packageName = resolveInfo.activityInfo.packageName
                    mContext.grantUriPermission(
                        packageName,
                        uri,
                        Intent.FLAG_GRANT_WRITE_URI_PERMISSION or Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                }
            }
            startActivityForResult(Intent.createChooser(intent, "Capture Using"), action.id)

        } else if (action == PICK_IMAGE) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(Intent.createChooser(intent, "Select Photo"), action.id)

        } else if (action == PICK_VIDEO) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")
            startActivityForResult(Intent.createChooser(intent, "Select Video"), action.id)

        } else if (action == PICK_DOCUMENT) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            startActivityForResult(Intent.createChooser(intent, "Pick File"), action.id)
        }
    }

    @AfterPermissionGranted(REQUEST_CONTACT_PERMISSION)
    private fun selectContact() {
        if (!requestContactPermission()) return
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
        startActivityForResult(intent, PICK_CONTACT.id)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Thread {
                showProgressBar(true)
                try {
                    processActivityResult(requestCode, data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                showProgressBar(false)
            }.start()
        } else {
            MediaLog.e("Activity Result Code $resultCode")
            if (directAction) hideBottomSheet()
        }
    }

    private fun showProgressBar(enable: Boolean) {
        if (mediaPickerCallback == null) return
        executeOnMain({ mediaPickerCallback?.showProgressBar(enable) })
    }

    private fun processActivityResult(requestCode: Int, intent: Intent?) {
        GlobalScope.launch {
            var media: Media? = null
            when (requestCode) {
                TAKE_IMAGE.id -> {
                    try {
                        if (context == null) return@launch
                        if (file == null) return@launch
                        file = FileUtil.imageCompress(
                            mContext,
                            file!!,
                            MediaType.IMAGE
                        ) // image compress
                        media = Media.create(Thumb.generate(mContext, MediaType.IMAGE, file!!))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        media = null
                    }
                }
                PICK_IMAGE.id -> {
//                file = FileUtil.getNewPath(context, intent.getData(), MediaType.IMAGE);
                    try {
                        if (context == null) return@launch
                        file =
                            FileUtil.getFileFromUri(mContext, intent!!.data, MediaType.IMAGE)
                        if (file == null) return@launch
                        file = FileUtil.imageCompress(
                            mContext,
                            file!!,
                            MediaType.IMAGE
                        ) // image compress
                        media = Media.create(Thumb.generate(mContext, MediaType.IMAGE, file!!))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        media = null
                    }
                }
                TAKE_VIDEO.id -> {
                    if (file != null) media =
                        Media.create(Thumb.generate(mContext, MediaType.VIDEO, file!!))
                }
                PICK_VIDEO.id -> {
                    //              trimRequest(data.getUser());
//                file = FileUtil.getNewPath(context, intent.getData(), MediaType.VIDEO);
                    file = FileUtil.getFileFromUri(mContext, intent!!.data, MediaType.VIDEO)
                    if (file == null) return@launch
                    val mMedia: Media =
                        Media.create(Thumb.generate(mContext, MediaType.VIDEO, file!!))
                    if (mMedia.isValid) media = mMedia
                }
                CROP_REQUEST -> {
                    if (file == null) return@launch
                    media = Media.create(Thumb.generate(mContext, MediaType.VIDEO, file!!))
                }
                PICK_DOCUMENT.id -> {
                    try {
                        if (intent!!.data == null) return@launch
                        file = FileUtil.getFileFromUri(
                            mContext,
                            intent.data,
                            MediaType.DOCUMENT
                        )
                        if (file == null) return@launch
                        media = Media.create(Thumb.generate(MediaType.DOCUMENT, file!!))
                    } catch (e: Exception) {
                        e.printStackTrace()
                        media = null
                    }
                }
                PICK_CONTACT.id -> {
                    if (intent!!.data == null) return@launch
//                media = Media.create(Ezvcard.write(readContactFromUri(context, intent.data)).version(VCardVersion.V4_0).go())
                }
            }
            if (mediaPickerCallback == null) return@launch
            executeOnMain {
                showProgressBar(false)
                if (media == null)
                    mediaPickerCallback!!.onPickedError(null)
                else
                    mediaPickerCallback!!.onPickedSuccess(media)
                hideBottomSheet()
            }
        }
    }

    fun setMediaListenerCallback(type: SelectionType, mediaPickerCallback: MediaPickerCallback?) {
        this.selectionType = type
        this.mediaPickerCallback = mediaPickerCallback
    }

    fun setAction(action: SelectionType) {
        this.action = action
        directAction = true
    }

    override fun onShow(dialog: DialogInterface) {
        super.onShow(dialog)
        if (!directAction) return
        if (bottomSheetBehavior != null) {
            bottomSheetBehavior?.setPeekHeight(10, true)
        }
    }

    companion object {
        private val permissions = if (VERSION.SDK_INT >= VERSION_CODES.Q) {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.ACCESS_MEDIA_LOCATION)
        } else {
            arrayOf(Manifest.permission.CAMERA, Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        }

        private val contact_permission = arrayOf(Manifest.permission.READ_CONTACTS)
        private val PROJECTION = arrayOf(ContactsContract.CommonDataKinds.Phone.NUMBER, ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME)

        private const val VIDEO_LIMIT = 10
        private const val REQUEST_PERMISSION = 101
        private const val REQUEST_CONTACT_PERMISSION = 102
        private const val CROP_REQUEST = 103

        fun isCorrectLimit(context: Context?, uri: Uri?): Boolean {
            try {
                val mp = MediaPlayer.create(context, uri)
                val duration = mp.duration
                mp.release()
                return duration <= VIDEO_LIMIT
            } catch (e: Exception) {
                e.printStackTrace()
            }
            return false
        }

        fun getFileUri(file: File): Uri {
            return Uri.fromFile(file)
        }

        fun getFileUri(path: String?): Uri? {
            if (path == null) return null
            return getFileUri(File(path))
        }
    }
}