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
import java.time.format.DateTimeFormatter


class BasketTasksAdapter(var tasks: List<Task>, var context: Context) : RecyclerView.Adapter<BasketTasksAdapter.MyViewHolder>(){


    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val taskName : TextView = view.findViewById(R.id.task_list_name)
        val taskTime : TextView = view.findViewById(R.id.task_list_time)
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
        var space = " "
        space = space.repeat(41 - tasks[position].tag.length - tasks[position].name.length)
        holder.taskName.text = tasks[position].name + space + tasks[position].tag
        holder.taskTime.text = tasks[position].date.format(dateFormatter) + "                                         " + tasks[position].time.toString()

        if (tasks[position].importance == true){
            val drawable = GradientDrawable()
            drawable.cornerRadius = 46f // Установите радиус закругления
            drawable.setColor(Color.parseColor("#FFCCCB")) // Цвет фона

            // Устанавливаем фон для вашего Layout
            holder.layout.background = drawable
        }


        holder.layout.setOnClickListener{
            val intent = Intent(context, BasketTaskActivity::class.java)

            intent.putExtra("nameTask", tasks[position].name);
            intent.putExtra("timeTask", tasks[position].time.toString());
            intent.putExtra("dateTask", tasks[position].date.format(dateFormatter));
            intent.putExtra("noteTask", tasks[position].note)
            intent.putExtra("importanceTask", tasks[position].importance)
            intent.putExtra("tagTask", tasks[position].tag)
            intent.putExtra("position", position)


            if (context is Activity) {
                (context as Activity).startActivityForResult(intent, 1)
            }
        }
    }

}