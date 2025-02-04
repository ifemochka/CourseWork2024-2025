package com.example.first

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.first.MainActivity.Companion.REQUEST_CODE
import java.time.LocalDate
import java.time.LocalTime

class BasketActivity : AppCompatActivity() {
    private lateinit var IfEmpty : TextView
    private lateinit var tasksList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)


        IfEmpty = findViewById(R.id.empty_label)
        tasksList = findViewById(R.id.tasksList)

        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.adapter = BasketTasksAdapter(Data.basket,this)
        if (Data.basket.size > 0){
            IfEmpty.text = ""
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(Data.tasks.size != 0){
            IfEmpty.text = "";
        }
        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.adapter = BasketTasksAdapter(Data.basket,this)
    }


}