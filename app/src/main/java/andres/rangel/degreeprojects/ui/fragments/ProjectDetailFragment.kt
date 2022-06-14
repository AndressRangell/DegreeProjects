package andres.rangel.degreeprojects.ui.fragments

import andres.rangel.degreeprojects.models.Project
import andres.rangel.degreeprojects.R
import andres.rangel.degreeprojects.models.StatusProject
import andres.rangel.degreeprojects.utils.Utils.Companion.email
import andres.rangel.degreeprojects.utils.Utils.Companion.projectAssigned
import andres.rangel.degreeprojects.databinding.FragmentProjectDetailBinding
import android.annotation.SuppressLint
import android.app.AlertDialog
import android.os.Bundle
import android.view.View
import android.widget.ArrayAdapter
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.navArgs
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore

class ProjectDetailFragment : Fragment(R.layout.fragment_project_detail) {

    private lateinit var binding: FragmentProjectDetailBinding
    private lateinit var firebase: FirebaseDatabase
    private lateinit var firestore: FirebaseFirestore
    private val arguments: ProjectDetailFragmentArgs by navArgs()

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProjectDetailBinding.bind(view)

        firebase = FirebaseDatabase.getInstance()
        firestore = FirebaseFirestore.getInstance()
        val project = arguments.project

        val arrayAdapter = ArrayAdapter.createFromResource(
            requireContext(),
            R.array.status_project,
            androidx.appcompat.R.layout.support_simple_spinner_dropdown_item
        )

