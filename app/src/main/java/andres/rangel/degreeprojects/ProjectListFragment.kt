package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.FragmentProjectListBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.*
import timber.log.Timber

class ProjectListFragment: Fragment(R.layout.fragment_project_list) {

    private lateinit var binding: FragmentProjectListBinding
    private lateinit var reference: DatabaseReference
    private lateinit var projectsAdapter: ProjectsAdapter

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProjectListBinding.bind(view)

        initRecyclerView()

        reference = FirebaseDatabase.getInstance().getReference("projects")

        reference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                val projectList: ArrayList<Project?> = arrayListOf()
                for (data in dataSnapshot.children) {
                    val project = data.getValue(Project::class.java) as Project
                    projectList.add(project)
                }
                projectsAdapter.differ.submitList(projectList)
            }

            override fun onCancelled(error: DatabaseError) {
                Timber.e(error.message)
            }
        })

        projectsAdapter.setOnItemClickListener { project ->
            val bundle = Bundle().apply {
                putSerializable("project", project)
            }
            findNavController().navigate(
                R.id.action_projectListFragment_to_projectDetailFragment,
                bundle
            )
        }

        binding.fabNewProject.setOnClickListener {
            findNavController().navigate(R.id.action_projectListFragment_to_newProjectFragment)
        }
    }

    private fun initRecyclerView() {
        projectsAdapter = ProjectsAdapter()
        binding.rvProjects.apply {
            adapter = projectsAdapter
            layoutManager = LinearLayoutManager(activity)
        }
    }

}