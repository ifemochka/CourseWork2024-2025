package com.example.first

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalTime
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class DayActivity : AppCompatActivity() {
    private lateinit var IfEmpty : TextView
    private lateinit var tasksList : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_day)

        tasksList = findViewById(R.id.tasksList)
        IfEmpty = findViewById(R.id.empty_label)

        val orant = Data.tasks.filter { it.date == LocalDate.ofYearDay(2025, Data.selectedDay) }

        if(orant.size != 0){
            IfEmpty.text = "";
        }

        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.adapter = TasksAdapter(orant, this)

    }
    companion object {
        const val REQUEST_CODE = 10
    }
}



