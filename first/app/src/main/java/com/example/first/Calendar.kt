package com.example.first

import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Calendar : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        var calendar : List<Pair<String, MutableList<CalendarTask>>> = List(356) { Pair("", mutableListOf()) }
        for(task in Data.tasks){
            val index = task.date.dayOfYear - 1
            var color: Int = Data.taskColorMap[task]!!
            if (task.importance == true){
                color = Color.parseColor("#FFCCCB")
                if (task.urgency == true){
                    color = 0xD6D75555.toInt()
                }
            }
            else if (task.urgency == true){
                color = Color.parseColor("#FFCAAA")
            }
            calendar[index].second.add(CalendarTask(task.name, color))
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)


        val adapter = CalendarAdapter(calendar.subList(Data.currentDay-1, Data.currentDay+30), this)
        recyclerView.adapter = adapter
    }
}