package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3

import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.LinearGradient
import android.graphics.Shader
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
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
import androidx.core.content.FileProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import java.io.File

class MainActivity : AppCompatActivity() {
    private lateinit var tabGallery: LinearLayout
    private lateinit var tabCamera: LinearLayout
    private lateinit var galleryText: TextView
    private lateinit var cameraText: TextView
    private lateinit var galleryIcon: ImageView
    private lateinit var cameraIcon: ImageView
    private lateinit var contentArea: FrameLayout
    private lateinit var auth: FirebaseAuth

    // Añade estas propiedades a la clase
    private lateinit var memesRecyclerView: RecyclerView
    private lateinit var memesAdapter: MemesAdapter
    private val memesList = mutableListOf<MemeItem>()

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

        memesRecyclerView = findViewById(R.id.memes_recycler_view)
        memesRecyclerView.layoutManager = LinearLayoutManager(this)
        memesAdapter = MemesAdapter(memesList, this)
        memesRecyclerView.adapter = memesAdapter

        loadSavedMemes()

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

    // Añade estos métodos a la clase
    private fun loadSavedMemes() {
        val memesDir = File(getExternalFilesDir(null), CreateActivity.MEME_DIRECTORY)
        if (memesDir.exists() && memesDir.isDirectory) {
            memesList.clear()
            memesDir.listFiles()?.sortedByDescending { it.lastModified() }?.forEach { file ->
                memesList.add(MemeItem(Uri.fromFile(file)))
            }
            memesAdapter.notifyDataSetChanged()
        }
    }

    fun deleteMeme(position: Int) {
        val memeItem = memesList[position]
        val file = File(memeItem.uri.path ?: return)
        if (file.exists()) {
            file.delete()
            memesList.removeAt(position)
            memesAdapter.notifyItemRemoved(position)
            Toast.makeText(this, "Meme eliminado", Toast.LENGTH_SHORT).show()
        }
    }

    fun shareMeme(position: Int) {
        val memeItem = memesList[position]
        try {
            val file = File(memeItem.uri.path ?: return)
            val contentUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, contentUri)
                type = "image/jpeg"
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION
            }

            // Otorga permisos temporales a todas las apps que puedan manejar este intent
            val resInfoList = packageManager.queryIntentActivities(shareIntent, PackageManager.MATCH_DEFAULT_ONLY)
            for (resolveInfo in resInfoList) {
                val packageName = resolveInfo.activityInfo.packageName
                grantUriPermission(
                    packageName,
                    contentUri,
                    Intent.FLAG_GRANT_READ_URI_PERMISSION
                )
            }

            startActivity(Intent.createChooser(shareIntent, "Compartir meme"))
        } catch (e: Exception) {
            Toast.makeText(this, "Error al compartir: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
            Log.e("ShareMeme", "Error al compartir meme", e)
        }
    }

    fun saveMemeToGallery(position: Int) {
        val memeItem = memesList[position]
        try {
            MediaStore.Images.Media.insertImage(
                contentResolver,
                memeItem.uri.path,
                "meme_${System.currentTimeMillis()}.jpg",
                "Meme from Meme Generator"
            )
            Toast.makeText(this, "Meme guardado en galería", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar en galería", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    // Clase de modelo para los memes
    data class MemeItem(val uri: Uri)
}