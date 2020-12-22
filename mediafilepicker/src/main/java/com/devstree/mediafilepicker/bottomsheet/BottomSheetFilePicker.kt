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
import com.devstree.mediafilepicker.listener.MediaPickerCallback
import com.devstree.mediafilepicker.R
import com.devstree.mediafilepicker.databinding.BottomSheetCameraDialogBinding
import com.devstree.mediafilepicker.enumeration.MediaType
import com.devstree.mediafilepicker.model.Media
import com.devstree.mediafilepicker.model.Thumb
import com.devstree.mediafilepicker.utils.FileUtil
import com.devstree.mediafilepicker.utils.MediaLog
import pub.devrel.easypermissions.AfterPermissionGranted
import pub.devrel.easypermissions.EasyPermissions
import pub.devrel.easypermissions.EasyPermissions.PermissionCallbacks
import java.io.File

/**
 * Created by Dhaval Baldha on 22/12/2020.
 */

// refer : https://developer.android.com/training/data-storage/shared/media#request-permissions
open class BottomSheetFilePicker : BaseBottomSheet(), OnClickListener {
    private var file: File? = null
    private var type = IMAGE
    private var action = TAKE_PHOTO
    private var directAction = false
    private var mediaPickerCallback: MediaPickerCallback? = null
    private lateinit var binding: BottomSheetCameraDialogBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetCameraDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        mapping()
        if (directAction) {
            selectFile()
        }
    }

    private fun mapping() {
        with(binding) {
            btnTakePhoto.setText(R.string.take_a_photo)
            btnChooseImage.setText(R.string.choose_image_from_gallery)
            btnTakeVideo.setText(R.string.take_a_video)
            btnChooseVideo.setText(R.string.choose_video_from_gallery)
            btnEditCancel.setText(R.string.cancel)
        }

        binding.btnTakePhoto.setOnClickListener(this)
        binding.btnChooseImage.setOnClickListener(this)
        binding.btnTakeVideo.setOnClickListener(this)
        binding.btnChooseVideo.setOnClickListener(this)
        binding.btnEditCancel.setOnClickListener(this)

        if (type == TAKE_ALL) {
            binding.btnTakePhoto.visibility = VISIBLE
            binding.btnChooseImage.visibility = VISIBLE
            binding.btnTakeVideo.visibility = VISIBLE
            binding.btnChooseVideo.visibility = VISIBLE
        } else if (type == PICK_IMAGE_VIDEO) {
            binding.btnTakePhoto.visibility = GONE
            binding.btnTakeVideo.visibility = GONE
            binding.btnChooseImage.visibility = VISIBLE
            binding.btnChooseVideo.visibility = VISIBLE
        } else if (type == PICK_IMAGE) {
            binding.btnTakePhoto.visibility = GONE
            binding.btnTakeVideo.visibility = GONE
            binding.btnChooseImage.visibility = GONE
            binding.btnChooseVideo.visibility = GONE
            binding.btnEditCancel.visibility = GONE
        } else if (type == IMAGE) {
            binding.btnTakePhoto.visibility = VISIBLE
            binding.btnTakeVideo.visibility = GONE
            binding.btnChooseVideo.visibility = GONE
        } else if (type == VIDEO) {
            binding.btnTakePhoto.visibility = GONE
            binding.btnChooseImage.visibility = GONE
            binding.btnTakeVideo.visibility = VISIBLE
        }
    }

    override fun onClick(view: View) {
        when (view) {
            binding.btnEditCancel -> hideBottomSheet()
            binding.btnTakePhoto -> {
                action = TAKE_PHOTO
                selectFile()
            }
            binding.btnTakeVideo -> {
                action = TAKE_VIDEO
                selectFile()
            }
            binding.btnChooseImage -> {
                action = CHOOSE_IMAGE_FROM_GALLERY
                selectFile()
            }
            binding.btnChooseVideo -> {
                action = CHOOSE_VIDEO_FROM_GALLERY
                selectFile()
            }
        }
    }

    private fun requestPermission(): Boolean {
        if (EasyPermissions.hasPermissions(mContext, *permissions)) return true
        requestPermissions(
            this,
            getString(R.string.permission_camera_rationale),
            REQUEST_PERMISSION,
            permissions
        )
        return false
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        EasyPermissions.onRequestPermissionsResult(requestCode, permissions, grantResults, this)
    }

