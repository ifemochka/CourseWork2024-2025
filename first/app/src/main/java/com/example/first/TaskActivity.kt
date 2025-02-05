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
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.first.MainActivity.Companion.REQUEST_CODE
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class TaskActivity : AppCompatActivity() {

    private lateinit var time : TextView
    private lateinit var date : TextView
    var tag : String = "Без тэга"
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    lateinit var adapter : ArrayAdapter<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task)


        val name : TextView = findViewById(R.id.name)
        time = findViewById(R.id.time)
        date = findViewById(R.id.date)
        val note : TextView = findViewById(R.id.note)
        val importance : TextView = findViewById(R.id.importance)
        val endButton : Button = findViewById(R.id.end_button)
        val saveButton : Button = findViewById(R.id.save_button)


        name.text = intent.getStringExtra("nameTask")
        time.text = intent.getStringExtra("timeTask")
        date.text = intent.getStringExtra("dateTask")
        note.text = intent.getStringExtra("noteTask")
        importance.text =  if (intent.getBooleanExtra("importanceTask", false) == true) "Да" else "Нет"
        tag = intent.getStringExtra("tagTask").toString()

        val spinner: Spinner = findViewById(R.id.spinner)
        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Data.options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(Data.options.indexOf(tag))

        var position : Int = intent.getIntExtra("position", 0)

        endButton.setOnClickListener{
            val intentToMain = Intent()
            Data.basket.add(Data.tasks[position])
            Data.tasks.removeAt(position)

            setResult(Activity.RESULT_OK, intentToMain)
            finish()
        }

        saveButton.setOnClickListener{
            val nameTask: String = name.getText().toString();
            val timeTask: String = time.getText().toString();
            val dateTask: String = date.getText().toString();
            val noteTask: String = note.getText().toString();
            if(nameTask == "" || timeTask == ""){
                Toast.makeText(this, "Not all values", Toast.LENGTH_LONG).show()

            }
            else{
                val intentToMain = Intent()
                val task = Task(nameTask.toString(), LocalTime.parse(timeTask, timeFormatter), LocalDate.parse(dateTask, dateFormatter), noteTask.toString(), Data.tasks[position].importance , Data.tasks[position].urgency, tag);
                Data.tasks[position] = task


                setResult(Activity.RESULT_OK, intentToMain)
                finish()
            }
        }

        time.setOnClickListener{
            showTimePickerDialog()
        }
        date.setOnClickListener{
            showDatePickerDialog()
        }






        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                if (Data.options[position] == "+ Добавить тэг"){
                    showInputDialog()
                }
                else{
                    tag = Data.options[position]
                }
            }
            override fun onNothingSelected(parent: AdapterView<*>) {
            }


        }
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Введите название нового тэга")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { dialog, which ->
            val userInput = input.text.toString()
            Data.options[Data.options.size - 1] = userInput
            Data.options.add("+ Добавить тэг")
            adapter.notifyDataSetChanged()
            tag = userInput

        }
        builder.setNegativeButton("Отмена") { dialog, which -> dialog.cancel() }

        builder.show()
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
}