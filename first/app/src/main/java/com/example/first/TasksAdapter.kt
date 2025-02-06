package com.example.first

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.format.DateTimeFormatter


class TasksAdapter(var tasks: List<Task>, var context: Context) : RecyclerView.Adapter<TasksAdapter.MyViewHolder>(){


    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val taskName : TextView = view.findViewById(R.id.task_list_name)
        val taskTime : TextView = view.findViewById(R.id.task_list_time)
        val taskTag : TextView = view.findViewById(R.id.task_list_tag)
        val layout : LinearLayout = view.findViewById(R.id.linearLayout)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasks.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.taskName.text = tasks[position].name
        if(tasks[position].note != ""){
            holder.taskName.text = tasks[position].name + " (!)"
        }
        holder.taskTag.text = tasks[position].tag
        holder.taskTime.text = tasks[position].date.format(dateFormatter) + "                                           " + tasks[position].time.toString()

        val currentDateTime = LocalDateTime.now()
        val currentTime = currentDateTime.toLocalTime()
        val currentDate = currentDateTime.toLocalDate()

        if (tasks[position].date < currentDate || (tasks[position].date == currentDate && tasks[position].time <= currentTime.minusMinutes(1) )){
            val drawable = GradientDrawable()
            drawable.cornerRadius = 46f
            drawable.setColor(Color.parseColor("#D6E9E1E1"))
            holder.layout.background = drawable
            holder.taskTime.setTextColor(Color.RED)
        }
        else{

            if (tasks[position].importance == true){
                val drawable = GradientDrawable()
                drawable.cornerRadius = 46f
                drawable.setColor(Color.parseColor("#FFCCCB")) // Цвет фона
                if (tasks[position].urgency == true){
                    drawable.setColor(Color.parseColor("#D6D75555"))
                }

                // Устанавливаем фон для вашего Layout
                holder.layout.background = drawable
            }
            else if (tasks[position].urgency == true){
                val drawable = GradientDrawable()
                drawable.cornerRadius = 46f // Установите радиус закругления
                drawable.setColor(Color.parseColor("#FFCAAA")) // Цвет фона

                // Устанавливаем фон для вашего Layout
                holder.layout.background = drawable
            }
        }


        holder.layout.setOnClickListener{
            val intent = Intent(context, TaskActivity::class.java)

            intent.putExtra("nameTask", tasks[position].name);
            intent.putExtra("timeTask", tasks[position].time.toString());
            intent.putExtra("dateTask", tasks[position].date.format(dateFormatter));
            intent.putExtra("noteTask", tasks[position].note)
            intent.putExtra("importanceTask", tasks[position].importance)
            intent.putExtra("urgencyTask", tasks[position].urgency)
            intent.putExtra("tagTask", tasks[position].tag)
            intent.putExtra("position", position)

            if (context is Activity) {
                (context as Activity).startActivityForResult(intent, 1)
            }
        }
    }

}