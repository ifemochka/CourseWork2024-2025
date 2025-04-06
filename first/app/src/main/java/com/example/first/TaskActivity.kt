package com.example.first

import android.annotation.SuppressLint
import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.time.temporal.ChronoUnit
import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class TaskActivity : BaseActivity() {

    private lateinit var time : TextView
    private lateinit var date : TextView
    private lateinit var note : TextView
    private var position : Int = 0
    var tag : String = "Без тэга"
    private val timeFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    private val dateFormatter: DateTimeFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    lateinit var adapter : ArrayAdapter<String>
    var color: Int = Color.parseColor("#D6BAAFBA")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_task)
        startCheckingTime();

        val name : TextView = findViewById(R.id.name)
        time = findViewById(R.id.time)
        date = findViewById(R.id.date)
        note = findViewById(R.id.note)
        val importance : TextView = findViewById(R.id.importance)
        val urgency : TextView = findViewById(R.id.urgency)
        val endButton : Button = findViewById(R.id.end_button)
        val saveButton : Button = findViewById(R.id.save_button)
        val moveButton : TextView = findViewById(R.id.move)
        val spinner: Spinner = findViewById(R.id.spinner)

        name.text = intent.getStringExtra("nameTask")
        time.text = intent.getStringExtra("timeTask")
        date.text = intent.getStringExtra("dateTask")
        note.text = intent.getStringExtra("noteTask")
        importance.text =  if (intent.getBooleanExtra("importanceTask", false)) "Да" else "Нет"
        urgency.text =  if (intent.getBooleanExtra("urgencyTask", false)) "Да" else "Нет"
        tag = intent.getStringExtra("tagTask").toString()
        position  = intent.getIntExtra("position", 0)

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Data.options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter
        spinner.setSelection(Data.options.indexOf(tag))

        moveButton.setOnClickListener{
            showReminderDialog(this)
        }

        endButton.setOnClickListener{

            val daysBetween = ChronoUnit.DAYS.between( LocalDate.of(2025, 3, 31), Data.currentTasks[position].date())

            Data.tasksInWeeks[(daysBetween/7).toInt()]++;

            val intentToMain = Intent()
            saveData(this)

            Data.basket.add(Data.currentTasks[position])
            Data.tasks.removeAt(Data.tasks.indexOf(Data.currentTasks[position]))

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
                saveData(this)
                val task = Task(nameTask.toString(), LocalTime.parse(timeTask, timeFormatter), LocalDate.parse(dateTask, dateFormatter), noteTask.toString(), Data.tasks[position].importance , Data.tasks[position].urgency, tag);
                color = Data.taskColorMap[Data.currentTasks[position]]!!
                val index = Data.tasks.indexOf(Data.currentTasks[position])
                Data.tasks[index] = task
                Data.taskColorMap[task] = color
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
        note.setOnClickListener{
            noteDialog()
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

    private fun noteDialog(){
        val builder = AlertDialog.Builder(this)
        val dialogView = LayoutInflater.from(this).inflate(R.layout.dialog_note, null)
        val editNote = dialogView.findViewById<EditText>(R.id.editNote)
        if(note.text != ""){
            editNote.setText(note.text)
            editNote.setSelection(note.text.length)
        }

        builder.setView(dialogView)
            .setTitle("Заметка")
            .setPositiveButton("Сохранить") { _, _ ->
                note.text = editNote.text.toString()
            }
            .setNegativeButton("Отмена") { dialog, _ ->
                dialog.cancel()
            }

        builder.show()
    }

    private fun showInputDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Введите название нового тэга")

        val input = EditText(this)
        builder.setView(input)

        builder.setPositiveButton("OK") { _, _ ->
            val userInput = input.text.toString()
            Data.options[Data.options.size - 1] = userInput
            Data.options.add("+ Добавить тэг")
            adapter.notifyDataSetChanged()
            tag = userInput

        }
        builder.setNegativeButton("Отмена") { dialog, _ -> dialog.cancel() }

        builder.show()
    }

    @SuppressLint("DefaultLocale")
    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            time.text = String.format("%02d:%02d", selectedHour, selectedMinute)
        }, hour, minute, true)

        timePickerDialog.show()
    }

    @SuppressLint("DefaultLocale")
    private fun showDatePickerDialog() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(this,
            { _, selectedYear, selectedMonth, selectedDay ->
                date.text = String.format("%02d.%02d.%d", selectedDay, selectedMonth + 1, selectedYear)
            }, year, month, day)

        datePickerDialog.datePicker.minDate = calendar.getTimeInMillis();
        datePickerDialog.show()
    }

    private fun showReminderDialog(context: Context) {
        val options = arrayOf("На 1 час", "На 1 день", "На 1 неделю", "Произовольно")
        val builder = android.app.AlertDialog.Builder(context)

        builder.setTitle("Задачу перенести на")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> time.text = Data.currentTasks[position].time().plusHours(1).toString()
                    1 -> date.text = Data.currentTasks[position].date().plusDays(1).format(dateFormatter)
                    2 -> date.text = Data.currentTasks[position].date().plusDays(7).format(dateFormatter)
                    3 -> showDatePickerDialog()
                }
                Data.moved++
            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }

        builder.create().show()

    }

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }
}