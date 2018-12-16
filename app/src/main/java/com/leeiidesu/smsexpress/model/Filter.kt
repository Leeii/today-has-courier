package com.leeiidesu.smsexpress.model

import android.os.Parcel
import android.os.Parcelable
import io.objectbox.annotation.Entity
import io.objectbox.annotation.Id

@Entity
data class Filter(
        @Id var id: Long = 0,
        var alias: String? = null,
        var keywordsRegex: String? = null,
        var codeRegex: String? = null,
        var nominate: String? = null,
        var fixedName: Boolean = false,
        var nameRegex: String? = null,
        var nameStartSplit: Int = 0,
        var nameEndSplit: Int = 0,
        var lastReadTime: Long = 0,
        var startColor: Int = 0,
        var endColor: Int = 0) : Parcelable {
    constructor(parcel: Parcel) : this(
            parcel.readLong(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readString(),
            parcel.readByte() != 0.toByte(),
            parcel.readString(),
            parcel.readInt(),
            parcel.readInt(),
            parcel.readLong(),
            parcel.readInt(),
            parcel.readInt()) {
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeLong(id)
        parcel.writeString(alias)
        parcel.writeString(keywordsRegex)
        parcel.writeString(codeRegex)
        parcel.writeString(nominate)
        parcel.writeByte(if (fixedName) 1 else 0)
        parcel.writeString(nameRegex)
        parcel.writeInt(nameStartSplit)
        parcel.writeInt(nameEndSplit)
        parcel.writeLong(lastReadTime)
        parcel.writeInt(startColor)
        parcel.writeInt(endColor)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Filter> {
        override fun createFromParcel(parcel: Parcel): Filter {
            return Filter(parcel)
        }

        override fun newArray(size: Int): Array<Filter?> {
            return arrayOfNulls(size)
        }
    }
}