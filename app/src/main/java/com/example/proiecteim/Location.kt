package com.example.proiecteim

import android.os.Parcel
import android.os.Parcelable

data class Location (
    val name: String?,
    val currTemp: Float,
    val maxTemp: Float,
    val minTemp: Float,
    val pressure: Int,
    val humidity: Int
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readInt()
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeFloat(currTemp)
        parcel.writeFloat(maxTemp)
        parcel.writeFloat(minTemp)
        parcel.writeInt(pressure)
        parcel.writeInt(humidity)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Location> {
        override fun createFromParcel(parcel: Parcel): Location {
            return Location(parcel)
        }

        override fun newArray(size: Int): Array<Location?> {
            return arrayOfNulls(size)
        }
    }
}