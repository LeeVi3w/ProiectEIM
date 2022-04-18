package com.example.proiecteim

import android.os.Parcel
import android.os.Parcelable

data class Location (
    val name: String?,
    val currTemp: Float,
    val feelsLike: Float,
    val windSpeed: Float,
    val pressure: Int,
    val humidity: Int,
    var alertMinTemp: Float?,
    var alertMaxTemp: Float?
) : Parcelable {

    constructor(parcel: Parcel) : this(
        parcel.readString(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readFloat(),
        parcel.readInt(),
        parcel.readInt(),
        parcel.readValue(Float::class.java.classLoader) as? Float,
        parcel.readValue(Float::class.java.classLoader) as? Float
    )

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeFloat(currTemp)
        parcel.writeFloat(feelsLike)
        parcel.writeFloat(windSpeed)
        parcel.writeInt(pressure)
        parcel.writeInt(humidity)
        parcel.writeValue(alertMinTemp)
        parcel.writeValue(alertMaxTemp)
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