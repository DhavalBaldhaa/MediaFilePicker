<h1 align="center">MediaFilePicker</h1>
<p align="center">
  <a href="https://jitpack.io/#dhaval-baldha1812/mediafilepicker"> <img src="https://jitpack.io/v/dhaval-baldha1812/mediafilepicker/month.svg" /></a>
  <a href="https://jitpack.io/#dhaval-baldha1812/mediafilepicker"> <img src="https://jitpack.io/v/dhaval-baldha1812/mediafilepicker.svg" /></a>
</p>

MediaFilePicker is android library which will help you to pick any type
of media file in your application. No need to manage any kind of extra
permission or result method override. Just create library class instance
and use it or also medify ui as your requirement.

## Installation
Step 1. Add the JitPack repository to your build file
```
allprojects {
    repositories {
	...
	maven { url 'https://jitpack.io' }
}
```
Step 2. Add the dependency
```
dependencies {
    implementation 'com.github.dhaval-baldha1812:MediaFilePicker:release_version'
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

## Author
Maintained by [Dhaval Baldha](https://www.github.com/dhaval-baldha1812)

## Contribution
[![GitHub contributors](https://img.shields.io/github/contributors/dhaval-baldha1812/MediaFilePicker.svg)](https://github.com/dhaval-baldha1812/MediaFilePicker/graphs/contributors)

* Bug reports and pull requests are welcome.
* Make sure you use [square/java-code-styles](https://github.com/square/java-code-styles) to format your code.
