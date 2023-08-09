package uk.co.imshowcase.imshowcase.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import com.airbnb.paris.extensions.style
import com.squareup.picasso.Picasso
import uk.co.imshowcase.imshowcase.util.OperationState
import uk.co.imshowcase.imshowcase.data.ProjectVM
import uk.co.eyrewiut.imshowcase.R
import uk.co.imshowcase.imshowcase.data.UserVM
import uk.co.eyrewiut.imshowcase.databinding.FragmentViewProjectBinding
import uk.co.imshowcase.imshowcase.models.ProjectContentType
import uk.co.imshowcase.imshowcase.models.Story
import uk.co.imshowcase.imshowcase.util.Constants

private const val PROJECT_ID = "PROJECT_IDENTIFIER"

/**
 * A simple [Fragment] subclass.
 * Use the [ViewProjectFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class ViewProjectFragment : MainFragment() {
    private val projectVM: ProjectVM by activityViewModels()
    private val userVM: UserVM by activityViewModels()
    private var _viewBinding: FragmentViewProjectBinding? = null
    private val viewBinding get() = _viewBinding!!

    private var projectId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        arguments?.let {
            projectId = it.getString(PROJECT_ID)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentViewProjectBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val projectId = if (!projectId.isNullOrEmpty()) projectId else savedInstanceState?.getString(
            PROJECT_ID
        )

        if (projectId.isNullOrBlank() || !projectId.isNullOrBlank() && savedInstanceState != null) {
            updateProjectUi(projectVM.project.value!!)
        } else {
            projectVM.loadProjectFromApi(projectId, Constants.STORYBLOK_ACCESS_TOKEN).observe(viewLifecycleOwner) {
                when (it.status) {
                    OperationState.SUCCESS -> {
                        viewBinding.viewProjectSpinner.hide()
                        val project = projectVM.project.value!!
                        updateProjectUi(project)
                    }
                    OperationState.ERROR -> {
                        Toast.makeText(
                            this.context,
                            "Something went wrong...Please try later!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    OperationState.LOADING -> {
                        viewBinding.viewProjectSpinner.show()
                    }
                }
            }
        }

        viewBinding.voteButton.setOnClickListener {
            val projectId = projectVM.project.value?.uuid
            val nominatedProjectId = userVM.vote.value?.nomination

            if (projectId == nominatedProjectId) {
                userVM.retractVote()
            } else {
                userVM.castVote(projectId!!)
            }.observe(viewLifecycleOwner) {
                when (it.status) {
                    OperationState.SUCCESS -> {
                        updateVoteButton()
                    }
                    OperationState.ERROR -> {
                        Toast.makeText(context, "Failed to nominate project!", Toast.LENGTH_SHORT).show()
                    }
                    else -> {}
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun updateVoteButton(projectId: String? = projectVM.project.value?.uuid) {
        val nominatedProjectId = userVM.vote.value?.nomination
        val currentStoryIsNominated = projectId == nominatedProjectId

        viewBinding.voteButton.text = if (currentStoryIsNominated) getString(R.string.vote_button_retract) else getString(R.string.vote_button_nominate)

        viewBinding.voteButton.style(if (currentStoryIsNominated)
            com.google.android.material.R.style.Widget_Material3_Button_OutlinedButton
        else
            com.google.android.material.R.style.Widget_Material3_Button)
    }

    private fun updateProjectUi(story: Story<ProjectContentType>) {
        val project = story.content

        viewBinding.projectTitle.text = project.title
        viewBinding.projectAbstract.text = project.description
        viewBinding.projectAuthor.text = project.author
        updateVoteButton(story.uuid)

        if (project.thumbnail != null && !project.thumbnail?.filename.isNullOrBlank()) {
            try {
                Picasso.get()
                    .load(project.thumbnail!!.filename)
                    .resize(1600, 900)
                    .onlyScaleDown()
                    .into(viewBinding.projectThumbnail)
            } catch (err: Exception) {
                // TODO: are there softer alternatives, such as just using a placeholder?
                Toast.makeText(context, "Error loading image", Toast.LENGTH_SHORT).show()
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param projectId Project ID
         * @return A new instance of fragment ViewProjectFragment.
         */
        @JvmStatic
        fun newInstance(projectId: String?) =
            ViewProjectFragment().apply {
                arguments = Bundle().apply {
                    putString(PROJECT_ID, projectId)
                }
            }
    }
}