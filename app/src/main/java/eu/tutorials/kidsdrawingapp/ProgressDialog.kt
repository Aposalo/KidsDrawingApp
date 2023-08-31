package eu.tutorials.kidsdrawingapp

import android.app.Dialog
import android.content.Context
import eu.tutorials.kidsdrawingapp.databinding.DialogCustomProgressBinding

class ProgressDialog(context: Context) :  Dialog(context) {

    private var binding = DialogCustomProgressBinding.inflate(layoutInflater)

    init{
        binding = DialogCustomProgressBinding.inflate(layoutInflater)
        setContentView(binding.root)
        this.setContentView(R.layout.dialog_custom_progress)
        this.show()
    }

    fun showProgressDialog() {
        this.show()
    }

    /**
     * This function is used to dismiss the progress dialog if it is visible to user.
     */
    fun cancelProgressDialog() {
        this.dismiss()
    }
}