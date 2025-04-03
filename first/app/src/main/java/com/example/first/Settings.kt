package com.example.first

import android.app.TimePickerDialog
import android.content.Context
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.CheckBox
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalTime

import java.util.Calendar

@RequiresApi(Build.VERSION_CODES.O)
class Settings : BaseActivity() {

    private lateinit var tvStartTime: TextView
    private lateinit var tvEndTime: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CheckboxAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        startCheckingTime();
        setContentView(R.layout.activity_settings)
        recyclerView = findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(this)
        tvStartTime = findViewById(R.id.tvStartTime)
        tvEndTime = findViewById(R.id.tvEndTime)
        tvStartTime.text = Data.start.toString()
        tvEndTime.text = Data.end.toString()

        tvStartTime.setOnClickListener {
            showTimePickerDialog(true)
        }

        tvEndTime.setOnClickListener {
            showTimePickerDialog(false)
        }

        val week = listOf("Понедельник", "Вторник", "Среда", "Четверг", "Пятница", "Суббота", "Воскресенье")
        adapter = CheckboxAdapter(this, week)
        recyclerView.adapter = adapter

        val backButton: Button = findViewById(R.id.back_button)
        backButton.setOnClickListener{
            finish()
        }
    }

    private fun showTimePickerDialog(isStartTime: Boolean) {
        val calendar = Calendar.getInstance()
        val hour = calendar.get(Calendar.HOUR_OF_DAY)

        val timePickerDialog = TimePickerDialog(this, { _, selectedHour, _ ->

            if (isStartTime) {
                tvStartTime.text = String.format("%02d:00", selectedHour)
                Data.start = LocalTime.of(selectedHour, 0)
            } else {
                if (selectedHour > Data.start.hour) {
                    tvEndTime.text = String.format("%02d:00", selectedHour)
                    Data.end = LocalTime.of(selectedHour, 0)
                } else {
                    tvEndTime.text = "23:00"
                    Data.end = LocalTime.of(23, 0)
                }
            }
        }, hour, 0, true)

        timePickerDialog.show()
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