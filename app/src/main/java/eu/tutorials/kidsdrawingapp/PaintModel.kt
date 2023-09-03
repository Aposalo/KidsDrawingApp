package eu.tutorials.kidsdrawingapp

data class PaintModel (
    var paintColorId : Int = 1,
    var filePath: String = String(),
    var brushSize : Float = 20.toFloat(),
    var drawingView : DrawingView?
)