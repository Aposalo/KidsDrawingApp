package eu.tutorials.kidsdrawingapp

import androidx.lifecycle.ViewModel

class DrawingViewModel(drawingView: DrawingView)  : ViewModel() {

    val paintModel = PaintModel(drawingView = drawingView)
}