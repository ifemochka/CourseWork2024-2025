package com.example.first

import android.os.Bundle
import android.os.Handler
import android.os.Parcel
import android.os.Parcelable
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.time.LocalDate
import java.time.LocalTime

import java.io.Serializable
import java.time.format.DateTimeFormatter

class Task(

    val name: String,
    private val timeString: String,
    private val dateString: String,
    val note: String,
    val importance: Boolean?,
    val urgency: Boolean?,
    val tag: String
) : Serializable {

    val time: LocalTime = LocalTime.parse(timeString)
    val date: LocalDate = LocalDate.parse(dateString)

    constructor(name: String, time: LocalTime, date: LocalDate, note: String, importance: Boolean?, urgency:Boolean?, tag: String) : this(
        name,
        time.toString(),
        date.toString(),
        note,
        importance,
        urgency,
        tag
    )
}

abstract class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

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
        Toast.makeText(this, message, Toast.LENGTH_LONG).show()
    }
    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null) // Останавливаем проверки при уничтожении активности
    }
}



object Data {
    val localTimes = mutableListOf<Pair<LocalTime, String>>()
    var tasks = arrayListOf<Task>()
    var currentTasks = arrayListOf<Task>()
    var basket = arrayListOf<Task>()
    var options = mutableListOf("Без тэга", "Учёба", "Работа", "Дом", "+ Добавить тэг")
    var currentDay = LocalDate.now().dayOfYear
    var selectedDay = 0
    val taskColorMap: MutableMap<Task, Int> = mutableMapOf()
    val taskId: MutableMap<TextView, Task> = mutableMapOf()
    var start: LocalTime = LocalTime.of(9,0)
    var end : LocalTime = LocalTime.of(17, 0)
    var hoursInDay = end.hour - start.hour
    val weekBool: MutableList<Boolean> = mutableListOf(true, true, true, true, true, false, false)
}
