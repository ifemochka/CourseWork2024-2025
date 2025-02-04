package com.example.first

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.first.MainActivity.Companion.REQUEST_CODE
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Calendar

class BasketTaskActivity : AppCompatActivity() {

    private lateinit var time : TextView
    private lateinit var date : TextView
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_basket_task)


        val name : TextView = findViewById(R.id.name)
        time = findViewById(R.id.time)
        date = findViewById(R.id.date)
        val note : TextView = findViewById(R.id.note)
        val importance : TextView = findViewById(R.id.importance)
        val tag : TextView = findViewById(R.id.tag)
        val endButton : Button = findViewById(R.id.end_button)
        val saveButton : Button = findViewById(R.id.save_button)


        name.text = intent.getStringExtra("nameTask")
        time.text = intent.getStringExtra("timeTask")
        date.text = intent.getStringExtra("dateTask")
        note.text = intent.getStringExtra("noteTask")
        importance.text =  if (intent.getBooleanExtra("importanceTask", false) == true) "Да" else "Нет"
        tag.text = intent.getStringExtra("tagTask")


        var position : Int = intent.getIntExtra("position", 0)

        endButton.setOnClickListener{


            val nameTask: String = name.getText().toString();
            val timeTask: String = time.getText().toString();
            val dateTask: String = date.getText().toString();
            val noteTask: String = note.getText().toString();
            val task = Task(nameTask, LocalTime.parse(timeTask, timeFormatter), LocalDate.parse(dateTask, dateFormatter), noteTask, Data.basket[position].importance , Data.basket[position].tag);
            Data.basket[position] = task
            Data.tasks.add(Data.basket[position])
            Data.basket.removeAt(position)
            val intentToMain = Intent()
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
                val task = Task(nameTask.toString(), LocalTime.parse(timeTask, timeFormatter), LocalDate.parse(dateTask, dateFormatter), noteTask.toString(), Data.basket[position].importance , Data.basket[position].tag);
                Data.basket[position] = task

            }
            val intentToMain = Intent()
            setResult(Activity.RESULT_OK, intentToMain)
            finish()
        }

        time.setOnClickListener{
            showTimePickerDialog()
        }
        date.setOnClickListener{
            showDatePickerDialog()
        }
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