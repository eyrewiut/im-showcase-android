package uk.co.imshowcase.imshowcase.models

import android.os.Parcel
import android.os.Parcelable
import com.squareup.moshi.Json

class ProjectContentType() : Parcelable {
    @Json(name = "_uid") var uid: String? = null

    @Json(name = "asset") var asset: List<Asset>? = null

    @Json(name = "email") var email: String? = null

    @Json(name = "title") var title: String? = null

    @Json(name = "author") var author: String? = null

    @Json(name = "comments") var comments: List<Comment>? = null

    @Json(name = "stickers") var stickers: List<Sticker>? = null

    @Json(name = "component") var component: String? = null

    @Json(name = "tech_used") var techUsed: String? = null

    @Json(name = "thumbnail") var thumbnail: Asset? = null

    @Json(name = "description") var description: String? = null

    @Json(name = "author_links") var authorLinks: List<AuthorLink>? = null

    @Json(name = "year_of_study") var yearOfStudy: String? = null

    @Json(name = "year_submitted") var yearSubmitted: Int? = null

    @Json(name = "content_warnings") var contentWarnings: String? = null

    @Json(name = "developed_this_year") var developedThisYear: Boolean? = null

    @Json(name = "social_media_promotion") var socialMediaPromotion: Boolean? = null

    @Json(name = "_editable") var editable: String? = null

    constructor(parcel: Parcel) : this() {
        uid = parcel.readString()
        asset = parcel.createTypedArrayList(Asset)
        email = parcel.readString()
        title = parcel.readString()
        author = parcel.readString()
        comments = parcel.createTypedArrayList(Comment)
        stickers = parcel.createTypedArrayList(Sticker)
        component = parcel.readString()
        techUsed = parcel.readString()
        thumbnail = parcel.readParcelable(Asset::class.java.classLoader)
        description = parcel.readString()
        authorLinks = parcel.createTypedArrayList(AuthorLink)
        yearOfStudy = parcel.readString()
        yearSubmitted = parcel.readValue(Int::class.java.classLoader) as? Int
        contentWarnings = parcel.readString()
        developedThisYear = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        socialMediaPromotion = parcel.readValue(Boolean::class.java.classLoader) as? Boolean
        editable = parcel.readString()
    }

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(uid)
        parcel.writeTypedList(asset)
        parcel.writeString(email)
        parcel.writeString(title)
        parcel.writeString(author)
        parcel.writeTypedList(comments)
        parcel.writeTypedList(stickers)
        parcel.writeString(component)
        parcel.writeString(techUsed)
        parcel.writeParcelable(thumbnail, flags)
        parcel.writeString(description)
        parcel.writeTypedList(authorLinks)
        parcel.writeString(yearOfStudy)
        parcel.writeValue(yearSubmitted)
        parcel.writeString(contentWarnings)
        parcel.writeValue(developedThisYear)
        parcel.writeValue(socialMediaPromotion)
        parcel.writeString(editable)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<ProjectContentType> {
        override fun createFromParcel(parcel: Parcel): ProjectContentType {
            return ProjectContentType(parcel)
        }

        override fun newArray(size: Int): Array<ProjectContentType?> {
            return arrayOfNulls(size)
        }
    }
}