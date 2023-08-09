package uk.co.imshowcase.imshowcase

import android.content.Context
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import uk.co.imshowcase.imshowcase.data.UserVM
import uk.co.imshowcase.imshowcase.network.SupabaseClient
import uk.co.imshowcase.imshowcase.network.SupabaseService

class TokenRefreshWorker(appContext: Context, workerParams: WorkerParameters) : CoroutineWorker(appContext, workerParams) {
    private val service = SupabaseClient.retrofitInstance!!.create(SupabaseService::class.java)
    private val credentialStorage = appContext.getSharedPreferences("uk.co.imshowcase.CREDENTIALS", Context.MODE_PRIVATE)

    // Refreshes the grant token to prevent the session from timing out.
    override suspend fun doWork(): Result {
        if (!credentialStorage.contains("REFRESH_TOKEN")) Result.failure()
        val tokenResponse = service.refreshToken(
            SupabaseService.TokenRefreshBody(
                credentialStorage.getString(
                    "REFRESH_TOKEN",
                    null
                )!!
            )
        )

        val newGrant = UserVM.Grant.fromResponse(tokenResponse)

        with(credentialStorage.edit()) {
            putString("TOKEN", newGrant.token)
            putString("REFRESH_TOKEN", newGrant.refreshToken)
            putInt("LIFETIME", newGrant.lifetime)
            putString("TYPE", newGrant.type)
            apply()
        }

        return Result.success()
    }
}