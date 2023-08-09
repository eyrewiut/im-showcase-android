package uk.co.imshowcase.imshowcase.network

import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory

/*
* Copied (and converted to kotlin using android studio) from a tutorial for the retrofit library
* Author: Prakash Pun
* Available at: https://medium.com/@prakash_pun/retrofit-a-simple-android-tutorial-48437e4e5a23
* Accessed: 26 Oct 2022
*/
object StoryblokApiClient {
    private var retrofit: Retrofit? = null
//    private const val BASE_URL = "https://api.storyblok.com/v2/"
    val retrofitInstance: Retrofit?
        get() {
            if (retrofit == null) {
                retrofit = Retrofit.Builder()
                    .baseUrl("https://api.storyblok.com/")
                    .addConverterFactory(MoshiConverterFactory.create())
                    .build()
            }
            return retrofit
        }
}