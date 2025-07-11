package com.example.translatorproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.*
import com.example.translatorproject.databinding.ActivityCropBinding
import com.example.translatorproject.utils.ClipboardUtils
import com.example.translatorproject.viewmodels.CropViewModel

class CropActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCropBinding
    private val viewModel: CropViewModel by viewModels()

    private lateinit var imageUri: Uri
    private lateinit var originalBitmap: Bitmap

    private var croppedUri: Uri? = null

    private val cropLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
            croppedUri = result.uriContent!!
            binding.imgBeforeTrans.setImageURI(croppedUri)
            Toast.makeText(this, "Crop successful!", Toast.LENGTH_SHORT).show()
        } else {
            croppedUri=imageUri
            Log.e("CropError", "Crop failed", result.error)

            Toast.makeText(this, "Crop failed: ${result.error}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)



        val uriString = intent.getStringExtra("image_uri")
        if (uriString != null) {
            imageUri = Uri.parse(uriString)
            contentResolver.openInputStream(imageUri)?.use {
                originalBitmap = BitmapFactory.decodeStream(it)
                binding.imgBeforeTrans.setImageBitmap(originalBitmap)
            }
        }

       /* imageUri = Uri.parse(intent.getStringExtra("image_uri")
        originalBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))!!
        binding.imgBeforeTrans.setImageURI(imageUri)
*/
        binding.cropIcon1.setOnClickListener {
            launchCropper(imageUri)
        }

        binding.transBtn.setOnClickListener {
            if (croppedUri!=null)
                croppedUri?.let {   viewModel.performOCR(it) }


            else
                viewModel.performOCR(imageUri)

        }
        binding.retakeCamera.setOnClickListener {
            val intent = Intent(this, CameraActivity::class.java)
            startActivity(intent)
            finish()
        }


        observeViewModel()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, CameraActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun launchCropper(uri: Uri) {
        val cropOptions = CropImageContractOptions(
            uri,
            CropImageOptions().apply {
                guidelines = CropImageView.Guidelines.ON
                cropShape = CropImageView.CropShape.RECTANGLE
                autoZoomEnabled = true
            }
        )
        cropLauncher.launch(cropOptions)
    }

    private fun observeViewModel() {
        viewModel.translatedBitmap.observe(this) { bitmap ->
            if (bitmap != null) {
                binding.beforeTransLayout.visibility = View.GONE
                binding.afterTranslationLayout.visibility = View.VISIBLE
                binding.imgAfterTrans.setImageBitmap(bitmap)



                binding.saveImage.setOnClickListener {
                    val fileName = "translated_${System.currentTimeMillis()}.jpg"
                    viewModel.saveBitmap(bitmap, fileName)
                }
                binding.switchToggle.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
                    if (isChecked) {
                        binding.imgAfterTrans.setImageBitmap(bitmap)
                    } else {
                        croppedUri?.let { uri ->
                            contentResolver.openInputStream(uri)?.use { inputStream ->
                                originalBitmap = BitmapFactory.decodeStream(inputStream)
                            }
                        } ?: run {
                            Toast.makeText(this, "Image URI is null", Toast.LENGTH_SHORT).show()
                        }
                        binding.imgAfterTrans.setImageBitmap(originalBitmap)
                    }
                }
            }
        }

        viewModel.ocrError.observe(this) { error ->
            error?.let {
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.detectedText.observe(this) {
            binding.speakIcon.setOnClickListener {

                Log.d("code",viewModel.detectedText.value.toString()+" " +viewModel.detectedLangCode.value.toString())

                viewModel.speak(viewModel.detectedText.value.toString(),viewModel.detectedLangCode.value.toString() )

            }

        }
        viewModel.translatedText.observe(this) {
            binding.speakIcon.setOnClickListener {

                Log.d("code",viewModel.detectedText.value.toString()+" " +viewModel.detectedLangCode.value.toString())

                viewModel.speak(viewModel.detectedText.value.toString(),viewModel.detectedLangCode.value.toString() )

            }
            binding.copyIcon.setOnClickListener {
                val intent = Intent(this, ImageTranslateActivity::class.java).apply {
                    putExtra("source_text",viewModel.detectedText.value.toString() )
                    putExtra("translated_text", viewModel.translatedText.value.toString())
                }
                startActivity(intent)

            }
        }

    }
}




/*

package com.example.translatorproject

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.Rect
import android.graphics.Typeface
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.CompoundButton
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.canhub.cropper.CropImage
import com.canhub.cropper.CropImageContract
import com.canhub.cropper.CropImageContractOptions
import com.canhub.cropper.CropImageOptions
import com.canhub.cropper.CropImageView
import com.example.translatorproject.databinding.ActivityCropBinding
import com.google.mlkit.nl.translate.TranslateLanguage
import com.google.mlkit.nl.translate.Translation
import com.google.mlkit.nl.translate.TranslatorOptions
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.text.TextRecognition
import com.google.mlkit.vision.text.latin.TextRecognizerOptions
import java.io.InputStream

class CropActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCropBinding
    private lateinit var imageUri: Uri
    private lateinit var croppedUri: Uri



    private lateinit var originalBitmap: Bitmap
    private var translatedBitmap: Bitmap? = null


    // Register the cropper result launcher
    private val cropLauncher = registerForActivityResult(CropImageContract()) { result ->
        if (result.isSuccessful) {
             croppedUri = result.uriContent!!
            if (croppedUri != null) {
                binding.img1.setImageURI(croppedUri)

                Toast.makeText(this, "Crop successful!", Toast.LENGTH_SHORT).show()

            } else {
                Toast.makeText(this, "Cropped URI is null", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Crop failed: ${result.error}", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCropBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Get image URI passed from intent
        val uriString = intent.getStringExtra("image_uri")
        if (uriString != null) {
            imageUri = Uri.parse(uriString)
            originalBitmap = BitmapFactory.decodeStream(contentResolver.openInputStream(imageUri))!!

            showImage(imageUri)
        } else {
            Toast.makeText(this, "Image not found", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        // When crop icon is clicked
        binding.cropIcon1.setOnClickListener {
            launchCropper(imageUri)
        }
        binding.transBtn.setOnClickListener {
             performOCR(croppedUri)
        }
    }

    private fun showImage(uri: Uri) {
        binding.img1.setImageURI(uri)
    }

    private fun launchCropper(uri: Uri) {
        val cropOptions = CropImageContractOptions(
            uri,
            CropImageOptions().apply {
                guidelines = CropImageView.Guidelines.ON
                aspectRatioX = 1
                aspectRatioY = 1
                cropShape = CropImageView.CropShape.RECTANGLE
                fixAspectRatio = false
                autoZoomEnabled = true
                allowFlipping = true
                allowRotation = true
            }
        )
        cropLauncher.launch(cropOptions)
    }
    private fun performOCR(imageUri: Uri) {
        try {
            val inputImage = InputImage.fromFilePath(this, imageUri)
            val recognizer = TextRecognition.getClient(TextRecognizerOptions.DEFAULT_OPTIONS)


            val originalBitmap = getBitmapFromUri(imageUri)
            if (originalBitmap == null) {
                Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show()
                return
            }
            recognizer.process(inputImage)
                .addOnSuccessListener { visionText ->
                    val extractedText = visionText.text
                    if (extractedText.isNotEmpty()) {
                        Toast.makeText(this, "OCR success!   $extractedText", Toast.LENGTH_SHORT).show()


                        val translatedBlocks = mutableListOf<Pair<Rect, String>>()

                        val blocks = visionText.textBlocks
                        if (blocks.isEmpty()) {
                            Toast.makeText(this, "No text found", Toast.LENGTH_SHORT).show()
                            return@addOnSuccessListener
                        }

                        for (block in blocks) {
                            val originalText = block.text
                            val rect = block.boundingBox ?: continue

                            // Replace this with actual translation

                            val options = TranslatorOptions.Builder()
                                .setSourceLanguage(TranslateLanguage.ENGLISH)
                                .setTargetLanguage(TranslateLanguage.URDU)
                                .build()
                            val translator = Translation.getClient(options)
                            translator.downloadModelIfNeeded().addOnSuccessListener {
                                translator.translate(originalText).addOnSuccessListener { translatedText ->
                                //  callback(translatedText)
                                    translatedBlocks.add(rect to translatedText)
                                    translatedBitmap = drawTranslatedTextOnImage(originalBitmap, translatedBlocks)



                                    changingLayout()

                                }
                            }

                           */
/* val translatedText = "↳ ${originalText.uppercase()}"
                            translatedBlocks.add(rect to translatedText)
                            translatedBitmap = drawTranslatedTextOnImage(originalBitmap, translatedBlocks)

                            binding.img1.setImageBitmap(translatedBitmap)*//*

                        }


                       // binding.ocrResultText.text = extractedText // if you have a TextView
                        // Optionally: Send to translator function
                       // translateText(extractedText)
                    } else {
                        Toast.makeText(this, "No text found in image", Toast.LENGTH_SHORT).show()
                    }
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "OCR failed: ${e.message}", Toast.LENGTH_SHORT).show()
                }

        } catch (e: Exception) {
            Toast.makeText(this, "Error loading image: ${e.message}", Toast.LENGTH_SHORT).show()
        }
    }
    private fun getBitmapFromUri(uri: Uri): Bitmap? {
        return try {
            val inputStream: InputStream? = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            null
        }
    }

    private fun drawTranslatedTextOnImage(
        bitmap: Bitmap,
        translatedTexts: List<Pair<Rect, String>>
    ): Bitmap {
        val mutableBitmap = bitmap.copy(Bitmap.Config.ARGB_8888, true)
        val canvas = Canvas(mutableBitmap)

        val bgPaint = Paint().apply {
            color = Color.WHITE
            style = Paint.Style.FILL
            isAntiAlias = true
        }

        val textPaint = Paint().apply {
            color = Color.BLACK
            textSize = 140f
            style = Paint.Style.FILL
            isAntiAlias = true
            typeface = Typeface.DEFAULT_BOLD
        }

        translatedTexts.forEach { (rect, text) ->
            // Draw background box
            val padding = 10
            val bgRect = Rect(
                rect.left - padding,
                rect.top - padding,
                rect.right + padding,
                rect.bottom + padding
            )
            canvas.drawRect(bgRect, bgPaint)

            // Draw text
            canvas.drawText(text, rect.left.toFloat(), rect.bottom.toFloat(), textPaint)
        }

        return mutableBitmap
    }
    private fun changingLayout() {
        binding.beforeTransLayout.visibility=View.GONE
        binding.afterTranslationLayout.visibility=View.VISIBLE
        binding.img.setImageBitmap(translatedBitmap)

        binding.switchToggle.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked && translatedBitmap != null) {
                binding.img.setImageBitmap(translatedBitmap)
            } else {
                binding.img.setImageBitmap(originalBitmap)
            }
        }
    }
}


*/
