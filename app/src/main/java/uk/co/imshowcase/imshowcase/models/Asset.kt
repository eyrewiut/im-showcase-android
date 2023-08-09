package uk.co.imshowcase.imshowcase.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class Asset() : Parcelable {
    @Json(name = "id") var id: Int? = null

    @Json(name = "alt") var alt: String? = null

    @Json(name = "name") var name: String? = null

    @Json(name = "focus") var focus: Any? = null

    @Json(name = "title") var title: String? = null

    @Json(name = "filename") var filename: String? = null

    @Json(name = "copyright") var copyright: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readValue(Int::class.java.classLoader) as? Int
        alt = parcel.readString()
        name = parcel.readString()
        title = parcel.readString()
        filename = parcel.readString()
        copyright = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeValue(id)
        parcel.writeString(alt)
        parcel.writeString(name)
        parcel.writeString(title)
        parcel.writeString(filename)
        parcel.writeString(copyright)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Asset> {
        override fun createFromParcel(parcel: Parcel): Asset {
            return Asset(parcel)
        }

        override fun newArray(size: Int): Array<Asset?> {
            return arrayOfNulls(size)
        }
    }
}
