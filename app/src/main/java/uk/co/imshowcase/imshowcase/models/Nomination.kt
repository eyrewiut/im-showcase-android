package uk.co.imshowcase.imshowcase.models

import com.serjltt.moshi.adapters.SerializeNulls

data class Nomination(
    val id: String?,
    @SerializeNulls val nomination: String?
)

data class Vote(
    val id: String?,
    var nomination: String?,
    var project: Story<ProjectContentType>?
)
