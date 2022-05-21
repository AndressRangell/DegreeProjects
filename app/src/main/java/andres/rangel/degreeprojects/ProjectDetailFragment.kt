package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.Utils.Companion.email
import andres.rangel.degreeprojects.databinding.FragmentProjectDetailBinding
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase

class ProjectDetailFragment : Fragment(R.layout.fragment_project_detail) {

    private lateinit var binding: FragmentProjectDetailBinding
    private lateinit var firebase: FirebaseDatabase
    private val arguments: ProjectDetailFragmentArgs by navArgs()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProjectDetailBinding.bind(view)

        firebase = FirebaseDatabase.getInstance()
        val project = arguments.project

        binding.apply {
            tvNameProject.text = project.name
            tvTools.text = project.tools
            tvDescriptionProject.text = project.description
            tvStatus.text = when (project.status) {
                StatusProject.UNAPPROVED -> "Estado: sin aprobar"
                StatusProject.FREE -> "Estado: libre"
                StatusProject.ASSIGNED -> "Estado: asignado"
                StatusProject.DEVELOPING -> "Estado: en desarrollo"
                StatusProject.FINISHED -> "Estado: Finalizado"
            }
            if(project.emailOne.isEmpty() && project.emailTwo.isEmpty()) {
                tvEmailOne.visibility = View.GONE
                tvEmailTwo.visibility = View.GONE
                etEmailTwo.visibility = View.VISIBLE
                btnTakeProject.visibility = View.VISIBLE
            }else{
                tvEmailOne.text = project.emailOne
                tvEmailTwo.text = project.emailTwo
            }
            btnTakeProject.setOnClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setMessage("¿Seguro que quieres desarrollar este proyecto?").setPositiveButton("Si") { _, _ ->
                    updateProject(project)
                }.setNegativeButton("Cancelar") { dialog, _ ->
                    dialog.dismiss()
                }
                builder.show()
            }
        }
    }

    private fun updateProject(project: Project) {
        val projectUpdate: Map<String, Any> = hashMapOf(
            "name" to project.name,
            "tools" to project.tools,
            "description" to project.description,
            "emailOne" to email!!,
            "emailTwo" to binding.etEmailTwo.text.toString(),
            "createBy" to project.createdBy,
            "status" to project.status
        )
        firebase.getReference("projects").child(project.name).updateChildren(projectUpdate).addOnCompleteListener {
            if(it.isSuccessful) {
                binding.apply {
                    tvEmailOne.visibility = View.VISIBLE
                    tvEmailTwo.visibility = View.VISIBLE
                    etEmailTwo.visibility = View.GONE
                    btnTakeProject.visibility = View.GONE
                    tvEmailOne.text = email!!
                    tvEmailTwo.text = binding.etEmailTwo.text.toString()
                    Snackbar.make(root, "El proyecto se ha actualizado con éxito", Snackbar.LENGTH_SHORT).show()
                }
            }else {
                Snackbar.make(binding.root, "Error al actualizar el proyecto", Snackbar.LENGTH_SHORT).show()
            }
        }.addOnFailureListener {
            Snackbar.make(binding.root, "Error al conectar con la base de datos", Snackbar.LENGTH_SHORT).show()
        }
    }
}