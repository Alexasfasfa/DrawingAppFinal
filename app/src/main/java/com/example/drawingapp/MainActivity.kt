package com.example.drawingapp

import android.app.Dialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.ImageButton
import android.widget.SeekBar
import android.widget.TextView

class MainActivity : AppCompatActivity(), View.OnClickListener {
    private lateinit var drawingView: DrawingView

    private lateinit var brushButton: ImageButton
    private lateinit var purpleButton: ImageButton
    private lateinit var greenButton: ImageButton
    private lateinit var redButton: ImageButton
    private lateinit var orangeButton: ImageButton
    private lateinit var blueButton: ImageButton
    private lateinit var undoButton: ImageButton

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
        }
    }

}