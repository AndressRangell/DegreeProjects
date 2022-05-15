package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.Utils.Companion.bitmap
import andres.rangel.degreeprojects.Utils.Companion.email
import andres.rangel.degreeprojects.Utils.Companion.name
import andres.rangel.degreeprojects.databinding.FragmentProfileBinding
import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class ProfileFragment: Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        binding.apply {
            ivProfile.setImageBitmap(bitmap)
            tvEmail.text = email
            etName.setText(name)

            btnLogout.setOnClickListener {
                signOut()
            }
        }
    }

    private fun signOut() {
        Firebase.auth.signOut()
        val intent = Intent(requireContext(), LoginActivity::class.java)
        startActivity(intent)
        requireActivity().finish()
    }

}