package com.example.translatorproject

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.Camera
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import androidx.lifecycle.ViewModelProvider
import com.bumptech.glide.Glide
import com.example.translatorproject.databinding.ActivityCameraBinding
import com.example.translatorproject.ui.camera.CameraViewModel
import com.example.translatorproject.ui.camera.CameraViewModelFactory
import java.io.File

class CameraActivity : AppCompatActivity() {

    private var binding: ActivityCameraBinding? = null
  // private val binding get() = _binding!!

    private lateinit var viewModel: CameraViewModel
    private var imageCapture: ImageCapture? = null
    private var camera: Camera? = null
    private var isFlashOn = false
    private val cameraPermissionCode = 1001
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCameraBinding.inflate(layoutInflater)
        setContentView(binding?.root)

        val factory = CameraViewModelFactory(application)
         viewModel = ViewModelProvider(this, factory)[CameraViewModel::class.java]

       // viewModel = ViewModelProvider(this)[CameraViewModel::class.java]
        binding?.previewView?.visibility = View.VISIBLE
        viewModel.clearImage()


        setupToolbar()
        setupObservers()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            this.window.statusBarColor = Color.BLACK
        }

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
            == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.CAMERA),
                cameraPermissionCode
            )
        }

        binding?.captureButton?.setOnClickListener {
            imageCapture?.let { viewModel.takePhoto(it) }
        }

        binding?.flashIcon?.setOnClickListener {
           /* isFlashOn = !isFlashOn
            camera?.cameraControl?.enableTorch(isFlashOn)*/

            if (camera?.cameraInfo?.hasFlashUnit() == true) {
                isFlashOn = !isFlashOn
                camera?.cameraControl?.enableTorch(isFlashOn)
            } else {
                Toast.makeText(this, "Flash not available on this device", Toast.LENGTH_SHORT).show()
            }

        }


        binding?.galleryIcon?.setOnClickListener {
            imagePicker.launch("image/*")
        }



    }
    private val imagePicker = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            val localUri = copyUriToCache(this, uri)
            if (localUri != null) {
                viewModel.setImage(localUri)
            } else {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "No image selected", Toast.LENGTH_SHORT).show()
        }
    }
    fun copyUriToCache(context: Context, uri: Uri): Uri? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri) ?: return null
            val file = File(context.cacheDir, "picked_${System.currentTimeMillis()}.jpg")
            val outputStream = file.outputStream()

            inputStream.copyTo(outputStream)

            inputStream.close()
            outputStream.close()

            FileProvider.getUriForFile(
                context,
                "${context.packageName}.provider",
                file
            )
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }


    private fun setupToolbar() {
        val toolbar = binding?.customToolbar
        (this as AppCompatActivity).setSupportActionBar(toolbar)

        binding?.backIcon?.setOnClickListener {
            this.onBackPressedDispatcher.onBackPressed()
        }

        binding?.rotateIcon?.setOnClickListener {
            Toast.makeText(this, "Rotate clicked", Toast.LENGTH_SHORT).show()
        }

        binding?.lang1TV?.setOnClickListener {
            Toast.makeText(this, "Lang 1", Toast.LENGTH_SHORT).show()
        }

        binding?.lang2TV?.setOnClickListener {
            Toast.makeText(this, "Lang 2", Toast.LENGTH_SHORT).show()
        }
    }

    private fun setupObservers() {
        viewModel.capturedImage.observe(this) { bitmap ->
            /*   if (bitmap != null) {
                   binding.capturedImageView.setImageBitmap(bitmap)
                   binding.capturedImageView.visibility = View.VISIBLE
                   binding.previewView.visibility = View.GONE
               } */
            if (bitmap != null) {

                binding?.capturedImageView?.setImageBitmap(bitmap)
                binding?.previewView?.visibility = View.GONE

                // 1. Save bitmap to a temp file
                val imageFile = File(this.cacheDir, "captured_${System.currentTimeMillis()}.jpg")
                val outputStream = imageFile.outputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream)
                outputStream.close()

                // 2. Get URI from FileProvider
                val uri = FileProvider.getUriForFile(
                    this,
                    "${this.packageName}.provider",
                    imageFile
                )

                // 3. Launch CropActivity with the URI
                val intent = Intent(this, CropActivity::class.java).apply {
                    putExtra("image_uri", uri.toString())
                    flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

                }
                startActivity(intent)
                finish()
                viewModel.clearImage()

                // 4. Optionally hide camera preview
                //binding?.previewView?.visibility = View.GONE
            }


            else {
                //Toast.makeText(this, "Failed to capture image", Toast.LENGTH_SHORT).show()
            }
        }
        // Observe selected image URI
        viewModel.imageUri.observe(this) { uri ->

            val intent = Intent(this, CropActivity::class.java).apply {
                putExtra("image_uri", uri.toString())
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION

            }
            startActivity(intent)
            finish()
            /*viewModel.clearImage()
            uri?.let {
                ProcessCameraProvider.getInstance(this).addListener({
                    val cameraProvider = ProcessCameraProvider.getInstance(this).get()
                    cameraProvider.unbindAll()

                    binding?.previewView?.visibility = View.GONE
                    binding?.capturedImageView?.visibility = View.VISIBLE
                    Glide.with(this).load(uri).into(binding?.capturedImageView!!)
                }, ContextCompat.getMainExecutor(this))
            }*/
        }


    }

    private fun startCamera() {
        if (binding?.capturedImageView?.visibility == View.VISIBLE) return // skip starting camera


        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder().build().also {
                it.setSurfaceProvider(binding?.previewView?.surfaceProvider)
            }

            imageCapture = ImageCapture.Builder().build()
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA

            try {
                cameraProvider.unbindAll()
                camera = cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageCapture
                )
                binding?.capturedImageView?.visibility = View.GONE
                binding?.previewView?.visibility = View.VISIBLE
            } catch (e: Exception) {
                Log.e("CameraFragment", "Camera use case binding failed", e)
            }
        }, ContextCompat.getMainExecutor(this))
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == cameraPermissionCode &&
            grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            startCamera()
        } else {
            Toast.makeText(this, "Camera permission denied", Toast.LENGTH_SHORT).show()
        }
    }
    override fun onResume() {
        super.onResume()

        // Clear any old image if needed
        /*viewModel.clearImage()

        // Make sure previewView is visible and camera restarts
        binding?.previewView?.visibility = View.VISIBLE
        binding?.capturedImageView?.visibility = View.GONE

        startCamera()*/
    }

   /* override fun onRestart() {
        super.onRestart()
        viewModel.imageUri.observe(this) { uri ->
            uri?.let {
                ProcessCameraProvider.getInstance(this).addListener({
                    val cameraProvider = ProcessCameraProvider.getInstance(this).get()
                    cameraProvider.unbindAll()

                    binding?.previewView?.visibility = View.GONE
                    binding?.capturedImageView?.visibility = View.VISIBLE
                    Glide.with(this).load(uri).into(binding?.capturedImageView!!)
                }, ContextCompat.getMainExecutor(this))
            }
        }

    }*/


}