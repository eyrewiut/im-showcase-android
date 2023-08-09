package uk.co.imshowcase.imshowcase.network

import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query
import uk.co.imshowcase.imshowcase.models.ManyStoryResponse
import uk.co.imshowcase.imshowcase.models.OneStoryResponse
import uk.co.imshowcase.imshowcase.models.ProjectContentType
import uk.co.imshowcase.imshowcase.util.Constants

interface StoryblokService {
    @GET("/v2/cdn/stories/{uuid}?find_by=uuid")
    suspend fun getProjectByUUID(@Path("uuid") uuid: String, @Query("token") token: String = Constants.STORYBLOK_ACCESS_TOKEN): OneStoryResponse<ProjectContentType>

    @GET("/v2/cdn/stories/{slug}?starts_with=projects%2f")
    suspend fun getProjectsBySlug(@Path("slug") slug: String = "", @Query("token") token: String = Constants.STORYBLOK_ACCESS_TOKEN): ManyStoryResponse<ProjectContentType>
}