package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.LinearGradient
import android.graphics.Shader
import android.os.Bundle
import android.view.LayoutInflater
import android.widget.Button
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import com.google.firebase.auth.FirebaseAuth

class MainActivity : AppCompatActivity() {
    private lateinit var tabGallery: LinearLayout
    private lateinit var tabCamera: LinearLayout
    private lateinit var galleryText: TextView
    private lateinit var cameraText: TextView
    private lateinit var galleryIcon: ImageView
    private lateinit var cameraIcon: ImageView
    private lateinit var contentArea: FrameLayout
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Verificar autenticación
        auth = FirebaseAuth.getInstance()
        if (auth.currentUser == null) {
            // Si no hay usuario logueado, redirigir a LoginActivity
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
            return
        }

        setContentView(R.layout.activity_main)

        val btnCreateMeme = findViewById<TextView>(R.id.btn_create_meme)
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val btnLogout = findViewById<ImageView>(R.id.btn_logout)

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

        // Gradient for the large main title
        val mainTitle = findViewById<TextView>(R.id.main_title_app)
        val mainPaint = mainTitle.paint
        val mainWidth = mainPaint.measureText(mainTitle.text.toString())
        val mainShader = LinearGradient(
            0f, 0f, mainWidth, 0f,
            intArrayOf(0xFF8B5CF6.toInt(), 0xFFEC4899.toInt()),
            null,
            Shader.TileMode.CLAMP
        )
        mainTitle.paint.shader = mainShader

//        Redigir a CreateActivity
        btnCreateMeme.setOnClickListener {
            val intent = Intent(this, CreateActivity::class.java)
            startActivity(intent)
        }

        // BTN Logout
        btnLogout.setOnClickListener {
            auth.signOut()
            // Mensaje de cierre de sesión
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
            finish()
        }

    }
}