//    override fun onPermissionsGranted(requestCode: Int, perms: List<String>) {
//    }
//
//    override fun onPermissionsDenied(requestCode: Int, perms: List<String>) {
//        hideBottomSheet()
//    }

    @AfterPermissionGranted(REQUEST_PERMISSION)
    private fun selectFile() {
        if (!requestPermission()) return
        if (action == TAKE_PHOTO || action == TAKE_VIDEO) {
            val intent: Intent
            if (action == TAKE_PHOTO) {
                intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
                file = FileUtil.createNewFile(context, MediaType.IMAGE)
            } else {
                intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                file = FileUtil.createNewFile(context, MediaType.VIDEO)
            }

            val uri: Uri = FileUtil.getURI(context, file)
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
            startActivityForResult(Intent.createChooser(intent, "Capture Using"), action)

        } else if (action == CHOOSE_IMAGE_FROM_GALLERY) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*")
            startActivityForResult(Intent.createChooser(intent, "Select Photo"), action)

        } else if (action == CHOOSE_VIDEO_FROM_GALLERY) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.setDataAndType(MediaStore.Video.Media.EXTERNAL_CONTENT_URI, "video/*")
            startActivityForResult(Intent.createChooser(intent, "Select Video"), action)

        } else if (action == PICK_DOCUMENT) {
            val intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "*/*"
            intent.addCategory(Intent.CATEGORY_OPENABLE)
            intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            intent.addFlags(Intent.FLAG_GRANT_PERSISTABLE_URI_PERMISSION)
            startActivityForResult(Intent.createChooser(intent, "Pick File"), action)

        } else if (action == PICK_CONTACT) {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = ContactsContract.CommonDataKinds.Phone.CONTENT_TYPE
            startActivityForResult(intent, PICK_CONTACT)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK) {
            Thread(Runnable {
                showProgressBar(true)
                try {
                    processActivityResult(requestCode, data)
                } catch (e: Exception) {
                    e.printStackTrace()
                }
                showProgressBar(false)
            }).start()
        } else {
            MediaLog.e("Activity Result Code $resultCode")
            if (directAction) hideBottomSheet()
        }
    }

    private fun showProgressBar(enable: Boolean) {
        if (mediaPickerCallback == null) return
        executeOnMain(Runnable { mediaPickerCallback?.showProgressBar(enable) })
    }

    private fun processActivityResult(requestCode: Int, intent: Intent?) {
        var media: Media? = null
        when (requestCode) {
            TAKE_PHOTO -> {
                try {
                    if (context == null) return
                    if (file == null) return
                    file = FileUtil.imageCompress(mContext, file!!, MediaType.IMAGE) // image compress
                    media = Media.create(Thumb.generate(mContext, MediaType.IMAGE, file!!))
                } catch (e: Exception) {
                    e.printStackTrace()
                    media = null
                }
            }
            CHOOSE_IMAGE_FROM_GALLERY -> {
//                file = FileUtil.getNewPath(context, intent.getData(), MediaType.IMAGE);
                try {
                    if (context == null) return
                    file =
                        FileUtil.getFileFromUri(mContext, intent!!.data, MediaType.IMAGE)
                    if (file == null) return
                    file = FileUtil.imageCompress(mContext, file!!, MediaType.IMAGE) // image compress
                    media = Media.create(Thumb.generate(mContext, MediaType.IMAGE, file!!))
                } catch (e: Exception) {
                    e.printStackTrace()
                    media = null
                }
            }
            TAKE_VIDEO -> {
                if (file != null) media =
                    Media.create(Thumb.generate(mContext, MediaType.VIDEO, file!!))
            }
            CHOOSE_VIDEO_FROM_GALLERY -> {
                //              trimRequest(data.getUser());
//                file = FileUtil.getNewPath(context, intent.getData(), MediaType.VIDEO);
                file = FileUtil.getFileFromUri(mContext, intent!!.data, MediaType.VIDEO)
                if (file == null) return
                val mMedia: Media =
                    Media.create(Thumb.generate(mContext, MediaType.VIDEO, file!!))
                if (mMedia.isValid) media = mMedia
            }
            CROP_REQUEST -> {
                if (file == null) return
                media = Media.create(Thumb.generate(mContext, MediaType.VIDEO, file!!))
            }
            PICK_DOCUMENT -> {
                try {
                    if (intent!!.data == null) return
                    file = FileUtil.getFileFromUri(
                        mContext,
                        intent.data,
                        MediaType.DOCUMENT
                    )
                    if (file == null) return
                    media = Media.create(Thumb.generate(MediaType.DOCUMENT, file!!))
                } catch (e: Exception) {
                    e.printStackTrace()
                    media = null
                }
            }
            PICK_CONTACT -> {
//                if (intent!!.data == null) return
//                media = Media.create(Ezvcard.write(readContactFromUri(context, intent.data)).version(VCardVersion.V4_0).go())
            }
        }
        if (mediaPickerCallback == null) return
        executeOnMain(Runnable {
            showProgressBar(false)
            if (media == null)
                mediaPickerCallback!!.onPickedError(null)
            else
                mediaPickerCallback!!.onPickedSuccess(media)
            hideBottomSheet()
        })
    }

    fun setMediaListenerCallback(type: Int, mediaPickerCallback: MediaPickerCallback?) {
        this.type = type
        this.mediaPickerCallback = mediaPickerCallback
    }

    fun setAction(action: Int) {
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
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.ACCESS_MEDIA_LOCATION,
                Manifest.permission.READ_CONTACTS
            )
        } else {
            arrayOf(
                Manifest.permission.CAMERA,
                Manifest.permission.READ_EXTERNAL_STORAGE,
                Manifest.permission.WRITE_EXTERNAL_STORAGE,
                Manifest.permission.READ_CONTACTS
            )
        }
        private val PROJECTION = arrayOf(
            ContactsContract.CommonDataKinds.Phone.NUMBER,
            ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME
        )
        private const val VIDEO_LIMIT = 10
        const val IMAGE = 1
        const val VIDEO = 2
        const val TEXT = 3
        private const val REQUEST_PERMISSION = 101
        const val TAKE_PHOTO = 1
        const val CHOOSE_IMAGE_FROM_GALLERY = 2
        const val CROP_REQUEST = 3
        const val TAKE_VIDEO = 4
        const val CHOOSE_VIDEO_FROM_GALLERY = 5
        const val TRIM_VIDEO = 6
        const val PICK_IMAGE_VIDEO = 7
        const val TAKE_ALL = 8
        const val PICK_DOCUMENT = 9
        const val PICK_CONTACT = 10
        const val PICK_IMAGE = 11
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

        fun getFileUri(file: File?): Uri {
            return Uri.fromFile(file)
        }

        fun getFileUri(path: String?): Uri {
            return getFileUri(File(path))
        }
    }
}