        binding.apply {
            spinner.adapter = arrayAdapter

            email?.let { email ->
                if (email.lowercase().contains("@correo.uts.edu.co")) {
                    changeView()
                }
            }

            tvNameProject.setText(project.name)
            tvTools.setText(project.tools)
            tvDescriptionProject.setText(project.description)
            tvCreateBy.text = "Contacto: ${project.createdBy}"
            tvStatus.setText(
                when (project.status) {
                    StatusProject.UNAPPROVED -> "Estado: sin aprobar"
                    StatusProject.FREE -> "Estado: libre"
                    StatusProject.ASSIGNED -> "Estado: asignado"
                    StatusProject.DEVELOPING -> "Estado: en desarrollo"
                    StatusProject.FINISHED -> "Estado: finalizado"
                }
            )
            spinner.setSelection(
                when (project.status) {
                    StatusProject.UNAPPROVED -> 0
                    StatusProject.FREE -> 1
                    StatusProject.ASSIGNED -> 2
                    StatusProject.DEVELOPING -> 3
                    StatusProject.FINISHED -> 4
                }
            )

            if (project.emailOne.isEmpty() && project.emailTwo.isEmpty()) {
                email?.let {
                    if (it.lowercase().contains("@uts.edu.co")) {
                        tvEmailOne.visibility = View.GONE
                        tvEmailTwo.visibility = View.GONE
                        etEmailTwo.visibility = View.VISIBLE
                        btnTakeProject.visibility = View.VISIBLE
                    }
                }
            } else {
                tvEmailOne.setText(project.emailOne)
                tvEmailTwo.setText(project.emailTwo)
            }
            if (project.emailTwo.isNotEmpty())
                firestore.collection("users").document(project.emailTwo).get()
                    .addOnCompleteListener {
                        if (!it.result.exists()) project.emailTwo = "Error"
                    }
            btnTakeProject.setOnClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setMessage("¿Seguro que quieres desarrollar este proyecto?")
                    .setPositiveButton("Si") { _, _ ->
                        if (projectAssigned) {
                            Snackbar.make(
                                root,
                                "Error!! Ya tienes un proyecto asignado",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else if (project.emailTwo == "Error") {
                            Snackbar.make(
                                root,
                                "Los correos de los participantes deben estar registrados",
                                Snackbar.LENGTH_SHORT
                            ).show()
                        } else {
                            project.emailOne = email.toString()
                            project.emailTwo = binding.etEmailTwo.text.toString()
                            updateProject(project)
                        }
                    }.setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                builder.show()
            }
            btnUpdate.setOnClickListener {
                val item = binding.spinner.selectedItemPosition
                val free = binding.tvEmailOne.text.isEmpty() && binding.tvEmailTwo.text.isEmpty()
                if (item == 1 && !free) {
                    Snackbar.make(
                        root,
                        "El proyecto no puede ser LIBRE si tiene responsables",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    spinner.setSelection(
                        when (project.status) {
                            StatusProject.UNAPPROVED -> 0
                            StatusProject.FREE -> 1
                            StatusProject.ASSIGNED -> 2
                            StatusProject.DEVELOPING -> 3
                            StatusProject.FINISHED -> 4
                        }
                    )
                } else if (item in 2..4 && free) {
                    Snackbar.make(
                        root,
                        "El proyecto no puede avanzar si no tiene responsables",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    spinner.setSelection(
                        when (project.status) {
                            StatusProject.UNAPPROVED -> 0
                            StatusProject.FREE -> 1
                            StatusProject.ASSIGNED -> 2
                            StatusProject.DEVELOPING -> 3
                            StatusProject.FINISHED -> 4
                        }
                    )
                } else {
                    val updateProject = Project(
                        binding.tvNameProject.text.toString(),
                        binding.tvTools.text.toString(),
                        binding.tvDescriptionProject.text.toString(),
                        binding.tvEmailOne.text.toString(),
                        binding.tvEmailTwo.text.toString(),
                        project.createdBy,
                        when (item) {
                            0 -> StatusProject.UNAPPROVED
                            1 -> StatusProject.FREE
                            2 -> StatusProject.ASSIGNED
                            3 -> StatusProject.DEVELOPING
                            else -> StatusProject.FINISHED
                        }
                    )
                    updateProject(updateProject)
                }
            }
            btnDelete.setOnClickListener {
                val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
                builder.setMessage("¿Está seguro que quiere eliminar el proyecto?")
                    .setPositiveButton("Aceptar") { _, _ ->
                        deleteProject(project)
                    }.setNegativeButton("Cancelar") { dialog, _ ->
                        dialog.dismiss()
                    }
                builder.show()
            }
        }
    }

    private fun deleteProject(project: Project) {
        val reference = firebase.reference.child("projects").child(project.name)
        reference.removeValue().addOnCompleteListener {
            if (it.isSuccessful) {
                Snackbar.make(
                    binding.root,
                    "El proyecto se eliminó correctamente",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Snackbar.make(
                    binding.root,
                    "Error al eliminar el proyecto",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
        }.addOnFailureListener {
            Snackbar.make(
                binding.root,
                "Algo salió mal, inténtalo de nuevo",
                Snackbar.LENGTH_SHORT
            ).show()
        }
    }

    private fun changeView() {
        binding.apply {
            tvNameProject.isFocusableInTouchMode = true
            tvTools.isFocusableInTouchMode = true
            tvDescriptionProject.isFocusableInTouchMode = true
            tvEmailOne.isFocusableInTouchMode = true
            tvEmailTwo.isFocusableInTouchMode = true
            tvStatus.visibility = View.GONE
            linearSpinner.visibility = View.VISIBLE
            btnUpdate.visibility = View.VISIBLE
        }
    }

    private fun updateProject(project: Project) {
        val projectUpdate: Map<String, Any> = hashMapOf(
            "name" to project.name,
            "tools" to project.tools,
            "description" to project.description,
            "emailOne" to project.emailOne,
            "emailTwo" to project.emailTwo,
            "createdBy" to project.createdBy,
            "status" to project.status
        )
        firebase.getReference("projects").child(project.name).updateChildren(projectUpdate)
            .addOnCompleteListener {
                if (it.isSuccessful) {
                    binding.apply {
                        tvEmailOne.visibility = View.VISIBLE
                        tvEmailTwo.visibility = View.VISIBLE
                        etEmailTwo.visibility = View.GONE
                        btnTakeProject.visibility = View.GONE
                        tvEmailOne.setText(project.emailOne)
                        tvEmailTwo.setText(project.emailTwo)
                        Snackbar.make(
                            root,
                            "El proyecto se ha actualizado con éxito",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                } else {
                    Snackbar.make(
                        binding.root,
                        "Error al actualizar el proyecto",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }.addOnFailureListener {
                Snackbar.make(
                    binding.root,
                    "Error al conectar con la base de datos",
                    Snackbar.LENGTH_SHORT
                ).show()
            }
    }
}