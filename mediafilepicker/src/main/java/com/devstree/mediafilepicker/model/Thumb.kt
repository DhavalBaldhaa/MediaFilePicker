package com.devstree.mediafilepicker.model

import android.content.Context
import android.os.Build
import android.util.Size
import com.devstree.mediafilepicker.enumeration.MediaType
import com.devstree.mediafilepicker.utils.FileUtil
import java.io.File

class Thumb {
    var mediaType: MediaType
    var file: File
    var thumb: File? = null
    var bytes: ByteArray? = null

    constructor(mediaType: MediaType, file: File) {
        this.mediaType = mediaType
        this.file = file
    }

    constructor(mediaType: MediaType, file: File, thumb: File?, bytes: ByteArray?) {
        this.mediaType = mediaType
        this.file = file
        this.thumb = thumb
        this.bytes = bytes
    }

    companion object {
        const val THUMB_SIZE = 320

        @JvmField
        val SIZE = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            Size(THUMB_SIZE, THUMB_SIZE)
        } else {
            null
        }

        fun generate(context: Context, mediaType: MediaType, file: File): Thumb {
            return FileUtil.getThumb(context, mediaType, file)
        }

        fun generate(mediaType: MediaType, file: File): Thumb {
            return Thumb(mediaType, file, null, null)
        }
    }
}