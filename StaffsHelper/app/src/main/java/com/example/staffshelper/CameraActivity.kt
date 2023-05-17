package com.example.staffshelper

import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException

import androidx.camera.view.LifecycleCameraController
import androidx.camera.view.PreviewView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import com.example.staffshelper.databinding.ActivityCameraBinding
import java.io.File
import java.text.SimpleDateFormat
import java.util.*

class CameraActivity : AppCompatActivity() {

    private lateinit var viewBinding: ActivityCameraBinding

    private  lateinit var cameraController: LifecycleCameraController

    var darkMode: Boolean = false
    var textSize: Int = 18
    var textColour: String = "none" //only get colour from darkmode

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        viewBinding = ActivityCameraBinding.inflate(layoutInflater)

        darkMode = intent.getBooleanExtra("SETTINGS_DARKMODE", false)
        textSize = intent.getIntExtra("SETTING_TXTSIZE", 18)
        textColour = intent.getStringExtra("SETTINGS_TEXTCOLOUR").toString()


        setContentView(viewBinding.root)

        if (!hasPermissions(baseContext)){
            activityResultLauncher.launch(REQUIRED_PERMS)
        }
        else {
            startCamera()
        }

        viewBinding.CameraStartButton.setOnClickListener {
            takePhoto()
        }

        settings()
    }

    private fun startCamera(){
        //val previewView: PreviewView = findViewById(R.id.viewFinder)
        val previewView: PreviewView = viewBinding.viewFinder
        cameraController = LifecycleCameraController(baseContext)
        cameraController.bindToLifecycle(this)
        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA

        previewView.controller = cameraController
    }

    private fun takePhoto(){
        val name= SimpleDateFormat(FILENAME_FORMAT, Locale.ENGLISH).format(System.currentTimeMillis())
        val contentValues = ContentValues().apply{
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.P) {
                put(MediaStore.MediaColumns.RELATIVE_PATH, "Pictures/StaffsHelper")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(contentResolver, MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues).build()
        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object : ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                    Toast.makeText(baseContext, "Photo taken: " + name, Toast.LENGTH_SHORT).show()
                    val imageFile = File("/storage/emulated/0/Pictures/StaffsHelper/$name.jpg")
                    val imageView: ImageView = findViewById(R.id.imageFromCameraView)

                    if(imageFile.exists()) {
                        val imageBitmap = BitmapFactory.decodeFile(imageFile.absolutePath)
                        imageView.setImageBitmap(imageBitmap)
                    }
                    else{
                        Toast.makeText(baseContext, "Image preview not found. $imageFile", Toast.LENGTH_SHORT).show()
                    }

                    //save file local
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(baseContext, "Failed to take photo", Toast.LENGTH_SHORT).show()

                }

            }
        )


    }

    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){
                permissions ->
            var permissionsGranted = true
            permissions.entries.forEach {
                if (it.key in REQUIRED_PERMS && it.value == false) permissionsGranted = false
            }
            if (!permissionsGranted){
                Toast.makeText(this, "Permission Denied", Toast.LENGTH_SHORT).show()
            }
            else{
                startCamera()
            }
        }


    companion object{
        private const val FILENAME_FORMAT = "yyyy-MM-dd-HH-mm-ss-SSS"
        //file format

        private val REQUIRED_PERMS =
            mutableListOf(
                android.Manifest.permission.CAMERA
            ).apply {
                if (Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
                    add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                }
            }.toTypedArray()
        fun hasPermissions(context: Context) = REQUIRED_PERMS.all {
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    private fun settings(){
        val backGround: ConstraintLayout = findViewById(R.id.CamaraLayout)
        if (darkMode){

            backGround.setBackgroundColor(Color.BLACK)
        }
        else{
            backGround.setBackgroundColor(Color.WHITE)
        }
    }


}
