package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.FragmentProjectListBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController

class ProjectListFragment: Fragment(R.layout.fragment_project_list) {

    private lateinit var binding: FragmentProjectListBinding

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProjectListBinding.bind(view)

        binding.fabNewProject.setOnClickListener {
            findNavController().navigate(R.id.action_projectListFragment_to_newProjectFragment)
        }
    }

}