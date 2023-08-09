package uk.co.imshowcase.imshowcase.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class Comment() : Parcelable {
    @Json(name = "_uid") var uid: String? = null

    @Json(name = "body") var body: String? = null

    @Json(name = "component") var component: String? = null

    @Json(name = "commentAuthor") var commentAuthor: String? = null

    @Json(name = "_editable") var editable: String? = null

    constructor(parcel: Parcel) : this() {
        uid = parcel.readString()
        body = parcel.readString()
        component = parcel.readString()
        commentAuthor = parcel.readString()
        editable = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeString(body)
        parcel.writeString(component)
        parcel.writeString(commentAuthor)
        parcel.writeString(editable)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Comment> {
        override fun createFromParcel(parcel: Parcel): Comment {
            return Comment(parcel)
        }

        override fun newArray(size: Int): Array<Comment?> {
            return arrayOfNulls(size)
        }
    }
}