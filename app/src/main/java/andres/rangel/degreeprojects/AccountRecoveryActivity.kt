package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.ActivityAccountRecoveryBinding
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase

class AccountRecoveryActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAccountRecoveryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityAccountRecoveryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.senEmailAppCompatButton.setOnClickListener {
            val emailAddress = binding.etEmail.text.toString()
            Firebase.auth.sendPasswordResetEmail(emailAddress).addOnCompleteListener { task ->
                if(task.isSuccessful) {
                    val intent = Intent(this, LoginActivity::class.java)
                    this.startActivity(intent)
                } else {
                    Toast.makeText(this, "Ingrese un email de una cuenta valida.",
                        Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}