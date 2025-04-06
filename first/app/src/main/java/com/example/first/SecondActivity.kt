package com.example.first

import android.app.Activity
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
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
import androidx.annotation.RequiresApi
import java.time.LocalDate
import androidx.appcompat.app.AlertDialog


@RequiresApi(Build.VERSION_CODES.O)
class SecondActivity: BaseActivity() {
    private lateinit var time: TextView
    private lateinit var date: TextView
    private lateinit var note: TextView
    lateinit var set_reminder: TextView
    var chosen_time : LocalTime = LocalTime.of(9, 0)
    var reminder_time : LocalTime? = null
    var color: Int = Color.parseColor("#D6BAAFBA")
    lateinit var adapter : ArrayAdapter<String>
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    var importance : Boolean = false
    var urgency : Boolean = false
    var isClickedG : Boolean = false
    var isClickedB : Boolean = false
    var isClickedY : Boolean = false
    val yellow : Int = Color.parseColor("#E6D79B")
    val blue : Int = Color.parseColor("#B0CEEF")
    val green : Int = Color.parseColor("#9ECEA0")

    var tag : String = Data.options[0]

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)
        val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
        startCheckingTime();

        set_reminder = findViewById(R.id.set)


        val name: EditText = findViewById(R.id.task_name)
        time = findViewById(R.id.task_time)
        date = findViewById(R.id.task_date)
        note = findViewById(R.id.task_note)

        val switchYesNo: Switch = findViewById(R.id.switchYesNo)
        val switchYesNo_urgency: Switch = findViewById(R.id.switchYesNo_urgency)
        val spinner: Spinner = findViewById(R.id.spinner)

        val readyButton: Button = findViewById(R.id.task_ready)
        val greenButton: TextView = findViewById(R.id.greenButton)
        val blueButton: TextView = findViewById(R.id.blueButton)
        val yellowButton: TextView = findViewById(R.id.yellowButton)


        greenButton.background = Background(green,true)
        blueButton.background = Background(blue,true)
        yellowButton.background = Background(yellow,true)

        time.setOnClickListener{
            showTimePickerDialog()
        }

        date.setOnClickListener{
            showDatePickerDialog()
        }
        note.setOnClickListener{
            noteDialog()
        }

        switchYesNo.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                importance = true
            } else {
                importance = false
            }
        }
        switchYesNo_urgency.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                urgency = true
            } else {
                urgency = false
            }
        }

        adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, Data.options)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner.adapter = adapter

        set_reminder.setOnClickListener{
            if(name.getText().toString() == "" || time.getText().toString() == "" || date.getText().toString() == ""){
                Toast.makeText(this, "Not all values", Toast.LENGTH_SHORT).show()

            } else {
                chosen_time =  LocalTime.parse(time.text, timeFormatter)
                showReminderDialog(this);
            }
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
                tag = Data.options[0]
            }
        }

        greenButton.setOnClickListener{
            if (isClickedG == false) {
                color = green
                greenButton.background = Background(green, false)
                blueButton.background = Background(blue, true)
                yellowButton.background = Background(yellow, true)
                isClickedG = true
                isClickedB = false
                isClickedY = false
            }
            else{
                color = Color.parseColor("#D6BAAFBA")
                greenButton.background = Background(green, true)
                isClickedG = false
            }
        }

        blueButton.setOnClickListener{
            if (isClickedB == false) {
                color = blue
                greenButton.background = Background(green, true)
                blueButton.background = Background(blue, false)
                yellowButton.background = Background(yellow, true)
                isClickedB= true
                isClickedG = false
                isClickedY = false
            }
            else{
                color = Color.parseColor("#D6BAAFBA")
                blueButton.background = Background(blue, true)
                isClickedB = false
            }
        }

        yellowButton.setOnClickListener{
            if (isClickedY == false) {
                color = yellow
                greenButton.background = Background(green, true)
                blueButton.background = Background(blue, true)
                yellowButton.background = Background(yellow, false)
                isClickedY= true
                isClickedG = false
                isClickedB = false
            }
            else{
                color = Color.parseColor("#D6BAAFBA")
                yellowButton.background = Background(yellow, true)
                isClickedY = false
            }
        }

        readyButton.setOnClickListener{
            val nameTask: String = name.getText().toString();
            val timeTask: String = time.getText().toString();
            val noteTask: String = note.getText().toString();
            val dateTask: String = date.getText().toString();
            val importanceTask = importance
            val urgencyTask = urgency
            val tagTask = tag


            if(nameTask == "" || timeTask == "" || dateTask == ""){
                Toast.makeText(this, "Not all values", Toast.LENGTH_SHORT).show()

            }
            else{
                val intentToMain = Intent()

                val task = Task(nameTask, LocalTime.parse(timeTask, timeFormatter), LocalDate.parse(dateTask, dateFormatter), noteTask, importanceTask, urgencyTask, tagTask);
                Data.tasks.add(task)
                Data.taskColorMap[task] = color
                if (reminder_time != null){
                Data.localTimes.add(Pair(reminder_time!!, nameTask))}

                setResult(Activity.RESULT_OK, intentToMain)
                finish()
            }

        }
    }

    fun showReminderDialog(context: Context) {
        val options = arrayOf("За 10 минут", "За 1 час", "За 1 день", "Выбрать своё время")
        val builder = android.app.AlertDialog.Builder(context)

        builder.setTitle("Выберите время напоминания")
            .setItems(options) { dialog, which ->
                when (which) {
                    0 -> reminder_time = chosen_time.minusMinutes(10) // 10 минут
                    1 -> reminder_time = chosen_time.minusHours(1)
                    2 -> reminder_time = chosen_time
                    3 -> showTimePickerDialog(context)
                }
                if(which!= 3){
                    setReminder()
                }

            }
            .setNegativeButton("Отмена") { dialog, _ -> dialog.dismiss() }

        builder.create().show()

    }

    fun showTimePickerDialog(context: Context) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Создаем TimePickerDialog
        val timePickerDialog = TimePickerDialog(context, { _, selectedHour, selectedMinute ->
            reminder_time = LocalTime.of(selectedHour, selectedMinute)
            setReminder()
        }, hour, minute, true)
        timePickerDialog.show()
    }

    fun setReminder() {
        set_reminder.text = "Напоминание установлено на ${reminder_time}"
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
            .setPositiveButton("Сохранить") { dialog, which ->
                note.text = editNote.text.toString()
            }
            .setNegativeButton("Отмена") { dialog, which ->
                dialog.cancel()
            }

        builder.show()
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

        datePickerDialog.getDatePicker().setMinDate(calendar.getTimeInMillis());

        datePickerDialog.show()
    }

    private fun showTimePickerDialog() {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)
        val minute = calendar.get(Calendar.MINUTE)

        // Создаем TimePickerDialog
        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, selectedMinute ->
            // Проверяем, находится ли выбранный час в допустимом диапазоне
            if (selectedHour < 9) {
                // Если час меньше 9, устанавливаем на 9:00
                time.text = "09:00"
            } else if (selectedHour > 21 || (selectedHour == 21 && selectedMinute > 0)) {
                // Если час больше 21 или 21:01 и позже, устанавливаем на 21:00
                time.text = "21:00"
            } else {
                // Если время в пределах допустимого диапазона, обновляем TextView
                time.text = String.format("%02d:%02d", selectedHour, selectedMinute)
            }
        }, hour, minute, true) // true - 24-часовой формат

        // Устанавливаем начальное время в пределах диапазона
        timePickerDialog.updateTime(9, 0)

        // Показываем диалог
        timePickerDialog.show()
    }

    fun Background(color: Int, isClicked : Boolean) :  GradientDrawable{
        val drawable = GradientDrawable().apply {
            shape = GradientDrawable.RECTANGLE
            setColor(color)
            if (isClicked == false){
                setStroke(7, Color.RED)
            }
            cornerRadius = 10f
        }
        return drawable
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