package com.example.first

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

@RequiresApi(Build.VERSION_CODES.O)
class BasketActivity : BaseActivity() {
    private lateinit var IfEmpty : TextView
    private lateinit var tasksList: RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_basket)
        startCheckingTime();

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

    override fun onDestroy() {
        super.onDestroy()
        handler.removeCallbacksAndMessages(null)
    }

    override fun onStop() {
        super.onStop()
        handler.removeCallbacksAndMessages(null)
    }
}