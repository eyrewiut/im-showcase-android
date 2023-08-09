package uk.co.imshowcase.imshowcase.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class LinkField() : Parcelable {
    @Json(name = "id") var id: String? = null

    @Json(name = "url") var url: String? = null

    @Json(name = "linktype") var linktype: String? = null

    @Json(name = "fieldtype") var fieldtype: String? = null

    @Json(name = "cached_url") var cachedUrl: String? = null

    constructor(parcel: Parcel) : this() {
        id = parcel.readString()
        url = parcel.readString()
        linktype = parcel.readString()
        fieldtype = parcel.readString()
        cachedUrl = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(id)
        parcel.writeString(url)
        parcel.writeString(linktype)
        parcel.writeString(fieldtype)
        parcel.writeString(cachedUrl)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<LinkField> {
        override fun createFromParcel(parcel: Parcel): LinkField {
            return LinkField(parcel)
        }

        override fun newArray(size: Int): Array<LinkField?> {
            return arrayOfNulls(size)
        }
    }
}