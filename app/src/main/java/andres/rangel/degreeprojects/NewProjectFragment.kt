package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.FragmentNewProjectBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class NewProjectFragment: Fragment(R.layout.fragment_new_project) {

    private lateinit var binding: FragmentNewProjectBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewProjectBinding.bind(view)

        binding.btnBack.setOnClickListener {
            findNavController().navigateUp()
        }
    }

}