package com.example.first

import android.annotation.SuppressLint
import android.app.Activity
import android.content.ClipData
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.os.Build
import android.text.TextUtils
import android.view.DragEvent
import android.view.GestureDetector
import android.view.Gravity
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.ScrollView
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import java.time.LocalDate
import java.time.LocalTime
import java.time.format.DateTimeFormatter

@RequiresApi(Build.VERSION_CODES.O)
class CalendarAdapter(private val items: List<Pair<String, List<Task>>>,  private val context: Context) : RecyclerView.Adapter<CalendarAdapter.ViewHolder>() {

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

        holder.dateTextView.text = LocalDate.ofYearDay(2025, position+Data.currentDay).format(dateFormatter)

        holder.tasksContainer.setOnDragListener(dragListener)

        holder.tasksContainer.removeAllViews()

        if (tasks.isNotEmpty()) {
            for (task in tasks) {
                val taskTextView = TextView(holder.itemView.context).apply {
                    text = task.name
                    id = View.generateViewId()

                    textSize = 14f
                    maxLines = 1
                    ellipsize = TextUtils.TruncateAt.END
                    gravity = Gravity.CENTER
                    val topMarginDp = 720 * (task.time.hour * 60 + task.time.minute - (Data.start.hour * 60)) / (Data.hoursInDay * 60)

                    setPadding(4, 4, 4, 4)
                    background = GradientDrawable().apply {
                        setColor(Data.taskColorMap[task]!!)
                        cornerRadius = 40f
                    }

                    layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT,
                        48.dpToPx(holder.itemView.context)
                    ).apply {
                        topMargin = (topMarginDp * resources.displayMetrics.density).toInt()
                    }
                }

                holder.tasksContainer.addView(taskTextView)
                Data.taskId[taskTextView] = task
                setupGestureDetector(taskTextView)
            }
        }

        createHorizontalStrips(holder.tasksContainer)

        holder.tasksContainer.setBackgroundResource(R.drawable.rounded_background)

        if(Data.weekBool[((LocalDate.ofYearDay(2025, position+Data.currentDay - 1)).dayOfWeek.value % 7)]){
            val drawable = GradientDrawable()
            drawable.cornerRadius = 46f
            drawable.setColor(Color.parseColor("#E3F1FF"))

            holder.tasksContainer.background = drawable
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
        val stripHeight = 1.5
        val stripCount = Data.hoursInDay
        val time = Data.start.hour

        for (i in 0 until stripCount) {
            val strip = View(context).apply {
                layoutParams = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    (stripHeight * resources.displayMetrics.density).toInt()
                ).apply {
                    topMargin = (i * (720/Data.hoursInDay) * resources.displayMetrics.density).toInt()
                }
                setBackgroundColor(Color.LTGRAY)
            }

            if(i%1 == 0){
                val label = TextView(context).apply {
                    text = (time + i).toString() + ":00"
                    setTextColor(Color.DKGRAY)
                    layoutParams = RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.WRAP_CONTENT,
                        RelativeLayout.LayoutParams.WRAP_CONTENT
                    ).apply {
                        topMargin = (i * (720/Data.hoursInDay) * resources.displayMetrics.density).toInt()
                        leftMargin = 160
                    }
                }
                parent.addView(label)
            }

            parent.addView(strip)
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private fun setupGestureDetector(view: TextView) {
        val gestureDetector = GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
            override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                val intent = Intent(context, TaskActivity::class.java)

                val task = Data.taskId[view]!!
                Data.currentTasks = arrayListOf<Task> (task)
                intent.putExtra("nameTask", task.name);
                intent.putExtra("timeTask", task.time.toString());
                intent.putExtra("dateTask", task.date.format(dateFormatter));
                intent.putExtra("noteTask", task.note)
                intent.putExtra("importanceTask", task.importance)
                intent.putExtra("urgencyTask", task.urgency)
                intent.putExtra("tagTask", task.tag)

                if (context is Activity) {
                    context.startActivityForResult(intent, 1)
                }
                return true
            }

            override fun onLongPress(e: MotionEvent) {
                val data = ClipData.newPlainText("", "")
                val shadowBuilder = View.DragShadowBuilder(view)
                view.startDragAndDrop(data, shadowBuilder, view, 0)
                view.visibility = View.INVISIBLE
            }
        })

        view.setOnTouchListener { v, event ->
            gestureDetector.onTouchEvent(event)
            true
        }
    }

    val dragListener = View.OnDragListener { v, event ->
        when (event.action) {
            DragEvent.ACTION_DRAG_STARTED -> true
            DragEvent.ACTION_DRAG_ENTERED -> true
            DragEvent.ACTION_DRAG_EXITED -> true
            DragEvent.ACTION_DROP -> {
                val draggedView = event.localState as View
                val owner = draggedView.parent as ViewGroup
                owner.removeView(draggedView)
                val container = v as RelativeLayout
                val dropY = event.y

                val parent = container.parent.parent as LinearLayout

                val scrollView = container.parent as ScrollView
                val position = parent.indexOfChild(scrollView)

                val dateTextView = parent.findViewById<TextView>(R.id.date_text_view)
                val date = dateTextView.text.toString()
                Toast.makeText(context, "Задача '${(draggedView as TextView).text}' был перенесена $date", Toast.LENGTH_SHORT).show()
                Data.moved++


                val temp = Data.taskId[draggedView]!!
                val index = Data.tasks.indexOf(temp)
                val time = (dropY / context.resources.displayMetrics.density * Data.hoursInDay * 60 / 720 ).toInt() + Data.start.hour * 60
                val hours = time / 60
                val minutes = time % 60

                val formattedTime = LocalTime.of(hours, minutes)

                Data.tasks[index] = Task(temp.name, formattedTime, LocalDate.parse(date, dateFormatter), temp.note, temp.importance, temp.urgency, temp.tag)
                Data.taskId[draggedView] = Data.tasks[index]
                Data.taskColorMap[Data.tasks[index]] = Data.taskColorMap[temp] as Int


                val params = RelativeLayout.LayoutParams(
                    RelativeLayout.LayoutParams.MATCH_PARENT,
                    48.dpToPx(context)
                ).apply {
                    topMargin = dropY.toInt()
                }
                container.addView(draggedView, params)
                draggedView.visibility = View.VISIBLE
                true
            }
            DragEvent.ACTION_DRAG_ENDED -> true
            else -> false
        }
    }
}