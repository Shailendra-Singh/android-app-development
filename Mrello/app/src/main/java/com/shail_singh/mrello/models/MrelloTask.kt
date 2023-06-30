package com.shail_singh.mrello.models

import android.os.Parcel
import android.os.Parcelable

data class MrelloTask(
    var id: String = "",
    var name: String = "",
    var createdBy: String = "",
    var cardList: ArrayList<MrelloCard> = ArrayList()
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.createTypedArrayList<MrelloCard>(MrelloCard.CREATOR)!!
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) = with(parcel) {
        parcel.writeString(id)
        parcel.writeString(name)
        parcel.writeString(createdBy)
        parcel.writeTypedList(cardList)
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