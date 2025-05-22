package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3

import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import com.google.firebase.FirebaseApp
import com.google.firebase.auth.FirebaseAuth

class LoginActivity : AppCompatActivity() {
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar si el usuario ya ha iniciado sesión
        FirebaseApp.initializeApp(this)
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser != null) {
            // El usuario ya ha iniciado sesión, redirigir a MainActivity
            startActivity(Intent(this, MainActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_login)

        val btnLogin = findViewById<TextView>(R.id.btn_login)
        val linkRegister = findViewById<TextView>(R.id.login_create_account)
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

        val loginTitle = findViewById<TextView>(R.id.login_title)
        val mainPaint = loginTitle.paint
        val mainWidth = mainPaint.measureText(loginTitle.text.toString())
        val mainShader = LinearGradient(
            0f, 0f, mainWidth, 0f,
            intArrayOf(0xFF8B5CF6.toInt(), 0xFFEC4899.toInt()),
            null,
            Shader.TileMode.CLAMP
        )
        loginTitle.paint.shader = mainShader

        // Redigir a SignUpActivity si no tiene cuenta
        linkRegister.setOnClickListener {
            val intent = Intent(this, SignUpActivity::class.java)
            startActivity(intent)
        }

        // Inicio de sesion
        // Correo y Contraseña
        btnLogin.setOnClickListener {
            val email = findViewById<TextView>(R.id.login_email_input).text.toString()
            val password = findViewById<TextView>(R.id.login_password_input).text.toString()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Por favor ingrese email y contraseña", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        // Inicio de sesión exitoso
                        // Mostrar notificación de éxito
                        Toast.makeText(this, "Inicio de sesión exitoso", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                    } else {
                        // Mostrar notificación de error
                        Toast.makeText(this, "Correo o contraseña incorrectos", Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }
}