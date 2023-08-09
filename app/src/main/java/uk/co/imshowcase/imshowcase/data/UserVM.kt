package uk.co.imshowcase.imshowcase.data

import android.app.Application
import android.content.Context
import android.os.Parcel
import android.os.Parcelable
import android.util.Log
import androidx.lifecycle.*
import androidx.work.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import retrofit2.HttpException
import uk.co.imshowcase.imshowcase.TokenRefreshWorker
import uk.co.imshowcase.imshowcase.models.Nomination
import uk.co.imshowcase.imshowcase.network.SupabaseClient
import uk.co.imshowcase.imshowcase.network.SupabaseService
import uk.co.imshowcase.imshowcase.models.TokenResponse
import uk.co.imshowcase.imshowcase.models.User
import uk.co.imshowcase.imshowcase.models.Vote
import uk.co.imshowcase.imshowcase.util.OperationResult
import java.time.Duration


class UserVM(application: Application, private val state: SavedStateHandle) : AndroidViewModel(application) {
    private val credentialStorage = application.getSharedPreferences("uk.co.imshowcase.CREDENTIALS", Context.MODE_PRIVATE)

    private var _token = MutableLiveData<Grant?>()
    private var _user = MutableLiveData<User?>()
    private var _vote = MutableLiveData<Vote?>()

    private val workManager = WorkManager.getInstance(application)

//    private var _loggedIn = MutableLiveData<Boolean>(false)

    private val service = SupabaseClient.retrofitInstance!!.create(SupabaseService::class.java)
//    var loggedIn: LiveData<Boolean> = _loggedIn
    val loggedIn: Boolean get() = _token.value != null
    val loggedInWithUser: Boolean get() = _user.value != null && _token.value != null
    var token = _token

    var user: LiveData<User?> = _user
    var vote: LiveData<Vote?> = _vote

    init {
//        state.set("LOGGED_IN", false)

        // Check if a user token is persisted, if so load it into the vm and fetch the user data
        val savedTokenExists = credentialStorage.contains("TOKEN") &&
            credentialStorage.contains("REFRESH_TOKEN") &&
            credentialStorage.contains("LIFETIME") &&
            credentialStorage.contains("TYPE")

        if (_token.value == null && savedTokenExists) {
            val savedGrant = Grant(
                credentialStorage.getString("TOKEN", null)!!,
                credentialStorage.getString("REFRESH_TOKEN", null)!!,
                credentialStorage.getInt("LIFETIME", 0),
                credentialStorage.getString("TYPE", "bearer")!!,
            )

            _token.value = savedGrant
//            state.set("LOGGED_IN", true)

            viewModelScope.launch {
                try {
                    _user.value = service.getUser("Bearer ${token.value?.token}")
//                    _vote.value = service.getNominationWithVote(
//                        "Bearer ${_token.value?.token}",
//                        "*",
//                        mapOf("id" to "eq.${_user.value?.id}")
//                    ).first()

                } catch (err: Exception) {
                    Log.e(TAG, "Error getting user", err)
                    // TODO logout user if the token is expired
                }
            }
        }
    }

    // Set the user as logged in.
    /*fun setLoggedInState(isLoggedIn: Boolean) {
        state.set("LOGGED_IN", isLoggedIn)
    }*/

    // Utility method to save token to persistant storage
    private fun saveCredentials(credentials: Grant? = _token.value) {
        val grant = credentials ?: return
        with(credentialStorage.edit()) {
            putString("TOKEN", grant.token)
            putString("REFRESH_TOKEN", grant.refreshToken)
            putInt("LIFETIME", grant.lifetime)
            putString("TYPE", grant.type)
            apply()
        }

        val workRequest = PeriodicWorkRequestBuilder<TokenRefreshWorker>(
            repeatInterval = Duration.ofSeconds(grant.lifetime.toLong()),
            flexTimeInterval = Duration.ofMinutes(15)
        )
            .setConstraints(Constraints(NetworkType.CONNECTED))
            .setInitialDelay(10, java.util.concurrent.TimeUnit.MINUTES)
            .build()


        workManager.enqueueUniquePeriodicWork("sessionRefresh", ExistingPeriodicWorkPolicy.UPDATE, workRequest)
    }

    // Utility method to remove persistant storage
    private fun clearCredentials() {
        with(credentialStorage.edit()) {
            remove("TOKEN")
            remove("REFRESH_TOKEN")
            remove("LIFETIME")
            remove("TYPE")
            apply()
        }
        workManager.cancelUniqueWork("sessionRefresh")
    }

    fun login(email: String, password: String) = liveData(Dispatchers.IO) {
        try {
            emit(OperationResult.loading("UserGrantResponse"))
            val res = service.login(SupabaseService.UserCredentialsBody(email, password))

            val grant = Grant(
                res.access_token,
                res.refresh_token,
                res.expires_in,
                res.token_type
            )

//            _token.value = grant
            _token.postValue(grant)
            saveCredentials(grant)
//            _user.value = res.user
            _user.postValue(res.user)
//            state.getLiveData<Boolean>("LOGGED_IN").postValue(true)
//            state.set("LOGGED_IN", true)
            emit(OperationResult.success())
        } catch(err: Exception) {
            Log.e(TAG, "Error while logging in", err)
            emit(OperationResult.error(err))
        }
    }

