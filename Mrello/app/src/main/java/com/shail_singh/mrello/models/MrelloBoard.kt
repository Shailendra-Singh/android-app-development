package com.shail_singh.mrello.models

import android.os.Parcel
import android.os.Parcelable
import com.shail_singh.mrello.utils.Utilities

data class MrelloBoard(
    var id: String = "",
    val name: String = "",
    val image: String = "",
    val createdBy: String = "",
    val createdDate: String = Utilities.getTodayDateString(),
    val assignedTo: ArrayList<String> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(image)
        parcel.writeString(createdBy)
        parcel.writeString(createdDate)
        parcel.writeStringList(assignedTo)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MrelloBoard> {
        override fun createFromParcel(parcel: Parcel): MrelloBoard {
            return MrelloBoard(parcel)
        }

        override fun newArray(size: Int): Array<MrelloBoard?> {
            return arrayOfNulls(size)
        }
    }
}