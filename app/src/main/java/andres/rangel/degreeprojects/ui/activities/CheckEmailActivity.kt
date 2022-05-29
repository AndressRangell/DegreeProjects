package andres.rangel.degreeprojects.ui.activities

import andres.rangel.degreeprojects.databinding.ActivityCheckEmailBinding
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.userProfileChangeRequest
import timber.log.Timber

class CheckEmailActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityCheckEmailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCheckEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = FirebaseAuth.getInstance()

        sendEmailVerification()

        val user = auth.currentUser

        binding.btnContinue.setOnClickListener {
            val profileUpdates = userProfileChangeRequest { }

            user!!.updateProfile(profileUpdates).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    if (user.isEmailVerified) {
                        reload()
                    } else {
                        Snackbar.make(
                            binding.root, "Por favor verifica tu correo",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }

        binding.ivLogout.setOnClickListener {
            auth.signOut()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }

    private fun sendEmailVerification() {
        val user = auth.currentUser
        user!!.sendEmailVerification().addOnCompleteListener(this) { task ->
            if (task.isSuccessful) {
                Timber.d("Verification message send")
            }
        }.addOnFailureListener {
            Snackbar.make(
                binding.root, "Error al enviar mensaje de verificación",
                Snackbar.LENGTH_SHORT
            ).show()
            Timber.e("Error: ${it.message}")
        }
    }

    private fun reload() {
        val intent = Intent(this, MainActivity::class.java)
        this.startActivity(intent)
        finish()
    }

}