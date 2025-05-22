package sv.edu.ues.fia.eisi.proyectoinnovacionpdm2025_gt2_grupo3

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

class tabCamera : Fragment() {
    private lateinit var btnActiveCamera: Button

    // Contracto para tomar foto
    private val takePictureLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == android.app.Activity.RESULT_OK) {
            val imageBitmap = result.data?.extras?.get("data") as Bitmap
            // Enviar la foto a CreateActivity
            (activity as? CreateActivity)?.onImageTaken(imageBitmap)
        }
    }

    // Contracto para permisos de cámara
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            takePicture()
        } else {
            Toast.makeText(
                requireContext(),
                "Se necesita permiso para usar la cámara",
                Toast.LENGTH_LONG
            ).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_tab_camera, container, false)

        btnActiveCamera = view.findViewById(R.id.btn_active_camera)

        setupButtons()
        return view
    }

    private fun setupButtons() {
        btnActiveCamera.setOnClickListener {
                requestCameraPermission()
        }
    }

    private fun requestCameraPermission() {
        when {
            ContextCompat.checkSelfPermission(
                requireContext(),
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED -> {
                takePicture()
            }
            shouldShowRequestPermissionRationale(Manifest.permission.CAMERA) -> {
                showPermissionExplanation()
            }
            else -> {
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
        }
    }

    private fun takePicture() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        takePictureLauncher.launch(takePictureIntent)
    }

    private fun showPermissionExplanation() {
        androidx.appcompat.app.AlertDialog.Builder(requireContext())
            .setTitle("Permiso requerido")
            .setMessage("Necesitamos acceso a tu cámara para tomar fotos para memes")
            .setPositiveButton("Entendido") { _, _ ->
                requestPermissionLauncher.launch(Manifest.permission.CAMERA)
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
}