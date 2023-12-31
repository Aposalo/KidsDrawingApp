package eu.tutorials.kidsdrawingapp.dialogs

import android.app.Dialog
import android.content.Context
import android.view.View
import eu.tutorials.kidsdrawingapp.databinding.ActivityMainBinding
import eu.tutorials.kidsdrawingapp.databinding.DialogBrushSizeBinding

class BrushSizeChooserDialog(context: Context, private var mainBinding: ActivityMainBinding) : Dialog(context) {

    private var binding = DialogBrushSizeBinding.inflate(layoutInflater)
    var brushSize = 20.toFloat()

    init {
        binding = DialogBrushSizeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setTitle("Brush size :")
        binding.ibSmallBrush.setOnClickListener(onClickSetSizeForBrush(10.toFloat()))
        binding.ibMediumBrush.setOnClickListener(onClickSetSizeForBrush(20.toFloat()))
        binding.ibLargeBrush.setOnClickListener(onClickSetSizeForBrush(30.toFloat()))
    }

    private fun onClickSetSizeForBrush(size: Float):View.OnClickListener {
        return View.OnClickListener {
            brushSize = size
            mainBinding.drawingView.setSizeForBrush(size)
            this.dismiss()
        }
    }

}