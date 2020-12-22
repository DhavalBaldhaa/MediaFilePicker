package com.devstree.mediafilepicker.enumeration;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.webkit.MimeTypeMap;

import com.devstree.mediafilepicker.utils.FileUtil;
import com.devstree.mediafilepicker.utils.MediaLog;

import java.io.File;
import java.util.Collections;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static com.devstree.mediafilepicker.utils.FileUtil.getAudioDirectory;
import static com.devstree.mediafilepicker.utils.FileUtil.getContactDirectory;
import static com.devstree.mediafilepicker.utils.FileUtil.getDocumentDirectory;
import static com.devstree.mediafilepicker.utils.FileUtil.getImageDirectory;
import static com.devstree.mediafilepicker.utils.FileUtil.getThumbDirectory;
import static com.devstree.mediafilepicker.utils.FileUtil.getVideoDirectory;

public enum MediaType implements Parcelable {

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


    private static final Map<Integer, MediaType> ENUM_MAP_ID;
    private static final Map<String, MediaType> ENUM_MAP_NAMES;

    static {
        Map<Integer, MediaType> map_id = new ConcurrentHashMap<>();
        Map<String, MediaType> map_name = new ConcurrentHashMap<>();
        for (MediaType instance : MediaType.values()) {
            map_id.put(instance.getId(), instance);
            map_name.put(instance.getName(), instance);
        }
        ENUM_MAP_ID = Collections.unmodifiableMap(map_id);
        ENUM_MAP_NAMES = Collections.unmodifiableMap(map_name);
    }


    int id;
    String name;

    MediaType(int id) {
        this.id = id;
        this.name = name;
    }


    public int getId() {
        return id;
    }

    public String getName() {
        switch (this) {
            case TEXT:
                return "TEXT";
            case INFO:
                return "INFO";
            case IMAGE:
                return "IMAGE";
            case THUMB:
                return "THUMB";
            case GIF:
                return "GIF";
            case VIDEO:
                return "VIDEO";
            case AUDIO:
                return "AUDIO";
            case DOCUMENT:
                return "DOCUMENT";
            case LOCATION:
                return "LOCATION";
            case VOICE_CALL:
                return "VOICE_CALL";
            case VIDEO_CALL:
                return "VIDEO_CALL";
            case PDF:
                return "PDF";
            case CONTACT:
                return "CONTACT";
            case STICKER:
                return "STICKER";
            case PAYMENT:
                return "PAYMENT";
        }
        return "TEXT";
    }

    public static MediaType get(int id) {
        return ENUM_MAP_ID.get(id);
    }

    public static MediaType get(String name) {
        return ENUM_MAP_NAMES.get(name);
    }

    public static final Creator<MediaType> CREATOR = new Creator<MediaType>() {
        @Override
        public MediaType createFromParcel(Parcel in) {
            return MediaType.values()[in.readInt()];
        }

        @Override
        public MediaType[] newArray(int size) {
            return new MediaType[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(id);
        dest.writeString(name);
    }


    public static String getExtension(MediaType mediaType) {
        if (mediaType == IMAGE || mediaType == THUMB) return ".jpg";
        else if (mediaType == VIDEO) return ".mp4";
        else if (mediaType == AUDIO) return ".m4a";
        else if (mediaType == GIF) return ".gif";
        else if (mediaType == DOCUMENT) return ".doc";
        else if (mediaType == PDF) return ".pdf";
        else if (mediaType == CONTACT) return ".vcf";
        return ".jpg";
    }

    public static String getRootDirectory(Context context, MediaType mediaType) {
        File DIRECTORY = FileUtil.getRootDirectory(context);
        if (mediaType == IMAGE) DIRECTORY = getImageDirectory(context);
        else if (mediaType == THUMB) DIRECTORY = getThumbDirectory(context);
        else if (mediaType == VIDEO) DIRECTORY = getVideoDirectory(context);
        else if (mediaType == AUDIO) DIRECTORY = getAudioDirectory(context);
        else if (mediaType == GIF) DIRECTORY = getImageDirectory(context);
        else if (mediaType == DOCUMENT) DIRECTORY = getDocumentDirectory(context);
        else if (mediaType == PDF) DIRECTORY = getVideoDirectory(context);
        else if (mediaType == CONTACT) DIRECTORY = getContactDirectory(context);

        if (!DIRECTORY.exists()) {
            boolean isCreated = DIRECTORY.mkdirs();
            MediaLog.INSTANCE.e("getRootDirectory isCreated " + isCreated);
        }

        return DIRECTORY.getPath() + File.separator;
    }


    public static String getMime(MediaType mediaType) {
        if (mediaType == IMAGE) return "image/*";
        else if (mediaType == VIDEO) return "video/*";
        else if (mediaType == AUDIO) return "audio/*";
        else if (mediaType == GIF) return "image/*";
        else if (mediaType == PDF) return "pdf/*";
        else if (mediaType == DOCUMENT) return "doc/*";
        return "*/*";
    }

    public static String getMimeFromUrl(String url) {
        return getMimeFromExtension(MimeTypeMap.getFileExtensionFromUrl(url));
    }

    private static String getMimeFromExtension(String extension) {
        if (extension == null) return "*/*";
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
    }

    public static boolean isMedia(MediaType mediaType) {
        if (mediaType == IMAGE) return true;
        else if (mediaType == VIDEO) return true;
        else if (mediaType == AUDIO) return true;
        else if (mediaType == GIF) return true;
        else if (mediaType == DOCUMENT) return true;
        else return mediaType == PDF;
    }

    public static String getEmoji(MediaType mediaType) {
        if (mediaType == IMAGE) return "\uD83D\uDCF7";
        else if (mediaType == AUDIO) return "\uD83D\uDD0A";
        else if (mediaType == LOCATION) return "\uD83D\uDCCD";
        else if (mediaType == VIDEO || mediaType == GIF) return "\uD83D\uDCF9";
        else if (mediaType == DOCUMENT || mediaType == PDF) return "\uD83D\uDCC4";
        else if (mediaType == VOICE_CALL || mediaType == VIDEO_CALL) return "\uD83D\uDCDE";
        else return "\uD83D\uDCF7";
    }

    public static String getOneShotEmoji() {
        return "⊙";
    }

    public static String getOneShotText() {
        return "One Shot";
    }

    public boolean shouldHaveUploadUrl() {
        return this == MediaType.IMAGE || this == MediaType.VIDEO
                || this == MediaType.AUDIO || this == MediaType.DOCUMENT
                || this == MediaType.GIF || this == MediaType.PDF;
    }
}
