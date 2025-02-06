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


class MainActivity : AppCompatActivity() {


    private var currentTasks = arrayListOf<Task>()
    private lateinit var IfEmpty : TextView
    private lateinit var tasksList : RecyclerView
    private val sortOptions = arrayOf("Время", "Важность", "Срочность", "Текущие задачи")
    val timeFormatter = DateTimeFormatter.ofPattern("HH:mm")
    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        val basketButton: Button = findViewById(R.id.basketButton)

        tasksList = findViewById(R.id.tasksList)
        IfEmpty = findViewById(R.id.empty_label)
        if(Data.tasks.size != 0){
            IfEmpty.text = "";
        }
        val reset : TextView = findViewById(R.id.reset)
        val newButton: Button = findViewById(R.id.new_button)
        val filterButton: Button = findViewById(R.id.filter_button)
        val sortButton: Button = findViewById(R.id.sort_button)


        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.adapter = TasksAdapter(Data.tasks,this)

        reset.setOnClickListener{
            Reset()
        }

        sortButton.setOnClickListener {
            showOptionsDialog(sortOptions)
        }

        filterButton.setOnClickListener {
            showOptionsDialog(Data.options.toTypedArray().copyOfRange(0, Data.options.size-1))
        }


        newButton.setOnClickListener{
            val intentToSecond = Intent(this, SecondActivity::class.java)
            startActivityForResult(intentToSecond, REQUEST_CODE)
        }

        basketButton.setOnClickListener{
            val intentToBasket = Intent(this, BasketActivity::class.java)
            startActivity(intentToBasket)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(Data.tasks.size != 0){
            IfEmpty.text = "";
        }
        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.adapter = TasksAdapter(Data.tasks,this)
    }

    override fun onResume(){
        super.onResume()
        if(Data.tasks.size != 0){
            IfEmpty.text = "";
        }
        else{
            IfEmpty.text = "Задач пока нет"
        }
        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.adapter = TasksAdapter(Data.tasks,this)

    }

    fun Reset(){
        tasksList.layoutManager = LinearLayoutManager(this)
        tasksList.adapter = TasksAdapter(Data.tasks,this)
    }


    private fun showOptionsDialog(list: Array<String>) {
        val dialog = OptionsDialogFragment.newInstance(list)
        dialog.show(supportFragmentManager, "OptionsDialog")
    }

    fun onOptionSelected(option: String) {
        currentTasks = Data.tasks
        tasksList.layoutManager = LinearLayoutManager(this)
        if (option == sortOptions[0]){
            tasksList.adapter = TasksAdapter(currentTasks.sortedWith(compareBy({ it.date }, { it.time })),this)
        }
        else if(option == sortOptions[1]){
            tasksList.adapter = TasksAdapter(currentTasks.sortedBy { it.importance }.reversed(),this)

        }
        else if(option == sortOptions[2]){
            tasksList.adapter = TasksAdapter(currentTasks.sortedBy { it.urgency }.reversed(),this)

        }
        else if(option == sortOptions[3]){
            tasksList.adapter = TasksAdapter(filterTasks(currentTasks),this)
        }
        else {
            tasksList.adapter = TasksAdapter(currentTasks.filter { it.tag == option },this)
        }
    }

    fun filterTasks(tasks: List<Task>): List<Task> {
        val currentDate = LocalDate.now()
        val currentTime = LocalTime.now()

        return tasks.filter { task ->
            task.date > currentDate || (task.date == currentDate && task.time > currentTime.minusMinutes(1) )
        }
    }


    companion object {
        const val REQUEST_CODE = 10
    }
}



