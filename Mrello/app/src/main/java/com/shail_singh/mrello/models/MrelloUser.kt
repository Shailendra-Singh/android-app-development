package com.shail_singh.mrello.models

import android.os.Parcel
import android.os.Parcelable

data class MrelloUser(
    val id: String = "",
    val name: String = "",
    val email: String = "",
    val image: String = "",
    val mobile: Long = -1,
    val fcmToken: String = ""
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readString().toString(),
        parcel.readLong(),
        parcel.readString().toString()
    )

    override fun describeContents(): Int = 0

    override fun writeToParcel(dest: Parcel, flags: Int) = with(dest) {
        writeString(id)
        writeString(name)
        writeString(email)
        writeString(image)
        writeLong(mobile)
        writeString(fcmToken)
    }

    companion object CREATOR : Parcelable.Creator<MrelloUser> {
        override fun createFromParcel(parcel: Parcel): MrelloUser {
            return MrelloUser(parcel)
        }

        override fun newArray(size: Int): Array<MrelloUser?> {
            return arrayOfNulls(size)
        }
    }
}
