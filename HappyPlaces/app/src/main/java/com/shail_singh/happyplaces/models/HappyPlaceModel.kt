package com.shail_singh.happyplaces.models

import android.os.Parcel
import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.shail_singh.happyplaces.AppConstants

@Entity(tableName = AppConstants.TABLE_NAME)
data class HappyPlaceModel(
    @PrimaryKey val id: Long = System.currentTimeMillis(),
    val title: String?,
    val imagePath: String?,
    val description: String?,
    val date: String?,
    val location: String?,
    val latitude: Double,
    val longitude: Double
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readLong(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readString(),
        parcel.readDouble(),
        parcel.readDouble()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(title)
        parcel.writeString(imagePath)
        parcel.writeString(description)
        parcel.writeString(date)
        parcel.writeString(location)
        parcel.writeDouble(latitude)
        parcel.writeDouble(longitude)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<HappyPlaceModel> {
        override fun createFromParcel(parcel: Parcel): HappyPlaceModel {
            return HappyPlaceModel(parcel)
        }

        override fun newArray(size: Int): Array<HappyPlaceModel?> {
            return arrayOfNulls(size)
        }
    }
}
