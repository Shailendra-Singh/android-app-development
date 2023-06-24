package com.shail_singh.mrello.models

import android.os.Parcel
import android.os.Parcelable

data class MrelloTask(
    var id: String = "", var name: String = "", var createdBy: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!, parcel.readString()!!, parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(createdBy)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MrelloTask> {
        override fun createFromParcel(parcel: Parcel): MrelloTask {
            return MrelloTask(parcel)
        }

        override fun newArray(size: Int): Array<MrelloTask?> {
            return arrayOfNulls(size)
        }
    }
}