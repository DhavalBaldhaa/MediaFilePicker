package com.devstree.mediafilepicker.enumeration

import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.webkit.MimeTypeMap
import com.devstree.mediafilepicker.utils.FileUtil.getAudioDirectory
import com.devstree.mediafilepicker.utils.FileUtil.getContactDirectory
import com.devstree.mediafilepicker.utils.FileUtil.getDocumentDirectory
import com.devstree.mediafilepicker.utils.FileUtil.getImageDirectory
import com.devstree.mediafilepicker.utils.FileUtil.getRootDirectory
import com.devstree.mediafilepicker.utils.FileUtil.getThumbDirectory
import com.devstree.mediafilepicker.utils.FileUtil.getVideoDirectory
import com.devstree.mediafilepicker.utils.MediaLog.e
import java.io.File
import java.util.*
import java.util.concurrent.ConcurrentHashMap

enum class MediaType(var id: Int) : Parcelable {
    TEXT(1),
    INFO(2),
    IMAGE(3),
    GIF(4),
    VIDEO(5),
    AUDIO(6),
    DOCUMENT(7),
    LOCATION(8),
    VOICE_CALL(9),
    VIDEO_CALL(10),
    STICKER(11),
    PDF(12),
    CONTACT(13),
    THUMB(14),
    PAYMENT(15),
    QUICK_PAYMENT(16),
    FRIEND_REQUEST(17);

    companion object {
        private var ENUM_MAP_ID: Map<Int, MediaType>? = null
        private var ENUM_MAP_NAMES: Map<String, MediaType>? = null

        init {
            val map_id: HashMap<Int, MediaType> = HashMap()
            val map_name: HashMap<String, MediaType> = HashMap()

            values().forEach { mediaType ->
                map_id[mediaType.id] = mediaType
                map_name[mediaType.name] = mediaType
            }

            ENUM_MAP_ID = Collections.unmodifiableMap(map_id)
            ENUM_MAP_NAMES = Collections.unmodifiableMap(map_name)
        }

        operator fun get(id: Int?): MediaType {
            if (id == null) return TEXT
            return ENUM_MAP_ID!![id] ?: TEXT
        }

        operator fun get(name: String?): MediaType {
            if (name == null) return TEXT
            return ENUM_MAP_NAMES!![name] ?: TEXT
        }

        @JvmField
        val CREATOR: Parcelable.Creator<MediaType> = object : Parcelable.Creator<MediaType> {
            override fun createFromParcel(`in`: Parcel): MediaType {
                return values()[`in`.readInt()]
            }

            override fun newArray(size: Int): Array<MediaType?> {
                return arrayOfNulls(size)
            }
        }

        fun getExtension(mediaType: MediaType): String {
            return when (mediaType) {
                IMAGE, THUMB -> ".jpg"
                VIDEO -> ".mp4"
                AUDIO -> ".m4a"
                GIF -> ".gif"
                DOCUMENT -> ".doc"
                PDF -> ".pdf"
                CONTACT -> ".vcf"
                else -> ".jpg"
            }
        }

        fun getRootDirectory(context: Context?, mediaType: MediaType?): String {
            if (context == null) return ""
            if (mediaType == null) return ""
            val DIRECTORY = when (mediaType) {
                IMAGE -> getImageDirectory(context)
                THUMB -> getThumbDirectory(context)
                VIDEO -> getVideoDirectory(context)
                AUDIO -> getAudioDirectory(context)
                GIF -> getImageDirectory(context)
                DOCUMENT -> getDocumentDirectory(context)
                PDF -> getVideoDirectory(context)
                CONTACT -> getContactDirectory(context)
                else -> getRootDirectory(context)
            }
            if (!DIRECTORY.exists()) {
                val isCreated = DIRECTORY.mkdirs()
                e("getRootDirectory isCreated $isCreated")
            }
            return DIRECTORY.path + File.separator
        }

        fun getMime(mediaType: MediaType): String {
            return when (mediaType) {
                IMAGE -> "image/*"
                VIDEO -> "video/*"
                AUDIO -> "audio/*"
                GIF -> "image/*"
                PDF -> "pdf/*"
                DOCUMENT -> "doc/*"
                else -> "*/*"
            }
        }

        fun getMimeFromUrl(url: String?): String? {
            return getMimeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url))
        }

        private fun getMimeFromExtension(extension: String?): String? {
            return if (extension == null) "*/*" else MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension)
        }

        fun isMedia(mediaType: MediaType): Boolean {
            return when (mediaType) {
                IMAGE -> true
                VIDEO -> true
                AUDIO -> true
                GIF -> true
                DOCUMENT -> true
                else -> mediaType == PDF
            }
        }

        fun getEmoji(mediaType: MediaType): String {
            return when (mediaType) {
                IMAGE -> "\uD83D\uDCF7"
                AUDIO -> "\uD83D\uDD0A"
                LOCATION -> "\uD83D\uDCCD"
                VIDEO, GIF -> "\uD83D\uDCF9"
                DOCUMENT, PDF -> "\uD83D\uDCC4"
                VOICE_CALL, VIDEO_CALL -> "\uD83D\uDCDE"
                else -> "\uD83D\uDCF7"
            }
        }

        val oneShotEmoji: String
            get() = "⊙"
        val oneShotText: String
            get() = "One Shot"

    }

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeInt(id)
        dest.writeString(name)
    }

    fun shouldHaveUploadUrl(): Boolean {
        return this == IMAGE || this == VIDEO || this == AUDIO || this == DOCUMENT || this == GIF || this == PDF
    }
}