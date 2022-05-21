package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.Utils.Companion.career
import andres.rangel.degreeprojects.Utils.Companion.document
import andres.rangel.degreeprojects.Utils.Companion.email
import andres.rangel.degreeprojects.Utils.Companion.imageUri
import andres.rangel.degreeprojects.Utils.Companion.name
import andres.rangel.degreeprojects.Utils.Companion.phone
import andres.rangel.degreeprojects.databinding.ActivityMainBinding
import android.Manifest
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.exifinterface.media.ExifInterface
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import pub.devrel.easypermissions.EasyPermissions
import java.io.ByteArrayOutputStream
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

        binding.bottomNavigationView.setupWithNavController(
            binding.navigationHostFragment.getFragment<NavHostFragment>().navController
        )

        binding.bottomNavigationView.setOnNavigationItemReselectedListener { /* NO-OP */ }

        binding.navigationHostFragment.getFragment<NavHostFragment>().navController
            .addOnDestinationChangedListener { _, destination, _ ->
                when (destination.id) {
                    R.id.homeFragment, R.id.projectListFragment, R.id.settingsFragment, R.id.profileFragment -> {
                        binding.bottomAppBar.visibility = View.VISIBLE
                    }
                    else -> {
                        binding.bottomAppBar.visibility = View.GONE
                    }
                }
            }

    }

    private fun getInfo() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        email = auth.currentUser?.email
        val result = firestore.collection("users").document(email.toString())
        result.get().addOnSuccessListener {
            name = it.get("name")?.toString()
            document = it.get("document")?.toString()
            phone = it.get("phone")?.toString()
            career = it.get("career")?.toString()
        }
    }

    private fun getProfileImage() {
        storage = FirebaseStorage.getInstance()
        val nameImage = "${email?.replace("@", "")}"
        val reference = storage.getReference(nameImage)

        try {
            val file = File.createTempFile("profile", ".jpg")
            reference.getFile(file).addOnSuccessListener {

                val bitmapReference = BitmapFactory.decodeFile(file.absolutePath)
                val exif = ExifInterface(file)
                val orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION,
                    ExifInterface.ORIENTATION_UNDEFINED
                )
                val matrix = Matrix()

                when (orientation) {
                    ExifInterface.ORIENTATION_ROTATE_90 -> matrix.postRotate(90F)
                    ExifInterface.ORIENTATION_ROTATE_180 -> matrix.postRotate(180F)
                    ExifInterface.ORIENTATION_ROTATE_270 -> matrix.postRotate(270F)
                }

                val bitmap = Bitmap.createBitmap(
                    bitmapReference,
                    0,
                    0,
                    bitmapReference.width,
                    bitmapReference.height,
                    matrix,
                    true
                )

                val bytes = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                val path: String = MediaStore.Images.Media.insertImage(
                    contentResolver,
                    bitmap,
                    "IMG_" + "profile",
                    null
                )

                imageUri = Uri.parse(path)

            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun onBackPressed() {
        val currentDestination = binding.navigationHostFragment.getFragment<NavHostFragment>()
            .navController.currentDestination?.label

        println(currentDestination)
        when(currentDestination) {
            "ProjectListFragment" -> binding.navigationHostFragment.getFragment<NavHostFragment>()
                .navController.navigate(R.id.action_projectListFragment_to_homeFragment)
            "SettingsFragment" -> binding.navigationHostFragment.getFragment<NavHostFragment>()
                .navController.navigate(R.id.action_settingsFragment_to_homeFragment)
            "ProfileFragment" -> binding.navigationHostFragment.getFragment<NavHostFragment>()
                .navController.navigate(R.id.action_profileFragment_to_homeFragment)
            "HomeFragment" -> finish()
            else -> binding.navigationHostFragment.getFragment<NavHostFragment>().navController.navigateUp()
        }
    }
}