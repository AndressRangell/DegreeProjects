package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.FragmentLoginBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class LoginFragment: Fragment(R.layout.fragment_login) {

    private lateinit var binding: FragmentLoginBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentLoginBinding.bind(view)

        binding.btnLogin.setOnClickListener {
            findNavController().navigate(
                R.id.action_loginFragment_to_homeFragment, savedInstanceState, null
            )
        }
    }

}