package com.devstree.mediafilepicker

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.devstree.mediafilepicker.bottomsheet.BaseBottomSheet
import com.devstree.mediafilepicker.listener.MediaPickerCallback
import com.devstree.mediafilepicker.bottomsheet.BottomSheetFilePicker
import com.devstree.mediafilepicker.databinding.ActivityMainBinding
import com.devstree.mediafilepicker.model.Media

class MainActivity : AppCompatActivity(), View.OnClickListener {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }

    override fun onClick(v: View?) {
        when (v) {
            binding.btnOpenFilePicker -> {
                val bottomSheetFilePicker = BottomSheetFilePicker()
//                bottomSheetFilePicker.setMediaListenerCallback(BottomSheetFilePicker.TAKE_ALL, object : MediaPickerCallback {
//                    override fun onPickedSuccess(media: Media?) {
//                        if (media == null) return
//                        mediaList.add(media)
//                        mediaAdapter?.setData(mediaList)
//                        binding.rvMediaItem.scrollToPosition(mediaList.size - 1)
//                        setUpViewPager()
//                    }
//
//                    override fun onPickedError(error: String?) {
//                        toast(error)
//                    }
//
//                    override fun showProgressBar(enable: Boolean) {
//                    }
//                })
//                bottomSheetFilePicker.show(supportFragmentManager, "take_all")
            }
        }
    }
}