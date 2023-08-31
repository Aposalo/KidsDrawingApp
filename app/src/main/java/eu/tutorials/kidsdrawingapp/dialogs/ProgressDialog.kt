package eu.tutorials.kidsdrawingapp.dialogs

import android.app.Dialog
import android.content.Context
import eu.tutorials.kidsdrawingapp.databinding.DialogCustomProgressBinding

class ProgressDialog(context: Context) :  Dialog(context) {

    private var binding = DialogCustomProgressBinding.inflate(layoutInflater)

    init {
        binding = DialogCustomProgressBinding.inflate(layoutInflater)
        this.setContentView(binding.root)
    }
}