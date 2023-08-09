package uk.co.imshowcase.imshowcase.network

import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.PATCH
import retrofit2.http.POST
import retrofit2.http.Query
import retrofit2.http.QueryMap
import uk.co.imshowcase.imshowcase.models.Nomination
import uk.co.imshowcase.imshowcase.models.TokenResponse
import uk.co.imshowcase.imshowcase.models.User
import uk.co.imshowcase.imshowcase.models.Vote

const val AUTH_PREFIX: String = "/auth/v1"
const val REST_PREFIX: String = "/rest/v1"
interface SupabaseService {
    // Register
    @POST("$AUTH_PREFIX/signup")
    suspend fun register(@Body user: UserCredentialsBody): User

    // Send the user a login link
    @POST("$AUTH_PREFIX/otp")
    suspend fun sendOneTimePassword(@Body user: OtpBody)

    data class OtpBody(
        var email: String,
        var createUser: Boolean
    )

    // Get user data
    @GET("$AUTH_PREFIX/user")
    suspend fun getUser(@Header("authorization") authorization: String): User

    // Refresh the token (tokens expire after a certain amount of time)
    @POST("$AUTH_PREFIX/token?grant_type=refresh_token")
    suspend fun refreshToken(@Body credentials: TokenRefreshBody): TokenResponse

    // Login with email
    @POST("$AUTH_PREFIX/token?grant_type=password")
    suspend fun login(@Body credentials: UserCredentialsBody): TokenResponse

    @POST("$AUTH_PREFIX/logout")
    suspend fun logout(@Header("Authorization") authorization: String): Response<Unit>

    data class UserCredentialsBody(var email: String, var password: String)
    data class TokenRefreshBody(var refresh_token: String)

    //Get nominations
    @GET("$REST_PREFIX/nominations")
    suspend fun getNominations(@Header("Authorization") authorization: String, @Query("select") columns: String? = "*", @QueryMap options: Map<String, String>? = mapOf()): List<Nomination>

    // Get vote view
    @GET("$REST_PREFIX/votes")
    suspend fun getNominationWithVote(@Header("Authorization") authorization: String, @Query("select") columns: String? = "*", @QueryMap options: Map<String, String>? = mapOf()): List<Vote>

    // Update nominations
    @PATCH("$REST_PREFIX/nominations")
    suspend fun updateNominations(@Header("Authorization") authorization: String, @QueryMap options: Map<String, String>, @Body modification: Nomination): Response<Unit>
}
