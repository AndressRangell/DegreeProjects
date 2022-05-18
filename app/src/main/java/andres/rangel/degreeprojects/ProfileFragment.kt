package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.Utils.Companion.bitmap
import andres.rangel.degreeprojects.Utils.Companion.career
import andres.rangel.degreeprojects.Utils.Companion.document
import andres.rangel.degreeprojects.Utils.Companion.email
import andres.rangel.degreeprojects.Utils.Companion.imageUri
import andres.rangel.degreeprojects.Utils.Companion.name
import andres.rangel.degreeprojects.Utils.Companion.phone
import andres.rangel.degreeprojects.databinding.FragmentProfileBinding
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.view.View
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import kotlinx.coroutines.delay
import kotlinx.coroutines.runBlocking
import java.io.BufferedInputStream
import java.io.FileNotFoundException


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

            bitmap?.let {
                circleImageView.setImageBitmap(bitmap)
            }
            tvEmail.text = email
            etName.setText(name)
            etDocument.setText(document)
            etPhone.setText(phone)
            etCareer.setText(career)

            switchEditable.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    editionEnabled()
                } else {
                    cleanFields()
                    editionDisabled()
                    Handler().postDelayed({
                        circleImageView.setImageBitmap(bitmap)
                    }, 1000)
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
                    switchEditable.isChecked = false
                } else {
                    signOut()
                }
            }
        }
    }

    private fun saveData() {
        imageUri?.let { uri ->
            val reference = storage.getReference(email?.replace("@", "") ?: "")

            val uploadTask = reference.putFile(uri)
            uploadTask.addOnSuccessListener {
                bitmap = uriToBitmap(uri)
            }.addOnFailureListener {
                Toast.makeText(requireContext(), "Formatos válidos: JPG/JPEG", Toast.LENGTH_LONG)
                    .show()
            }
        }

        email?.let {
            firestore.collection("users").document(it).set(
                hashMapOf(
                    "name" to binding.etName.text.toString(),
                    "document" to binding.etDocument.text.toString(),
                    "phone" to binding.etPhone.text.toString(),
                    "career" to binding.etCareer.text.toString()
                )
            )
        }
    }

    private fun uriToBitmap(imageUri: Uri): Bitmap? {
        return try {
            val inputStream = requireContext().contentResolver.openInputStream(imageUri)
            val bis = BufferedInputStream(inputStream)
            BitmapFactory.decodeStream(bis)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
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
            imageUri = data?.data!!
            imageUri?.let {
                binding.circleImageView.setImageURI(it)
            }
        }
    }

    private fun cleanFields() {
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