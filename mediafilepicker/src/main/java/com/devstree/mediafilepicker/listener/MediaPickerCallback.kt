package com.devstree.mediafilepicker.listener

import com.devstree.mediafilepicker.model.Media


interface MediaPickerCallback {
    fun onPickedSuccess(media: Media?)
    fun onPickedError(error: String?)
    fun showProgressBar(enable: Boolean)
}