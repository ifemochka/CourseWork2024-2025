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
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class BasketTasksAdapter(var tasks: List<Task>, var context: Context) : RecyclerView.Adapter<BasketTasksAdapter.MyViewHolder>(){


    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")

    class MyViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val taskName : TextView = view.findViewById(R.id.task_list_name)
        val taskTime : TextView = view.findViewById(R.id.task_list_time)
        val noteText : TextView = view.findViewById(R.id.note)
        val taskTag : TextView = view.findViewById(R.id.task_list_tag)
        val layout : LinearLayout = view.findViewById(R.id.linearLayout)
        val svgImageView: ImageView = view.findViewById(R.id.my_svg_image)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.task_in_list, parent, false)
        return MyViewHolder(view)
    }

    override fun getItemCount(): Int {
        return tasks.count()
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        var flag = false
        holder.taskName.text = tasks[position].name
        if(tasks[position].note != ""){
            holder.svgImageView.visibility = View.VISIBLE
        }
        holder.svgImageView.setOnClickListener{
            if(flag == false){
                val layoutParams = holder.noteText.layoutParams
                layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
                holder.noteText.layoutParams = layoutParams
                holder.noteText.text = tasks[position].note
                holder.svgImageView.setImageResource(R.drawable.arrow_up)
                flag = true
            }
            else{
                val layoutParams = holder.noteText.layoutParams
                layoutParams.height = 0
                holder.noteText.layoutParams = layoutParams
                holder.svgImageView.setImageResource(R.drawable.arrow_down)
                flag = false
            }
        }
        holder.taskTag.text = tasks[position].tag
        holder.taskTime.text =tasks[position].date.format(dateFormatter) + "                                           " + tasks[position].time.toString()

        val currentDateTime = LocalDateTime.now()
        val currentTime = currentDateTime.toLocalTime()
        val currentDate = currentDateTime.toLocalDate()

        val drawable = GradientDrawable()
        drawable.cornerRadius = 46f
        Data.taskColorMap[tasks[position]]?.let { drawable.setColor(it) }
        holder.layout.background = drawable

        if (tasks[position].importance == true) {
            val drawable = GradientDrawable()
            drawable.cornerRadius = 46f
            drawable.setColor(Color.parseColor("#FFCCCB")) // Цвет фона
            if (tasks[position].urgency == true) {
                drawable.setColor(Color.parseColor("#D6D75555"))
            }

            // Устанавливаем фон для вашего Layout
            holder.layout.background = drawable
        } else if (tasks[position].urgency == true) {
            val drawable = GradientDrawable()
            drawable.cornerRadius = 46f // Установите радиус закругления
            drawable.setColor(Color.parseColor("#FFCAAA")) // Цвет фона

            // Устанавливаем фон для вашего Layout
            holder.layout.background = drawable
        }


        holder.layout.setOnClickListener {
            val intent = Intent(context, BasketTaskActivity::class.java)

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