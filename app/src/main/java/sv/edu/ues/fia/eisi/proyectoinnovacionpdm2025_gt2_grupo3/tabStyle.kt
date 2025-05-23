package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.SeekBar
import android.widget.TextView
import androidx.fragment.app.Fragment

class tabStyle : Fragment() {
    private var textSize = 50f
    private var borderSize = 5f
    private var textColor = Color.WHITE
    private var borderColor = Color.BLACK

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab_style, container, false)

        // Configurar SeekBar para tamaño de texto
        val seekBarTextSize = view.findViewById<SeekBar>(R.id.seekBarTextSize)
        val textFontSizeValue = view.findViewById<TextView>(R.id.textFontSizeValue)

        seekBarTextSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                textSize = progress.toFloat() + 20f // Rango 20-120
                textFontSizeValue.text = progress.toString()
                updateMemeStyle()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configurar botones de color de texto
        val colorButtons = mapOf(
            R.id.btn_color_white to Color.WHITE,
            R.id.btn_color_black to Color.BLACK,
            R.id.btn_color_gray to Color.GRAY,
            R.id.btn_color_red to Color.RED,
            R.id.btn_color_green to Color.GREEN,
            R.id.btn_color_blue to Color.BLUE,
            R.id.btn_color_yellow to Color.YELLOW,
            R.id.btn_color_orange to Color.parseColor("#FFA500"),
            R.id.btn_color_brown to Color.parseColor("#A52A2A"),
            R.id.btn_color_purple to Color.parseColor("#800080"),
            R.id.btn_color_pink to Color.parseColor("#FFC0CB"),
            R.id.btn_color_cyan to Color.CYAN,
            R.id.btn_color_lime to Color.parseColor("#00FF00"),
            R.id.btn_color_turquoise to Color.parseColor("#40E0D0"),
            R.id.btn_color_gold to Color.parseColor("#FFD700"),
            R.id.btn_color_silver to Color.parseColor("#C0C0C0")
        )

        colorButtons.forEach { (id, color) ->
            view.findViewById<View>(id).setOnClickListener {
                textColor = color
                updateMemeStyle()
            }
        }

        // Configurar SeekBar para tamaño de borde
        val seekBarBorderSize = view.findViewById<SeekBar>(R.id.seekBar_borderSize)
        val borderSizeValue = view.findViewById<TextView>(R.id.bordersizeValue)

        seekBarBorderSize.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                borderSize = progress.toFloat()
                borderSizeValue.text = "${progress}px"
                updateMemeStyle()
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {}
            override fun onStopTrackingTouch(seekBar: SeekBar?) {}
        })

        // Configurar botones de color de borde
        val borderColorButtons = mapOf(
            R.id.btn_border_white to Color.WHITE,
            R.id.btn_border_black to Color.BLACK,
            R.id.btn_border_gray to Color.GRAY,
            R.id.btn_border_red to Color.RED,
            R.id.btn_border_green to Color.GREEN,
            R.id.btn_border_blue to Color.BLUE,
            R.id.btn_border_yellow to Color.YELLOW,
            R.id.btn_border_orange to Color.parseColor("#FFA500"),
            R.id.btn_border_brown to Color.parseColor("#A52A2A"),
            R.id.btn_border_purple to Color.parseColor("#800080"),
            R.id.btn_border_pink to Color.parseColor("#FFC0CB"),
            R.id.btn_border_cyan to Color.CYAN,
            R.id.btn_border_lime to Color.parseColor("#00FF00"),
            R.id.btn_border_turquoise to Color.parseColor("#40E0D0"),
            R.id.btn_border_gold to Color.parseColor("#FFD700"),
            R.id.btn_border_silver to Color.parseColor("#C0C0C0")
        )

        borderColorButtons.forEach { (id, color) ->
            view.findViewById<View>(id).setOnClickListener {
                borderColor = color
                updateMemeStyle()
            }
        }

        // Botones de minimizar/maximizar tamaño
        view.findViewById<View>(R.id.btn_minimize_size).setOnClickListener {
            seekBarTextSize.progress = maxOf(0, seekBarTextSize.progress - 5)
        }

        view.findViewById<View>(R.id.btn_maximize_size).setOnClickListener {
            seekBarTextSize.progress = minOf(200, seekBarTextSize.progress + 5)
        }

        return view
    }

    private fun updateMemeStyle() {
        (activity as? CreateActivity)?.updateTextStyle(textSize, textColor, borderSize, borderColor)
    }
}