package uk.co.imshowcase.imshowcase.ui

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavOptions
import androidx.navigation.findNavController
import androidx.navigation.ui.NavigationUI
import com.google.android.material.appbar.MaterialToolbar
import com.google.android.material.bottomnavigation.BottomNavigationView
import uk.co.imshowcase.imshowcase.util.OperationState
import uk.co.eyrewiut.imshowcase.R
import uk.co.imshowcase.imshowcase.data.UserVM

//private const val NAVIGATE_TO = "NAVIGATE_TO"

/**
 * Sets up the navigation bar and app bar to avoid code repetition
 */
open class MainFragment : Fragment() {
    private val userVM: UserVM by activityViewModels()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = requireView().findNavController()
        val topAppBar = view.findViewById<MaterialToolbar>(R.id.topAppBar)
        val bottomNav = view.findViewById<BottomNavigationView>(R.id.bottom_app_bar)

        if (topAppBar != null) {
            NavigationUI.setupWithNavController(topAppBar, navController)
            topAppBar.setOnMenuItemClickListener { item ->
                when (item.itemId) {
                    R.id.top_nav_logout -> {
                        userVM.logout().observe(viewLifecycleOwner) { result ->
                            when (result.status) {
                                OperationState.SUCCESS -> {
                                    navController.navigate(R.id.action_global_login/*, null, options*/)
                                }
                                OperationState.ERROR -> {
                                    Toast
                                        .makeText(context, "Failed to log out.", Toast.LENGTH_SHORT)
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

        if (bottomNav != null) {
            NavigationUI.setupWithNavController(bottomNav, navController)

            bottomNav.setOnItemSelectedListener { item ->
                val options = NavOptions.Builder()
                    .setPopUpTo(navController.graph.startDestinationId, false, true)
                    .setLaunchSingleTop(true)
                    .build()

                navController.navigate(item.itemId, null, options)

                navController.currentDestination?.hierarchy?.any {
                    it.id == item.itemId
                } ?: false
            }
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment MainFragment.
         */
        /*@JvmStatic
        fun newInstance(@IdRes navDest: Int) = MainFragment().apply {
            arguments = Bundle().apply {
                putInt(NAVIGATE_TO, navDest)
            }
        }*/
    }
}