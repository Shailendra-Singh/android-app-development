package com.shail_singh.mrello.models

import android.os.Parcel
import android.os.Parcelable

data class MrelloCard(
    var name: String = "",
    var createdBy: String = "",
    var assignedTo: ArrayList<String> = ArrayList(),
    var labelColor: String = "",
    var dueDate: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createStringArrayList()!!,
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(name)
        parcel.writeString(createdBy)
        parcel.writeStringList(assignedTo)
        parcel.writeString(labelColor)
        parcel.writeString(dueDate)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<MrelloCard> {
        override fun createFromParcel(parcel: Parcel): MrelloCard {
            return MrelloCard(parcel)
        }

        override fun newArray(size: Int): Array<MrelloCard?> {
            return arrayOfNulls(size)
        }
    }
}