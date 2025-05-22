package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.auth.FirebaseAuth

class SignUpActivity: AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_signup)
        auth = FirebaseAuth.getInstance()

        val nameText = findViewById<TextView>(R.id.register_name_input)
        val emailText = findViewById<TextView>(R.id.register_email_input)
        val passwordText = findViewById<TextView>(R.id.register_password_input)
        val btnRegister = findViewById<Button>(R.id.btn_signup)

        val linkLogin = findViewById<TextView>(R.id.resgister_link_login)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        val title = findViewById<TextView>(R.id.toolbar_title)
        val paint = title.paint
        val width = paint.measureText(title.text.toString())
        val shader = LinearGradient(
            0f, 0f, width, 0f,
            intArrayOf(0xFF8B5CF6.toInt(), 0xFFEC4899.toInt()),
            null,
            Shader.TileMode.CLAMP
        )
        title.paint.shader = shader

        val loginTitle = findViewById<TextView>(R.id.register_title)
        val mainPaint = loginTitle.paint
        val mainWidth = mainPaint.measureText(loginTitle.text.toString())
        val mainShader = LinearGradient(
            0f, 0f, mainWidth, 0f,
            intArrayOf(0xFF8B5CF6.toInt(), 0xFFEC4899.toInt()),
            null,
            Shader.TileMode.CLAMP
        )
        loginTitle.paint.shader = mainShader

        // Redigir a LoginActivity si ya tiene cuenta
        linkLogin.setOnClickListener {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }

        // Registrar usuario
        btnRegister.setOnClickListener {
            val email = emailText.text.toString().trim()
            val password = passwordText.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Email and password required", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Registration successful, go to MainActivity
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        // Show error
                        Toast.makeText(this, "Sign up failed: ${task.exception?.message}", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}