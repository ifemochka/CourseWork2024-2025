package com.example.first

import android.content.Context
import android.os.Build
import android.os.Handler
import android.view.View
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken
import java.time.LocalDate
import java.time.LocalTime

import java.io.Serializable
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class Task(
    val name: String,
    private val timeString: String,
    private val dateString: String,
    val note: String,
    val importance: Boolean?,
    val urgency: Boolean?,
    val tag: String
) : Serializable {

    fun date() : LocalDate {
        return LocalDate.parse(dateString)
    }
    fun time() : LocalTime {
        return LocalTime.parse(timeString)
    }


    constructor(name: String, time: LocalTime, date: LocalDate, note: String, importance: Boolean?, urgency:Boolean?, tag: String) : this(
        name,
        time.toString(),
        date.toString(),
        note,
        importance,
        urgency,
        tag
    )

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (other !is Task) return false

        return name == other.name &&
                timeString == other.timeString &&
                dateString == other.dateString &&
                note == other.note &&
                importance == other.importance &&
                urgency == other.urgency &&
                tag == other.tag
    }

    override fun hashCode(): Int {
        var result = name.hashCode()
        result = 31 * result + timeString.hashCode()
        result = 31 * result + dateString.hashCode()
        result = 31 * result + note.hashCode()
        result = 31 * result + (importance?.hashCode() ?: 0)
        result = 31 * result + (urgency?.hashCode() ?: 0)
        result = 31 * result + tag.hashCode()
        return result
    }
}

@RequiresApi(Build.VERSION_CODES.O)
abstract class BaseActivity : AppCompatActivity() {

    val handler = Handler()
    private val checkInterval: Long = 60000 // 60 секунд
    var k = 1

    fun startCheckingTime() {
        handler.post(object : Runnable {
            override fun run() {
                checkCurrentTime()
                if(k == 1){
                    k = 0
                    val now = LocalTime.now()
                    val seconds = 60 - now.second
                    handler.postDelayed(this, (seconds * 1000).toLong())
                    checkCurrentTime()
                }

                handler.postDelayed(this, checkInterval)
            }
        })
    }

    private fun checkCurrentTime() {
        val currentTime = LocalTime.now()

        for (pair in Data.localTimes) {
            var time = pair.first
            if (currentTime.hour == time.hour && currentTime.minute == time.minute) {
                showToast("Напоминание: ${time.format(DateTimeFormatter.ofPattern("HH:mm"))} ${pair.second}")
            }
        }
    }

    private fun showToast(message: String) {
        val inflater = layoutInflater
        // Создаем View на основе кастомной разметки
        val layout: View = inflater.inflate(R.layout.custom_toast, null)

        // Находим TextView в кастомном макете
        val toastText: TextView = layout.findViewById(R.id.toast_text)
        toastText.text = message  // Устанавливаем текст

        // Создаем Toast
        val toast = Toast(applicationContext)
        toast.duration = Toast.LENGTH_SHORT
        toast.view = layout  // Устанавливаем кастомный макет
        toast.show()  // Показываем Toast
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Останавливаем проверки при уничтожении активности
    }

    fun saveData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()


        val gson = Gson()

        // Сериализация данных
        editor.putInt("moved", Data.moved)

        val times = Data.localTimes.map { it.first.format(DateTimeFormatter.ISO_LOCAL_TIME) } // Сохраняем время как строку
        val strings = Data.localTimes.map { it.second }
        editor.putStringSet("times", times.toSet())
        editor.putStringSet("strings", strings.toSet())

        editor.putString("tasks", gson.toJson(Data.tasks))
        editor.putString("currentTasks", gson.toJson(Data.currentTasks))
        editor.putString("basket", gson.toJson(Data.basket))
        editor.putString("options", gson.toJson(Data.options))
        editor.putInt("currentDay", Data.currentDay)
        editor.putInt("selectedDay", Data.selectedDay)

        val tasksArray = Data.taskColorMap.keys.toList()
        val colorsArray = Data.taskColorMap.values.toList()

        // Сериализация массивов в JSON
        editor.putString("tasksArray", gson.toJson(tasksArray))
        editor.putString("colorsArray", gson.toJson(colorsArray))

        editor.putString("start", Data.start.toString())
        editor.putString("end", Data.end.toString())
        editor.putString("weekBool", gson.toJson(Data.weekBool))
        editor.putString("labels", gson.toJson(Data.labels))
        editor.putString("tasksInWeeks", gson.toJson(Data.tasksInWeeks))
        editor.putInt("score", Data.score)

