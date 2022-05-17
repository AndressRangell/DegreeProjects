package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.Utils.Companion.bitmap
import andres.rangel.degreeprojects.Utils.Companion.career
import andres.rangel.degreeprojects.Utils.Companion.document
import andres.rangel.degreeprojects.Utils.Companion.email
import andres.rangel.degreeprojects.Utils.Companion.name
import andres.rangel.degreeprojects.Utils.Companion.phone
import andres.rangel.degreeprojects.databinding.ActivityMainBinding
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.File


class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var storage: FirebaseStorage
    private lateinit var firestore: FirebaseFirestore
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        getInfo()
        getProfileImage()

        setSupportActionBar(binding.bottomAppBar)

        binding.bottomNavigationView.background = null
        binding.bottomNavigationView.menu.getItem(2).isEnabled = false

        binding.bottomNavigationView.setupWithNavController(
            binding.navigationHostFragment.getFragment<NavHostFragment>().navController
        )

        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /* NO-OP */ }

        binding.navigationHostFragment.getFragment<NavHostFragment>().navController
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.homeFragment, R.id.projectListFragment, R.id.settingsFragment, R.id.profileFragment -> {
                        binding.bottomAppBar.visibility = View.VISIBLE
                        binding.fabNewProject.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.bottomAppBar.visibility = View.GONE
                        binding.fabNewProject.visibility = View.GONE
                    }
                }
            }

        binding.fabNewProject.setOnClickListener {
            binding.navigationHostFragment.getFragment<NavHostFragment>().navController
                .navigate(R.id.newProjectFragment)
        }
    }

    private fun getInfo() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        email = auth.currentUser?.email
        val result = firestore.collection("users").document(email.toString())
        result.get().addOnSuccessListener {
            name = it.get("name").toString()
            document = it.get("document") as Long?
            phone = it.get("phone") as Long?
            career = it.get("career").toString()
        }
    }

    private fun getProfileImage() {
        storage = FirebaseStorage.getInstance()
        val nameImage = "${email?.replace("@", "")}.png"
        val reference = storage.getReference(nameImage)

        try {
            val file = File.createTempFile("profile", ".jpg")
            reference.getFile(file).addOnSuccessListener {
                val bitmapReference = BitmapFactory.decodeFile(file.absolutePath)
                val exif = ExifInterface(file)
                val orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, ExifInterface.ORIENTATION_UNDEFINED)
                val matrix = Matrix()

                when(orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
                }

                bitmap = Bitmap.createBitmap(
                    bitmapReference,
                    0,
                    0,
                    bitmapReference.width,
                    bitmapReference.height,
                    matrix,
                    true
                )
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        return
    }
}