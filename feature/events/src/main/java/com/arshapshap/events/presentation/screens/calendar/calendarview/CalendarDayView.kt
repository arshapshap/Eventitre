package com.arshapshap.events.presentation.screens.calendar.calendarview

import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import android.graphics.drawable.Drawable
import android.util.AttributeSet
import android.view.Gravity
import android.widget.FrameLayout
import android.widget.TextView
import com.arshapshap.events.R

class CalendarDayView(context: Context, attrs: AttributeSet) : FrameLayout(context, attrs) {
    private val textView: TextView
    private val circlePaint: Paint = Paint().apply {
        color = Color.BLACK
        style = Paint.Style.FILL
        isAntiAlias = true
    }
    var circleCount: Int = 0
        set(count) {
            field = count
            invalidate()
        }
    var contentColor: Int = Color.BLACK
        set(color) {
            field = color
            textView.setTextColor(color)
            invalidate()
        }
    var contentAlpha: Float = 1f
        set(alpha) {
            field = alpha
            textView.alpha = alpha
            invalidate()
        }
    var text: String = ""
        set(text) {
            field = text
            textView.text = text
            textView.invalidate()
        }

    init {
        textView = TextView(context, null, 0, com.arshapshap.common_ui.R.style.Text_Small)
        textView.layoutParams = LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT, Gravity.CENTER)
        textView.gravity = Gravity.CENTER
        addView(textView)


        val typedArray = context.obtainStyledAttributes(attrs, R.styleable.CalendarDayView)
        circleCount = typedArray.getInt(R.styleable.CalendarDayView_circleCount, 0)
        contentColor = typedArray.getColor(R.styleable.CalendarDayView_circleColor, 0)
        typedArray.recycle()
    }

    override fun setBackground(background: Drawable?) {
        super.setBackground(background)
        if (background == null)
            setBackgroundColor(Color.TRANSPARENT)
    }

    override fun onDraw(canvas: Canvas) {
        super.onDraw(canvas)
        drawSmallCircles(canvas)
    }

    private fun drawSmallCircles(canvas: Canvas) {
        val centerX = width * 0.5f
        val centerY = height * 0.8f
        val radius = width * 0.04f
        val distanceBetweenCircles = radius * 1.3f

        circlePaint.color = contentColor
        circlePaint.alpha = (contentAlpha * 255).toInt()
        when (circleCount) {
            0 -> { }
            1 -> {
                canvas.drawCircle(centerX, centerY, radius, circlePaint)
            }
            2 -> {
                canvas.drawCircle(centerX - distanceBetweenCircles, centerY, radius, circlePaint)
                canvas.drawCircle(centerX + distanceBetweenCircles, centerY, radius, circlePaint)
            }
            3 -> {
                canvas.drawCircle(centerX - distanceBetweenCircles * 2, centerY, radius, circlePaint)
                canvas.drawCircle(centerX, centerY, radius, circlePaint)
                canvas.drawCircle(centerX + distanceBetweenCircles * 2, centerY, radius, circlePaint)
            }
            else -> {
                val leftCircleX = centerX - distanceBetweenCircles * 3
                val rightCircleX = centerX + distanceBetweenCircles * 3
                val distance = (rightCircleX - leftCircleX) / (circleCount - 1)

                canvas.drawCircle(leftCircleX, centerY, radius, circlePaint)
                for (i in 1..circleCount-2)
                    canvas.drawCircle(leftCircleX + distance * i, centerY, radius, circlePaint)
                canvas.drawCircle(rightCircleX, centerY, radius, circlePaint)
            }
        }
    }
}

