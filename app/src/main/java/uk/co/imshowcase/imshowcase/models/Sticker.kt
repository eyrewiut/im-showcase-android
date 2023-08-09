package uk.co.imshowcase.imshowcase.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class Sticker() : Parcelable {
    @Json(name = "_uid") var uid: String? = null

    @Json(name = "award") var award: String? = null

    @Json(name = "component") var component: String? = null

    @Json(name = "acheived_award") var acheivedAward: Boolean? = null

    @Json(name = "_editable") var editable: String? = null

    constructor(parcel: Parcel) : this() {
        uid = parcel.readString()
        award = parcel.readString()
        component = parcel.readString()
        acheivedAward = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        editable = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(award)
        parcel.writeString(component)
        parcel.writeValue(acheivedAward)
        parcel.writeString(editable)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Sticker> {
        override fun createFromParcel(parcel: Parcel): Sticker {
            return Sticker(parcel)
        }

        override fun newArray(size: Int): Array<Sticker?> {
            return arrayOfNulls(size)
        }
    }
}