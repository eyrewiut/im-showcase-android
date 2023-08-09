package uk.co.imshowcase.imshowcase.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class AuthorLink() : Parcelable {
    @Json(name = "_uid") var uid: String? = null

    @Json(name = "twitter") var twitter: LinkField? = null

    @Json(name = "linkedin") var linkedin: LinkField? = null

    @Json(name = "component") var component: String? = null

    @Json(name = "portfolio") var portfolio: LinkField? = null

    @Json(name = "_editable") var editable: String? = null

    constructor(parcel: Parcel) : this() {
        uid = parcel.readString()
        twitter = parcel.readParcelable(LinkField::class.java.classLoader)
        linkedin = parcel.readParcelable(LinkField::class.java.classLoader)
        component = parcel.readString()
        portfolio = parcel.readParcelable(LinkField::class.java.classLoader)
        editable = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeParcelable(twitter, flags)
        parcel.writeParcelable(linkedin, flags)
        parcel.writeString(component)
        parcel.writeParcelable(portfolio, flags)
        parcel.writeString(editable)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<AuthorLink> {
        override fun createFromParcel(parcel: Parcel): AuthorLink {
            return AuthorLink(parcel)
        }

        override fun newArray(size: Int): Array<AuthorLink?> {
            return arrayOfNulls(size)
        }
    }
}