package uk.co.imshowcase.imshowcase.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.squareup.picasso.Picasso
import uk.co.imshowcase.imshowcase.util.OperationState
import uk.co.eyrewiut.imshowcase.R
import uk.co.imshowcase.imshowcase.data.UserVM
import uk.co.eyrewiut.imshowcase.databinding.FragmentVoteManagementBinding
import uk.co.imshowcase.imshowcase.models.ProjectContentType

/**
 * A simple [Fragment] subclass.
 * Use the [VoteManagementFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class VoteManagementFragment : MainFragment() {
    private var _viewBinding: FragmentVoteManagementBinding? = null
    private val viewBinding get() = _viewBinding!!

    private val userVM: UserVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentVoteManagementBinding.inflate(layoutInflater)
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        if (savedInstanceState == null) {
            userVM.getUserNomination().observe(viewLifecycleOwner) {
                when (it.status) {
                    OperationState.LOADING -> {
                        viewBinding.loadingSpinner.show()
                        viewBinding.notVotedHelp.visibility = View.GONE
                        viewBinding.voteCurrentNominationCard.visibility = View.GONE
                    }
                    OperationState.SUCCESS -> {
                        viewBinding.loadingSpinner.hide()
                        if (userVM.vote.value?.nomination != null) {
                            val project = userVM.vote.value?.project?.content
                            updateUi(project!!)
                            setShowProjectCard(true)
                        } else {
                            setShowProjectCard(false)
                        }
                    }
                    OperationState.ERROR -> {
                        viewBinding.loadingSpinner.hide()
                        setShowProjectCard(false)
                    }
                }
            }
        }

        viewBinding.retractVoteButton.setOnClickListener {
            setShowProjectCard(false)
            userVM.retractVote().observe(viewLifecycleOwner) {
                if (it.status == OperationState.ERROR) {
                    setShowProjectCard(true)
                    Toast.makeText(context, "Error retracting vote!", Toast.LENGTH_SHORT).show()
                }
            }
        }
        viewBinding.voteBrowseProjectsBtn.setOnClickListener {
            navController.navigate(R.id.action_global_projectsListFragment)
        }
        viewBinding.voteScanQrBtn.setOnClickListener {
            navController.navigate(R.id.action_global_scanFragment)
        }
    }

    private fun setShowProjectCard(showCard: Boolean) {
        viewBinding.voteCurrentNominationCard.visibility = if (showCard) View.VISIBLE else View.GONE
        viewBinding.notVotedHelp.visibility = if (showCard) View.GONE else View.VISIBLE
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }

    private fun updateUi(project: ProjectContentType) {
        viewBinding.projectTitle.text = project.title
        viewBinding.projectAuthor.text = project.author
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

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment VoteManagementFragment.
         */
        @JvmStatic
        fun newInstance() = VoteManagementFragment()
    }
}