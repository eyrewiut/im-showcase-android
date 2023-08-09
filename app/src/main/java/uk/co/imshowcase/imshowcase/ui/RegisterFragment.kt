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
import uk.co.eyrewiut.imshowcase.databinding.FragmentRegisterBinding

/**
 * A simple [Fragment] subclass.
 * Use the [RegisterFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class RegisterFragment : Fragment() {
    private lateinit var viewBinding: FragmentRegisterBinding
    private val userVM: UserVM by activityViewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = FragmentRegisterBinding.inflate(layoutInflater)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        return viewBinding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val navController = findNavController()

        viewBinding.registerButton.setOnClickListener {
            val email = viewBinding.registerInputEmail.editText?.text.toString()
            val password = viewBinding.registerInputPassword.editText?.text.toString()

            userVM.register(email, password).observe(viewLifecycleOwner) { result ->
                if (result.status == OperationState.SUCCESS) {
//                    userVM.setLoggedInState(true)
                    navController.navigate(R.id.action_global_startFragment)
                } else if (result.status == OperationState.ERROR) {
                    Toast.makeText(context, "Couldn't log in! Do you already have an account with us?", Toast.LENGTH_SHORT).show()
                }
            }

        }

        viewBinding.registerToLoginBtn.setOnClickListener {
            navController.navigate(R.id.action_registerFragment_to_loginFragment)
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.

         * @return A new instance of fragment LoginFragment.
         */
        @JvmStatic
        fun newInstance() = LoginFragment()
    }
}