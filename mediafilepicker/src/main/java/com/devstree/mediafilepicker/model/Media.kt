package com.devstree.mediafilepicker.model

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Parcel
import android.os.Parcelable
import android.text.format.DateUtils
import android.util.Base64
import com.devstree.mediafilepicker.enumeration.ActionState
import com.devstree.mediafilepicker.enumeration.MediaType
import com.devstree.mediafilepicker.utils.FileUtil
import java.io.*
import java.util.*

class Media : Parcelable {
    var id: Long = System.currentTimeMillis()
    var messageId: Long = 0L
    var url: String? = null
    var thumbUrl: String? = null
    var remoteUrl: String? = null
    var remoteThumbUrl: String? = null
    var thumb: ByteArray? = null
    var size: Long = 0L
    var duration: Long = 0L
    var mediaType: MediaType = MediaType.IMAGE
    var progress = 0f
    var actionState: ActionState = ActionState.NONE
    var playbackDuration: Int = 0
    var isLocal = false
    var latitude: String? = null
    var longitude: String? = null
    var description: String? = null
    var base64: String? = null


    val totalDuration: Int get() = (duration / 1000).toInt()

    val localFile: File? get() = if (url == null) null else File(url.orEmpty())

    val localThumbFile: File? get() = if (thumbUrl == null) null else File(thumbUrl.orEmpty())

    fun getNotNullUrl(): String {
        return if (url.isNullOrEmpty()) remoteUrl.orEmpty() else url.orEmpty()
    }

    fun hasLocalUrl(): Boolean {
        return !url.isNullOrEmpty()
    }

    fun getNotNullRemoteThumbUrl(): String? {
        if (remoteThumbUrl.isNullOrEmpty()) {
            return remoteUrl
        }
        return remoteThumbUrl
    }

    fun getNotNullThumbUrl(): String? {
        if (!thumbUrl.isNullOrEmpty()) {
            return thumbUrl
        }
        return remoteThumbUrl
    }

    val hasLocalThumb: Boolean get() = if (thumbUrl == null) false else File(thumbUrl.orEmpty()).exists()

    fun isExist(context: Context?): Boolean {
        return FileUtil.isAvailable(context, this)
    }

    fun getRootDirectory(context: Context?): String {
        return MediaType.getRootDirectory(context, mediaType)
    }

    val filename: String
        get() =
            FileUtil.getFileName(url).takeIf { isLocal } ?: FileUtil.getFileName(remoteUrl)

    val remoteFileName: String
        get() = FileUtil.getFileName(remoteUrl)

    val formattedFileName: String get() = FileUtil.getFormattedFilename(filename)

    val extension: String get() = FileUtil.getExtensionName(filename)

    fun getDownloadPath(context: Context?): String {
        return getRootDirectory(context) + filename
    }

    fun getDownloadFile(context: Context?): File {
        return File(getDownloadPath(context))
    }

    fun isDownloadableFileExist(context: Context?): Boolean {
        return File(getDownloadPath(context)).exists()
    }

    val decodedBitmap: Bitmap get() = BitmapFactory.decodeByteArray(thumb, 0, thumb!!.size)

    val isValid: Boolean get() = size > 0

    fun hasRemoteUrl(): Boolean {
        return !remoteUrl.isNullOrEmpty()
    }

    fun isActionInProgress(): Boolean {
        return actionState == ActionState.PROGRESS || actionState == ActionState.PROCESSING
    }

    fun copy(): Media {
        val media = Media()
        media.id = id
        media.messageId = messageId
        media.url = url
        media.thumbUrl = thumbUrl
        media.remoteUrl = remoteUrl
        media.remoteThumbUrl = remoteThumbUrl
        media.size = size
        media.duration = duration
        media.mediaType = mediaType
        media.playbackDuration = playbackDuration
        media.actionState = ActionState.NONE
        media.progress = progress
        media.isLocal = isLocal
        media.latitude = latitude
        media.longitude = longitude
        media.description = description
        media.base64 = base64
        return media
    }

    fun clearLocal(): Media? {
        url = null
        thumbUrl = null
        url = null
        progress = 0f
        isLocal = false
        actionState = ActionState.NONE
        return this
    }

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as Media

        if (id != other.id) return false
        if (messageId != other.messageId) return false

