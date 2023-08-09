package uk.co.imshowcase.imshowcase.ui

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import uk.co.imshowcase.imshowcase.util.OperationState
import uk.co.eyrewiut.imshowcase.R
import uk.co.imshowcase.imshowcase.data.UserVM
import uk.co.eyrewiut.imshowcase.databinding.FragmentLoginBinding

/**
 * A simple [Fragment] subclass.
 * Use the [LoginFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class LoginFragment : Fragment() {
    private lateinit var viewBinding: FragmentLoginBinding
    private val userVM: UserVM by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = FragmentLoginBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        viewBinding.loginButton.setOnClickListener {
            val email = viewBinding.loginInputEmail.editText?.text.toString()
            val password = viewBinding.loginInputPassword.editText?.text.toString()
            userVM.login(email, password).observe(viewLifecycleOwner) { result ->
                if (result.status == OperationState.SUCCESS) {
//                    userVM.setLoggedInState(true)
                    navController.navigate(R.id.action_global_startFragment)
                } else if (result.status == OperationState.ERROR) {
                    Toast.makeText(context, "Couldn't log in! check your details and try again.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        viewBinding.loginToRegisterBtn.setOnClickListener {
            navController.navigate(R.id.action_loginFragment_to_registerFragment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @return A new instance of fragment LoginFragment.
         */
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}