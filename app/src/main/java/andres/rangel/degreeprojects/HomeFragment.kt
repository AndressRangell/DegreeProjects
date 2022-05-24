package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.Utils.Companion.email
import andres.rangel.degreeprojects.Utils.Companion.imageUri
import andres.rangel.degreeprojects.Utils.Companion.name
import andres.rangel.degreeprojects.databinding.FragmentHomeBinding
import android.annotation.SuppressLint
import android.os.Bundle
import android.view.View
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class HomeFragment : Fragment(R.layout.fragment_home) {

    private lateinit var binding: FragmentHomeBinding
    private lateinit var reference: DatabaseReference
    private var project: Project? = null

    @SuppressLint("SetTextI18n")
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentHomeBinding.bind(view)

        getInfo()

        Glide.with(this)
            .load("https://www.uts.edu.co/sitio/wp-content/uploads/2020/01/UTS_8714.jpg")
            .into(binding.imgHeader)

        binding.apply {
            binding.circleImageView.setImageURI(imageUri)
            name?.let { tvName.text = "Hola ${it.split(" ").first()}" }
            binding.linearItem.setOnClickListener {
                val bundle = Bundle().apply {
                    putSerializable("project", project)
                }
                findNavController().navigate(
                    R.id.action_homeFragment_to_projectDetailFragment,
                    bundle
                )
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun getInfo() {
        reference = FirebaseDatabase.getInstance().getReference("projects")
        reference.get().addOnCompleteListener {
            if (it.isSuccessful){
                for(data in it.result.children) {
                    val item = data.getValue(Project::class.java) as Project
                    if(item.emailOne == email || item.emailTwo == email) {
                        project = item
                        binding.apply {
                            tvNameProject.text = item.name
                            tvDescription.text = item.description
                            tvStatus.text = when (item.status) {
                                StatusProject.UNAPPROVED -> "Estado: sin aprobar"
                                StatusProject.FREE -> "Estado: libre"
                                StatusProject.ASSIGNED -> "Estado: asignado"
                                StatusProject.DEVELOPING -> "Estado: en desarrollo"
                                StatusProject.FINISHED -> "Estado: Finalizado"
                            }
                        }
                    }
                }
            }
        }
    }

}