package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3

import android.content.Intent
import android.graphics.*
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.view.animation.AnimationUtils
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.google.android.material.tabs.TabLayout
import com.google.android.material.tabs.TabLayoutMediator
import android.provider.MediaStore
import androidx.core.content.FileProvider
import com.google.firebase.auth.FirebaseAuth
import java.io.File
import java.io.FileOutputStream

class CreateActivity : AppCompatActivity(), tabText.TextListener {

    private lateinit var tabLayout: TabLayout
    private lateinit var tabLayoutStyle: TabLayout
    private lateinit var viewPager2: ViewPager2
    private lateinit var viewPagerStyle: ViewPager2
    private lateinit var viewPagerAdapter: ViewPagerAdapter
    private lateinit var viewPageStyleAdapter: ViewPageStyleAdapter
    private lateinit var auth: FirebaseAuth
    private lateinit var imagePreview: ImageView
    private lateinit var viewContainer: LinearLayout
    private var selectedImageUri: Uri? = null
    // Variables de clase
    private var topText: String = ""
    private var bottomText: String = ""
    private var currentTextSize = 48f
    private var currentTextColor = Color.WHITE
    private var currentBorderSize = 5f
    private var currentBorderColor = Color.BLACK

    companion object {
        const val MEME_DIRECTORY = "MemeFactory"
        private const val MEME_PREFIX = "meme_"
        private const val MEME_EXTENSION = ".jpg"
        private const val QUALITY = 90
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create)

        // Inicializar Firebase Auth
        auth = FirebaseAuth.getInstance()

        // Inicializar Vistas
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        val btnBack = findViewById<TextView>(R.id.btn_back)
        val btnLogout = findViewById<ImageView>(R.id.btn_logout)
        imagePreview = findViewById(R.id.icon_image_preview)
        viewContainer = findViewById(R.id.view_container)

        // Animación de rebote para el icono de vista previa
        val bounceAnimation = AnimationUtils.loadAnimation(this, R.anim.bounce)
        imagePreview.startAnimation(bounceAnimation)

        // Configurar listeners para los textos
        //setupTextListeners()

        // Resto de la configuración inicial...
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayShowTitleEnabled(false)

        // Configurar ViewPager y TabLayout
        tabLayout = findViewById(R.id.tab_layout)
        tabLayoutStyle = findViewById(R.id.tab_layout_text)
        viewPager2 = findViewById(R.id.view_pager)
        viewPagerStyle = findViewById(R.id.view_pager_text)
        viewPagerAdapter = ViewPagerAdapter(this)
        viewPageStyleAdapter = ViewPageStyleAdapter(this)
        viewPager2.adapter = viewPagerAdapter
        viewPagerStyle.adapter = viewPageStyleAdapter

