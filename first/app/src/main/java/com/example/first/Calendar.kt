package com.example.first

import android.content.ClipData
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.DragEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first.Data.end
import com.example.first.Data.start

class Calendar : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_calendar)
        startCheckingTime();

        val list: ImageView = findViewById(R.id.list)

        ResetCalendar()
        list.setOnClickListener{
            val intentToCalendar = Intent(this, MainActivity::class.java)
            startActivity(intentToCalendar)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        ResetCalendar()
    }
    override fun onResume(){
        super.onResume()
        ResetCalendar()

    }

    private fun ResetCalendar() {
        Data.hoursInDay = Data.end.hour - Data.start.hour
        Data.taskId.clear()
        var calendar : List<Pair<String, MutableList<Task>>> = List(356) { Pair("", mutableListOf()) }
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
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }
}