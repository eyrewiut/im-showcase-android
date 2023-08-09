package uk.co.imshowcase.imshowcase.models

data class User(
    var id: String,
    var aud: String,
    var role: String,
    var email: String,
    var phone: String,
    var confirmation_sent_at: String,
    var app_metadata: UserAppMetadata,
    var identities: List<UserIdentity>,
    var created_at: String,
    var updated_at: String
)

data class UserAppMetadata(
    var provider: String,
    var providers: List<String>
)

data class UserIdentity(
    var id: String,
    var userId: String,
    var identity_data: UserIdentityData,
    var provider: String,
    var last_sign_in_at: String,
    var created_at: String,
    var updated_at: String
)

data class UserIdentityData(
    var email: String,
    var sub: String
)