        return true
    }

    constructor()

    constructor(parcel: Parcel) : this() {
        id = parcel.readLong()
        messageId = parcel.readLong()
        url = parcel.readString()
        thumbUrl = parcel.readString()
        remoteUrl = parcel.readString()
        remoteThumbUrl = parcel.readString()
        thumb = parcel.createByteArray()
        size = parcel.readLong()
        duration = parcel.readLong()
        mediaType =
            MediaType.get(parcel.readString()) //parcel.readParcelable(MediaType::class.java.classLoader) ?: MediaType.IMAGE
        progress = parcel.readFloat()
        actionState =
            ActionState.get(parcel.readString()) //parcel.readParcelable(ActionState::class.java.classLoader) ?: ActionState.NONE
        playbackDuration = parcel.readInt()
        isLocal = parcel.readByte() != 0.toByte()
        latitude = parcel.readString()
        longitude = parcel.readString()
        description = parcel.readString()
    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeLong(id)
        dest.writeLong(messageId)
        dest.writeString(url)
        dest.writeString(thumbUrl)
        dest.writeString(remoteUrl)
        dest.writeString(remoteThumbUrl)
        dest.writeByteArray(thumb)
        dest.writeLong(size)
        dest.writeLong(duration)
        dest.writeString(mediaType.name)
        dest.writeFloat(progress)
        dest.writeString(actionState.value)
        dest.writeInt(playbackDuration)
        dest.writeByte(if (isLocal) 1 else 0)
        dest.writeString(latitude)
        dest.writeString(longitude)
        dest.writeString(description)
    }

    override fun hashCode(): Int {
        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Objects.hash(remoteUrl, url)
        } else {
            arrayListOf(remoteUrl, url).hashCode()
        }
    }

    companion object CREATOR : Parcelable.Creator<Media> {
        override fun createFromParcel(parcel: Parcel): Media {
            return Media(parcel)
        }

        override fun newArray(size: Int): Array<Media?> {
            return arrayOfNulls(size)
        }

        fun getFormattedDuration(seconds: Long): String {
            return DateUtils.formatElapsedTime(seconds)
        }

        fun create(thumb: Thumb): Media {
            val media = Media()
            media.mediaType = thumb.mediaType
            media.url = thumb.file.path
            media.size = thumb.file.length()
            media.isLocal = true
            if (thumb.thumb != null) {
                media.thumbUrl = thumb.thumb?.path
                media.thumb = thumb.bytes
            }
            media.getBase64String()
            return media
        }

        fun create(thumb: Thumb, duration: Long): Media {
            val media = Media()
            media.mediaType = thumb.mediaType
            media.url = thumb.file.path
            media.size = thumb.file.length()
            media.duration = duration
            media.isLocal = true
            if (thumb.thumb != null) {
                media.thumbUrl = thumb.thumb?.path
                media.thumb = thumb.bytes
            }
            media.getBase64String()
            return media
        }
    }

    fun getBase64String(): String? {
        if (!base64.isNullOrEmpty()) return base64
        try {
            var bytesRead: Int
            val buffer = ByteArray(8192)
            val inputStream: InputStream = FileInputStream(localFile)
            val output = ByteArrayOutputStream()
            while (inputStream.read(buffer).also { bytesRead = it } != -1) {
                output.write(buffer, 0, bytesRead)
            }
            return Base64.encodeToString(output.toByteArray(), Base64.DEFAULT).also {
                base64 = it
            }
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return null
    }

    fun hasBase64(): Boolean {
        return !base64.isNullOrEmpty()
    }


    // add duration in seconds
//    val mediaMetaData: MediaMetaData
//        get() {
//            val infoData = MediaMetaData()
//            infoData.mediaUrl =
//                getDownloadPath(Controller.instance).takeIf { isDownloadableFileExist(Controller.instance) }
//                    ?: remoteUrl
//
//            infoData.mediaId = infoData.mediaUrl
//            infoData.mediaTitle = MediaType.AUDIO.getName()
//            infoData.mediaArtist = ""
//            infoData.mediaAlbum = ""
//            infoData.mediaDuration = totalDuration.toString() // add duration in seconds
//            //infoData.setMediaArt("Media album image goes here");
//            return infoData
//        }

}