    fun register(email: String, password: String) = liveData(Dispatchers.IO) {
        val credentialsBody = SupabaseService.UserCredentialsBody(email, password)
        try {
            emit(OperationResult.loading("UserGrantResponse"))
            service.register(credentialsBody)
            val loginResult = service.login(credentialsBody)

            val grant = Grant.fromResponse(loginResult)

            _token.postValue(grant)
            _user.postValue(loginResult.user)
            saveCredentials(grant)
//            state.getLiveData<Boolean>("LOGGED_IN").postValue(true)
            emit(OperationResult.success())
        } catch (err: Exception) {
            Log.e(TAG, "Error registering user", err)
            emit(OperationResult.error(err))
        }
    }

    fun logout() = liveData(Dispatchers.IO) {
        if (loggedIn) {
            try {
                emit(OperationResult.loading("LogoutResponse"))
                service.logout("Bearer ${_token.value!!.token}")
                _token.postValue(null)
//                state.set("LOGGED_IN", true)
//                state.getLiveData<Boolean>("LOGGED_IN").postValue(false)
                _vote.postValue(null)
                clearCredentials()
                emit(OperationResult.success(null))
            } catch (err: Exception) {
                Log.e(TAG, "Failed to logout", err)

                // If the error is because the token has expired, just remove from storage
                if (err is HttpException && err.code() == 401) {
//                    state.set("LOGGED_IN", false)
//                    state.getLiveData<Boolean>("LOGGED_IN").postValue(false)
                    clearCredentials()
                    emit(OperationResult.success(null))
                } else {
                    emit(OperationResult.error(err))
                }
            }
        }
    }

    fun getUserNomination() = liveData(Dispatchers.IO) {
        if (!loggedInWithUser) emit(OperationResult.error(Exception("Not logged in")))
        else {
            try {
                emit(OperationResult.loading("UserNominationResponse"))
                val nomination = service.getNominationWithVote(
                    "Bearer ${_token.value!!.token}",
                    "*",
                    mapOf("id" to "eq.${_user.value!!.id}")
                ).firstOrNull()
                _vote.postValue(nomination)
                emit(OperationResult.success())
            } catch (err: Exception) {
                emit(OperationResult.error(err))
            }
        }
    }

    fun castVote(projectId: String) = liveData(Dispatchers.IO) {
        if (!loggedInWithUser) emit(OperationResult.error(Exception("Not logged in")))
        else {
            try {
                emit(OperationResult.loading("VoteCastResponse"))

                service.updateNominations(
                    "Bearer ${_token.value!!.token}",
                    mapOf("id" to "eq.${user.value!!.id}"),
                    Nomination(null, projectId)
                )

                val nomination = service.getNominationWithVote(
                    "Bearer ${_token.value!!.token}",
                    "*",
                    mapOf("id" to "eq.${_user.value!!.id}")
                ).firstOrNull()

                _vote.postValue(nomination!!)
                emit(OperationResult.success(Nomination(user.value?.id, projectId)))
            } catch (err: Exception) {
                Log.e(TAG, "Error casting vote", err)
                emit(OperationResult.error(err))
            }
        }
    }

    fun retractVote() = liveData(Dispatchers.IO) {
        if (!loggedInWithUser) emit(OperationResult.error(Exception("Not logged in")))
        else if (vote.value == null) emit(OperationResult.success())
        else {
            try {
                emit(OperationResult.loading("RetractVoteResponse"))

                service.updateNominations("Bearer ${_token.value!!.token}", mapOf(
                    "id" to "eq.${user.value!!.id}"
                ),
                Nomination(null, null)
                )

                _vote.postValue(_vote.value?.apply {
                    nomination = null
                    project = null
                })
                emit(OperationResult.success())
            } catch (err: Exception) {
                Log.e(TAG, "Error retracting vote", err)
                emit(OperationResult.error(err))
            }
        }
    }

    data class Grant(
        var token: String,
        var refreshToken: String,
        var lifetime: Int,
        var type: String,
    ) : Parcelable {
        constructor(parcel: Parcel) : this(
            parcel.readString()!!,
            parcel.readString()!!,
            parcel.readInt(),
            parcel.readString()!!,
        )

        override fun writeToParcel(parcel: Parcel, flags: Int) {
            parcel.writeString(token)
            parcel.writeString(refreshToken)
            parcel.writeInt(lifetime)
            parcel.writeString(type)
        }

        override fun describeContents(): Int {
            return 0
        }

        companion object CREATOR : Parcelable.Creator<Grant> {
            override fun createFromParcel(parcel: Parcel): Grant {
                return Grant(parcel)
            }

            override fun newArray(size: Int): Array<Grant?> {
                return arrayOfNulls(size)
            }

            fun fromResponse(tokenResponse: TokenResponse): Grant {
                return Grant(
                    tokenResponse.access_token,
                    tokenResponse.refresh_token,
                    tokenResponse.expires_in,
                    tokenResponse.token_type
                )
            }
        }
    }

    companion object {
        const val TAG = "UserVM"
    }
}