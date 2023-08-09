package uk.co.imshowcase.imshowcase.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import uk.co.eyrewiut.imshowcase.*
import uk.co.imshowcase.imshowcase.data.ProjectVM
import uk.co.imshowcase.imshowcase.data.ProjectsListVM
import uk.co.eyrewiut.imshowcase.databinding.FragmentProjectsListBinding
import uk.co.imshowcase.imshowcase.ProjectListAdapter
import uk.co.imshowcase.imshowcase.util.OperationState

class ProjectsListFragment : MainFragment() {
    private var _viewBinding: FragmentProjectsListBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val listVM: ProjectsListVM by activityViewModels()
    private val projectVM: ProjectVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentProjectsListBinding.inflate(layoutInflater)

        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val navController = findNavController()

        // Initialise the list adapter with the onclick listener
        val listAdapter = ProjectListAdapter { story ->
            projectVM.loadProject(story)
            navController.navigate(R.id.viewProjectFragment)
        }

        viewBinding.projectListView.adapter = listAdapter

        // Load the list data
        if (savedInstanceState == null && listVM.projects.value.isNullOrEmpty()) {
            loadProjects()
        } else if (listVM.projects.value != null) {
            listAdapter.submitList(listVM.projects.value)
        }
    }

    private fun loadProjects() {
        listVM.fetchProjects().observe(viewLifecycleOwner) {
            when (it.status) {
                OperationState.ERROR -> {
//                    Toast.makeText(context, "Error loading projects...", Toast.LENGTH_SHORT)
//                        .show()
                    Snackbar.make(viewBinding.root, "Couldn't load projects", Snackbar.LENGTH_INDEFINITE)
                        .setAction("Retry") {
                            loadProjects()
                        }
                        .show()
                }
                OperationState.LOADING -> {
                    viewBinding.loadingSpinner.show()
                }
                OperationState.SUCCESS -> {
                    viewBinding.loadingSpinner.hide()
                    (viewBinding.projectListView.adapter as ProjectListAdapter)
                        .submitList(listVM.projects.value!!)
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}