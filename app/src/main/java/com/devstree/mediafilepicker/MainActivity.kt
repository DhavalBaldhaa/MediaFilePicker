package com.devstree.mediafilepicker

import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.bumptech.glide.Glide
import com.devstree.mediafilepicker.bottomsheet.BottomSheetFilePicker
import com.devstree.mediafilepicker.databinding.ActivityMainBinding
import com.devstree.mediafilepicker.listener.MediaPickerCallback
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
                bottomSheetFilePicker.actionButtonBg = R.drawable.button_bg
                bottomSheetFilePicker.cancelButtonBg = R.drawable.button_bg_filled
                bottomSheetFilePicker.actionButtonTextColor = R.color.purple_500
                bottomSheetFilePicker.cancelButtonTextColor = R.color.white
                bottomSheetFilePicker.setMediaListenerCallback(BottomSheetFilePicker.IMAGE, object : MediaPickerCallback {
                    override fun onPickedSuccess(media: Media?) {
                        if (media == null) return
                        Glide
                            .with(this@MainActivity)
                            .load(media.getNotNullUrl())
                            .centerCrop()
                            .into(binding.imgMedia)

                        binding.txtMediaDetails.text =
                            String.format("Media type : ${media.mediaType.getName()} \n" +
                                    "Media file name : ${media.filename} \n" +
                                    "Media file path : ${media.getNotNullUrl()}"
                            )
                    }

                    override fun onPickedError(error: String?) {
                        Toast.makeText(this@MainActivity, error, Toast.LENGTH_SHORT).show()
                    }

                    override fun showProgressBar(enable: Boolean) {
                    }
                })
                bottomSheetFilePicker.show(supportFragmentManager, "take_all")
            }
        }
    }
}