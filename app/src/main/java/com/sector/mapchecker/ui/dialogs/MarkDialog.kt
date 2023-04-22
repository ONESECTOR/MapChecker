package com.sector.mapchecker.ui.dialogs

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.sector.mapchecker.databinding.BottomSheetMarkBinding
import com.sector.mapchecker.extension.setWhiteNavigationBar

class MarkDialog: BottomSheetDialogFragment() {

    private var binding: BottomSheetMarkBinding? = null

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = BottomSheetMarkBinding.inflate(layoutInflater)

        return binding?.rootView
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = super.onCreateDialog(savedInstanceState)

        dialog.apply {
            setOnShowListener {
                //Делает навбар белым
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O_MR1) {
                    dialog.setWhiteNavigationBar()
                }
            }
        }

        return dialog
    }

    companion object {
        const val TAG = "MarkDialog"
    }
}