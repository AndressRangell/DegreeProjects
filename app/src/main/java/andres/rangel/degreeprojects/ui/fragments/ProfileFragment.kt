package andres.rangel.degreeprojects.ui.fragments

import andres.rangel.degreeprojects.R
import andres.rangel.degreeprojects.utils.Utils.Companion.career
import andres.rangel.degreeprojects.utils.Utils.Companion.document
import andres.rangel.degreeprojects.utils.Utils.Companion.email
import andres.rangel.degreeprojects.utils.Utils.Companion.imageUri
import andres.rangel.degreeprojects.utils.Utils.Companion.name
import andres.rangel.degreeprojects.utils.Utils.Companion.phone
import andres.rangel.degreeprojects.databinding.FragmentProfileBinding
import andres.rangel.degreeprojects.ui.activities.LoginActivity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.fragment.app.Fragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private val selectedPicture = 200

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        storage = FirebaseStorage.getInstance()
        firestore = FirebaseFirestore.getInstance()

        binding.apply {

            imageUri?.let {
                circleImageView.setImageURI(it)
            }
            tvEmail.text = email
            chargeData()

            switchEditable.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    editionEnabled()
                } else {
                    chargeData()
                    editionDisabled()
                }
            }

            circleChangeImage.setOnClickListener {
                val intent =
                    Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
                startActivityForResult(intent, selectedPicture)
            }

            btnLogout.setOnClickListener {
                if (switchEditable.isChecked) {
                    saveData()
                } else {
                    signOut()
                }
            }
        }
    }

    private fun saveData() {
        email?.let {
            firestore.collection("users").document(it).set(
                hashMapOf(
                    "name" to binding.etName.text.toString(),
                    "document" to binding.etDocument.text.toString(),
                    "phone" to binding.etPhone.text.toString(),
                    "career" to binding.etCareer.text.toString()
                )
            ).addOnSuccessListener {
                name = binding.etName.text.toString()
                document = binding.etDocument.text.toString()
                phone = binding.etPhone.text.toString()
                career = binding.etCareer.text.toString()
                binding.switchEditable.isChecked = false
            }
        }
    }

    private fun signOut() {
        val builder: AlertDialog.Builder = AlertDialog.Builder(requireContext())
        builder.setMessage("¿Desea cerrar sesión?").setPositiveButton("Si") { _, _ ->
            Firebase.auth.signOut()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
            requireActivity().finish()
        }.setNegativeButton("Cancelar") { dialog, _ ->
            dialog.dismiss()
        }
        builder.show()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == selectedPicture) {
            data?.data?.let { uri ->
                val reference = storage.getReference(email?.replace("@", "") ?: "")

                val uploadTask = reference.putFile(uri)
                uploadTask.addOnSuccessListener {
                    binding.circleImageView.setImageURI(uri)
                }.addOnFailureListener {
                    Snackbar.make(
                        binding.root,
                        "Formatos válidos: JPG/JPEG",
                        Snackbar.LENGTH_SHORT
                    ).show()
                    imageUri?.let {
                        binding.circleImageView.setImageURI(it)
                    }
                }

                imageUri = uri
            }
        }
    }

    private fun chargeData() {
        binding.apply {
            etName.setText(name)
            etDocument.setText(document)
            etPhone.setText(phone)
            etCareer.setText(career)
        }
    }

    private fun editionEnabled() {
        binding.apply {
            etName.isFocusableInTouchMode = true
            etDocument.isFocusableInTouchMode = true
            etPhone.isFocusableInTouchMode = true
            etCareer.isFocusableInTouchMode = true
            circleChangeImage.visibility = View.VISIBLE
            btnLogout.text = getString(R.string.save_data)
        }
    }

    private fun editionDisabled() {
        binding.apply {
            etName.isFocusableInTouchMode = false
            etName.clearFocus()
            etDocument.isFocusableInTouchMode = false
            etDocument.clearFocus()
            etPhone.isFocusableInTouchMode = false
            etPhone.clearFocus()
            etCareer.isFocusableInTouchMode = false
            etCareer.clearFocus()
            circleChangeImage.visibility = View.GONE
            btnLogout.text = getString(R.string.logout)
        }
    }

}