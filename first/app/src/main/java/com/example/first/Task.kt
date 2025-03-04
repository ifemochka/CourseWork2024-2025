package com.example.first

import android.os.Parcel
import android.os.Parcelable
import java.time.LocalDate
import java.time.LocalTime

import java.io.Serializable

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
data class CalendarTask(val name: String, val color: Int, val time: LocalTime)


object Data {
    var tasks = arrayListOf<Task>()
    var currentTasks = arrayListOf<Task>()
    var basket = arrayListOf<Task>()
    var options = mutableListOf("Без тэга", "Учёба", "Работа", "Дом", "+ Добавить тэг")
    var currentDay = LocalDate.now().dayOfYear
    var selectedDay = 0
    val taskColorMap: MutableMap<Task, Int> = mutableMapOf()
    var hoursInDay = 8

    val weekBool: MutableList<Boolean> = mutableListOf(true, true, true, true, true, false, false)
}
