package eu.tutorials.kidsdrawingapp.activities

import android.Manifest
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import android.widget.ImageButton
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.get
import eu.tutorials.kidsdrawingapp.DrawingViewModel
import eu.tutorials.kidsdrawingapp.FileOperations
import eu.tutorials.kidsdrawingapp.R
import eu.tutorials.kidsdrawingapp.databinding.ActivityMainBinding
import eu.tutorials.kidsdrawingapp.dialogs.BrushSizeChooserDialog

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private var mImageButtonCurrentPaint: ImageButton? = null
    private lateinit var drawingViewModel: DrawingViewModel
    private var brushDialog : BrushSizeChooserDialog? = null
    private lateinit var colorList: Map<String, ImageButton>

  private val openGalleryLauncher: ActivityResultLauncher<Intent> =
      registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result->
        if (result.resultCode == RESULT_OK && result.data != null)
            binding.ivBackground.setImageURI(result.data?.data)
    }

    /** create an ActivityResultLauncher with MultiplePermissions since we are requesting
     * both read and write
     */
    private val requestOpenGalleryPermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                   val pickIntent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                    openGalleryLauncher.launch(pickIntent)
                }
            }
        }

    private val requestSaveFilePermission =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->
            permissions.entries.forEach {
                val isGranted = it.value
                if (isGranted) {
                    val fileOperations = FileOperations(this, this, binding)
                    fileOperations.getSaveOperationForBitmapFile()
                    drawingViewModel.paintModel.filePath = fileOperations.imageFilePath!!
                }
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        drawingViewModel = DrawingViewModel(binding.drawingView)
        val fileOperations = FileOperations(this, this, binding)
        fileOperations.getSaveOperationForBitmapFile(drawingViewModel.paintModel.filePath)
        colorList = mapOf("0" to binding.color0,
            "1" to binding.color1,
            "2" to binding.color2,
            "3" to binding.color3,
            "4" to binding.color4,
            "5" to binding.color5,
            "6" to binding.color6,
            "7" to binding.color7,
        )
        binding.drawingView.setSizeForBrush(drawingViewModel.paintModel.brushSize)
        val linearLayoutPaintColors = binding.llPaintColors
        mImageButtonCurrentPaint = linearLayoutPaintColors[drawingViewModel.paintModel.paintColorId] as ImageButton
        mImageButtonCurrentPaint?.setImageDrawable(
            ContextCompat.getDrawable(
                this,
                R.drawable.pallet_pressed
            )
        )
        binding.ibBrush.setOnClickListener {
            showBrushSizeChooserDialog()
        }
        binding.ibGallery.setOnClickListener {
            requestReadStoragePermission()
        }
        binding.ibUndo.setOnClickListener {
            binding.drawingView.onClickUndo()
        }
        binding.ibSave.setOnClickListener {
            requestWriteStoragePermission()
        }
    }

    override fun onStop() {
        super.onStop()
        drawingViewModel.paintModel.brushSize = brushDialog?.brushSize ?: 20.toFloat()
        drawingViewModel.paintModel.drawingView = binding.drawingView
    }

    private fun requestWriteStoragePermission() {
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE
            )
        ) {
            showRationaleDialog()
        } else {
            requestSaveFilePermission.launch(
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }

    /**
     * Method is used to launch the dialog to select different brush sizes.
     */
    private fun showBrushSizeChooserDialog() {
        brushDialog = BrushSizeChooserDialog(this, binding)
        brushDialog?.show()
    }

    /**
     * Method is called when color is clicked from pallet_normal.
     *
     * @param view ImageButton on which click took place.
     */
    fun paintClicked(view: View) {
        if (view !== mImageButtonCurrentPaint) {
            val imageButton = view as ImageButton
            val colorTag = imageButton.tag.toString()
            val colorClicked = colorList.filter { it.value.tag == colorTag }
            val id = colorClicked.keys.toList()[0]
            drawingViewModel.paintModel.paintColorId = id.toInt()
            binding.drawingView.setColor(colorTag)
            imageButton.setImageDrawable(ContextCompat.getDrawable(this, R.drawable.pallet_pressed))
            mImageButtonCurrentPaint?.setImageDrawable(
                ContextCompat.getDrawable(
                    this,
                    R.drawable.pallet_normal
                )
            )
            mImageButtonCurrentPaint = view
        }
    }


    //create a method to requestStorage permission
    private fun requestReadStoragePermission() {
    // Check if the permission was denied and show rationale
        if (
            ActivityCompat.shouldShowRequestPermissionRationale(
                this,
                    Manifest.permission.READ_EXTERNAL_STORAGE)
        ){
            showRationaleDialog()
        }
        else {
            requestOpenGalleryPermission.launch(
                arrayOf (
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                )
            )
        }
    }


    /**  Create rationale dialog
     * Shows rationale dialog for displaying why the app needs permission,
     * only shown if the user has denied the permission request previously
     */
    private fun showRationaleDialog(
        title: String = "Kids Drawing App",
        message: String = "Kids Drawing App needs to Access Your External Storage",
    ) {
        val builder: AlertDialog.Builder = AlertDialog.Builder(this)
        builder.setTitle(title)
            .setMessage(message)
            .setPositiveButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
        builder.create().show()
    }

}