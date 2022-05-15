package andres.rangel.degreeprojects

import andres.rangel.degreeprojects.databinding.ActivityUpdatePasswordBinding
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.util.regex.Pattern

class UpdatePasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityUpdatePasswordBinding
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityUpdatePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        auth = Firebase.auth

        val passwordRegex = Pattern.compile("^" +
                "(?=.*[-@#$%^/&+=])" +     // Al menos 1 carácter especial
                ".{6,}" +                // Al menos 6 caracteres
                "$")

        binding.btnUpdate.setOnClickListener {
            val currentPassword = binding.etCurrentPassword.text.toString()
            val newPassword = binding.etNewPassword.text.toString()
            val confirmPassword = binding.etConfirmPassword.text.toString()

            if (newPassword.isEmpty() || !passwordRegex.matcher(newPassword).matches()){
                Toast.makeText(this, "La contraseña es debil.",
                    Toast.LENGTH_SHORT).show()
            } else if (newPassword != confirmPassword){
                Toast.makeText(this, "Confirma la contraseña.",
                    Toast.LENGTH_SHORT).show()
            } else {
                chagePassword(currentPassword, newPassword)
            }
        }

    }

    private  fun chagePassword(current : String, password : String){
        val user = auth.currentUser

        if (user != null){
            val email = user.email
            val credential = EmailAuthProvider
                .getCredential(email!!, current)

            user.reauthenticate(credential)
                .addOnCompleteListener { task ->
                    if(task.isSuccessful) {

                        user.updatePassword(password)
                            .addOnCompleteListener { taskUpdatePassword ->
                                if (taskUpdatePassword.isSuccessful) {
                                    Toast.makeText(this, "Se cambio la contraseña.",
                                        Toast.LENGTH_SHORT).show()
                                    val intent = Intent(this, MainActivity::class.java)
                                    this.startActivity(intent)
                                }
                            }

                    } else {
                        Toast.makeText(this, "La contraseña actual es incorrecta.",
                            Toast.LENGTH_SHORT).show()
                    }
                }
        }




    }
}