package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.ActivityRegisterBinding
import android.content.Intent
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.ktx.Firebase
import timber.log.Timber
import java.util.regex.Pattern


class RegisterActivity : AppCompatActivity() {

    private lateinit var auth: FirebaseAuth
    private lateinit var firestore: FirebaseFirestore
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)

        auth = Firebase.auth
        firestore = FirebaseFirestore.getInstance()

        binding.btnSignUp.setOnClickListener {

            val email = binding.etEmail.text.toString()
            val password = binding.etPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            val passwordRegex = Pattern.compile(
                "^" +
                        "(?=.*[-@#$%^&+*/=])" +     // Al menos 1 carácter especial
                        ".{6,}" +                // Al menos 6 caracteres
                        "$"
            )

            if (email.isEmpty() || !Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                Snackbar.make(
                    binding.root, "Ingrese un email valido.",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else if (password.isEmpty() || !passwordRegex.matcher(password).matches()) {
                Snackbar.make(
                    binding.root,
                    "contraseña: mínimo 5 caracteres alfanuméricos y 1 caracter especial",
                    Snackbar.LENGTH_LONG
                ).show()
            } else if (password != confirmPassword) {
                Snackbar.make(
                    binding.root, "Las contraseñas no coinciden",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else if (!(email.contains("@uts.edu.co") || email.contains("@correo.uts.edu.co"))) {
                Snackbar.make(
                    binding.root, "Ingresa tu correo institucional",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                createAccount(email, password)
            }
        }

        binding.btnBack.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

    }

    private fun createAccount(email: String, password: String) {
        auth.createUserWithEmailAndPassword(email, password)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    startActivity(Intent(this, CheckEmailActivity::class.java))
                    finish()
                } else {
                    Timber.e("createUserWithEmail:failure ${task.exception}")
                    Snackbar.make(
                        binding.root, "Error al crear la cuenta",
                        Snackbar.LENGTH_SHORT
                    ).show()
                }
            }

        firestore.collection("users").document(email).set(
            hashMapOf(
                "name" to binding.etName.text.toString(),
                "document" to "",
                "phone" to "",
                "career" to ""
            )
        )
    }

    override fun onBackPressed() {
        startActivity(Intent(this, LoginActivity::class.java))
        finish()
    }
}