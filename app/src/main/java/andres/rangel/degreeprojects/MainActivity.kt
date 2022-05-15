package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.Utils.Companion.bitmap
import andres.rangel.degreeprojects.Utils.Companion.email
import andres.rangel.degreeprojects.Utils.Companion.name
import andres.rangel.degreeprojects.databinding.ActivityMainBinding
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Matrix
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
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

        getInfo()
        getProfileImage()
    }

    private fun getInfo() {
        auth = FirebaseAuth.getInstance()
        firestore = FirebaseFirestore.getInstance()
        email = auth.currentUser?.email
        val document = firestore.collection("users").document(email.toString())
        document.get().addOnSuccessListener { document ->
            name = document.get("name").toString()
        }
    }

    private fun getProfileImage() {
        storage = FirebaseStorage.getInstance()
        val reference = storage.getReference("perfil.jpg")

        try {
            val file = File.createTempFile("profile", ".jpg")
            reference.getFile(file).addOnSuccessListener {
                val bitmapReference = BitmapFactory.decodeFile(file.absolutePath)
                val matrix = Matrix()
                matrix.postRotate(270F)
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