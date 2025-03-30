package com.example.first

import android.os.Bundle
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.util.AttributeSet
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity


class StatisticActivity : AppCompatActivity() {

    lateinit var mAndC : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_statistic)

        mAndC = findViewById(R.id.completedAndMoved)

        mAndC.text = "Выполнено задач: ${Data.basket.size}. Перенесено задач: ${Data.moved}"

        val barChartView = findViewById<BarChartView>(R.id.barChartView)


        // Пример данных для диаграммы
        var data = Data.tasksInWeeks.toIntArray()


        barChartView.setData(data)
    }
}
class BarChartView(context: Context, attrs: AttributeSet) : View(context, attrs) {

    private val paint = Paint()
    private var data = intArrayOf()

    val labels = Data.labels

    // Фиксированная ширина столбиков
    private val fixedBarWidth = 150f

    fun setData(data: IntArray) {
        this.data = data
        invalidate()
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)

        if (data.isEmpty()) return

        val width = width.toFloat() + 30f
        val height = height.toFloat()
        val maxValue = data.maxOrNull() ?: 1

        paint.color = Color.BLACK
        paint.strokeWidth = 5f
        canvas.drawLine(30f, height - 100f, width+30f, height - 100f, paint) // X-axis

        // Рисуем столбики
        for (i in data.indices) {
            val barHeight = (height - 150) * (data[i].toFloat() / maxValue)
            val left = i * fixedBarWidth + 30f // Используем фиксированную ширину
            val right = left + fixedBarWidth
            val top = height - 100 - barHeight

            paint.color = Color.parseColor("#1E88E5")
            canvas.drawRect(left, top, right, height - 100f, paint)

            // Пишем значения на столбиках
            paint.color = Color.BLACK
            paint.textSize = 40f
            canvas.drawText(data[i].toString(), left + fixedBarWidth / 2, top - 10, paint)

            // Подписи по оси X
            paint.color = Color.BLACK
            paint.textSize = 35f
            canvas.drawText(labels[i], left-30f, height - 65f, paint)
        }
    }

    override fun onMeasure(widthMeasureSpec: Int, heightMeasureSpec: Int) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec)

        val desiredWidth = (data.size * fixedBarWidth).toInt()
        setMeasuredDimension(desiredWidth, measuredHeight)
    }
}
