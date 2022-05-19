package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.ActivityAccountRecoveryBinding
import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountRecoveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountRecoveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSend.setOnClickListener {
            val email = binding.etEmail.text.toString()
            if (email.isEmpty()) {
                Snackbar.make(
                    binding.root,
                    "Debes completar todos los campos",
                    Snackbar.LENGTH_SHORT
                ).show()
            } else {
                Firebase.auth.sendPasswordResetEmail(email).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Snackbar.make(
                            binding.root,
                            "Se envió un mensaje a tu correo institucional para reestablecer tu contraseña",
                            Snackbar.LENGTH_LONG
                        ).show()
                    } else {
                        Snackbar.make(
                            binding.root,
                            "Ingrese un correo institucional válido",
                            Snackbar.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}