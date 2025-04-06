package com.example.first

import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.widget.ImageView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@RequiresApi(Build.VERSION_CODES.O)
class Calendar : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        saveData(this)
        startCheckingTime();

        val list: ImageView = findViewById(R.id.list)

        ResetCalendar()
        list.setOnClickListener{
            saveData(this)
            val intentToCalendar = Intent(this, MainActivity::class.java)
            startActivity(intentToCalendar)
            saveData(this)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        saveData(this)
        ResetCalendar()
    }

    override fun onResume(){
        super.onResume()
        saveData(this)
        ResetCalendar()
    }

    private fun ResetCalendar() {
        Data.hoursInDay = Data.end.hour - Data.start.hour
        Data.taskId.clear()
        var calendar : List<Pair<String, MutableList<Task>>> = List(356) { Pair("", mutableListOf()) }
        for(task in Data.tasks){
            val index = task.date().dayOfYear - 1
           // var color: Int = 0xD6D75555.toInt()
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
            calendar[index].second.add(task)
            Data.taskColorMap[task] = color
        }

        val recyclerView = findViewById<RecyclerView>(R.id.recycler_view)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        val adapter = CalendarAdapter(calendar.subList(Data.currentDay-1, Data.currentDay+30), this)
        recyclerView.adapter = adapter
    }

    override fun onDestroy() {
        super.onDestroy()
        saveData(this)
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        saveData(this)
        handler.removeCallbacksAndMessages(null)
    }
}