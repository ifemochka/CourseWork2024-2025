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
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import java.time.DayOfWeek
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
import java.util.Locale


class CalendarAdapter(private val items: List<Pair<String, List<CalendarTask>>>,  private val context: Context) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

    val dateFormatter = DateTimeFormatter.ofPattern("dd.MM.yyyy")
    class ViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val dateTextView: TextView = view.findViewById(R.id.date_text_view)
        val tasksContainer: RelativeLayout = view.findViewById(R.id.tasks_container)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.item_view, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val (date, tasks) = items[position]

        // Устанавливаем текст даты
        holder.dateTextView.text = LocalDate.ofYearDay(2025, position+Data.currentDay).format(dateFormatter)
        //val dayOfWeek = (LocalDate.ofYearDay(2025, position+Data.currentDay).dayOfWeek).value % 7




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
                    val topMarginDp = 720 * (task.time.hour * 60 + task.time.minute - (9 * 60)) / (Data.hoursInDay * 60)

                    setPadding(4, 4, 4, 4)
                    background = GradientDrawable().apply {
                        setColor(task.color)
                        cornerRadius = 40f
                    }

                    layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        36.dpToPx(holder.itemView.context)
                    ).apply {
                        topMargin = (topMarginDp * resources.displayMetrics.density).toInt() // Конвертация dp в px
                    }
                }


                holder.tasksContainer.addView(taskTextView)
            }
        } /*else {
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
        }*/

        createHorizontalStrips(holder.tasksContainer)

        holder.tasksContainer.setBackgroundResource(R.drawable.rounded_background)

        if(Data.weekBool[((LocalDate.ofYearDay(2025, position+Data.currentDay - 1)).dayOfWeek.value % 7)]){
            holder.tasksContainer.setBackgroundColor(Color.parseColor("#E3F1FF"))
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

    private fun createHorizontalStrips(parent: RelativeLayout) {
        val stripHeight = 1.5 // высота полоски в dp
        val stripCount = Data.hoursInDay // количество полосок (500dp / 100dp)
        val time = 9

        for (i in 0 until stripCount) {
            val strip = View(context).apply {
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    (stripHeight * resources.displayMetrics.density).toInt()
                ).apply {
                    topMargin = (i * (720/Data.hoursInDay) * resources.displayMetrics.density).toInt() // каждая полоска через 100dp
                }
                setBackgroundColor(Color.LTGRAY) // цвет полоски
            }

            // Создание TextView для подписи полоски
            if(i%3 == 0){
                val label = TextView(context).apply {
                    text = (time + i).toString() + ":00"// Подпись номером полоски
                    setTextColor(Color.DKGRAY)
                    layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = (i * (720/Data.hoursInDay) * resources.displayMetrics.density).toInt() // совпадает с верхним отступом полоски
                        leftMargin = 160 // Отступ слева для текста
                    }
                }
                parent.addView(label)
            }

            parent.addView(strip)
        }
    }

}