        editor.apply()
    }

    fun loadData(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val gson = Gson()

        Data.moved = sharedPreferences.getInt("moved", 0)

        val timesSet = sharedPreferences.getStringSet("times", emptySet()) ?: emptySet()
        val stringsSet = sharedPreferences.getStringSet("strings", emptySet()) ?: emptySet()
        val times = timesSet.map { LocalTime.parse(it, DateTimeFormatter.ISO_LOCAL_TIME) }
        val strings = stringsSet.toList()
        Data.localTimes = times.zip(strings).toMutableList()


        Data.tasks.clear()
        Data.tasks.addAll(
            gson.fromJson(
                sharedPreferences.getString("tasks", "[]"),
                object : TypeToken<List<Task>>() {}.type
            )
        )

        Data.currentTasks.clear()
        Data.currentTasks.addAll(
            gson.fromJson(
                sharedPreferences.getString("currentTasks", "[]"),
                object : TypeToken<List<Task>>() {}.type
            )
        )

        Data.basket.clear()
        Data.basket.addAll(
            gson.fromJson(
                sharedPreferences.getString("basket", "[]"),
                object : TypeToken<List<Task>>() {}.type
            )
        )

        Data.options.clear()
        Data.options.addAll(
            gson.fromJson(
                sharedPreferences.getString("options", "[\"Без тэга\", \"Учёба\", \"Работа\", \"Дом\", \"+ Добавить тэг\"]"),
                object : TypeToken<List<String>>() {}.type
            )
        )

        Data.currentDay = sharedPreferences.getInt("currentDay", LocalDate.now().dayOfYear)
        Data.selectedDay = sharedPreferences.getInt("selectedDay", 0)


        val tasksJson = sharedPreferences.getString("tasksArray", null)
        val colorsJson = sharedPreferences.getString("colorsArray", null)

        val taskType = object : TypeToken<List<Task>>() {}.type
        val colorType = object : TypeToken<List<Int>>() {}.type
        val tasksList : List<Task> = if (tasksJson != null) {
            gson.fromJson(tasksJson, taskType) ?: emptyList()
        } else {
            emptyList()
        }
        val colorsList :List<Int> = if (colorsJson != null) {
            gson.fromJson(colorsJson, colorType) ?: emptyList()
        } else {
            emptyList()
        }
        Data.taskColorMap.clear()
        Data.taskColorMap = tasksList.zip(colorsList).toMap().toMutableMap()


        Data.start =
            LocalTime.parse(sharedPreferences.getString("start", LocalTime.of(9, 0).toString()))
        Data.end =
            LocalTime.parse(sharedPreferences.getString("end", LocalTime.of(17, 0).toString()))

        Data.weekBool.clear()
        Data.weekBool.addAll(
            gson.fromJson(
                sharedPreferences.getString("weekBool", "[true, true, true, true, true, false, false]"),
                object : TypeToken<List<Boolean>>() {}.type
            )
        )

        Data.labels.clear()
        Data.labels.addAll(
            gson.fromJson(
                sharedPreferences.getString("labels", "[\"31.03\", \"07.04\", \"14.04\", \"21.04\", \"28.04\", \"05.05\"]"),
                object : TypeToken<List<String>>() {}.type
            )
        )

        Data.tasksInWeeks.clear()
        Data.tasksInWeeks.addAll(
            gson.fromJson(
                sharedPreferences.getString("tasksInWeeks", "[0, 0, 0, 0, 0,0]"),
                object : TypeToken<List<Int>>() {}.type
            )
        )
        Data.score = sharedPreferences.getInt("score", 0)
    }

    fun clearSharedPreferences(context: Context) {
        val sharedPreferences = context.getSharedPreferences("MyPrefs", Context.MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.clear()
        editor.apply()
    }
}

@RequiresApi(Build.VERSION_CODES.O)
object Data {
    var moved = 0
    var localTimes = mutableListOf<Pair<LocalTime, String>>()
    var tasks = arrayListOf<Task>()
    var currentTasks = arrayListOf<Task>()
    var basket = arrayListOf<Task>()
    var options = mutableListOf("Без тэга", "Учёба", "Работа", "Дом", "+ Добавить тэг")
    var currentDay = LocalDate.now().dayOfYear
    var selectedDay = 0
    var taskColorMap: MutableMap<Task, Int> = mutableMapOf()
    var taskId: MutableMap<TextView, Task> = mutableMapOf()
    var start: LocalTime = LocalTime.of(9,0)
    var end : LocalTime = LocalTime.of(17, 0)
    var hoursInDay = end.hour - start.hour
    val weekBool: MutableList<Boolean> = mutableListOf(true, true, true, true, true, false, false)
    val labels =  arrayListOf<String>("31.03", "07.04", "14.04", "21.04", "28.04", "05.05")
    val tasksInWeeks = mutableListOf(0, 0, 0, 0, 0,0)
    var score = 0
}
