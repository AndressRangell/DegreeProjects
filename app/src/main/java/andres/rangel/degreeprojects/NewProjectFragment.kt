package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.Utils.Companion.email
import andres.rangel.degreeprojects.Utils.Companion.name
import andres.rangel.degreeprojects.databinding.FragmentNewProjectBinding
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class NewProjectFragment : Fragment(R.layout.fragment_new_project) {

    private lateinit var binding: FragmentNewProjectBinding
    private lateinit var firebase: FirebaseDatabase
    private lateinit var firestore: FirebaseFirestore

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentNewProjectBinding.bind(view)

        firebase = FirebaseDatabase.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.btnCreateProject.setOnClickListener {
            val project = Project(
                binding.etNameProject.text.toString(),
                binding.etTools.text.toString(),
                binding.etDescriptionProject.text.toString(),
                binding.etEmailOne.text.toString(),
                binding.etEmailTwo.text.toString(),
                email.toString(),
                StatusProject.UNAPPROVED
            )
            val result = validateFields(project)
            if (result == "OK") {
                createProject(project)
            } else {
                Snackbar.make(binding.root, result, Snackbar.LENGTH_SHORT).show()
            }
        }
    }

    private fun createProject(project: Project) {
        val reference = firebase.reference
        val id = reference.push().key
        id?.let {
            reference.child("projects").child(it).setValue(project).addOnSuccessListener {
                findNavController().navigateUp()
            }.addOnFailureListener {
                Snackbar.make(binding.root, "Error al crear el proyecto", Snackbar.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun validateFields(project: Project): String {
        var emailOneValid = true
        var emailTwoValid = true
        if (project.emailOne.isNotEmpty())
            firestore.collection("users").document(project.emailOne).get().addOnCompleteListener {
                if(!it.result.exists()) emailOneValid = false
            }
        if (project.emailTwo.isNotEmpty())
            firestore.collection("users").document(project.emailTwo).get().addOnCompleteListener {
                if(!it.result.exists()) emailTwoValid = false
            }

        return when {
            project.name.isEmpty() || project.tools.isEmpty() || project.description.isEmpty() ->
                "Debes llenar todos los campos"
            project.name.length < 30 ->
                "El nombre del proyecto es demasiado corto"
            project.tools.split(",").size < 3 ->
                "Escribe por lo menos 3 herramientas separadas con coma: a, b, c"
            project.description.length < 80 ->
                "Ingresa una descripción mas detallada sobre el proyecto"
            !emailOneValid || !emailTwoValid ->
                "Los correos de los participantes deben estar registrados"
            else -> "OK"
        }
    }

}