package uk.co.imshowcase.imshowcase.data

import android.util.Log
import androidx.lifecycle.*
import kotlinx.coroutines.Dispatchers
import uk.co.imshowcase.imshowcase.models.ProjectContentType
import uk.co.imshowcase.imshowcase.models.Story
import uk.co.imshowcase.imshowcase.network.StoryblokApiClient
import uk.co.imshowcase.imshowcase.network.StoryblokService
import uk.co.imshowcase.imshowcase.util.OperationResult

class ProjectsListVM : ViewModel() {
    private val service = StoryblokApiClient.retrofitInstance!!.create(StoryblokService::class.java)

    private var _projects = MutableLiveData<List<Story<ProjectContentType>>>()
    val projects: LiveData<List<Story<ProjectContentType>>> = _projects

    fun fetchProjects() = liveData(Dispatchers.IO) {
        emit(OperationResult.loading("ProjectsList"))
        try {
            _projects.postValue(service.getProjectsBySlug().stories)
            emit(OperationResult.success())
        } catch (err: Exception) {
            Log.e(TAG, "Error getting list of projects from api", err)
            _projects.postValue(listOf())
            emit(OperationResult.error(err))
        }
    }

    companion object {
        const val TAG = "ProjectsListVM"
    }
}