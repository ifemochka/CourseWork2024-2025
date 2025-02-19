package com.example.first

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.text.TextUtils
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.format.DateTimeFormatter


class CalendarAdapter(private val items: List<Pair<String, List<CalendarTask>>>,  private val context: Context) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.date_text_view)
        val tasksContainer: LinearLayout = view.findViewById(R.id.tasks_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val (date, tasks) = items[position]

        // Устанавливаем текст даты
        holder.dateTextView.text = LocalDate.ofYearDay(2025, position+Data.currentDay).format(dateFormatter)

        // Очищаем контейнер задач
        holder.tasksContainer.removeAllViews()

        // Если есть задачи, добавляем их в контейнер
        if (tasks.isNotEmpty()) {
            for (task in tasks) {
                val taskTextView = TextView(holder.itemView.context).apply {
                    text = task.name
                    textSize = 14f
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    gravity = Gravity.CENTER

                    setPadding(4, 4, 4, 4)
                    background = GradientDrawable().apply {
                        setColor(task.color)
                        cornerRadius = 40f
                    }

                    layoutParams = LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.MATCH_PARENT,
                        48.dpToPx(holder.itemView.context)
                    ).apply {
                        setMargins(0,  0,0, 8.dpToPx(holder.itemView.context),) // Отступ сверху (можно изменить по желанию)
                    }

                }
                holder.tasksContainer.addView(taskTextView)
            }
        } else {
            val taskTextView = TextView(holder.itemView.context).apply {
                text = "Задач нет"
                textSize = 14f
                maxLines = 1
                ellipsize = TextUtils.TruncateAt.END
                gravity = Gravity.CENTER

                setPadding(4, 4, 4, 4)
                layoutParams = LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    48.dpToPx(holder.itemView.context)
                )

            }
            holder.tasksContainer.addView(taskTextView)
            // Если нет задач, можно добавить пустую ячейку или оставить ее пустой
            //holder.tasksContainer.visibility = View.GONE // Скрываем контейнер если нет задач
        }

        holder.tasksContainer.setOnClickListener{
            Data.selectedDay = position+Data.currentDay
            val intentToDay = Intent(context, DayActivity::class.java)
            context.startActivity(intentToDay)
        }
    }

    override fun getItemCount(): Int {
        return items.size
    }

    fun Int.dpToPx(context: Context): Int {
        return (this * context.resources.displayMetrics.density).toInt()
    }

}


