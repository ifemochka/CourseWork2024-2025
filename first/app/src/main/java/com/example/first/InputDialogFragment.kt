package com.example.first

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


@RequiresApi(Build.VERSION_CODES.O)
class OptionsDialogFragment : DialogFragment() {

    companion object {
        private const val ARG_OPTIONS = "options"

        fun newInstance(options: Array<String>): OptionsDialogFragment {
            val fragment = OptionsDialogFragment()
            val args = Bundle()
            args.putStringArray(ARG_OPTIONS, options)
            fragment.arguments = args
            return fragment
        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val options = arguments?.getStringArray(ARG_OPTIONS) ?: arrayOf()

        val builder = AlertDialog.Builder(requireActivity())
        var title : String = "Выбрать задачи:"
        if (options.size == 2){
            title = "Сортировать по:"
        }
        builder.setTitle(title)
            .setItems(options) { dialog, which ->
                val selectedOption = options[which]
                (activity as MainActivity).onOptionSelected(selectedOption)
            }
            .setNegativeButton("Отмена") { dialog, id ->
                dialog.dismiss()
            }

        return builder.create()
    }
}
