package com.example.first

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.CompoundButton
import android.widget.EditText
import android.widget.ListView
import android.widget.Spinner
import android.widget.Switch
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar


import android.widget.TextView
import java.time.LocalDate
import androidx.appcompat.app.AlertDialog

class SecondActivity : AppCompatActivity() {
    private lateinit var time: TextView
    private lateinit var date: TextView
    lateinit var adapter : ArrayAdapter<String>
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    var importance : Boolean = false

    var tag : String = Data.options[0]

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")

        val name: EditText = findViewById(R.id.task_name)
        time = findViewById(R.id.task_time)
        date = findViewById(R.id.task_date)
        val note: EditText = findViewById(R.id.task_note)

        val switchYesNo: Switch = findViewById(R.id.switchYesNo)
        val spinner: Spinner = findViewById(R.id.spinner)

        val readyButton: Button = findViewById(R.id.task_ready)

        time.setOnClickListener{
            showTimePickerDialog()
        }

        date.setOnClickListener{
            showDatePickerDialog()
        }

        switchYesNo.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                importance = true
            } else {
                importance = false
            }
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Data.options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter


        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (Data.options[position] == "+ Добавить тэг"){
                    showInputDialog()
                }

                tag = Data.options[position]

            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                tag = Data.options[0]
            }
        }


        readyButton.setOnClickListener{
            val nameTask: String = name.getText().toString();
            val timeTask: String = time.getText().toString();
            val noteTask: String = note.getText().toString();
            val dateTask: String = date.getText().toString();
            val importanceTask = importance
            val tagTask = tag


            if(nameTask == "" || timeTask == "" || dateTask == ""){
                Toast.makeText(this, "Not all values", Toast.LENGTH_LONG).show()

            }
            else{
                val intentToMain = Intent()

                val task = Task(nameTask, LocalTime.parse(timeTask, timeFormatter), LocalDate.parse(dateTask, dateFormatter), noteTask, importanceTask, tagTask);
                Data.tasks.add(task)

                setResult(Activity.RESULT_OK, intentToMain)
                finish()
            }

        }
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Введите информацию")


        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val userInput = input.text.toString()
            Data.options[Data.options.size - 1] = userInput
            Data.options = Data.options.plus("+ Добавить тэг")
            adapter.notifyDataSetChanged()
            Toast.makeText(this, "Добавлено: $userInput", Toast.LENGTH_SHORT).show()

        }
        builder.setNegativeButton("Отмена") { dialog, which -> dialog.cancel() }

        builder.show()
    }

    private fun showDatePickerDialog() {
        // Получаем текущую дату
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Создаем DatePickerDialog
        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                // Обновляем текстовое поле с выбранной датой
                date.text = String.format("%02d.%02d.%d", selectedDay, selectedMonth + 1, selectedYear)
            }, year, month, day)

        // Показываем диалог
        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        // Получаем текущее время
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Создаем TimePickerDialog
        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Обновляем TextView с выбранным временем
            time.text = String.format("%02d:%02d", selectedHour, selectedMinute)
        }, hour, minute, true) // true - 24-часовой формат

        // Показываем диалог
        timePickerDialog.show()
    }
}