package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.ActivityLoginBinding
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import timber.log.Timber

class LoginActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth

        binding.signInAppCompatButton.setOnClickListener {
            val email = binding.etEmail.text.toString()
            val password = binding.passwordEditText.text.toString()

            when {
                email.isEmpty() || password.isEmpty() -> {
                    Snackbar.make(
                        binding.root, "Debes completar todos los campos",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
                else -> {
                    signIn(email, password)
                }
            }

        }

        binding.signUpTextView.setOnClickListener {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }

        binding.recoveryAccountTextView.setOnClickListener {
            val intent = Intent(this, AccountRecoveryActivity::class.java)
            startActivity(intent)
        }

    }

    public override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        if (currentUser != null) {
            if (currentUser.isEmailVerified) {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            } else {
                startActivity(Intent(this, CheckEmailActivity::class.java))
                finish()
            }
        }
    }

    private fun signIn(email: String, password: String) {
        auth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    if (task.result.user?.isEmailVerified == true) {
                        Timber.d("signInWithEmail: success")
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Timber.d("signInWithEmail: Failure")
                        startActivity(Intent(this, CheckEmailActivity::class.java))
                        finish()
                    }
                } else {
                    Timber.e("signInWithEmail:failure ${task.exception}")
                    Snackbar.make(
                        binding.root, "Correo o contraseña icorrectos.",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }
    }

}