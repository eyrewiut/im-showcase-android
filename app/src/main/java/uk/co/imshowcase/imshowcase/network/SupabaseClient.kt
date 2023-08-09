package uk.co.imshowcase.imshowcase.network

import com.serjltt.moshi.adapters.SerializeNulls
import com.squareup.moshi.Moshi
import okhttp3.Interceptor
import okhttp3.Response
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import uk.co.imshowcase.imshowcase.util.Constants
import uk.co.imshowcase.imshowcase.util.Constants.SUPABASE_URL

/*
* Copied (and converted to kotlin using android studio) from a tutorial for the retrofit library
* Author: Prakash Pun
* Available at: https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
* Accessed: 26 Oct 2022
*/
object SupabaseClient {
    private var retrofit: Retrofit? = null

    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                val moshi = Moshi.Builder()
                    .add(SerializeNulls.ADAPTER_FACTORY)
                    .build()

                val okHttpClient = okhttp3.OkHttpClient()
                    .newBuilder()
                    .addInterceptor(InterceptorAddToken())
                    .build()

                retrofit = Retrofit.Builder()
                    .client(okHttpClient)
                    .baseUrl(SUPABASE_URL)
                    .addConverterFactory(MoshiConverterFactory.create(moshi))
                    .build()
            }
            return retrofit
        }

    class InterceptorAddToken : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            var req = chain.request()
            req = req.newBuilder()
                .addHeader("apikey", Constants.SUPABASE_TOKEN)
                .build()

            return chain.proceed(req)
        }
    }
}
