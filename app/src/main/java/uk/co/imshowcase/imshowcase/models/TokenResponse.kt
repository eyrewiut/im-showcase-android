package uk.co.imshowcase.imshowcase.models

data class TokenResponse(
    var access_token: String,
    var token_type: String,
    var expires_in: Int,
    var refresh_token: String,
    var user: User
)
