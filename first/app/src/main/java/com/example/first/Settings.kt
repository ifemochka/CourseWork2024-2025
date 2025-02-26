package com.example.first

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.CompoundButton
import android.widget.Switch
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

class Settings : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CheckboxAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_settings)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        val switcher: Switch = findViewById(R.id.switcher)
        if (Data.hoursInDay == 12){
            switcher.isChecked = true}
        else{
            switcher.isChecked = false
        }
        switcher.setOnCheckedChangeListener { _: CompoundButton, isChecked: Boolean ->
            if (isChecked) {
                Data.hoursInDay = 12
            } else {
                Data.hoursInDay = 8
            }
        }

        // Пример списка элементов
        val week = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
        adapter = CheckboxAdapter(this, week)
        recyclerView.adapter = adapter

        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener{
            finish()
        }
    }
}

class CheckboxAdapter(private val context: Context, private val items: List<String>) :
    RecyclerView.Adapter<CheckboxAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val checkBox: CheckBox = view.findViewById(R.id.checkBox)
        val textView: TextView = view.findViewById(R.id.textView)

        fun bind(item: String, position: Int) {
            textView.text = item
            checkBox.isChecked = Data.weekBool[position]

            checkBox.setOnCheckedChangeListener { _, isChecked ->
                Data.weekBool[position] = isChecked
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_checkbox, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position], position)
    }

    override fun getItemCount(): Int {
        return items.size
    }
}