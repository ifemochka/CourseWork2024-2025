package com.example.first

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment


class OptionsDialogFragment : DialogFragment() {

    // Метод для создания нового экземпляра фрагмента с передачей аргументов
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
        // Получаем массив опций из аргументов
        val options = arguments?.getStringArray(ARG_OPTIONS) ?: arrayOf()

        // Создание AlertDialog с использованием Builder
        val builder = AlertDialog.Builder(requireActivity())
        var title : String = "Выбрать задачи:"
        if (options.size == 2){
            title = "Сортировать по:"
        }
        builder.setTitle(title)
            .setItems(options) { dialog, which ->
                // Обработка выбора опции
                val selectedOption = options[which]
                // Передача выбранной опции обратно в MainActivity
                (activity as MainActivity).onOptionSelected(selectedOption)
            }
            .setNegativeButton("Отмена") { dialog, id ->
                // Закрытие диалога при нажатии кнопки Отмена
                dialog.dismiss()
            }

        return builder.create()
    }
}
