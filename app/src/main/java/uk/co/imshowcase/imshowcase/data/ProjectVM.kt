package uk.co.imshowcase.imshowcase.data

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import uk.co.imshowcase.imshowcase.models.ProjectContentType
import uk.co.imshowcase.imshowcase.models.Story
import uk.co.imshowcase.imshowcase.network.StoryblokApiClient
import uk.co.imshowcase.imshowcase.network.StoryblokService
import uk.co.imshowcase.imshowcase.util.Constants
import uk.co.imshowcase.imshowcase.util.OperationResult

/*
enum class ProjectLoadingStatus {
    LOADING,
    ERROR,
    DONE
}
*/

class ProjectVM : ViewModel() {
    private var _project = MutableLiveData<Story<ProjectContentType>?>()

    private val service = StoryblokApiClient.retrofitInstance!!.create(StoryblokService::class.java)

    val project: LiveData<Story<ProjectContentType>?> = _project

    fun loadProjectFromApi(id: String, accessToken: String = Constants.STORYBLOK_ACCESS_TOKEN) = liveData(Dispatchers.IO) {
        emit(OperationResult.loading("ProjectResponse"))
        try {
            _project.postValue(service.getProjectByUUID(id, accessToken).story)
            emit(OperationResult.success())
        } catch(err: Exception) {
            Log.e("ProjectVM", "Failure while fetching project from api", err)
            emit(OperationResult.error(err))
        }
    }

    fun loadProject(project: Story<ProjectContentType>) {
        _project.value = project
    }
}