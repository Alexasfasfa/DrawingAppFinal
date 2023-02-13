package com.example.drawingapp

import android.app.AlertDialog
import android.app.Dialog
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.Bundle
import android.view.View
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import yuku.ambilwarna.AmbilWarnaDialog
import yuku.ambilwarna.AmbilWarnaDialog.OnAmbilWarnaListener
import android.Manifest
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Canvas
import android.os.Environment
import android.provider.MediaStore
import android.widget.*
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.ContextCompat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers.IO
import kotlinx.coroutines.Dispatchers.Main
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.util.*

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var drawingView: DrawingView

    private lateinit var brushButton: ImageButton
    private lateinit var purpleButton: ImageButton
    private lateinit var greenButton: ImageButton
    private lateinit var redButton: ImageButton
    private lateinit var orangeButton: ImageButton
    private lateinit var blueButton: ImageButton
    private lateinit var undoButton: ImageButton
    private lateinit var colorPickerButton: ImageButton
    private lateinit var galleryButton: ImageButton
    private lateinit var saveButton: ImageButton

    private val openGalleryLauncher: ActivityResultLauncher<Intent> =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            findViewById<ImageView>(R.id.gallery_image).setImageURI(result.data?.data)
        }

    private val requestPermission: ActivityResultLauncher<Array<String>> =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val permissionName = it.key
                val isGranted = it.value

                if (isGranted && (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Permission $permissionName granted", Toast.LENGTH_LONG)
                        .show()
                    val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)
                } else if (isGranted && (permissionName == Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "Permission $permissionName granted", Toast.LENGTH_LONG)
                        .show()
                    CoroutineScope(IO).launch {
                        saveImage(getBitmapFromView(findViewById(R.id.constraint_l3)))
                    }
                } else {
                    if (permissionName == Manifest.permission.READ_EXTERNAL_STORAGE || permissionName == Manifest.permission.WRITE_EXTERNAL_STORAGE) {
                        Toast.makeText(
                            this,
                            "Permission $permissionName denied",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            }
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        brushButton = findViewById(R.id.brush_button)
        purpleButton = findViewById(R.id.purple_button)
        greenButton = findViewById(R.id.green_button)
        redButton = findViewById(R.id.red_button)
        orangeButton = findViewById(R.id.orange_button)
        blueButton = findViewById(R.id.blue_button)
        undoButton = findViewById(R.id.undo_button)
        colorPickerButton = findViewById(R.id.color_picker_button)
        galleryButton = findViewById(R.id.button_gallery)
        saveButton = findViewById(R.id.button_save)

        drawingView = findViewById(R.id.drawing_view)
        drawingView.changeBurshSize(23.toFloat())

        brushButton.setOnClickListener {
            showBrushChooserDialog()
        }

        purpleButton.setOnClickListener(this)
        greenButton.setOnClickListener(this)
        redButton.setOnClickListener(this)
        orangeButton.setOnClickListener(this)
        blueButton.setOnClickListener(this)
        undoButton.setOnClickListener(this)
        colorPickerButton.setOnClickListener(this)
        galleryButton.setOnClickListener(this)
        saveButton.setOnClickListener(this)

    }


    private fun showBrushChooserDialog() {
        val brushDialog = Dialog(this@MainActivity)
        brushDialog.setContentView(R.layout.dialog_brush)
        val seekBarProgress = brushDialog.findViewById<SeekBar>(R.id.dialog_seek_bar)
        val showProgressTv = brushDialog.findViewById<TextView>(R.id.dialog_text_view_progress)

        seekBarProgress.setOnSeekBarChangeListener(object : SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(seekBar: SeekBar, p1: Int, p2: Boolean) {
                drawingView.changeBurshSize(seekBar.progress.toFloat())
                showProgressTv.text = seekBar.progress.toString()
            }

            override fun onStartTrackingTouch(p0: SeekBar?) {

            }

            override fun onStopTrackingTouch(p0: SeekBar?) {

            }

        })
        brushDialog.show()
    }

    override fun onClick(view: View?) {
        when (view?.id) {
            R.id.purple_button -> {
                drawingView.setColor("#D14EF6")
            }
            R.id.red_button -> {
                drawingView.setColor("#FA5B68")
            }
            R.id.orange_button -> {
                drawingView.setColor("#EFB041")
            }
            R.id.green_button -> {
                drawingView.setColor("#2DC40B")
            }
            R.id.blue_button -> {
                drawingView.setColor("#2F6FF1")
            }
            R.id.undo_button -> {
                drawingView.undoPath()
            }
            R.id.color_picker_button -> {
                showColorPickerDialog()
            }
            R.id.button_gallery -> {
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    requestStoragePermission()
                } else {
                    // get the image
                    val pickIntent =
                        Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)
                }
            }
            R.id.button_save -> {
                //save the image
                if (ActivityCompat.checkSelfPermission(
                        this,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                    != PackageManager.PERMISSION_GRANTED
                ) {
                    Toast.makeText(this, "IF called", Toast.LENGTH_SHORT).show()
                    requestStoragePermission()
                } else {
                    val layout = findViewById<ConstraintLayout>(R.id.constraint_l3)
                    CoroutineScope(IO).launch {
                        saveImage(getBitmapFromView(layout))
                    }
                }
            }
        }
    }

    private fun showColorPickerDialog() {
        val dialog = AmbilWarnaDialog(this, Color.GREEN, object : OnAmbilWarnaListener {
            override fun onCancel(dialog: AmbilWarnaDialog?) {

            }

            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                drawingView.setColor(color)
            }

        })
        dialog.show()
    }

    private fun requestStoragePermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.READ_EXTERNAL_STORAGE
            )
        ) {
            showRationaleDialog()
        } else if (ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            showRationaleDialog()
        } else {
            requestPermission.launch(
                arrayOf(
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                )
            )
        }
    }

    private fun showRationaleDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Storage permission")
            .setMessage("We need this permission in order to access the internal storage")
            .setPositiveButton(R.string.dialog_yes) { dialog, _ ->
                requestPermission.launch(
                    arrayOf(
                        Manifest.permission.READ_EXTERNAL_STORAGE,
                        Manifest.permission.WRITE_EXTERNAL_STORAGE
                    )
                )
                dialog.dismiss()
            }
        builder.create().show()
    }

    private fun getBitmapFromView(view: View): Bitmap {
        val bitmap = Bitmap.createBitmap(
            view.width, view.height,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(bitmap)
        view.draw(canvas)
        return bitmap
    }

    private suspend fun saveImage(bitmap: Bitmap) {
        val root =
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString()
                .toString()
        val myDir = File("$root/saved_images")
        myDir.mkdir()
        val generator = Random()
        var n = 10000
        n = generator.nextInt(n)
        val outPutFile = File(myDir, "Images-$n.jpg")
        if (outPutFile.exists()) {
            outPutFile.delete()
        } else {
            try {
                val out = FileOutputStream(outPutFile)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
                out.flush()
                out.close()
            } catch (e: Exception) {
                e.stackTrace
            }

            withContext(Main) {
                Toast.makeText(
                    this@MainActivity,
                    "${outPutFile.absolutePath} saved!",
                    Toast.LENGTH_LONG
                ).show()
            }
        }
    }
}