package com.example.first

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class DayActivity : BaseActivity() {
    private lateinit var IfEmpty : TextView
    private lateinit var tasksList : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)
        startCheckingTime();

        val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

        var title : TextView = findViewById(R.id.main_label)
        tasksList = findViewById(R.id.tasksList)
        IfEmpty = findViewById(R.id.empty_label)
        title.text = "Список задач на ${LocalDate.ofYearDay(2025, Data.selectedDay).format(dateFormatter)}"

        val orant = Data.tasks.filter { it.date() == LocalDate.ofYearDay(2025, Data.selectedDay) }

        if(orant.size != 0){
            IfEmpty.text = "";
        }

        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.adapter = TasksAdapter(orant, this)

    }
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val orant = Data.tasks.filter { it.date() == LocalDate.ofYearDay(2025, Data.selectedDay) }

        if(orant.size != 0){
            IfEmpty.text = "";
        }

        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.adapter = TasksAdapter(orant, this)
    }

    override fun onResume(){
        super.onResume()
        val dayTasks = Data.tasks.filter { it.date() == LocalDate.ofYearDay(2025, Data.selectedDay) }

        if(dayTasks.size != 0){
            IfEmpty.text = "";
        }

        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.adapter = TasksAdapter(dayTasks, this)
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



