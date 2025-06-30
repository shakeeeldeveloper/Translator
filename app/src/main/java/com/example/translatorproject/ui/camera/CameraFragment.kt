/*
package com.example.translatorproject.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.translatorproject.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private lateinit var captureButton: ImageView
    private lateinit var flashIcon: ImageView
    private lateinit var galleryIcon: ImageView
    private lateinit var toolbar: Toolbar

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var isFlashOn = false

    private val cameraPermissionCode = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // View bindings
        previewView = view.findViewById(R.id.previewView)
        captureButton = view.findViewById(R.id.captureButton)
        flashIcon = view.findViewById(R.id.flashIcon)
        galleryIcon = view.findViewById(R.id.galleryIcon)
        toolbar = view.findViewById(R.id.customToolbar)

        setupToolbar()

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionCode
            )
        }

        captureButton.setOnClickListener {
            takePhoto()
        }

        flashIcon.setOnClickListener {
            isFlashOn = !isFlashOn
            camera?.cameraControl?.enableTorch(isFlashOn)
        }

        galleryIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Gallery clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        toolbar.title = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.findViewById<ImageView>(R.id.backIcon).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        toolbar.findViewById<ImageView>(R.id.rotateIcon).setOnClickListener {
            Toast.makeText(requireContext(), "Rotate clicked", Toast.LENGTH_SHORT).show()
        }

        toolbar.findViewById<TextView>(R.id.lang1TV).setOnClickListener {
            Toast.makeText(requireContext(), "Lang 1", Toast.LENGTH_SHORT).show()
        }

        toolbar.findViewById<TextView>(R.id.lang2TV).setOnClickListener {
            Toast.makeText(requireContext(), "Lang 2", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                Log.d("CameraFragment", "Camera started")
            } catch (e: Exception) {
                Log.e("CameraFragment", "Camera use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            requireContext().cacheDir,
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(requireContext(), "Saved: ${photoFile.absolutePath}", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Capture failed: ${exc.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == cameraPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }
}
*/


package com.example.translatorproject.ui.camera

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.*
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.camera.core.*
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.example.translatorproject.R
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraFragment : Fragment() {

    private lateinit var previewView: PreviewView
    private lateinit var captureButton: ImageView
    private lateinit var flashIcon: ImageView
    private lateinit var galleryIcon: ImageView
    private lateinit var capturedImageView: ImageView
    private lateinit var toolbar: Toolbar

    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var isFlashOn = false

    private val cameraPermissionCode = 1001

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_camera, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        previewView = view.findViewById(R.id.previewView)
        captureButton = view.findViewById(R.id.captureButton)
        flashIcon = view.findViewById(R.id.flashIcon)
        galleryIcon = view.findViewById(R.id.galleryIcon)
        toolbar = view.findViewById(R.id.customToolbar)
        capturedImageView = view.findViewById(R.id.capturedImageView)

        setupToolbar()
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            requireActivity().window.statusBarColor = Color.BLACK
        }

        if (ContextCompat.checkSelfPermission(requireContext(), Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                requireActivity(),
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionCode
            )
        }

        captureButton.setOnClickListener {
            takePhoto()
        }

        flashIcon.setOnClickListener {
            isFlashOn = !isFlashOn
            camera?.cameraControl?.enableTorch(isFlashOn)
        }

        galleryIcon.setOnClickListener {
            Toast.makeText(requireContext(), "Gallery clicked", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupToolbar() {
        toolbar.title = ""
        (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

        toolbar.findViewById<ImageView>(R.id.backIcon).setOnClickListener {
            requireActivity().onBackPressedDispatcher.onBackPressed()
        }

        toolbar.findViewById<ImageView>(R.id.rotateIcon).setOnClickListener {
            Toast.makeText(requireContext(), "Rotate clicked", Toast.LENGTH_SHORT).show()
        }

        toolbar.findViewById<TextView>(R.id.lang1TV).setOnClickListener {
            Toast.makeText(requireContext(), "Lang 1", Toast.LENGTH_SHORT).show()
        }

        toolbar.findViewById<TextView>(R.id.lang2TV).setOnClickListener {
            Toast.makeText(requireContext(), "Lang 2", Toast.LENGTH_SHORT).show()
        }
    }

    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(previewView.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()

            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    cameraSelector,
                    preview,
                    imageCapture
                )
                capturedImageView.visibility = View.GONE
                previewView.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("CameraFragment", "Camera use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = File(
            requireContext().cacheDir,
            "IMG_${SimpleDateFormat("yyyyMMdd_HHmmss", Locale.US).format(Date())}.jpg"
        )

        val outputOptions = ImageCapture.OutputFileOptions.Builder(photoFile).build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageSavedCallback {
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(requireContext(), "Saved: ${photoFile.absolutePath}", Toast.LENGTH_SHORT).show()

                    val bitmap = BitmapFactory.decodeFile(photoFile.absolutePath)
                    capturedImageView.setImageBitmap(bitmap)
                    capturedImageView.visibility = View.VISIBLE
                    previewView.visibility = View.GONE
                }

                override fun onError(exc: ImageCaptureException) {
                    Toast.makeText(requireContext(), "Capture failed: ${exc.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        if (requestCode == cameraPermissionCode) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startCamera()
            } else {
                Toast.makeText(requireContext(), "Camera permission denied", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        requireActivity().findViewById<View>(R.id.bottom_navigation)?.visibility = View.GONE

    }

    override fun onDestroy() {
        super.onDestroy()
        requireActivity().findViewById<View>(R.id.bottom_navigation)?.visibility = View.VISIBLE

    }
}
