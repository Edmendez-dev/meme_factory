package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3

import android.content.Intent
import android.graphics.*
import android.net.Uri
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
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
import com.google.firebase.auth.FirebaseAuth

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

//    private fun setupTextListeners() {
//        val textWatcher = object : TextWatcher {
//            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
//            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
//            override fun afterTextChanged(s: Editable?) {
//                updateMemePreview()
//            }
//        }
//
//        editTopText.addTextChangedListener(textWatcher)
//        editBottomText.addTextChangedListener(textWatcher)
//    }

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
                fun calculateAdjustedTextSize(text: String, availableWidth: Float, availableHeight: Float): Float {
                    val testPaint = Paint(fillPaint)
                    var testSize = currentTextSize
                    testPaint.textSize = testSize

                    // Reducir tamaño hasta que quepa en el ancho
                    while (testPaint.measureText(text) > availableWidth * 0.9f && testSize > 10f) {
                        testSize -= 1f
                        testPaint.textSize = testSize
                    }

                    // Asegurar que también quepa en altura
                    val metrics = testPaint.fontMetrics
                    val textHeight = metrics.descent - metrics.ascent
                    if (textHeight > availableHeight * 0.8f) {
                        testSize = (availableHeight * 0.8f) / (metrics.descent - metrics.ascent) * testSize
                        testPaint.textSize = testSize
                    }

                    return testSize
                }

                // Dibujar texto superior
                if (topText.isNotEmpty()) {
                    val availableWidth = mutableBitmap.width * 0.9f
                    val availableHeight = mutableBitmap.height * 0.4f
                    val adjustedSize = calculateAdjustedTextSize(topText, availableWidth, availableHeight)

                    borderPaint.textSize = adjustedSize
                    fillPaint.textSize = adjustedSize

                    val textWidth = fillPaint.measureText(topText)
                    val xPos = (mutableBitmap.width - textWidth) / 2
                    val yPos = 50f + (fillPaint.descent() - fillPaint.ascent()) // Margen superior + altura texto

                    canvas.drawText(topText, xPos, yPos, borderPaint)
                    canvas.drawText(topText, xPos, yPos, fillPaint)
                }

                // Dibujar texto inferior
                if (bottomText.isNotEmpty()) {
                    val availableWidth = mutableBitmap.width * 0.9f
                    val availableHeight = mutableBitmap.height * 0.4f
                    val adjustedSize = calculateAdjustedTextSize(bottomText, availableWidth, availableHeight)

                    borderPaint.textSize = adjustedSize
                    fillPaint.textSize = adjustedSize

                    val textWidth = fillPaint.measureText(bottomText)
                    val xPos = (mutableBitmap.width - textWidth) / 2
                    val yPos = mutableBitmap.height - 50f // Margen inferior

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
}