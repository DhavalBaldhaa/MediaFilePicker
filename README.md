MediaFilePicker is android library which will help you to pick any type
of media file in your application. No need to manage any kind of extra
permission or result method override. Just create library class instance
and use it or also medify ui as your requirement.

## Installation

Include MediaFilePickr dependency in to your app build.gradle file

```bash
    dependencies {
        implementation 'com.github.sdk:1.0.0'
    }
```

## Usage

To **initialize** the sdk class, Use below code and setListeners to
receive the callback.

```kotlin
val bottomSheetFilePicker = BottomSheetFilePicker()

bottomSheetFilePicker.setMediaListenerCallback(BottomSheetFilePicker.TAKE_ALL /*file pick action*/, object : MediaPickerCallback {
    override fun onPickedSuccess(media: Media?) {
      /*use media object for get your file information like path, image url, thumb url*/
    }

    override fun onPickedError(error: String?) {
        /*handle file pick error*/
    }

    override fun showProgressBar(enable: Boolean) {
        /*show progressbar if you want*/
    }
})

/*show file picker dialog in bottom*/
bottomSheetFilePicker.show(supportFragmentManager, "take_all")
```

**UI Customization** Use this method for customize of default library ui

```kotlin
// change action button background using custom drawable file 
bottomSheetFilePicker.actionButtonBg = R.drawable.button_bg

// change cancel button background using custom drawable file 
bottomSheetFilePicker.cancelButtonBg = R.drawable.button_bg_filled

// change action button text color 
bottomSheetFilePicker.actionButtonTextColor = R.color.purple_500

// change cancel button text color
bottomSheetFilePicker.cancelButtonTextColor = R.color.white
```