        TabLayoutMediator(tabLayout, viewPager2) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.text_gallery)
                1 -> getString(R.string.text_camera)
                else -> null
            }
        }.attach()

        TabLayoutMediator(tabLayoutStyle, viewPagerStyle) { tab, position ->
            tab.text = when(position) {
                0 -> getString(R.string.text_style)
                1 -> getString(R.string.text_text)
                else -> null
            }
        }.attach()

        // Configurar botones
        btnBack.setOnClickListener {
            finish()
        }

        btnLogout.setOnClickListener {
            auth.signOut()
            Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    fun onImageSelected(uri: Uri) {
        selectedImageUri = uri
        try {
            val initialPreview = findViewById<LinearLayout>(R.id.initial_preview)
            val selectedImageView = findViewById<ImageView>(R.id.selected_image_view)
            val iconPreview = findViewById<ImageView>(R.id.icon_image_preview)

            // Detener la animación del icono
            iconPreview.clearAnimation()

            // Ocultar vista previa inicial
            initialPreview.visibility = View.GONE

            // Mostrar ImageView para la imagen seleccionada
            selectedImageView.visibility = View.VISIBLE

            // Cargar la imagen con Glide
            Glide.with(this)
                .load(uri)
                .centerCrop()
                .into(selectedImageView)

            // Actualizar la vista previa del meme con los textos
            updateMemePreview()

            Toast.makeText(this, "Imagen seleccionada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al cargar la imagen", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun updateMemePreview() {
        if (selectedImageUri != null) {
            try {
                val selectedImageView = findViewById<ImageView>(R.id.selected_image_view)

                // Cargar la imagen original
                val originalBitmap = MediaStore.Images.Media.getBitmap(contentResolver, selectedImageUri)
                val mutableBitmap = originalBitmap.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(mutableBitmap)

                // Configurar el paint para el borde del texto
                val borderPaint = Paint().apply {
                    color = currentBorderColor
                    textSize = currentTextSize
                    style = Paint.Style.STROKE
                    strokeWidth = currentBorderSize
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }

                // Configurar el paint para el relleno del texto
                val fillPaint = Paint().apply {
                    color = currentTextColor
                    textSize = currentTextSize
                    style = Paint.Style.FILL
                    typeface = Typeface.DEFAULT_BOLD
                    isAntiAlias = true
                }

                // Calcular posición del texto
                fun calculateAdjustedTextSize(text: String, availableWidth: Float): Float {
                    val testPaint = Paint(fillPaint).apply {
                        textSize = currentTextSize
                    }
                    var adjustedSize = currentTextSize

                    // Reducir tamaño hasta que quepa en el ancho disponible
                    while (testPaint.measureText(text) > availableWidth && adjustedSize > 10f) {
                        adjustedSize -= 1f
                        testPaint.textSize = adjustedSize
                    }
                    return adjustedSize
                }

                // Margenes fijos en pixeles
                val topMargin = 20f    // 50px desde el borde superior
                val bottomMargin = 20f  // 50px desde el borde inferior

                // Dibujar texto superior
                if (topText.isNotEmpty()) {
                    val availableWidth = mutableBitmap.width * 0.9f  // 90% del ancho de imagen
                    val adjustedSize = calculateAdjustedTextSize(topText, availableWidth)

                    borderPaint.textSize = adjustedSize
                    fillPaint.textSize = adjustedSize

                    val textWidth = fillPaint.measureText(topText)
                    val xPos = (mutableBitmap.width - textWidth) / 2  // Centrado horizontal

                    // Posición Y fija usando fontMetrics
                    val metrics = fillPaint.fontMetrics
                    val yPos = topMargin - metrics.ascent  // Ajuste preciso desde el borde superior

                    canvas.drawText(topText, xPos, yPos, borderPaint)
                    canvas.drawText(topText, xPos, yPos, fillPaint)
                }


                // Dibujar texto inferior
                if (bottomText.isNotEmpty()) {
                    val availableWidth = mutableBitmap.width * 0.9f
                    val adjustedSize = calculateAdjustedTextSize(bottomText, availableWidth)

                    borderPaint.textSize = adjustedSize
                    fillPaint.textSize = adjustedSize

                    val textWidth = fillPaint.measureText(bottomText)
                    val xPos = (mutableBitmap.width - textWidth) / 2

                    // Posición Y fija usando fontMetrics
                    val metrics = fillPaint.fontMetrics
                    val yPos = mutableBitmap.height - bottomMargin - metrics.descent

                    canvas.drawText(bottomText, xPos, yPos, borderPaint)
                    canvas.drawText(bottomText, xPos, yPos, fillPaint)
                }

                selectedImageView.setImageBitmap(mutableBitmap)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }


    fun showPermissionDeniedMessage() {
        Toast.makeText(this,
            "Se necesita permiso para acceder a la galería",
            Toast.LENGTH_LONG).show()
    }

    fun updateTextStyle(textSize: Float, textColor: Int, borderSize: Float, borderColor: Int) {
        this.currentTextSize = textSize
        this.currentTextColor = textColor
        this.currentBorderSize = borderSize
        this.currentBorderColor = borderColor
        updateMemePreview()
    }

    override fun onTopTextChanged(text: String) {
        topText = text
        updateMemePreview()
    }

    override fun onBottomTextChanged(text: String) {
        bottomText = text
        updateMemePreview()
    }

    fun onImageTaken(bitmap: Bitmap) {
        try {
            val initialPreview = findViewById<LinearLayout>(R.id.initial_preview)
            val selectedImageView = findViewById<ImageView>(R.id.selected_image_view)
            val iconPreview = findViewById<ImageView>(R.id.icon_image_preview)

            // Detener animación del icono
            iconPreview.clearAnimation()

            // Ocultar vista previa inicial
            initialPreview.visibility = View.GONE

            // Mostrar imagen tomada
            selectedImageView.visibility = View.VISIBLE
            selectedImageView.setImageBitmap(bitmap)

            // Guardar URI temporal
            selectedImageUri = saveBitmapToCache(bitmap)

            // Actualizar vista previa
            updateMemePreview()

            Toast.makeText(this, "Foto tomada", Toast.LENGTH_SHORT).show()
        } catch (e: Exception) {
            Toast.makeText(this, "Error al procesar foto", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun saveBitmapToCache(bitmap: Bitmap): Uri? {
        return try {
            val file = File(cacheDir, "temp_photo.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, 90, outputStream)
            outputStream.close()
            Uri.fromFile(file)
        } catch (e: Exception) {
            null
        }
    }

    fun onSaveMemeClick(view: View) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Primero selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Obtener el bitmap actualizado con los textos
            val selectedImageView = findViewById<ImageView>(R.id.selected_image_view)
            val drawable = selectedImageView.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                saveMemeToStorage(bitmap)
                Toast.makeText(this, "Meme guardado correctamente", Toast.LENGTH_SHORT).show()

                // Redirigir a MainActivity
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al guardar el meme", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    fun onShareMemeClick(view: View) {
        if (selectedImageUri == null) {
            Toast.makeText(this, "Primero selecciona una imagen", Toast.LENGTH_SHORT).show()
            return
        }

        try {
            // Obtener el bitmap actualizado con los textos
            val selectedImageView = findViewById<ImageView>(R.id.selected_image_view)
            val drawable = selectedImageView.drawable
            if (drawable is BitmapDrawable) {
                val bitmap = drawable.bitmap
                shareMeme(bitmap)
            }
        } catch (e: Exception) {
            Toast.makeText(this, "Error al compartir el meme", Toast.LENGTH_SHORT).show()
            e.printStackTrace()
        }
    }

    private fun saveMemeToStorage(bitmap: Bitmap): Uri? {
        return try {
            // Crear directorio si no existe
            val storageDir = File(getExternalFilesDir(null), MEME_DIRECTORY)
            if (!storageDir.exists()) {
                storageDir.mkdirs()
            }

            // Crear archivo con nombre único
            val timeStamp = System.currentTimeMillis().toString()
            val memeFile = File(storageDir, "${MEME_PREFIX}${timeStamp}${MEME_EXTENSION}")

            // Guardar bitmap
            val outputStream = FileOutputStream(memeFile)
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
            outputStream.close()

            // Añadir a la galería
            MediaStore.Images.Media.insertImage(
                contentResolver,
                memeFile.absolutePath,
                memeFile.name,
                "Meme created with Meme Generator"
            )

            // Notificar a la galería que hay un nuevo archivo
            sendBroadcast(
                Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.fromFile(memeFile))
            )

            Uri.fromFile(memeFile)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    private fun shareMeme(bitmap: Bitmap) {
        try {
            // Guardar temporalmente el meme para compartir
            val file = File(cacheDir, "meme_to_share.jpg")
            val outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, QUALITY, outputStream)
            outputStream.close()

            val shareUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                file
            )

            val shareIntent = Intent().apply {
                action = Intent.ACTION_SEND
                putExtra(Intent.EXTRA_STREAM, shareUri)
                type = "image/jpeg"
                addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
            }

            startActivity(Intent.createChooser(shareIntent, "Compartir meme"))
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}