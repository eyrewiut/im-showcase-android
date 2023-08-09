package uk.co.imshowcase.imshowcase

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import uk.co.eyrewiut.imshowcase.R
import uk.co.imshowcase.imshowcase.data.ProjectVM
import uk.co.imshowcase.imshowcase.data.ProjectsListVM
import uk.co.imshowcase.imshowcase.data.UserVM
import uk.co.eyrewiut.imshowcase.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private val projectVM: ProjectVM by viewModels()
    private val listVM: ProjectsListVM by viewModels()
    private val userVM: UserVM by viewModels()
    private lateinit var viewBinding: ActivityMainBinding
    private lateinit var navHostFragment: NavHostFragment
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(viewBinding.root)

        navHostFragment = supportFragmentManager.findFragmentById(R.id.globalFragmentContainer) as NavHostFragment
        navController = navHostFragment.navController

        // If the user is logged out and are not in the login flow, take them there.
        if (!userVM.loggedIn) navController.navigate(R.id.action_global_login)

        userVM.token.observe(this) {
            if (it == null && navController.currentDestination!!.id != R.id.loginFragment && navController.currentDestination!!.id != R.id.registerFragment) {
                navController.navigate(R.id.action_global_login)
            }
        }
    }
}