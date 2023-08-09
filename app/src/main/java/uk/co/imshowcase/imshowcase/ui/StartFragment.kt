package uk.co.imshowcase.imshowcase.ui

import android.os.Bundle
import android.view.*
import android.widget.PopupMenu
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import uk.co.imshowcase.imshowcase.util.OperationState
import uk.co.eyrewiut.imshowcase.R
import uk.co.imshowcase.imshowcase.data.UserVM
import uk.co.eyrewiut.imshowcase.databinding.FragmentStartBinding

class StartFragment : Fragment() {
    private var _viewBinding: FragmentStartBinding? = null
    private val viewBinding get() = _viewBinding!!
    private val userVM: UserVM by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _viewBinding = FragmentStartBinding.inflate(layoutInflater)
        // Inflate the layout for this fragment
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()
        viewBinding.accountMenuButton.setOnClickListener { button ->
            val popup = PopupMenu(requireContext(), button)
            popup.menuInflater.inflate(R.menu.settings, popup.menu)

            popup.setOnMenuItemClickListener(onMenuItemClickHandler(navController))

            popup.show()
        }
        viewBinding.scanQrBtn.setOnClickListener {
            navController.navigate(R.id.scanFragment)
        }
        viewBinding.browseProjectsBtn.setOnClickListener {
            navController.navigate(R.id.projectsListFragment)
        }
    }

    // Wrapper function to pass NavController to click event handler for items in the dropdown menu.
    private fun onMenuItemClickHandler(navController: NavController): (MenuItem) -> Boolean {
        return { menuItem: MenuItem ->
            when (menuItem.itemId) {
                R.id.settings_menu_vote -> {
                    navController.navigate(R.id.voteManagementFragment)
                    true
                }
                R.id.settings_menu_logout -> {
                    userVM.logout().observe(viewLifecycleOwner) { result ->
                        when (result.status) {
                            OperationState.SUCCESS -> {
                                navController.navigate(R.id.action_global_login)
                            }
                            OperationState.ERROR -> {
                                Toast.makeText(context, "Failed to log out.", Toast.LENGTH_SHORT)
                                    .show()
                            }
                            else -> {}
                        }
                    }
                    true
                }
                else -> false
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _viewBinding = null
    }
}