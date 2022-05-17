package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.Utils.Companion.bitmap
import andres.rangel.degreeprojects.Utils.Companion.career
import andres.rangel.degreeprojects.Utils.Companion.document
import andres.rangel.degreeprojects.Utils.Companion.email
import andres.rangel.degreeprojects.Utils.Companion.newBitmap
import andres.rangel.degreeprojects.Utils.Companion.name
import andres.rangel.degreeprojects.Utils.Companion.phone
import andres.rangel.degreeprojects.databinding.FragmentProfileBinding
import android.app.AlertDialog
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.fragment.app.Fragment
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import java.io.BufferedInputStream
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException


class ProfileFragment : Fragment(R.layout.fragment_profile) {

    private lateinit var binding: FragmentProfileBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private val selectedPicture = 200

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding = FragmentProfileBinding.bind(view)

        binding.apply {

            bitmap?.let {
                circleImageView.setImageBitmap(bitmap)
            }
            tvEmail.text = email
            etName.setText(name)

            switchEditable.setOnCheckedChangeListener { _, isChecked ->
                if (isChecked) {
                    etName.isFocusableInTouchMode = true
                    etDocument.isFocusableInTouchMode = true
                    etPhone.isFocusableInTouchMode = true
                    etCareer.isFocusableInTouchMode = true
                    circleChangeImage.visibility = View.VISIBLE
                    btnLogout.text = getString(R.string.save_data)
                } else {
                    if (bitmap != null) {
                        circleImageView.setImageBitmap(bitmap)
                    } else {
                        circleImageView.setImageResource(R.drawable.ic_default_profile)
                    }
                    tvName.text = name ?: ""
                    tvDocument.text = document.toString()
                    tvPhone.text = phone.toString()
                    tvCareer.text = career ?: ""

                    etName.isFocusableInTouchMode = false
                    etDocument.isFocusableInTouchMode = false
                    etPhone.isFocusableInTouchMode = false
                    etCareer.isFocusableInTouchMode = false
                    circleChangeImage.visibility = View.GONE
                    btnLogout.text = getString(R.string.logout)
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
        storage = FirebaseStorage.getInstance()
        val reference = storage.getReference(email?.replace("@", "") ?: "")
        val baos = ByteArrayOutputStream()
        newBitmap?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        bitmap = newBitmap
        val data = baos.toByteArray()

        val uploadTask = reference.putBytes(data)
        uploadTask.addOnFailureListener {
            // Handle unsuccessful uploads
        }.addOnSuccessListener { taskSnapshot ->
            // taskSnapshot.metadata contains file metadata such as size, content-type, etc.
            // ...
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
            val selectedImage: Uri? = data?.data
            try {
                val inputStream = selectedImage?.let {
                    requireContext().contentResolver.openInputStream(it)
                }
                val bis = BufferedInputStream(inputStream)
                newBitmap = BitmapFactory.decodeStream(bis)
                binding.circleImageView.setImageBitmap(newBitmap)
            } catch (e: FileNotFoundException) {
                e.printStackTrace()
            }
        }
    }

}