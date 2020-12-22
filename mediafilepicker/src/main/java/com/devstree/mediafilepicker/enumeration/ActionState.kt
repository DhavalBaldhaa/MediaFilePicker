package com.devstree.mediafilepicker.enumeration

import android.os.Parcel
import android.os.Parcelable
import java.util.*

enum class ActionState(private val id: Int, val value: String) : Parcelable {
    NONE(0, "NONE"),
    PAUSE(1, "PAUSED"),
    PROGRESS(2, "PROGRESS"),
    PROCESSING(3, "PROCESSING"),
    FINISH(4, "FINISH"),
    FAILED(5, "FAILED");

    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(parcel: Parcel, i: Int) {
        parcel.writeInt(id)
        parcel.writeString(value)
    }

    companion object {
        val CREATOR: Parcelable.Creator<ActionState> = object : Parcelable.Creator<ActionState> {
            override fun createFromParcel(parcel: Parcel): ActionState {
                return values()[parcel.readInt()]
            }

            override fun newArray(size: Int): Array<ActionState?> {
                return arrayOfNulls(size)
            }
        }

        //Lookup table
        private val lookup: MutableMap<String, ActionState> = HashMap()

        //This method can be used for reverse lookup purpose
        operator fun get(value: String?): ActionState {
            return if (value.isNullOrEmpty()) NONE else lookup[value] ?: NONE
        }

        //Populate the lookup table on loading time
        init {
            for (type in values()) {
                lookup[type.value] = type
            }
        }
    }
}
